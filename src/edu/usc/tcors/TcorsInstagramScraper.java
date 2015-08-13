package edu.usc.tcors;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.jinstagram.Instagram;
import org.jinstagram.auth.model.Token;
import org.jinstagram.entity.tags.TagMediaFeed;
import org.jinstagram.entity.users.feed.MediaFeed;
import org.jinstagram.entity.users.feed.MediaFeedData;
import org.jinstagram.exceptions.InstagramException;

public class TcorsInstagramScraper {
	
	public static void main(String[] args) throws InstagramException {

		TcorsInstagramScraper tis = new TcorsInstagramScraper();
		tis.getStuff();
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
		Token secretToken = getSecretToken();
		Instagram instagram = new Instagram(secretToken);
		
		// get search terms
		String[] test = new String[]{"ecigarette"};
		
		// for each search term
		for (String term : test) {
		
			// get data
			
			TagMediaFeed mediaFeed = null;
			List<MediaFeedData> mediaList = null;
			
			try {
				mediaFeed = instagram.getRecentMediaTags(term);
				
				mediaList = mediaFeed.getData();
				
				MediaFeed recentMediaNextPage = instagram.getRecentMediaNextPage(mediaFeed.getPagination());
				int counter = 1;
				while (recentMediaNextPage.getPagination() != null && counter < 100) {
					mediaList.addAll(recentMediaNextPage.getData());
					recentMediaNextPage = instagram.getRecentMediaNextPage(recentMediaNextPage.getPagination());
					counter++;
					System.out.println("Counter (pages):" + counter);
				}
				System.out.println("Size (x20):" + mediaList.size());
			} catch (InstagramException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("mediaFeed APILS:" + mediaFeed.getAPILimitStatus());
			System.out.println("mediaFeed REMAIN:" + mediaFeed.getRemainingLimitStatus());
			// store data
		
		}
		// pause
	}
}
