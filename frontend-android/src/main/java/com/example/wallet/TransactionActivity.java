package com.example.wallet;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wallet.sdk.NotOkResponseException;
import com.example.wallet.sdk.model.Card;
import com.example.wallet.sdk.model.Transaction;
import com.example.wallet.util.DebugUtils;
import com.example.wallet.util.UiUtils;
import com.example.wallet.util.component.TransactionBox;

/**
 * @author Masoud Taghavian (https://github.com/mtaghavian)
 */

public class TransactionActivity extends Activity {

	public Context context;
	public TransactionActivity activity;
	public LinearLayout mainLayout, transLayout;
	private final static String activityName = "Transactions";
	private List<TransactionBox> transBoxes = new ArrayList<TransactionBox>();
	private static final int bgColor = Color.WHITE;

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		this.context = this;
		this.activity = this;

		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		Configuration config = context.getResources().getConfiguration();
		displayMetrics.densityDpi = DisplayMetrics.DENSITY_340;
		config.densityDpi = displayMetrics.densityDpi;
		context.getResources().updateConfiguration(config, displayMetrics);

		mainLayout = new LinearLayout(this);
		mainLayout.setBackgroundColor(bgColor);
		mainLayout.setOrientation(LinearLayout.VERTICAL);

		LinearLayout optionsLayout = new LinearLayout(this);
		optionsLayout.setBackgroundColor(MainActivity.headerBgColor);
		optionsLayout.setOrientation(LinearLayout.HORIZONTAL);

		int space = 40;
		optionsLayout.addView(UiUtils.createImage(context, R.drawable.add,
				new Runnable() {

					@Override
					public void run() {
						add();
					}
				}, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT, 1.0f), 0, space, 0, space));
		optionsLayout.addView(UiUtils.createImage(context, R.drawable.refresh,
				new Runnable() {

					@Override
					public void run() {
						list();
					}
				}, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT, 1.0f), 0, space, 0, space));

		mainLayout.addView(MainActivity.createHeaderLayout(this, activityName));

		transLayout = new LinearLayout(this);
		transLayout.setBackgroundColor(bgColor);
		transLayout.setOrientation(LinearLayout.VERTICAL);
		mainLayout.addView(UiUtils.createScrollView(context, transLayout,
				new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.MATCH_PARENT, 1.0f)));

		mainLayout.addView(optionsLayout);

		setContentView(mainLayout);

		new Thread(new Runnable() {

			@Override
			public void run() {
				list();
			}
		}).start();
	}

	public void list() {
		try {
			final List<Transaction> trans = MainActivity.mobileSdk
					.listTransactions();
			mainLayout.post(new Runnable() {

				@Override
				public void run() {
					transBoxes.clear();
					int space = 50;
					transLayout.removeAllViews();
					transLayout.addView(UiUtils.createSpace(context, 10, space));
					if (trans.isEmpty()) {
						transLayout.addView(UiUtils.createSpace(context, 10,
								space));
						transLayout.addView(UiUtils.createTextView(context,
								"Empty list", bgColor, Color.GRAY,
								MainActivity.textSize, true,
								new LinearLayout.LayoutParams(
										LayoutParams.MATCH_PARENT,
										LayoutParams.WRAP_CONTENT, 1.0f)));
					} else {
						LinearLayout tableHeader = new LinearLayout(context);
						tableHeader
								.setLayoutParams(new LinearLayout.LayoutParams(
										LayoutParams.MATCH_PARENT,
										LayoutParams.WRAP_CONTENT));
						tableHeader.setGravity(Gravity.CENTER_VERTICAL);
						tableHeader.setBackgroundColor(bgColor);
						tableHeader.setOrientation(LinearLayout.HORIZONTAL);

						TextView view = UiUtils.createTextView(context,
								"Description", bgColor,
								MainActivity.ButtonColor,
								MainActivity.textSize, true,
								new LinearLayout.LayoutParams(
										LayoutParams.MATCH_PARENT,
										LayoutParams.WRAP_CONTENT, 1.0f));
						view.setGravity(Gravity.CENTER_VERTICAL
								| Gravity.CENTER_HORIZONTAL);
						tableHeader.addView(view);
						view = UiUtils.createTextView(context, "Status",
								bgColor, MainActivity.ButtonColor,
								MainActivity.textSize, true,
								new LinearLayout.LayoutParams(
										LayoutParams.MATCH_PARENT,
										LayoutParams.WRAP_CONTENT, 1.0f));
						view.setGravity(Gravity.CENTER_VERTICAL
								| Gravity.CENTER_HORIZONTAL);
						tableHeader.addView(view);
						view = UiUtils.createTextView(context, "Action",
								bgColor, MainActivity.ButtonColor,
								MainActivity.textSize, true,
								new LinearLayout.LayoutParams(
										LayoutParams.MATCH_PARENT,
										LayoutParams.WRAP_CONTENT, 1.0f));
						view.setGravity(Gravity.CENTER_VERTICAL
								| Gravity.CENTER_HORIZONTAL);
						tableHeader.addView(view);
						transLayout.addView(tableHeader);
						transLayout.addView(UiUtils.createSpace(context, 10,
								space));

						View fillerView = UiUtils.createFiller(context,
								Color.rgb(242, 247, 255), 200, 10);
						fillerView
								.setLayoutParams(new LinearLayout.LayoutParams(
										LayoutParams.MATCH_PARENT,
										LayoutParams.WRAP_CONTENT));
						transLayout.addView(fillerView);
						transLayout.addView(UiUtils.createSpace(context, 10,
								space));

						for (int i = 0; i < trans.size(); i++) {
							TransactionBox box = new TransactionBox(context,
									mainLayout, trans.get(i), bgColor,
									MainActivity.textSize,
									MainActivity.smallTextSize);
							transBoxes.add(box);
							transLayout.addView(box);
							transLayout.addView(UiUtils.createSpace(context,
									10, space));
						}
					}
				}
			});
		} catch (IOException e) {
			UiUtils.makeToast(context, "IOException", mainLayout);
		} catch (URISyntaxException e) {
			UiUtils.makeToast(context, "URISyntaxException", mainLayout);
		} catch (JSONException e) {
			UiUtils.makeToast(context, "JSONException", mainLayout);
		} catch (NotOkResponseException e) {
			UiUtils.makeToast(context, e.getMessage(), mainLayout);
		} catch (Exception e) {
			UiUtils.makeToast(context, DebugUtils.serializeException(e),
					mainLayout);
		}
	}

	private Button mySpinner;

	private void add() {
		mainLayout.post(new Runnable() {

			@Override
			public void run() {
				final Dialog dialog = new Dialog(context);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.getWindow().setBackgroundDrawableResource(
						android.R.color.transparent);

				int bgColor = Color.WHITE, padding = 80, width = 600;
				LinearLayout layout = new LinearLayout(context);
				layout.setPadding(100, 0, 100, 0);
				layout.setBackgroundColor(bgColor);
				layout.setOrientation(LinearLayout.VERTICAL);
				layout.setGravity(Gravity.CENTER_VERTICAL
						| Gravity.CENTER_HORIZONTAL);
				layout.addView(UiUtils.createSpace(context, 10, padding));

				mySpinner = UiUtils.createButton(
						context,
						"Select source card",
						new Runnable() {

							@Override
							public void run() {
								try {
									final List<Card> cards = MainActivity.mobileSdk
											.listCards();
									mainLayout.post(new Runnable() {

										@Override
										public void run() {
											final Dialog dialog = new Dialog(
													context);
											int bgColor = Color.WHITE, padding = 80;

											dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
											dialog.getWindow()
													.setBackgroundDrawableResource(
															android.R.color.transparent);
											// dialog.setCancelable(false);

											LinearLayout layout = new LinearLayout(
													context);
											layout.setPadding(50, 0, 50, 0);
											layout.setBackgroundColor(bgColor);
											layout.setOrientation(LinearLayout.VERTICAL);
											layout.setGravity(Gravity.CENTER_VERTICAL
													| Gravity.CENTER_HORIZONTAL);
											layout.addView(UiUtils.createSpace(
													context, 100, padding));

											OnClickListener listener = new View.OnClickListener() {
												public void onClick(View v) {
													TextView tv = (TextView) v;
													String str = ("" + tv
															.getText());
													str = str.substring(
															str.lastIndexOf(" ") + 1);
													mySpinner.setText(str);
													dialog.dismiss();
												}
											};
											for (int i = 0; i < cards.size(); i++) {
												TextView view = UiUtils
														.createTextView(
																context,
																""
																		+ cards.get(
																				i)
																				.getName()
																		+ " - "
																		+ cards.get(
																				i)
																				.getNumber(),
																bgColor,
																Color.GRAY,
																MainActivity.textSize,
																true,
																new LinearLayout.LayoutParams(
																		LayoutParams.WRAP_CONTENT,
																		LayoutParams.WRAP_CONTENT));
												view.setOnClickListener(listener);
												layout.addView(view);
												layout.addView(UiUtils
														.createSpace(context,
																100, padding));
											}

											dialog.setContentView(layout);
											dialog.show();
										}
									});
								} catch (IOException e) {
									UiUtils.makeToast(context, "IOException",
											mainLayout);
								} catch (URISyntaxException e) {
									UiUtils.makeToast(context,
											"URISyntaxException", mainLayout);
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
						}, true, Color.rgb(242, 247, 255),
						MainActivity.ButtonColor, MainActivity.textSize,
						new LinearLayout.LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT));
				mySpinner.setWidth(width);
				layout.addView(mySpinner);
				layout.addView(UiUtils.createSpace(context, 10, padding));

				final EditText targetCard = UiUtils.createEditText(context,
						"Target card", "", true, true,
						Color.rgb(255, 249, 232), MainActivity.textSize,
						new LinearLayout.LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT));
				targetCard.setWidth(width);
				layout.addView(targetCard);
				layout.addView(UiUtils.createSpace(context, 10, padding));

				final EditText amount = UiUtils.createEditText(context,
						"Amount", "", true, true, Color.rgb(255, 249, 232),
						MainActivity.textSize, new LinearLayout.LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT));
				amount.setWidth(width);
				layout.addView(amount);
				layout.addView(UiUtils.createSpace(context, 10, padding));

				Button addBut = UiUtils.createButton(
						context,
						"ADD",
						new Runnable() {

							@Override
							public void run() {
								try {
									Transaction nt = new Transaction();
									nt.setSourceCard("" + mySpinner.getText());
									nt.setTargetCard("" + targetCard.getText());
									nt.setAmount(Double.parseDouble(""
											+ amount.getText()));
									MainActivity.mobileSdk.addTransaction(nt);
									dialog.dismiss();
								} catch (NumberFormatException e) {
									UiUtils.makeToast(context,
											"NumberFormatException", mainLayout);
								} catch (IOException e) {
									UiUtils.makeToast(context, "IOException",
											mainLayout);
								} catch (URISyntaxException e) {
									UiUtils.makeToast(context,
											"URISyntaxException", mainLayout);
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
								} finally {
									list();
								}
							}
						}, true, Color.rgb(242, 247, 255),
						MainActivity.ButtonColor, MainActivity.textSize,
						new LinearLayout.LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT));
				addBut.setWidth(width);
				layout.addView(addBut);
				layout.addView(UiUtils.createSpace(context, 10, padding));

				Button cancellBut = UiUtils.createButton(context, "CANCEL",
						new Runnable() {

							@Override
							public void run() {
								dialog.dismiss();
							}
						}, true, Color.rgb(242, 247, 255),
						MainActivity.ButtonColor, MainActivity.textSize,
						new LinearLayout.LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT));
				cancellBut.setWidth(width);
				layout.addView(cancellBut);
				layout.addView(UiUtils.createSpace(context, 10, padding));

				dialog.setContentView(layout);
				dialog.show();
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return false;
	}
}
