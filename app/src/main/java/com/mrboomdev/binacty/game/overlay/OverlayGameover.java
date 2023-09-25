package com.mrboomdev.binacty.game.overlay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.divider.MaterialDivider;
import com.mrboomdev.binacty.game.core.CoreLauncher;
import com.mrboomdev.binacty.ui.widgets.Button;
import com.mrboomdev.platformer.game.GameHolder;

public class OverlayGameover extends LinearLayout {
	private final GameHolder game = GameHolder.getInstance();

	public OverlayGameover(Context context) {
		super(context);

		var layoutParams = new LinearLayout.LayoutParams(500, ViewGroup.LayoutParams.MATCH_PARENT);
		layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
		setLayoutParams(layoutParams);
		setGravity(Gravity.CENTER_HORIZONTAL);

		setPadding(75, 25, 50, 25);

		setBackgroundColor(Color.parseColor("#11071F"));
		setOrientation(VERTICAL);
		createLayout();
	}

	public void startAnimation() {
		startAnimation(new Animation() {
			{
				this.setDuration(200);
				this.setInterpolator(new AccelerateDecelerateInterpolator());
			}

			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				setTranslationX(-500 * (1 - interpolatedTime));
			}
		});
	}

	@SuppressLint("SetTextI18n")
	private void createLayout() {
		var title = new TextView(getContext());
		title.setTextSize(40);
		title.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
		title.setPadding(0, 15, 15, 15);
		title.setTextColor(Color.WHITE);
		title.setText(game.stats.isWin ? "You Win!" : "Game Over");
		addView(title);

		var stats = new LinearLayout(getContext());
		var statsParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
		statsParams.weight = 1;
		stats.setLayoutParams(statsParams);
		stats.setGravity(Gravity.CENTER);
		stats.setOrientation(VERTICAL);
		addView(stats);

		var iterator = GameHolder.getInstance().stats.getAsArray().iterator();

		while(iterator.hasNext()) {
			var stat = iterator.next();

			var statLinear = new LinearLayout(getContext());
			var statTitleText = new TextView(getContext());
			var statValueText = new TextView(getContext());

			var statParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			statLinear.setLayoutParams(statParams);
			statLinear.setPadding(0, 20, 0, 20);

			var statTitleParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
			statTitleParams.weight = 1;
			statTitleText.setLayoutParams(statTitleParams);

			statTitleText.setText(stat.getTitle());
			statValueText.setText(stat.getValue());

			statTitleText.setTextColor(Color.WHITE);
			statValueText.setTextColor(Color.WHITE);

			statTitleText.setTextSize(16);
			statValueText.setTextSize(14);

			statLinear.addView(statTitleText);
			statLinear.addView(statValueText);
			stats.addView(statLinear);

			if(iterator.hasNext()) {
				var divider = new MaterialDivider(getContext());
				divider.setDividerColor(Color.parseColor("#22ffffff"));
				stats.addView(divider);
			}
		}

		var actions = new LinearLayout(getContext());
		var actionsParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		actionsParams.gravity = Gravity.CENTER;
		actions.setLayoutParams(actionsParams);
		actions.setOrientation(HORIZONTAL);
		addView(actions);

		var nextButton = createButton("Continue", () -> game.launcher.exit(CoreLauncher.ExitStatus.LOBBY));
		actions.addView(nextButton);
	}

	@NonNull
	private Button createButton(String text, Runnable callback) {
		var button = new Button(getContext());
		button.setText(text);
		button.setOnClickListener(view -> callback.run());

		var layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
		layoutParams.weight = 1;

		button.setLayoutParams(layoutParams);
		return button;
	}
}