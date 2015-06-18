package edu.usc.tcors.utils.gnip;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TcorsGnipParser {

	public static void main (String args[]) {
	
//		 final File folder = new File("/Users/karhai/tmp/json");
//		 listFilesForFolder(folder);
		
		try {
			readJSON();
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void listFilesForFolder(final File folder) {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry);
			} else {
				// System.out.println(fileEntry.getName());
				try {
					processFile(fileEntry.getName());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void processFile(String file_name) throws IOException {
		BufferedReader br = null;
		br = new BufferedReader(new FileReader("/Users/karhai/tmp/json/" + file_name));
		File UIFile = new File("/Users/karhai/tmp/json/corrected/corrected_" + file_name);
		FileWriter fw = null;
		fw = new FileWriter(UIFile.getAbsoluteFile());
		BufferedWriter out = new BufferedWriter(fw);
		String line;
		out.write("[");
		int previous = 1;
		while ((line = br.readLine()) != null) {
			if (line.isEmpty()) {
				if (previous == 1) {
					// do nothing
				} else {
					out.write(",");
					previous = 1;
				}
			} else {
				out.write(line);
				previous = 0;
			}
		}
		out.write("]");
		System.out.println("wrote:" + UIFile.getName());
		
		out.flush();
		out.close();
		br.close();
	}
	
	public static void readJSON() throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		List<GnipObj> gnipObjs = mapper.readValue(new File("tmp/test2-corrected.json"), new TypeReference<List<GnipObj>>(){});
		System.out.println(gnipObjs.size());
	}
	
}
