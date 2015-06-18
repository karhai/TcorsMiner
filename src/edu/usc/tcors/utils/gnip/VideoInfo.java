package edu.usc.tcors.utils.gnip;

import java.util.List;

public class VideoInfo {
	private int[] aspect_ratio;
	private int duration_millis;
	private List<Variant> variants;
	
	public int[] getAspect_ratio() {
		return aspect_ratio;
	}
	public void setAspect_ratio(int[] aspect_ratio) {
		this.aspect_ratio = aspect_ratio;
	}
	public int getDuration_millis() {
		return duration_millis;
	}
	public void setDuration_millis(int duration_millis) {
		this.duration_millis = duration_millis;
	}
	public List<Variant> getVariants() {
		return variants;
	}
	public void setVariants(List<Variant> variants) {
		this.variants = variants;
	}
}
