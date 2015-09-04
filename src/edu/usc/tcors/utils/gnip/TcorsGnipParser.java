package edu.usc.tcors.utils.gnip;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.usc.tcors.utils.TcorsMinerUtils;

public class TcorsGnipParser {

	// final static String directory = "/Users/karhai/tmp/json/";
	final static String directory = "/Users/karhai/tmp/json2/json/";
	
	public static void main (String args[]) {
	
		/*
		 * process raw gnip files
		 */
//		final File folder = new File(directory);
//		listFilesForFolder(folder);
		
		/*
		 * process and store clean gnip files
		 */
		try {
			// readJSON("/Users/karhai/tmp/json/corrected/corrected_file1.json");
			// final File folder = new File("/Users/karhai/tmp/json/corrected");
			final File folder = new File(directory + "corrected");
			readMultiJSON(folder);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		System.out.println("Pau!");
		
	}
	
	public static void readMultiJSON(final File folder) throws JsonParseException, JsonMappingException, IOException, SQLException {

		List<GnipObj> gnipObjs = null;
		String file_fullpath;
		
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry);
			} else {
				if (fileEntry.getName().startsWith(".")) {
					// do nothing
				} else {
					file_fullpath = folder.getPath() + "/" + fileEntry.getName();
					gnipObjs = readJSON(file_fullpath);
					storeDB(gnipObjs);
					System.out.println("Finished:" + file_fullpath);
				}
			}
		}
	}
	
	public static void storeDB(List<GnipObj> gnipObjs) throws SQLException {
		Connection conn = null;
		TcorsMinerUtils tmu = new TcorsMinerUtils();
		
		try {
			conn = tmu.getDBConn("gnip_configuration.properties");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		String sql = "REPLACE INTO GnipObj (id,body,postedTime,userId,coord,summary,location) " +
				"VALUES (?,?,?,?,?,?,?)";
		PreparedStatement ps = null;
		
		for (GnipObj g : gnipObjs) {
		
			if (g.getId() != null) {
			
				String id = g.getId();
				String body = g.getBody();
				String postedTime = g.getPostedTime();
				String userId = g.getActor().getId();
				String userSummary = g.getActor().getSummary();
				String userLocation = "";
				ArrayList coord = null;
				
				try {
					coord = (ArrayList) g.getGeo().getCoordinates();
				} catch (NullPointerException n) {
					coord = new ArrayList<String>();
				}
				
				try {
					userLocation = g.getActor().getLocation().getDisplayName();
				} catch (NullPointerException n) {
					
				}
				
				try {
					ps = conn.prepareStatement(sql);
					
					ps.setString(1, id);
					ps.setString(2, body);
					ps.setString(3, postedTime);
					ps.setString(4, userId);
					ps.setString(5, coord.toString());
					ps.setString(6, userSummary);
					ps.setString(7, userLocation);
					
					ps.execute();
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					ps.close();
				}
			}
		}
	}
	
	public static void listFilesForFolder(final File folder) {
		for (final File fileEntry : folder.listFiles()) {
			System.out.println("Working on:" + fileEntry.getAbsolutePath());
			if (fileEntry.isDirectory()) { // TODO bug where /corrected files get reprocessed if the directory is alphabetically after processed files
				listFilesForFolder(fileEntry);
			} else {
				// System.out.println(fileEntry.getName());
				try {
					processFile(fileEntry.getName());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void processFile(String file_name) throws IOException {
		BufferedReader br = null;
		br = new BufferedReader(new FileReader(directory + file_name));
		File UIFile = new File(directory + "corrected/corrected_" + file_name);
		FileWriter fw = null;
		fw = new FileWriter(UIFile.getAbsoluteFile());
		BufferedWriter out = new BufferedWriter(fw);
		String line;
		out.write("[");
		while ((line = br.readLine()) != null) {
			if (line.isEmpty()) {
				// do nothing
			} else {
				if (line.startsWith("{\"info\"")) {
					out.write(line);
				} else {
					out.write(line + ",");
				}
			}
		}
		out.write("]");
		System.out.println("wrote:" + UIFile.getName());
		
		out.flush();
		out.close();
		br.close();
	}
	
	public static List<GnipObj> readJSON(String filename) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		List<GnipObj> gnipObjs = mapper.readValue(new File(filename), new TypeReference<List<GnipObj>>(){});
		// System.out.println(gnipObjs.size());
		return gnipObjs;
	}
	
}
