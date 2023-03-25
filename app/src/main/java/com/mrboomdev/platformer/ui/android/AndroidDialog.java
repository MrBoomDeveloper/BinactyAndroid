package com.mrboomdev.platformer.ui.android;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mrboomdev.platformer.ui.ActivityManager;
import com.mrboomdev.platformer.R;

public class AndroidDialog {
	private Activity context;
	private AlertDialog.Builder builder;
	private AlertDialog dialog;
	private View holder;
	private LinearLayout fields, actions;
		
	public AndroidDialog() {
		this.context = ActivityManager.current;
		this.holder = context.getLayoutInflater().inflate(R.layout.dialog_layout, null);
		this.fields = holder.findViewById(R.id.fields);
		this.actions = holder.findViewById(R.id.actions);
		this.builder = new AlertDialog.Builder(context);
	}
	
	public AndroidDialog setCancelable(boolean isCancelable) {
		this.builder.setCancelable(isCancelable);
		return this;
	}
		
	public AndroidDialog setTitle(String title) {
		((TextView)holder.findViewById(R.id.title)).setText(title);
		return this;
	}
	
	public AndroidDialog addField(Field field) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		fields.addView(field.getView(context), params);
		return this;
	}
	
	public AndroidDialog addSpace(int size) {
		var space = new Space(context);
		space.setMinimumHeight(size);
		fields.addView(space);
		return this;
	}
	
	public AndroidDialog addAction(Action action) {
		if(actions.getChildCount() > 0) {
			var space = new Space(context);
			space.setMinimumWidth(15);
			actions.addView(space);
		}
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.weight = 1;
		actions.addView(action.getView(context), params);
		return this;
	}
		
	public void show() {
		context.runOnUiThread(() -> {
			dialog = builder.setView(holder).create();
			dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			dialog.show();
		});
	}
	
	public void close() {
		dialog.cancel();
	}
	
	public static class Field {
		public FieldType type;
		public FieldTextMode mode;
		public String text = "", hint = "";
		private String color;
		private View view;
		private int size;
		
		public Field(FieldType type) {
			this.type = type;
		}
		
		public Field setTextMode(FieldTextMode mode) {
			this.mode = mode;
			return this;
		}
		
		public Field setText(String text) {
			this.text = text;
			return this;
		}
		
		public Field setHint(String text) {
			this.hint = text;
			return this;
		}
		
		public Field setTextSize(int size) {
			this.size = size;
			return this;
		}
		
		public Field setTextColor(String color) {
			this.color = color;
			return this;
		}
		
		public String getText() {
			return ((TextView)view).getText().toString();
		}
		
		public View getView(Activity activity) {
			if(view != null) return view;
			switch(type) {
				case EDIT_TEXT:
					TextInputLayout inputLayout = new TextInputLayout(activity);
					TextInputEditText editText = new TextInputEditText(activity);
					editText.setText(text);
					editText.setBackground(null);
					editText.setPadding(15, 15, 15, 15);
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
					inputLayout.addView(editText, params);
					activity.runOnUiThread(() -> editText.setHint(hint));
					this.view = editText;
					return inputLayout;
				case TEXT:
					TextView textView = new TextView(activity);
					textView.setText(text);
					if(size != 0) textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
					if(color != null) textView.setTextColor(Color.parseColor(color));
					this.view = textView;
					break;
			}
			return view;
		}
	}
	
	public static class Action {
		public String text;
		public ClickListener clickListener;
		private MaterialButton view;
		
		public Action setText(String text) {
			this.text = text;
			return this;
		}
		
		public String getText() {
			return view.getText().toString();
		}
		
		public Action setClickListener(ClickListener listener) {
			this.clickListener = listener;
			return this;
		}
		
		public View getView(Activity activity) {
			if(view == null) {
				view = new MaterialButton(activity);
				view.setText(text);
				view.setTextColor(Color.BLACK);
				view.setAllCaps(false);
				if(clickListener == null) return view;
				view.setOnClickListener((button) -> {
					clickListener.clicked(this);
				});
			}
			return view;
		}
		
		public interface ClickListener {
			public void clicked(Action action);
		}
	}
	
	public enum FieldType {
		EDIT_TEXT,
		CHECKBOX,
		TEXT
	}
		
	public enum FieldTextMode {
		TEXT,
		NUMBER
	}
}