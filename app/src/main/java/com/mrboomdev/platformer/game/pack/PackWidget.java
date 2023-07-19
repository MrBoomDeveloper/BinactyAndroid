package com.mrboomdev.platformer.game.pack;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.PopupMenu;

import com.mrboomdev.platformer.R;
import com.mrboomdev.platformer.ui.android.AndroidDialog;
import com.mrboomdev.platformer.util.io.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PackWidget {
	
	public static class DialogPackWidget extends AndroidDialog.Field {
		private final PackData.Manifest data;
		private LinearLayout view;
		private TextView isActive;
		
		public DialogPackWidget(PackData.Manifest data) {
			this.data = data;
		}
		
		@Override
		public View getView(Activity activity) {
			if(view != null) return view; {
				view = new LinearLayout(activity);
				view.setClickable(true);
				view.setFocusable(true);
				view.setBackground(AppCompatResources.getDrawable(activity, R.drawable.light_ripple_background));

				var icon = new ImageView(activity);
				var iconParams = new LayoutParams(70, 70);
				iconParams.setMargins(0, 0, 20, 0);
				icon.setLayoutParams(iconParams);

				if(data.icon != null) {
					try {
						InputStream is = data.source.source == FileUtil.Source.INTERNAL
							? activity.getAssets().open(data.source.goTo(data.icon).getPath())
							: new FileInputStream(new File(activity.getExternalFilesDir(null), data.source.goTo(data.icon).getPath()));
						icon.setImageDrawable(Drawable.createFromStream(is, null));
						is.close();
					} catch(IOException e) {
						e.printStackTrace();
					}
				} else {
					icon.setImageDrawable(AppCompatResources.getDrawable(activity, R.drawable.pack_icon_missing));
				}

				view.addView(icon);
				var info = new LinearLayout(activity);
				var infoParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				infoParams.weight = 1;
				info.setPadding(0, 0, 25, 0);
				info.setLayoutParams(infoParams);
				info.setOrientation(LinearLayout.VERTICAL);
				var header = new LinearLayout(activity);

				{
					var title = new TextView(activity);
					title.setTextColor(Color.parseColor("#ffffff"));
					title.setTextSize(16);
					title.setText(data.name);
					
					var params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					params.setMargins(0, 0, 20, 0);
					title.setLayoutParams(params);
					
					header.addView(title);
				}

				if(data.author != null) {
					var author = new TextView(activity);
					author.setText(activity.getString(R.string.made_by, data.author));
					header.addView(author);
				}

				var params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				params.setMargins(0, 2, 0, 5);
				header.setLayoutParams(params);
				info.addView(header);

				if(data.description != null) {
					var description = new TextView(activity);
					description.setText(activity.getString(R.string.description, data.description));
					info.addView(description);
				}
				view.addView(info);
				view.setPadding(0, 0, 25, 0);
				view.setGravity(Gravity.CENTER_VERTICAL);

				{
					isActive = new TextView(activity);
					isActive.setGravity(Gravity.CENTER);
					isActive.setTextSize(15);
					var textParams = new LayoutParams(90, 36);
					isActive.setLayoutParams(textParams);
					GradientDrawable back = new GradientDrawable();
					back.setCornerRadius(10);
					isActive.setBackground(back);
					view.addView(isActive);
				}

				view.setOnClickListener(v -> {
					if(data.required && data.source.source == FileUtil.Source.INTERNAL) return;
					data.config.active = !data.config.active;
					updateState();
				});

				view.setOnLongClickListener(v -> {
					PopupMenu popup = new PopupMenu(activity, view);
					if(data.source.source != FileUtil.Source.INTERNAL) popup.getMenu().add(0, 0, 0, "Remove");
					popup.setOnMenuItemClickListener(item -> {
						switch(item.getItemId()) {
							case 0: {
								data.source.remove();
								PackLoader.getConfigs().remove(data.config);
								PackLoader.saveConfig();
								view.setClickable(false);
								view.setFocusable(false);
								view.setAlpha(.2f);
								view.setOnClickListener(null);
								view.setOnLongClickListener(null);
								return true;
							}
							default: return false;
						}
					});
					popup.setGravity(Gravity.END);
					if(popup.getMenu().size() > 0) popup.show();
					return false;
				});
				updateState();
			} return view;
		}
		
		private void updateState() {
			isActive.setText(data.config.active ? "Active" : "Disabled");
			var back = (GradientDrawable)isActive.getBackground();
			back.setColor(Color.parseColor(data.config.active ? "#da33ff" : "#25c8c8c8"));
			isActive.setTextColor(Color.parseColor(data.config.active ? "#000000" : "#ffffff"));
		}
	}
}