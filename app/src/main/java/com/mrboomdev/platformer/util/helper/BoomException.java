package com.mrboomdev.platformer.util.helper;

public class BoomException extends RuntimeException {
	
	public BoomException(Object object) {
		super(object.toString());
	}
	
	public BoomException(Throwable t) {
		super(t);
	}
	
	public static Builder builder() {
		return new Builder("");
	}
	
	public static Builder builder(String text) {
		return new Builder(text);
	}
	
	public static class Builder {
		private String text;
		
		public Builder(String text) {
			this.text = text;
		}
		
		public Builder addQuoted(String text) {
			this.text += "'" + text +"'";
			return this;
		}
		
		public Builder append(String text) {
			this.text += text;
			return this;
		}
		
		public BoomException build() {
			return new BoomException(text);
		}
	}
}