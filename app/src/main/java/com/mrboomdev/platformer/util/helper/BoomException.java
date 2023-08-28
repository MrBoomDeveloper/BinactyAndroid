package com.mrboomdev.platformer.util.helper;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

public class BoomException extends RuntimeException {
	
	public BoomException(@NonNull Object object) {
		super(object.toString());
	}
	
	public BoomException(@NonNull Object object, Throwable t) {
		super(object.toString(), t);
	}
	
	public BoomException(Throwable t) {
		super(t);
	}
	
	@NonNull
	@Contract(value = "_ -> new", pure = true)
	public static Builder builder(String text) {
		return new Builder(text);
	}

	@NonNull
	@Contract(value = " -> new", pure = true)
	public static Builder builder() {
		return builder("");
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