package com.example.wallet;

import com.example.wallet.util.UiUtils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

/**
 * @author Masoud Taghavian (https://github.com/mtaghavian)
 */

public class FingerPrintDialog extends Dialog {

	public FingerPrintDialog(Context context) {
		super(context);

		int bgColor = Color.WHITE, padding = 80;

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		// dialog.setCancelable(false);

		LinearLayout layout = new LinearLayout(context);
		layout.setBackgroundColor(bgColor);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

		layout.addView(UiUtils.createSpace(context, 100, padding));
		layout.addView(UiUtils.createTextView(context,
				"Sign In with your fingerprint", bgColor, Color.BLACK,
				MainActivity.textSize, true, new LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)));
		layout.addView(UiUtils.createSpace(context, 100, padding));

		layout.addView(UiUtils.createImage(context, R.drawable.fingerprint,
				null, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT), 10, 10, 10, 10));
		layout.addView(UiUtils.createSpace(context, 100, padding));

		layout.addView(UiUtils.createTextView(context,
				"    Please place your finger on the sensor    ", bgColor,
				Color.BLACK, MainActivity.textSize, true,
				new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT)));
		layout.addView(UiUtils.createSpace(context, 100, padding));

		layout.addView(UiUtils.createButton(context, "CANCEL", new Runnable() {

			@Override
			public void run() {
				dismiss();
			}
		}, true, bgColor, MainActivity.ButtonColor, MainActivity.textSize,
				new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT)));
		layout.addView(UiUtils.createSpace(context, 100, padding));

		setContentView(layout);
	}
}
