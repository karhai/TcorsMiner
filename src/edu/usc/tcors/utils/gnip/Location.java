package edu.usc.tcors.utils.gnip;

public class Location {

	private String objectType;
	private String displayName;
	private String name;
	private String country_code;
	private String twitter_country_code;
	private String link;
	private Geo geo;
	private String twitter_place_type;
	
	public String getObjectType() {
		return objectType;
	}
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCountry_code() {
		return country_code;
	}
	public void setCountry_code(String country_code) {
		this.country_code = country_code;
	}
	public String getTwitter_country_code() {
		return twitter_country_code;
	}
	public void setTwitter_country_code(String twitter_country_code) {
		this.twitter_country_code = twitter_country_code;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public Geo getGeo() {
		return geo;
	}
	public void setGeo(Geo geo) {
		this.geo = geo;
	}
	public String getTwitter_place_type() {
		return twitter_place_type;
	}
	public void setTwitter_place_type(String twitter_place_type) {
		this.twitter_place_type = twitter_place_type;
	}
	
	
}
