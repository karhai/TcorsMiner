package edu.usc.tcors.utils.gnip;

import java.sql.Date;

public class Activity {
	
	private int id;
	private String tweet_id;
	private boolean tweet_unavailable;
	private Date posted_at;
	private String payload;
	private String body;
	private String verb;
	private int repost_of;
	private String gnip_lang;
	private String twitter_lang;
	private String generator;
	private String link;
	
	private String hash_tags;
	private String mentions;
	private String urls;
	private String media;
	private String rule_values;
	private String rule_tags;
	
	private String place;
	private String country_code;
	private float longitude;
	private float lat;
	private float long_box;
	private float lat_box;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTweet_id() {
		return tweet_id;
	}
	public void setTweet_id(String tweet_id) {
		this.tweet_id = tweet_id;
	}
	public boolean isTweet_unavailable() {
		return tweet_unavailable;
	}
	public void setTweet_unavailable(boolean tweet_unavailable) {
		this.tweet_unavailable = tweet_unavailable;
	}
	public Date getPosted_at() {
		return posted_at;
	}
	public void setPosted_at(Date posted_at) {
		this.posted_at = posted_at;
	}
	public String getPayload() {
		return payload;
	}
	public void setPayload(String payload) {
		this.payload = payload;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getVerb() {
		return verb;
	}
	public void setVerb(String verb) {
		this.verb = verb;
	}
	public int getRepost_of() {
		return repost_of;
	}
	public void setRepost_of(int repost_of) {
		this.repost_of = repost_of;
	}
	public String getGnip_lang() {
		return gnip_lang;
	}
	public void setGnip_lang(String gnip_lang) {
		this.gnip_lang = gnip_lang;
	}
	public String getTwitter_lang() {
		return twitter_lang;
	}
	public void setTwitter_lang(String twitter_lang) {
		this.twitter_lang = twitter_lang;
	}
	public String getGenerator() {
		return generator;
	}
	public void setGenerator(String generator) {
		this.generator = generator;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getHash_tags() {
		return hash_tags;
	}
	public void setHash_tags(String hash_tags) {
		this.hash_tags = hash_tags;
	}
	public String getMentions() {
		return mentions;
	}
	public void setMentions(String mentions) {
		this.mentions = mentions;
	}
	public String getUrls() {
		return urls;
	}
	public void setUrls(String urls) {
		this.urls = urls;
	}
	public String getMedia() {
		return media;
	}
	public void setMedia(String media) {
		this.media = media;
	}
	public String getRule_values() {
		return rule_values;
	}
	public void setRule_values(String rule_values) {
		this.rule_values = rule_values;
	}
	public String getRule_tags() {
		return rule_tags;
	}
	public void setRule_tags(String rule_tags) {
		this.rule_tags = rule_tags;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public String getCountry_code() {
		return country_code;
	}
	public void setCountry_code(String country_code) {
		this.country_code = country_code;
	}
	public float getLongitude() {
		return longitude;
	}
	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}
	public float getLat() {
		return lat;
	}
	public void setLat(float lat) {
		this.lat = lat;
	}
	public float getLong_box() {
		return long_box;
	}
	public void setLong_box(float long_box) {
		this.long_box = long_box;
	}
	public float getLat_box() {
		return lat_box;
	}
	public void setLat_box(float lat_box) {
		this.lat_box = lat_box;
	}
}
