package com.example.wallet;

import com.example.wallet.util.UiUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

/**
 * @author Masoud Taghavian (https://github.com/mtaghavian)
 */

public class DashboardActivity extends Activity {

	public Context context;
	public DashboardActivity activity;
	public LinearLayout mainLayout;
	private long backKeyPressTime = 0;
	private final static String activityName = "Dashboard";

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		this.context = this;
		this.activity = this;

		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		Configuration config = context.getResources().getConfiguration();
		displayMetrics.densityDpi = DisplayMetrics.DENSITY_340;
		config.densityDpi = displayMetrics.densityDpi;
		context.getResources().updateConfiguration(config, displayMetrics);

		int bgColor = Color.WHITE;
		String userPresentation = getIntent().getExtras().getString(
				"UserPresentation");

		mainLayout = new LinearLayout(this);
		mainLayout.setBackgroundColor(bgColor);
		mainLayout.setOrientation(LinearLayout.VERTICAL);
		mainLayout.addView(MainActivity.createHeaderLayout(this, activityName));

		mainLayout.addView(UiUtils.createSpace(context, 10, 60));
		mainLayout.addView(UiUtils.createTextView(context, "    Wellcome, "
				+ userPresentation + "!", bgColor, Color.rgb(0, 31, 82),
				MainActivity.textSize, true, new LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)));
		mainLayout.addView(UiUtils.createSpace(context, 10, 80));

		mainLayout.addView(generateRow(bgColor, R.drawable.balance,
				new Runnable() {

					@Override
					public void run() {

					}
				}, "Balance", R.drawable.transfer, new Runnable() {

					@Override
					public void run() {
						Intent intent = new Intent(
								getApplicationContext(),
								TransactionActivity.class);
						// intent.putExtra("PAGE_NUM", "1");
						startActivity(intent);
					}
				}, "Transaction"));
		mainLayout.addView(UiUtils.createSpace(context, 10, 140));
		mainLayout.addView(generateRow(bgColor, R.drawable.cards,
				new Runnable() {

					@Override
					public void run() {
						mainLayout.post(new Runnable() {

							@Override
							public void run() {
								Intent intent = new Intent(
										getApplicationContext(),
										CardsActivity.class);
								// intent.putExtra("PAGE_NUM", "1");
								startActivity(intent);
							}
						});
					}
				}, "Cards", R.drawable.history, new Runnable() {

					@Override
					public void run() {

					}
				}, "History"));
		mainLayout.addView(UiUtils.createSpace(context, 10, 140));
		mainLayout.addView(generateRow(bgColor, R.drawable.profile,
				new Runnable() {

					@Override
					public void run() {

					}
				}, "Profile", R.drawable.about, new Runnable() {

					@Override
					public void run() {

					}
				}, "About"));

		setContentView(mainLayout);
	}

	public View generateRow(int bgColor, int img1, final Runnable run1,
			String text1, int img2, final Runnable run2, String text2) {
		LinearLayout rowLayout = new LinearLayout(this);
		rowLayout.setBackgroundColor(bgColor);
		rowLayout.setOrientation(LinearLayout.HORIZONTAL);
		rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		rowLayout.setGravity(Gravity.CENTER_HORIZONTAL);

		LinearLayout groupLayout = new LinearLayout(this);
		groupLayout.setBackgroundColor(bgColor);
		groupLayout.setOrientation(LinearLayout.HORIZONTAL);
		groupLayout.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		groupLayout.setGravity(Gravity.CENTER_HORIZONTAL);

		LinearLayout entryLayout = new LinearLayout(this);
		entryLayout.setBackgroundColor(bgColor);
		entryLayout.setOrientation(LinearLayout.VERTICAL);
		entryLayout.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		entryLayout.setGravity(Gravity.CENTER_HORIZONTAL);
		entryLayout.addView(UiUtils.createImage(context, img1, run1,
				new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT), 0, 0, 0, 0));
		entryLayout.addView(UiUtils.createSpace(context, 10, 30));
		entryLayout.addView(UiUtils.createTextView(context, text1, bgColor,
				Color.BLACK, MainActivity.smallTextSize, true, new LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)));
		entryLayout.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramAnonymousView) {
				new Thread(run1).start();
			}
		});
		groupLayout.addView(entryLayout);
		groupLayout.addView(UiUtils.createSpace(context, 200, 10));

		entryLayout = new LinearLayout(this);
		entryLayout.setBackgroundColor(bgColor);
		entryLayout.setOrientation(LinearLayout.VERTICAL);
		entryLayout.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		entryLayout.setGravity(Gravity.CENTER_HORIZONTAL);
		entryLayout.addView(UiUtils.createImage(context, img2, run2,
				new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT), 0, 0, 0, 0));
		entryLayout.addView(UiUtils.createSpace(context, 10, 30));
		entryLayout.addView(UiUtils.createTextView(context, text2, bgColor,
				Color.BLACK, MainActivity.smallTextSize, true, new LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)));
		entryLayout.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramAnonymousView) {
				new Thread(run2).start();
			}
		});
		groupLayout.addView(entryLayout);
		rowLayout.addView(groupLayout);
		return rowLayout;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			long ct = System.currentTimeMillis();
			if (ct - backKeyPressTime > 3000) {
				backKeyPressTime = ct;
				UiUtils.makeToast(context, "Tap again to exit "
						+ MainActivity.appName, mainLayout);
			} else {
				finish();
			}
		}
		return false;
	}

	@Override
	protected void onStop() {
		super.onStop();
		setResult(0);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		setResult(0);
	}
}
