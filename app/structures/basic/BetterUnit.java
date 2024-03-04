package structures.basic;

import java.util.Set;

public class BetterUnit extends Unit {
	Set<String> keywords;

	public BetterUnit() {
	}

	public BetterUnit(Set<String> keywords) {
		super();
		this.keywords = keywords;
	}

	public Set<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(Set<String> keywords) {
		this.keywords = keywords;
	};
}
