package br.com.cod3r.robot.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Arrays;

public class Files {

	private Files() {
	}
	
	public static String readFile(String path) {
		try {
			byte[] encoded = java.nio.file.Files.readAllBytes(Paths.get(path));
			return new String(encoded, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public static void writeFile(String file, String content) {
		PrintWriter out = null;
		OutputStream fileOut = null;
		try {
			fileOut = new FileOutputStream(file);
			out = new PrintWriter(new OutputStreamWriter(fileOut, StandardCharsets.UTF_8), true);
			out.println(content);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		} finally {
			if (out != null) {
				out.close();
			}
			if (fileOut != null) {
				try {
					fileOut.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static String[] getFolderFiles(String folder) {
		try {
			String[] files = new File(folder).list();
			Arrays.sort(files);
			return files;
		} catch (Exception e) {
			return new String[] {};
		}
	}
}
