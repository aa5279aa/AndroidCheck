package com.check.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FileUtil {

	public static boolean copyToFile(File oldfile, File folder) {
		if (!folder.exists()) {
			folder.mkdirs();
		}

		try {
			int byteread = 0;
			if (oldfile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(oldfile); // 读入原文件
				FileOutputStream fs = new FileOutputStream(
						folder.getAbsolutePath() + File.separator
								+ oldfile.getName());
				byte[] buffer = new byte[128];
				// int length;
				while ((byteread = inStream.read(buffer)) != -1) {
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
				fs.close();
			}
			return true;
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			return false;

		}

	}

}
