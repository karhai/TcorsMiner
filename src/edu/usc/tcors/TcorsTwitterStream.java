package edu.usc.tcors;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;

import edu.usc.tcors.utils.TcorsMinerUtils;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import twitter4j.FilterQuery;
import twitter4j.Place;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

/*
 * command line execution: java
 * 							-XX:+HeapDumpOnOutOfMemoryError 
 * 							-Xms512m -Xmx1024m 
 * 							-cp ./lib/*:./bin TcorsTwitterStream; 
 * 							echo "process completed" | 
 * 							mail -s "process completed" mail@email.com
 * 
 * jar file execution: java -cp TcorsMiner.jar edu.usc.tcors.TcorsTwitterStream
 */

public class TcorsTwitterStream {
	
	String fileName = "keywords.txt";
	
	public static void main(String[] args) throws IOException {
		System.out.println("Hello TcorsTwitterStream!");
		
		TcorsTwitterStream tts = new TcorsTwitterStream();
		Connection conn = null;
		try {
			conn = tts.getDBConn();
			tts.searchStream(conn);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void searchStream(final Connection conn) throws IOException {
		TwitterStream twitterStream = getStreamInstance();
		StatusListener listener = new StatusListener() {
			
			public void onStatus(Status status) {
				storeTwitterData(conn, status);
			}

			@Override
			public void onException(Exception arg0) {
				// dump to file
				BufferedWriter bw = null;
				try {
					bw = new BufferedWriter(new FileWriter("exception.txt",true));
					PrintWriter pw = new PrintWriter(bw, true);
					arg0.printStackTrace(pw);
				} catch (Exception ie) {
					ie.printStackTrace();
				}
			}

			@Override
			public void onDeletionNotice(StatusDeletionNotice arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onScrubGeo(long arg0, long arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStallWarning(StallWarning arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTrackLimitationNotice(int arg0) {
				// dump to file
				BufferedWriter bw = null;
				try {
					bw = new BufferedWriter(new FileWriter("limits.txt",true));
					bw.write("Limit:" + arg0);
					bw.newLine();
				} catch (IOException io) {
					io.printStackTrace();
				}
			}
		};
		
                // String keywords[] = loadSearchTerms();
		String keywords[] = loadSearchTerms(conn);
		
		FilterQuery filter = new FilterQuery();
		
		filter.track(keywords);
		
		twitterStream.addListener(listener);
		twitterStream.filter(filter);
	}

        /*	
	private String[] loadSearchTerms() throws IOException {
		ArrayList<String> ret = new ArrayList<String>();
		
		InputStream is = ClassLoader.getSystemResourceAsStream(fileName);
		BufferedReader file = new BufferedReader(new InputStreamReader(is));
		String line = null;
		while ((line = file.readLine()) != null) {
			if (!line.startsWith("#")) {
				ret.add(line);
			}
		}
		file.close();
		
		return ret.toArray(new String[ret.size()]);
	}
        */
        
	// Read the keywords from the 'twitter_keywords' table in the database BEGIN
	// https://docs.oracle.com/javase/tutorial/jdbc/basics/processingsqlstatements.html
	public String[] loadSearchTerms(Connection conn) {
	    Statement stmt = null;
            ResultSet rs = null;
	    List<String> results = new ArrayList<String>();
	    try {
	        String baseQuery = "SELECT keyword FROM twitter_keywords where enabled=1";
	        stmt = conn.createStatement();
                rs = stmt.executeQuery(baseQuery);
	        while(rs.next()) {
	            results.add(rs.getString("keyword"));
	        }
	    } catch (SQLException e) {
	        //handle your exception
	        e.printStackTrace(System.out);
	    } finally {
	        closeResource(rs);
	        closeResource(conn);
	    }
	    return results.toArray(new String[results.size()]);
	}

	//both Connection and ResultSet interfaces extends from AutoCloseable interface
	public void closeResource(AutoCloseable ac) {
	    try {
	        if (ac != null) {
	            ac.close();
	        }
	    } catch (Exception e) {
	        //handle this exception as well...
	    }
	}	
	
	// Read the keywords from the 'twitter_keywords' table in the database END
        
        
	private void storeTwitterData(Connection conn, Status status) {
		try {
			storeTweetData(conn, status);
			storeUserData(conn, status);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * TODO: refactor
	 */
	
	private void storeUserData(Connection conn, Status status) throws SQLException {
		String sql = "REPLACE INTO twitter_profiles(userId, description, friendsCount, followersCount, screenName, statusesCount, location, name)" +
				"VALUES (?,?,?,?,?,?,?,?)";
		PreparedStatement ps = null; 
		try {
			ps = conn.prepareStatement(sql);
			
			User u = status.getUser();
			ps.setString(1, String.valueOf(u.getId()));
			ps.setString(2, u.getDescription());
			ps.setInt(3, u.getFriendsCount());
			ps.setInt(4, u.getFollowersCount());
			ps.setString(5, u.getScreenName());
			ps.setInt(6, u.getStatusesCount());
			ps.setString(7, u.getLocation());
			ps.setString(8, u.getName());
			
			ps.execute();
			System.out.println("Stored profile " + u.getScreenName());
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			ps.close();
		}
	}
	
	/*
	 * TODO: refactor
	 */
	
	private void storeTweetData(Connection conn, Status status) throws SQLException {
		String sql = "INSERT INTO tweets (id, createdAt, text, userId, isRetweet, latitude, longitude, place_country, place_name, place_type)" +
				"VALUES (?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement ps = null;
		
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, Long.toString(status.getId()));
			ps.setTimestamp(2, new Timestamp(status.getCreatedAt().getTime()));
			ps.setString(3, status.getText());
			ps.setString(4, String.valueOf(status.getUser().getId()));
			ps.setBoolean(5, status.isRetweet());
			if (status.getGeoLocation() != null) {
				ps.setDouble(6, status.getGeoLocation().getLatitude());
				ps.setDouble(7, status.getGeoLocation().getLongitude());
			} else {
				ps.setDouble(6, 0);
				ps.setDouble(7, 0);
			}
			if (status.getPlace() != null) {
				Place p = status.getPlace();
				
				String c = p.getCountry();
				String full_name = p.getFullName();
				String type = p.getPlaceType();
				
				ps.setString(8, c);
				ps.setString(9, full_name);
				ps.setString(10, type);
			} else {
				ps.setString(8, "");
				ps.setString(9, "");
				ps.setString(10, "");
			}
			
			ps.execute();
			System.out.println("Stored tweet " + status.getId());
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			ps.close();
		}
	}
	
	/*
	 * TODO: refactor
	 */
	
	private Connection getDBConn() throws SQLException {
		System.out.println("Creating a DB connection...");
		Connection conn = null;
		Properties prop = TcorsMinerUtils.getProps("configuration.properties");

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String url = prop.getProperty("url");
		String user = prop.getProperty("user");
		String password = prop.getProperty("password");
			
		conn = DriverManager.getConnection(url,user,password);
		return conn;
	}
	
	/*
	 * TODO: refactor
	 */
	
	private TwitterStream getStreamInstance() {
		ConfigurationBuilder twitterConf = getConf();
		
		TwitterStream twitterStream = new TwitterStreamFactory(twitterConf.build()).getInstance();
		return twitterStream;
	}
	
	/*
	 * TODO: refactor
	 */
	
	private ConfigurationBuilder getConf() {
		Properties prop = new Properties();
		try {
			InputStream in = TcorsTwitterStream.class.getClassLoader().getResourceAsStream("twitter4j.properties");
			prop.load(in);
		} catch (IOException e) {
			// log.error(e.toString());
		}
		ConfigurationBuilder twitterConf = new ConfigurationBuilder();
		twitterConf.setIncludeEntitiesEnabled(true);
		twitterConf.setDebugEnabled(Boolean.valueOf(prop.getProperty("debug")));
		twitterConf.setOAuthAccessToken(prop.getProperty("oauth.accessToken"));
		twitterConf.setOAuthAccessTokenSecret(prop.getProperty("oauth.accessTokenSecret"));
		twitterConf.setOAuthConsumerKey(prop.getProperty("oauth.consumerKey"));
		twitterConf.setOAuthConsumerSecret(prop.getProperty("oauth.consumerSecret"));
		twitterConf.setJSONStoreEnabled(true);
		
		return twitterConf;
	}
}