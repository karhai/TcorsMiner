package edu.usc.tcors.utils;

import java.io.File;

public class TcorsGnipParser {

	public static void main (String args[]) {
	
		final File folder = new File("/Users/karhai/tmp/json");
		listFilesForFolder(folder);
	}
	
	public static void listFilesForFolder(final File folder) {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry);
			} else {
				System.out.println(fileEntry.getName());
			}
		}
	}
	
}
