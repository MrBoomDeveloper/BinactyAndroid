package com.mrboomdev.platformer.online;

import com.mrboomdev.platformer.util.helper.BoomException;

import java.io.IOException;

import okhttp3.Response;

public class ResultData {
	protected boolean isOk;
	protected Exception exception;
	protected String text;
	
	public boolean getIsOk() {
		return this.isOk;
	}
	
	public String getText() {
		if(text == null) return "";
		return text;
	}
	
	public Exception getException() {
		return this.exception;
	}
	
	public interface Callback {
		void onResult(ResultData result);
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		private final ResultData data = new ResultData();
		
		public Builder setIsOK(boolean isOk) {
			data.isOk = isOk;
			return this;
		}
		
		public Builder setResponse(Response response) {
			try {
				data.text = response.body().string();
			} catch(IOException e) {
				throw new BoomException("Failed to parse text.", e);
			}
			return this;
		}
		
		public Builder setException(Exception e) {
			data.exception = e;
			return this;
		}
		
		public ResultData build() {
			return data;
		}
	}
}