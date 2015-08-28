package edu.usc.tcors.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class TcorsInstagramUtils {

	public static void main(String[] args) throws Exception {
		String imageURL = "";
		String destinationFile = "";
		
		saveImage(imageURL, destinationFile);
	}
	
	// TODO get URL from DB
	
	// TODO update profile bios
	
	public static void saveImage(String imageURL, String destinationFile) throws IOException {
		URL url = new URL(imageURL);
		InputStream is = url.openStream();
		OutputStream os = new FileOutputStream(destinationFile);
		
		byte[] b = new byte[2048];
		int length;
		
		while((length = is.read(b)) != -1) {
			os.write(b,0,length);
		}
		
		is.close();
		os.close();
	}
}
