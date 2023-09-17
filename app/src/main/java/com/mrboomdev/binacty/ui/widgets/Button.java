package com.mrboomdev.binacty.ui.widgets;

import android.content.Context;
import android.graphics.Color;

import com.google.android.material.button.MaterialButton;

public class Button extends MaterialButton {

	public Button(Context context) {
		super(context);
		setAllCaps(false);
		setTextColor(Color.BLACK);
		setBackgroundColor(Color.parseColor("#fa52fa"));
	}
}