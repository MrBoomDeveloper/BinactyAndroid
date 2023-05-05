package com.mrboomdev.platformer.util.io;

import com.mrboomdev.platformer.ui.ActivityManager;
import com.mrboomdev.platformer.util.helper.BoomException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.GZIPOutputStream;
import java.io.ByteArrayInputStream;
import java.util.zip.GZIPInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtil {
	private static final int BUFFER_SIZE = 4096;

	public static byte[] getCompressedString(String input) throws IOException {
		byte[] data = input.getBytes("UTF-8");
		ByteArrayOutputStream os = new ByteArrayOutputStream(data.length);
		GZIPOutputStream gos = new GZIPOutputStream(os);
		gos.write(data);
		gos.close();
		os.close();
		return os.toByteArray();
    }

    public static String getUncompressedString(byte[] input) throws IOException {
		final int BUFFER_SIZE = input.length;
		ByteArrayInputStream is = new ByteArrayInputStream(input);
		GZIPInputStream gis = new GZIPInputStream(is, BUFFER_SIZE);
		StringBuilder string = new StringBuilder();
		byte[] data = new byte[BUFFER_SIZE];
		int bytesRead;
		while ((bytesRead = gis.read(data)) != -1) {
			string.append(new String(data, 0, bytesRead));
		}
		gis.close();
		is.close();
		return string.toString();
	}
	
	public static void unzipFile(FileUtil target, FileUtil destination, Runnable callback) {
		if(destination.source == FileUtil.Source.INTERNAL) {
			throw BoomException.builder("Failed to unzip archive. Internal storage isn't editable! Path: ").addQuoted(destination.getPath()).build();
		} else if(destination.source == FileUtil.Source.NETWORK) {
			throw BoomException.builder("Failed to unzip archive. Internet isn't editable! Path: ").addQuoted(destination.getPath()).build();
		}
		
		File dir = new File(destination.getFullPath(false));
        if(!dir.exists()) dir.mkdirs();
		try {
			unzipFile(new FileInputStream(target.getFullPath(false)), destination, callback);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

    public static void unzipFile(InputStream stream, FileUtil destDirectory, Runnable callback) throws IOException {
        File destDir = destDirectory.getFile();
        if(!destDir.exists()) destDir.mkdir();
        ZipInputStream zipIn = new ZipInputStream(stream);
        ZipEntry entry = zipIn.getNextEntry();
        while(entry != null) {
            String filePath = destDirectory.getFullPath(false) + File.separator + entry.getName();
            if(!entry.isDirectory()) {
                File file = new File(filePath);
                file.getParentFile().mkdirs();
                FileOutputStream fos = new FileOutputStream(file);
                byte[] bytes = new byte[BUFFER_SIZE];
                int length;
                while ((length = zipIn.read(bytes)) >= 0) {
                    fos.write(bytes, 0, length);
                }
                fos.close();
            } else {
                File dir = new File(filePath);
                dir.mkdir();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
        callback.run();
    }
}