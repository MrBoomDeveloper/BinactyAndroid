package com.mrboomdev.platformer.util;

public interface Analytics {
    public void logDebug(String tag, String content);
    
    public void logInfo(String tag, String content);
}