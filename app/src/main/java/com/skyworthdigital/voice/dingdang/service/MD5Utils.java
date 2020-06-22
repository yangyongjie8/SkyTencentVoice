package com.skyworthdigital.voice.dingdang.service;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

public class MD5Utils {
	private final static String DIGEST_ALGORITHM_MD5 = "MD5";

	private final static String[] hexDigits = { // 用来将字节转换成 16 进制表示的字符
			"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e",
	"f" };

	public static String getFileMD5(File file)  {
		if (file == null || !file.exists()) {
			return null;
		}
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			MessageDigest messageDigest = MessageDigest.getInstance(DIGEST_ALGORITHM_MD5);
			byte[] buffer = new byte[1024];
			int length = -1;
			while ((length = fis.read(buffer, 0, 1024)) != -1) {
				messageDigest.update(buffer, 0, length);
			}
			return byteArrayToHexString(messageDigest.digest());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			DownloadUtils.silentClose(fis);
		}
	}

	/**
	 * MD5加密算法的具体实现
	 * 
	 * @param src
	 *            需要加密的字符串
	 * @return 加密后的结果字符串,32位,大写
	 */
	public static String getStringMD5(String src) {
		if (FileCacheUtils.isStringInvalid(src)) {
			return null;
		}
		String result = null;
		try {
			result = new String(src);
			result = byteArrayToHexString(MessageDigest.getInstance(
					DIGEST_ALGORITHM_MD5).digest(result.getBytes()));
		} catch (Exception err) {
			err.printStackTrace();
		}
		return result.toUpperCase();
	}

	private static String byteArrayToHexString(byte[] digest) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < digest.length; i++)
			result.append(byteToHexString(digest[i]));
		return result.toString();
	}

	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n = 256 + n;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

}
