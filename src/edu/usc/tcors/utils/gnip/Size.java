package edu.usc.tcors.utils.gnip;

public class Size {

	private Small small;
	private Thumb thumb;
	private Medium medium;
	private Large large;
	
	private int w;
	private int h;
	private String resize;
	public Small getSmall() {
		return small;
	}
	public void setSmall(Small small) {
		this.small = small;
	}
	public Thumb getThumb() {
		return thumb;
	}
	public void setThumb(Thumb thumb) {
		this.thumb = thumb;
	}
	public Medium getMedium() {
		return medium;
	}
	public void setMedium(Medium medium) {
		this.medium = medium;
	}
	public Large getLarge() {
		return large;
	}
	public void setLarge(Large large) {
		this.large = large;
	}
	public int getW() {
		return w;
	}
	public void setW(int w) {
		this.w = w;
	}
	public int getH() {
		return h;
	}
	public void setH(int h) {
		this.h = h;
	}
	public String getResize() {
		return resize;
	}
	public void setResize(String resize) {
		this.resize = resize;
	}
	
	
}
