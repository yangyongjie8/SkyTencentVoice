package com.skyworthdigital.voice.dingdang.service;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.io.File;

public class FileCacheUtils {
	private static final String TEMP_FILE_SUFFIX = "_temp";
	
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static String getRootPath(Context context, String dirName) {
		String rootPath = DownloadUtils.getDownloadRootPath(context);
		if(isStringInvalid(rootPath)){
			return null;
		}
		StringBuilder builder = new StringBuilder();
		builder.append(rootPath).append(File.separator).append(dirName);
		return builder.toString();
	}

	public static String getCacheFilePath(Context context, String dirName,
                                          String cacheFileName) {
		return getFilePath(context, dirName, cacheFileName, false);
	}

	private static String getFilePath(Context context, String dirName,
                                      String cacheFileName, boolean temp) {
		String rootPath = getRootPath(context, dirName);
		if (isStringInvalid(rootPath)) {
			return null;
		}
		// String[] fileInfo = new String[2];
		// splitFileInfo(cacheFileName, fileInfo);
		String key4File = DownloadUtils.genKeyForUrl(cacheFileName);
		key4File += ".apk";
		if (temp) {
			key4File += TEMP_FILE_SUFFIX;
		}
		Log.i("FILE", "****cacheFile:" + rootPath + " key4File:" + key4File);
		File cacheFile = new File(rootPath, key4File);
		File parrentFile = cacheFile.getParentFile();
		if (!parrentFile.exists()) {
			parrentFile.mkdirs();
		}
		return cacheFile.getAbsolutePath();
	}


	public static boolean isFileCached(Context context, String dirName,
                                       String cacheFileName) {
		String cachePath = getCacheFilePath(context, dirName, cacheFileName);
		File cacheFile = new File(cachePath);
		if (cacheFile.exists()) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isStringInvalid(String value) {
		if(value == null || value.length() <= 0) {
			return true;
		}
		return false;
	}

}
