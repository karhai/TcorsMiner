package edu.usc.tcors;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.jinstagram.Instagram;
import org.jinstagram.auth.model.Token;
import org.jinstagram.exceptions.InstagramException;

public class TcorsInstagramStream {
	
	public static void main(String[] args) throws InstagramException {

	}
	
	private Properties getProps() {
		Properties prop = new Properties();
		try {
			InputStream in = TcorsTwitterStream.class.getClassLoader().getResourceAsStream("jInstagram.properties");
			prop.load(in);
		} catch (IOException e) {
			// log.error(e.toString());
		}
		return prop;
	}
	
	private Token getSecretToken() {
		Properties iProp = getProps();
		Token secretToken = new Token(iProp.getProperty("oauth.accessToken"),null);
		return secretToken;
	}
	
	private void getStuff() {
		Instagram instagram = new Instagram(getSecretToken());
		
		
	}
}
