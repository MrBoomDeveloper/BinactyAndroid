package com.mrboomdev.platformer.util.io;

import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPOutputStream;
import java.io.ByteArrayInputStream;
import java.util.zip.GZIPInputStream;
import java.io.IOException;

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
}