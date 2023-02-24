package com.mrboomdev.platformer.util;

public class BadWordChecker {
	private String[] badWords;
	private BadSymbol[] badSymbols;
	
	/* TODO:
	private boolean containsBadWords(String text) {
		String formatted = text.toLowerCase().replaceAll("0", "o")
											 .replaceAll("1", "i");
		
		formatted = formatted.replaceAll(Pattern.quote("!"), "i")
							 .replaceAll(Pattern.quote("+"), "t")
							 .replaceAll(Pattern.quote("-"), "")
							 .replaceAll(Pattern.quote("_"), "")
							 .replaceAll(Pattern.quote("—"), "")
							 .replaceAll(Pattern.quote("·"), "")
							 .replaceAll(Pattern.quote("("), "c")
							 .replaceAll(Pattern.quote("~"), "")
							 .replaceAll(Pattern.quote("|"), "")
							 .replaceAll(Pattern.quote("•"), "")
							 .replaceAll(Pattern.quote("×"), "")
							 .replaceAll(Pattern.quote("="), "")
							 .replaceAll(Pattern.quote("*"), "")
							 .replaceAll(Pattern.quote("."), "");
		
		for(String badWord : badWords) {
			if(formatted.contains(badWord) || 
			 removeDoubleChars(formatted, 0).contains(badWord) ||
			 removeDoubleChars(formatted, 1).contains(badWord) ||
			   formatted.replaceAll(" ", "").contains(badWord)) {
				return true;
			}
		}
		return false;
	}
	
	private String removeDoubleChars(String request, int maxRepeats) {
		StringBuilder result = new StringBuilder();
		char previousChar = '$';
		int repeats = 0;
		for(char nextChar : request.toCharArray()) {
			if(nextChar != previousChar) {
				result.append(nextChar);
				repeats = 0;
			} else {
				if(repeats < maxRepeats) result.append(nextChar);
				repeats++;
			}
			previousChar = nextChar;
		}
		return result.toString();
	}
	*/
	
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