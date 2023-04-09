package com.mrboomdev.platformer.util.io;

import com.mrboomdev.platformer.ui.ActivityManager;
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
			throw new RuntimeException("Can't unzip to internal!");
		} else if(destination.source == FileUtil.Source.NETWORK) {
			throw new RuntimeException("Can't unzip to the internet!");
		}
		
		File dir = new File(destination.getFullPath(false));
        if(!dir.exists()) dir.mkdirs();
		try {
			unzipFile(new FileInputStream(target.getFullPath(false)), destination, callback);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void unzipFile(InputStream fis, FileUtil destination, Runnable callback) throws IOException {
        byte[] buffer = new byte[1024];
        try {
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while(ze != null) {
                String fileName = ze.getName();
                File newFile = new File(destination.getFullPath(false) + File.separator + fileName);
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while((len = zis.read(buffer)) > 0) {
                	fos.write(buffer, 0, len);
                }
                fos.close();
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
            fis.close();
			callback.run();
        } catch(IOException e) {
            e.printStackTrace();
        }
	}
}