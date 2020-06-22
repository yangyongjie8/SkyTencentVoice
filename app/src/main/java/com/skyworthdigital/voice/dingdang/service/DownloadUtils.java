package com.skyworthdigital.voice.dingdang.service;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.Closeable;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

public class DownloadUtils {
	private static final long MIN_SAPCE = 0;// 目前只检查极限值 容量不为0的

	private static String downloadRootPath;

	public static String genKeyForUrl(String url) {
		if (url == null) {
			return null;
		}

		String cacheKey = MD5Utils.getStringMD5(url.trim());
		// Log.d("DownloadUtils", "getKeyForUrl() url: " + url + ", cacheKey: "
		// + cacheKey);
		if (cacheKey == null || cacheKey.trim().length() <= 0) {
			cacheKey = String.valueOf(url.hashCode());
		}
		return cacheKey;
	}

	private static String getDownloadRootPathWithSdcard(Context context) {
		StringBuilder builder = new StringBuilder();
		File dirFile = Environment.getExternalStorageDirectory();
		if (dirFile == null || !dirFile.canWrite()) {
			return null;
		}

		builder.append(dirFile.getAbsolutePath()).append(File.separator).append("Download").append(File.separator)
				.append(context.getPackageName());
		File file = new File(builder.toString());
		if (!file.exists()) {
			file.mkdirs();
		}
		if (!file.canWrite()) {
			return null;
		}
		return file.getAbsolutePath();
	}

	private static String getDownloadRootPathWithoutSdcard(Context context) {
		String rootPath = context.getCacheDir().getAbsolutePath() + File.separator + "apps";
//		String[] args1 = { "chmod", "705", rootPath };
//		CommonUtils.exec(args1);
		return rootPath;
	}

	public static synchronized String getDownloadRootPath(Context context) {
		if (downloadRootPath == null) {
			String rootPath = null;
			try {
				if (rootPath == null && Build.VERSION.SDK_INT >= 23) {
					rootPath = getDownloadRootPathForApi19(context);
					if (!canCreateFlie(rootPath)) {
						rootPath = null;
					}
				}

				if (rootPath == null) {
					if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
							&& hasEnoughSapceOnSDCard()) {
						rootPath = getDownloadRootPathWithSdcard(context);
						if (!canCreateFlie(rootPath)) {
							rootPath = null;
						}
					}
				}

				if (rootPath == null && Build.VERSION.SDK_INT >= 19) {
					rootPath = getDownloadRootPathForApi19(context);
					if (!canCreateFlie(rootPath)) {
						rootPath = null;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (rootPath == null) {
				rootPath = getDownloadRootPathWithoutSdcard(context);
			}
			downloadRootPath = rootPath;
		}
		return downloadRootPath;
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static String getDownloadRootPathForApi19(Context context) {
		String rootPath = null;
		File[] fs = context.getExternalFilesDirs(null);

		if (fs == null || fs.length == 0) {
			return null;
		}

		ArrayList<File> fileList = new ArrayList<File>(fs.length);
		for (File f : fs) {
			if (f != null) {
				if (!f.exists()) {
					f.mkdirs();
				}
				if (f.isDirectory() && f.canWrite() && f.canRead()) {
					fileList.add(f);
				}
			}
		}

		if (fileList.size() == 0) {
			return null;
		}

		if (fileList.size() == 1) {
			fileList.get(0).mkdirs();
			rootPath = fileList.get(0).getAbsolutePath();
		} else {
			long max = 0;
			for (int i = 0; i < fileList.size(); i++) {
				long[] l = getStorageSpacesLong(context, fileList.get(i).getAbsolutePath());
				if (l[1] > max) {
					max = l[1];
					fileList.get(i).mkdirs();
					rootPath = fileList.get(i).getAbsolutePath();
				}
			}
		}

		return rootPath;
	}

	private static boolean canCreateFlie(String path) {
		if (path == null) {
			return false;
		}

		File file = new File(path, "file.test");
		if (file == null) {
			return false;
		}
		if (file.exists()) {
			file.delete();
			return true;
		} else {
			try {
				boolean b = file.createNewFile();
				file.delete();
				return b;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	/**
	 * 获取存储卡的可用空间和大小
	 * 
	 * @return l[0] 可用空间, l[1] 总空间
	 */
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public static long[] getStorageSpacesLong(Context context, String path) {
		String rootPath = getUsbRootPath(context, path);
		File file = new File(rootPath);
		if (!file.exists()) {
			return null;
		}
		long[] l = new long[2];
		StatFs stat = new StatFs(rootPath);
		long blockSize = 0, blockCount = 0, availableBlocks = 0;
		if (Build.VERSION.SDK_INT < 18) {// android 4.3以下支持的API
			blockSize = stat.getBlockSize();
			blockCount = stat.getBlockCount();
			availableBlocks = stat.getAvailableBlocks();
		} else { // android 4.3API
			blockSize = stat.getBlockSizeLong();
			blockCount = stat.getBlockCountLong();
			availableBlocks = stat.getAvailableBlocksLong();
		}
		l[0] = blockSize * availableBlocks;
		l[1] = blockSize * blockCount;
		return l;
	}

	private static boolean hasEnoughSapceOnSDCard() {
		File file = Environment.getExternalStorageDirectory();
		if (!file.exists() || !file.canWrite() || !file.canRead()) {
			Log.i("DownloadUtils", "file is not exists");
			return false;
		}
		StatFs stat = new StatFs(file.getAbsolutePath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		// Log.i("DownloadUtils", "space : " + blockSize * availableBlocks);
		return blockSize * availableBlocks > MIN_SAPCE;
	}


	public static void renameFile(File tempFile, File newFile) {
		tempFile.renameTo(newFile);
	}

	public static File getDownloadFile(Context context, String key) {
		String rootPath = getDownloadRootPath(context);
		return getDownloadFile(context, key, rootPath);
	}

	public static File getDownloadFile(Context context, String key, String rootPath) {
		File rootDir = new File(rootPath);
		File[] files = rootDir.listFiles(new FileSearchComp(key));
		if (files == null || files.length <= 0) {
			return null;
		}
		return files[0];
	}

	private static final class FileSearchComp implements FilenameFilter {
		private String key;

		public FileSearchComp(String key) {
			this.key = key;
		}

		@Override
		public boolean accept(File dir, String filename) {
			if (null == key) {
				return false;
			}
			if (null == filename) {
				return false;
			}
			if (filename.startsWith(key)) {
				return true;
			}
			return false;
		}
	}

	public static String getUsbRootPath(Context context, String path) {
		String usbRootPath;
		String packageName;
		if (path.contains("Android/data")) {
			usbRootPath = path.substring(0, path.indexOf("Android/data"));
		} else if (path.contains(packageName = context.getPackageName())) {
			usbRootPath = path.substring(0, path.indexOf(packageName));
		} else {
			usbRootPath = path;
		}
		return usbRootPath;
	}

	public static void silentClose(Closeable c) {
		if (c != null) {
			try {
				c.close();
			} catch (IOException e) {
				Log.e("IO Close", ""+e.getMessage());
			}
		}
	}
}
