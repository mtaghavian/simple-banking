package com.example.wallet.util.component;

import java.io.IOException;
import java.net.URISyntaxException;

import org.json.JSONException;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wallet.MainActivity;
import com.example.wallet.R;
import com.example.wallet.sdk.NotOkResponseException;
import com.example.wallet.sdk.model.Transaction;
import com.example.wallet.sdk.model.enums.TransactionStatus;
import com.example.wallet.util.DateUtils;
import com.example.wallet.util.DebugUtils;
import com.example.wallet.util.UiUtils;

/**
 * @author Masoud Taghavian (https://github.com/mtaghavian)
 */

public class TransactionBox extends LinearLayout {

	private Transaction trans;

	public static enum Action {
		CANCEL, PERFORM, DETAIL
	}

	private Action currentAction;

	public TransactionBox(final Context context, final LinearLayout mainLayout,
			final Transaction trans, int bgColor, int textSize,
			int smallTextSize) {
		super(context);
		this.trans = trans;
		setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		setGravity(Gravity.CENTER_VERTICAL);
		setBackgroundColor(bgColor);
		setOrientation(LinearLayout.HORIZONTAL);

		addView(UiUtils.createSpace(context, 40, 10));

		LinearLayout desciptionLayout = new LinearLayout(context);
		desciptionLayout.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		desciptionLayout.setBackgroundColor(bgColor);
		desciptionLayout.setOrientation(LinearLayout.VERTICAL);
		desciptionLayout.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1.0f));

		String destination = (trans.getTargetName() != null) ? trans
				.getTargetName() : trans.getTargetCard();
		desciptionLayout.addView(UiUtils.createTextView(context, "To: "
				+ destination, bgColor, Color.BLACK, smallTextSize, false,
				new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT)));
		desciptionLayout.addView(UiUtils.createTextView(context, "Amount: "
				+ trans.getAmount(), bgColor, Color.BLACK, smallTextSize,
				false, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT)));
		String dateTime = DateUtils.formatTime(trans.getDate());
		String dateTimeSplit[] = dateTime.split(" ");
		desciptionLayout.addView(UiUtils.createTextView(context, "Date: "
				+ dateTimeSplit[0], bgColor, Color.BLACK, smallTextSize, false,
				new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT)));
		desciptionLayout.addView(UiUtils.createTextView(context, "Time: "
				+ dateTimeSplit[1] + " " + dateTimeSplit[2], bgColor,
				Color.BLACK, smallTextSize, false,
				new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT)));
		addView(desciptionLayout);

		TextView statusView = UiUtils.createTextView(context,
				"" + trans.getStatus(), bgColor, Color.BLACK, textSize, true,
				new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT, 1.0f));
		statusView.setGravity(Gravity.CENTER_VERTICAL
				| Gravity.CENTER_HORIZONTAL);
		addView(statusView);

		if (TransactionStatus.Verifying.equals(trans.getStatus())
				|| TransactionStatus.Performing.equals(trans.getStatus())) {
			currentAction = Action.CANCEL;
		} else if (TransactionStatus.Verified.equals(trans.getStatus())) {
			currentAction = Action.PERFORM;
		} else {
			currentAction = Action.DETAIL;
		}

		Button actionBut = UiUtils.createButton(
				context,
				"" + currentAction,
				new Runnable() {

					@Override
					public void run() {
						try {
							if (Action.CANCEL.equals(currentAction)) {
								MainActivity.mobileSdk.cancelTransaction(trans);
								UiUtils.makeToast(context,
										"Cancel command sent!", mainLayout);
							} else if (Action.DETAIL.equals(currentAction)) {
								mainLayout.post(new Runnable() {

									@Override
									public void run() {
										try {
											detail(context);
										} catch (Exception e) {
											UiUtils.makeToast(
													context,
													DebugUtils
															.serializeException(e),
													mainLayout);
										}
									}
								});
							} else if (Action.PERFORM.equals(currentAction)) {
								mainLayout.post(new Runnable() {

									@Override
									public void run() {
										try {
											perform(context, mainLayout);
										} catch (Exception e) {
											UiUtils.makeToast(
													context,
													DebugUtils
															.serializeException(e),
													mainLayout);
										}
									}
								});
							}
						} catch (IOException e) {
							UiUtils.makeToast(context, "IOException",
									mainLayout);
						} catch (URISyntaxException e) {
							UiUtils.makeToast(context, "URISyntaxException",
									mainLayout);
						} catch (JSONException e) {
							UiUtils.makeToast(context, "JSONException",
									mainLayout);
						} catch (NotOkResponseException e) {
							UiUtils.makeToast(context, e.getMessage(),
									mainLayout);
						} catch (Exception e) {
							UiUtils.makeToast(context,
									DebugUtils.serializeException(e),
									mainLayout);
						}
					}
				}, true, bgColor, MainActivity.ButtonColor, textSize,
				new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT, 1.0f));
		addView(actionBut);
	}

	public Transaction getTrans() {
		return trans;
	}

	public void detail(Context context) {
		int bgColor = Color.WHITE, padding = 80;

		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);
		// dialog.setCancelable(false);

		LinearLayout layout = new LinearLayout(context);
		layout.setBackgroundColor(bgColor);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
		layout.addView(UiUtils.createSpace(context, 100, padding));
		layout.setPadding(50, 0, 50, 0);

		if (TransactionStatus.Succeeded.equals(trans.getStatus())) {
			layout.addView(UiUtils.createImage(context, R.drawable.yes, null,
					new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT), 10, 10, 10, 10));
			layout.addView(UiUtils.createSpace(context, 100, padding));

			layout.addView(UiUtils.createTextView(context,
					"" + trans.getAmount() + " €", bgColor, Color.BLACK,
					MainActivity.textSize, true, new LinearLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT)));
			layout.addView(UiUtils.createSpace(context, 100, padding));

			layout.addView(UiUtils.createTextView(context,
					"Transferred successfully to", bgColor, Color.BLACK,
					MainActivity.smallTextSize, true,
					new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT)));
			layout.addView(UiUtils.createSpace(context, 100, padding));

			layout.addView(UiUtils.createTextView(context, trans
					.getTargetName(), bgColor, Color.BLACK,
					MainActivity.textSize, true, new LinearLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT)));
			layout.addView(UiUtils.createSpace(context, 100, padding));

			layout.addView(UiUtils.createTextView(context,
					"At " + DateUtils.formatTime(trans.getDate()), bgColor,
					Color.BLACK, MainActivity.smallTextSize, true,
					new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT)));
			layout.addView(UiUtils.createSpace(context, 100, padding));
		} else {
			layout.addView(UiUtils.createImage(context, R.drawable.no, null,
					new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT), 10, 10, 10, 10));
			layout.addView(UiUtils.createSpace(context, 100, padding));

			layout.addView(UiUtils.createTextView(context,
					"Transferring failed due to", bgColor, Color.BLACK,
					MainActivity.smallTextSize, true,
					new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT)));
			layout.addView(UiUtils.createSpace(context, 100, padding));

			layout.addView(UiUtils.createTextView(context, trans.getMsg(),
					bgColor, Color.BLACK, MainActivity.textSize, true,
					new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT)));
			layout.addView(UiUtils.createSpace(context, 100, padding));
		}

		layout.addView(UiUtils.createButton(context, "CLOSE", new Runnable() {

			@Override
			public void run() {
				dialog.dismiss();
			}
		}, true, bgColor, MainActivity.ButtonColor, MainActivity.textSize,
				new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT)));
		layout.addView(UiUtils.createSpace(context, 100, padding));

		dialog.setContentView(layout);
		dialog.show();
	}

	private void perform(final Context context, final LinearLayout mainLayout) {
		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);

		int bgColor = Color.WHITE, padding = 80, width = 600;
		LinearLayout layout = new LinearLayout(context);
		layout.setPadding(100, 0, 100, 0);
		layout.setBackgroundColor(bgColor);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
		layout.addView(UiUtils.createSpace(context, 10, padding));

		TextView view = UiUtils.createTextView(context,
				"To: " + trans.getTargetName(), bgColor, Color.BLACK,
				MainActivity.textSize, true, new LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		view.setWidth(width);
		layout.addView(view);
		layout.addView(UiUtils.createSpace(context, 10, padding));

		view = UiUtils.createTextView(context, "Amount: " + trans.getAmount(),
				bgColor, Color.BLACK, MainActivity.textSize, true,
				new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT));
		view.setWidth(width);
		layout.addView(view);
		layout.addView(UiUtils.createSpace(context, 10, padding));

		final EditText passwordField = UiUtils.createEditText(context,
				"Password", "", true, true, Color.rgb(255, 249, 232),
				MainActivity.textSize, new LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		passwordField.setWidth(width);
		layout.addView(passwordField);
		layout.addView(UiUtils.createSpace(context, 10, padding));

		Button performBut = UiUtils.createButton(context, "PERFORM",
				new Runnable() {

					@Override
					public void run() {
						try {
							Transaction nt = new Transaction();
							Transaction.copy(trans, nt);
							nt.setPassword("" + passwordField.getText());
							MainActivity.mobileSdk.performTransaction(trans);
							dialog.dismiss();
						} catch (NumberFormatException e) {
							UiUtils.makeToast(context, "NumberFormatException",
									mainLayout);
						} catch (IOException e) {
							UiUtils.makeToast(context, "IOException",
									mainLayout);
						} catch (URISyntaxException e) {
							UiUtils.makeToast(context, "URISyntaxException",
									mainLayout);
						} catch (JSONException e) {
							UiUtils.makeToast(context, "JSONException",
									mainLayout);
						} catch (NotOkResponseException e) {
							UiUtils.makeToast(context, e.getMessage(),
									mainLayout);
						} catch (Exception e) {
							UiUtils.makeToast(context,
									DebugUtils.serializeException(e),
									mainLayout);
						}
					}
				}, true, Color.rgb(242, 247, 255), MainActivity.ButtonColor,
				MainActivity.textSize, new LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		performBut.setWidth(width);
		layout.addView(performBut);
		layout.addView(UiUtils.createSpace(context, 10, padding));

		Button cancellBut = UiUtils.createButton(context, "CANCEL",
				new Runnable() {

					@Override
					public void run() {
						dialog.dismiss();
					}
				}, true, Color.rgb(242, 247, 255), MainActivity.ButtonColor,
				MainActivity.textSize, new LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		cancellBut.setWidth(width);
		layout.addView(cancellBut);
		layout.addView(UiUtils.createSpace(context, 10, padding));

		dialog.setContentView(layout);
		dialog.show();
	}
}
