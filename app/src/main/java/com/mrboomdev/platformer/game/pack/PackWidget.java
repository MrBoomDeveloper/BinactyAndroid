package com.mrboomdev.platformer.game.pack;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.mrboomdev.platformer.ui.ActivityManager;
import com.mrboomdev.platformer.ui.android.AndroidDialog;
import com.mrboomdev.platformer.util.io.FileUtil;
import com.mrboomdev.platformer.R;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PackWidget {
	
	public static class DialogPackWidget extends AndroidDialog.Field {
		private PackData.Manifest data;
		private LinearLayout view;
		
		public DialogPackWidget(PackData.Manifest data) {
			this.data = data;
		}
		
		@Override
		public View getView(Activity activity) {
			if(view != null) return view; {
				view = new LinearLayout(activity);
				view.setClickable(true);
				view.setFocusable(true);
				view.setBackground(ActivityManager.current.getDrawable(R.drawable.light_ripple_background));
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
					icon.setImageDrawable(ActivityManager.current.getDrawable(R.drawable.pack_icon_missing));
				}
				view.addView(icon);
				var info = new LinearLayout(activity);
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
					author.setText("Made by:  " + data.author.name);
					header.addView(author);
				}
				var params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				params.setMargins(0, 2, 0, 5);
				header.setLayoutParams(params);
				info.addView(header);
				if(data.description != null) {
					var description = new TextView(activity);
					description.setText("Description: " + data.description);
					info.addView(description);
				}
				view.addView(info);
				view.setOnClickListener(v -> {
					
				});
				view.setOnLongClickListener(v -> {
					
					return false;
				});
			} return view;
		}
	}
}