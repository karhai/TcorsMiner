package edu.usc.tcors.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import edu.usc.tcors.TcorsTwitterStream;

public class TcorsTwitterUtils {

	
	public static void main(String[] args) {
		// missing 4/17 data, from 589011127519948800L to 589100941686743042L
		new TcorsTwitterUtils().search("vaping", 589011127519948800L, 589100941686743042L);
	}
	
	private void search(String searchTerm, long sinceId, long maxId) {
		// Map<String,Integer> terms = new HashMap<String,Integer>();
		Twitter twitter = getInstance();
		Query query = new Query(searchTerm);
		
		query.setSinceId(sinceId);
		query.setMaxId(maxId);
		query.setCount(1000);
		
		QueryResult result = null;
		try {
			result = twitter.search(query);
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int i = 0;
		long newMaxId = 0L;
		for (Status status : result.getTweets()) {
			System.out.println(++i + " @" + status.getUser().getScreenName() + " " + status.getCreatedAt());
			newMaxId = status.getId();
		}
		
		if (i == 100 && result.getRateLimitStatus().getRemaining() > 10) {
			System.out.println("more needed");
			search(searchTerm, sinceId, newMaxId);
		}
	}
	
	private Twitter getInstance() {
		ConfigurationBuilder cb = getConf();
		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();
		
		return twitter;
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
