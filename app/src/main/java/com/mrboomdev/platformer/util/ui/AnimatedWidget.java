package com.mrboomdev.platformer.util.ui;

public class AnimatedWidget<T extends WidgetAnimatable> {
	private final WidgetAnimatable child;
	
	public AnimatedWidget(WidgetAnimatable child) {
		this.child = child;
	}
}