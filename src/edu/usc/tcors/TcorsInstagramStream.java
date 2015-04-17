package edu.usc.tcors;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.jinstagram.exceptions.InstagramException;

import twitter4j.conf.ConfigurationBuilder;

public class TcorsInstagramStream {
	
	public static void main(String[] args) throws InstagramException {
		
	}

	private ConfigurationBuilder getConf() {
		Properties prop = new Properties();
		try {
			InputStream in = TcorsTwitterStream.class.getClassLoader().getResourceAsStream("jInstagram.properties");
			prop.load(in);
		} catch (IOException e) {
			// log.error(e.toString());
		}
		ConfigurationBuilder twitterConf = new ConfigurationBuilder();
		twitterConf.setIncludeEntitiesEnabled(true);
		twitterConf.setDebugEnabled(Boolean.valueOf(prop.getProperty("debug")));
		twitterConf.setOAuthAccessToken(prop.getProperty("oauth.accessToken"));
		twitterConf.setJSONStoreEnabled(true);
		
		return twitterConf;
	}
	
}
