package com.mrboomdev.platformer.ui.gameplay.widgets;

import com.mrboomdev.platformer.util.ActorUtil;
import java.util.HashMap;

public class TabsWidget extends ActorUtil {
	private HashMap<String, Tab> tabs = new HashMap<>();
	private Style style;
	
	public TabsWidget(Style style) {
		this.style = style;
	}
	
	public TabsWidget add(String title, ActorUtil.onClickListener clickListener) {
		return this;
	}
	
	public class Tab {
		public ActorUtil.onClickListener clickListener;
	}
	
	public enum Style {
		BULLET,
		UNDERLINE
	}
}