package com.example.wallet.util;

import com.example.wallet.MainActivity;
import com.example.wallet.util.component.Filler;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Masoud Taghavian (https://github.com/mtaghavian)
 */

public class UiUtils {

	public static void makeToast(final Context context, final String msg,
			LinearLayout mainLayout) {
		mainLayout.post(new Runnable() {

			@Override
			public void run() {
				Toast t = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
				((TextView) ((LinearLayout) t.getView()).getChildAt(0))
						.setGravity(Gravity.CENTER_HORIZONTAL);
				((TextView) ((LinearLayout) t.getView()).getChildAt(0)).setTextSize(MainActivity.textSize);
				t.show();
			}
		});
	}

	public static TextView createTextView(Context context, String text,
			int bgColor, int fgColor, float TextSize, boolean bold,
			LayoutParams params) {
		TextView view = new TextView(context);
		view.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
		view.setText(text);
		view.setTextSize(TextSize);
		if (bold) {
			view.setTypeface(null, Typeface.BOLD);
		}
		view.setBackgroundColor(bgColor);
		view.setTextColor(fgColor);
		view.setLayoutParams(params);
		return view;
	}

	public static TextView createSpace(Context context, int w, int h) {
		TextView view = new TextView(context);
		view.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
		view.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		view.setWidth(w);
		view.setHeight(h);
		return view;
	}

	public static ScrollView createScrollView(Context context, View view,
			LayoutParams params) {
		ScrollView scrollView = new ScrollView(context);
		scrollView.addView(view);
		scrollView.setLayoutParams(params);
		return scrollView;
	}

	public static ImageView createImage(Context context, int res,
			final Runnable run, LayoutParams params, int leftPad, int topPad,
			int rightPad, int bottomPad) {
		ImageView view = new ImageView(context);
		view.setImageResource(res);
		view.setLayoutParams(params);
		view.setPadding(leftPad, topPad, rightPad, bottomPad);
		view.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramAnonymousView) {
				if (run != null) {
					new Thread(run).start();
				}
			}
		});
		return view;
	}

	public static Button createButton(Context context, String text,
			final Runnable run, boolean bold, int bgColor, int fgColor,
			float textSize, LayoutParams params) {
		Button view = new Button(context);
		view.setText(text);
		view.setTextSize(textSize);
		if (bold) {
			view.setTypeface(null, Typeface.BOLD);
		}

		float r = 20;
		ShapeDrawable shape = new ShapeDrawable(new RoundRectShape(new float[] {
				r, r, r, r, r, r, r, r }, null, null));
		shape.getPaint().setColor(bgColor);
		view.setBackground(shape);
		view.setTextColor(fgColor);

		view.setLayoutParams(params);
		view.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramAnonymousView) {
				if (run != null) {
					new Thread(run).start();
				}
			}
		});
		return view;
	}

	public static EditText createEditText(Context context, String hint,
			String text, boolean singleLine, boolean bold, int bgColor,
			float textSize, LayoutParams params) {
		EditText view = new EditText(context);
		view.setHint(hint);
		view.setText(text);
		view.setSingleLine(singleLine);
		view.setTextSize(textSize);
		if (bold) {
			view.setTypeface(null, Typeface.BOLD);
		}

		float r = 20;
		ShapeDrawable shape = new ShapeDrawable(new RoundRectShape(new float[] {
				r, r, r, r, r, r, r, r }, null, null));
		shape.getPaint().setColor(bgColor);
		view.setBackground(shape);
		view.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
		view.setLayoutParams(params);
		return view;
	}

	public static View createFiller(Context context, int bgColor, int w, int h) {
		Filler view = new Filler(context, bgColor);
		view.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		view.setWidth(w);
		view.setHeight(h);
		return view;
	}

	public static void showDialog(final Context context,
			LinearLayout mainLayout, final String msg, final String pStr,
			final Runnable pAction, final String nStr, final Runnable nAction) {
		mainLayout.post(new Runnable() {

			@Override
			public void run() {
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case DialogInterface.BUTTON_POSITIVE: {
							if (pAction != null) {
								new Thread(pAction).start();
							}
							break;
						}
						case DialogInterface.BUTTON_NEGATIVE: {
							if (nAction != null) {
								new Thread(nAction).start();
							}
							break;
						}
						}
					}
				};
				AlertDialog.Builder builder = new AlertDialog.Builder(context,
						AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
				builder.setMessage(msg)
						.setPositiveButton(pStr, dialogClickListener)
						.setNegativeButton(nStr, dialogClickListener).show();
			}
		});
	}

}
