package edu.usc.tcors;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.jinstagram.exceptions.InstagramException;
import org.jinstagram.realtime.InstagramSubscription;
import org.jinstagram.realtime.SubscriptionType;

public class TcorsInstagramStream {
	
	public static void main(String[] args) throws InstagramException {

	}

	private void getStreamSubscription() {
		Properties prop = getProps();
		InstagramSubscription igSub = new InstagramSubscription()
			.clientId(prop.getProperty("oauth.consumerKey"))
			.clientSecret(prop.getProperty("oauth.consumerSecret"))
			.object(SubscriptionType.TAGS)
			.objectId("ecig")
			.aspect("media")
			.callback("http://karhaich.usc.edu/handleSubscription")
			.verifyToken("ecigTagSubscription");
	}
	
	private Properties getProps() {
		Properties prop = new Properties();
		try {
			InputStream in = TcorsTwitterStream.class.getClassLoader().getResourceAsStream("jInstagram.properties");
			prop.load(in);
		} catch (IOException e) {
			// log.error(e.toString());
		}
//		ConfigurationBuilder igConf = new ConfigurationBuilder();
//		igConf.setIncludeEntitiesEnabled(true);
//		igConf.setDebugEnabled(Boolean.valueOf(prop.getProperty("debug")));
//		igConf.setOAuthConsumerKey(prop.getProperty("oauth.consumerKey"));
//		igConf.setOAuthConsumerSecret(prop.getProperty("oauth.consumerSecret"));
//		igConf.setJSONStoreEnabled(true);
		
		return prop;
	}
	
}
