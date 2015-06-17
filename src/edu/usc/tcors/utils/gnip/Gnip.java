package edu.usc.tcors.utils.gnip;

import java.util.List;

public class Gnip {

	private List<MatchingRule> matching_rules;
	private int klout_score;
	private Language language;

	public List<MatchingRule> getMatching_rules() {
		return matching_rules;
	}

	public void setMatching_rules(List<MatchingRule> matching_rules) {
		this.matching_rules = matching_rules;
	}
	
	public int getKlout_score() {
		return klout_score;
	}
	public void setKlout_score(int klout_score) {
		this.klout_score = klout_score;
	}
	public Language getLanguage() {
		return language;
	}
	public void setLanguage(Language language) {
		this.language = language;
	}
}
