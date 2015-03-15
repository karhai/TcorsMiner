import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Properties;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TcorsTwitterStream {
	
	public static void main(String[] args) throws IOException {
		System.out.println("Hello TwitterParser!");
		new TcorsTwitterStream().searchStream();
	}
	
	public void searchStream() {
		TwitterStream twitterStream = getStreamInstance();
		StatusListener listener = new StatusListener() {
			int tweetCount = 0;
			
			public void onStatus(Status status) {
				
				storeTweet(status);
				
//				if (tweetCount < 10) {
//					System.out.println("\n\n" + tweetCount + ": @" + status.getUser().getScreenName() + " - " + status.getText() + "\n\n");
//					storeTweet(status);
//					tweetCount++;
//				} else {
//					System.exit(0);
//				}
				
			}

			@Override
			public void onException(Exception arg0) {
				// TODO Auto-generated method stub
				
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
				// TODO Auto-generated method stub
				
			}
		};
		
		FilterQuery filter = new FilterQuery();
		String keyword[] = {"e-cig", "ecig"};
		filter.track(keyword);
		
		twitterStream.addListener(listener);
		twitterStream.filter(filter);
	}
	
	private void storeTweet(Status status) {
		Connection conn = getDBConn();
		String sql = "INSERT INTO tweets (id, createdAt, text, username, isRetweet)" +
				"VALUES (?,?,?,?,?)";
		PreparedStatement ps = null;
		
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, Long.toString(status.getId()));
			ps.setTimestamp(2, new Timestamp(status.getCreatedAt().getTime()));
			ps.setString(3, status.getText());
			ps.setString(4, status.getUser().getName());
			ps.setBoolean(5, status.isRetweet());
			
			ps.execute();
			System.out.println("Stored tweet " + status.getId());
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private Properties getDBConf() {
		Properties prop = new Properties();
		try {
			InputStream in = TcorsTwitterStream.class.getClassLoader().getResourceAsStream("configuration.properties");
			prop.load(in);
		} catch (IOException e) {
			// log.error(e.toString());
		}
		return prop;
	}
	
	private Connection getDBConn() {
		Connection conn = null;
		Properties prop = getDBConf();
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = prop.getProperty("url");
			String user = prop.getProperty("user");
			String password = prop.getProperty("password");
			
			conn = DriverManager.getConnection(url,user,password);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conn;
	}
	
	private TwitterStream getStreamInstance() {
		ConfigurationBuilder twitterConf = getConf();
		
		TwitterStream twitterStream = new TwitterStreamFactory(twitterConf.build()).getInstance();
		return twitterStream;
	}
	
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