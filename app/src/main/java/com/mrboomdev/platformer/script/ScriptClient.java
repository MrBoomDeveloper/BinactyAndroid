package com.mrboomdev.platformer.script;

import com.mrboomdev.binacty.api.client.ClientHolder;
import com.mrboomdev.platformer.util.io.LogUtil;

public class ScriptClient implements ClientHolder {

	@Override
	public void setReady() {
		LogUtil.debug(LogUtil.Tag.SCRIPT_API, "Set ready");
	}

	@Override
	public void setStatus(String status) {
		LogUtil.debug(LogUtil.Tag.SCRIPT_API, "Set status: " + status);
	}

	@Override
	public void print(Object object) {
		LogUtil.debug(LogUtil.Tag.SCRIPT_API, "Print: " + object);
	}
}