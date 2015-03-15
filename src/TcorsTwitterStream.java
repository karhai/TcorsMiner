import java.io.IOException;
import java.io.InputStream;
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
		// new TwitterTest().search("#ecigs");
		new TcorsTwitterStream().searchStream();
	}
	
	public void searchStream() {
		TwitterStream twitterStream = getStreamInstance();
		StatusListener listener = new StatusListener() {
			int tweetCount = 0;
			
			public void onStatus(Status status) {
				if (tweetCount < 10) {
					System.out.println("\n\n" + tweetCount + ": @" + status.getUser().getScreenName() + " - " + status.getText() + "\n\n");
					tweetCount++;
				} else {
					System.exit(0);
				}
				
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