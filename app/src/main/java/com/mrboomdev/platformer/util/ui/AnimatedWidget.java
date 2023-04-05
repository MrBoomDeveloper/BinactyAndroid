package com.mrboomdev.platformer.util.ui;

import com.mrboomdev.platformer.util.ui.WidgetAnimatable;

public class AnimatedWidget<T extends WidgetAnimatable> {
	private WidgetAnimatable child;
	
	public AnimatedWidget(WidgetAnimatable child) {
		this.child = child;
	}
}