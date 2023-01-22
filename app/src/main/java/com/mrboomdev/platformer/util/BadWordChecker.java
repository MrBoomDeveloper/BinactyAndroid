package com.mrboomdev.platformer.util;

public class BadWordChecker {
	private String[] badWords;
	private BadSymbol[] badSymbols;
	
	public boolean check(String query) {
		if(query.contains(badWords[0])) {
			return true;
		}
		return false;
	}
	
	public void loadBadWords(String[] badWords) {
		this.badWords = badWords;
	}
	
	public void loadBadSymbols(BadSymbol[] badSymbols) {
		this.badSymbols = badSymbols;
	}
	
	public class BadSymbol {
		public String character;
		public String replaceWith;
		public boolean remove;
	}
	
	private void removeBadSymbols(String query) {
		
	}
}