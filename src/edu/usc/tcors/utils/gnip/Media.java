package edu.usc.tcors.utils.gnip;

public class Media {

	private String id;
	private String id_str;
	private int[] indices;
	private String media_url;
	private String media_url_https;
	private String url;
	private String display_url;
	private String expanded_url;
	private String type;
	private Size sizes;
	private String source_status_id;
	private String source_status_id_str;
	private VideoInfo video_info;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getId_str() {
		return id_str;
	}
	public void setId_str(String id_str) {
		this.id_str = id_str;
	}
	public int[] getIndices() {
		return indices;
	}
	public void setIndices(int[] indices) {
		this.indices = indices;
	}
	public String getMedia_url() {
		return media_url;
	}
	public void setMedia_url(String media_url) {
		this.media_url = media_url;
	}
	public String getMedia_url_https() {
		return media_url_https;
	}
	public void setMedia_url_https(String media_url_https) {
		this.media_url_https = media_url_https;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDisplay_url() {
		return display_url;
	}
	public void setDisplay_url(String display_url) {
		this.display_url = display_url;
	}
	public String getExpanded_url() {
		return expanded_url;
	}
	public void setExpanded_url(String expanded_url) {
		this.expanded_url = expanded_url;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Size getSizes() {
		return sizes;
	}
	public void setSizes(Size sizes) {
		this.sizes = sizes;
	}
	public String getSource_status_id() {
		return source_status_id;
	}
	public void setSource_status_id(String source_status_id) {
		this.source_status_id = source_status_id;
	}
	public String getSource_status_id_str() {
		return source_status_id_str;
	}
	public void setSource_status_id_str(String source_status_id_str) {
		this.source_status_id_str = source_status_id_str;
	}
	public VideoInfo getVideo_info() {
		return video_info;
	}
	public void setVideo_info(VideoInfo video_info) {
		this.video_info = video_info;
	}
}
