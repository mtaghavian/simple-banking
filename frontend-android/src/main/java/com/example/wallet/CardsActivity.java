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
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.wallet.sdk.NotOkResponseException;
import com.example.wallet.sdk.model.Card;
import com.example.wallet.util.DebugUtils;
import com.example.wallet.util.UiUtils;
import com.example.wallet.util.component.CardBox;

/**
 * @author Masoud Taghavian (https://github.com/mtaghavian)
 */

public class CardsActivity extends Activity {

	public Context context;
	public CardsActivity activity;
	public LinearLayout mainLayout, cardsLayout;
	private final static String activityName = "Cards";
	private List<CardBox> cardList = new ArrayList<CardBox>();
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
		optionsLayout.addView(UiUtils.createImage(context, R.drawable.delete,
				new Runnable() {

					@Override
					public void run() {
						boolean hasChecked = false;
						for (int i = 0; i < cardList.size(); i++) {
							if (cardList.get(i).isChecked()) {
								hasChecked = true;
								break;
							}
						}
						if (!hasChecked) {
							return;
						}
						UiUtils.showDialog(context, mainLayout,
								"Are you sure to delete selected cards?",
								"YES", new Runnable() {
									public void run() {
										try {
											for (int i = 0; i < cardList.size(); i++) {
												if (cardList.get(i).isChecked()) {
													MainActivity.mobileSdk
															.deleteCard(cardList
																	.get(i)
																	.getCard());
												}
											}
										} catch (IOException e) {
											UiUtils.makeToast(context,
													"IOException", mainLayout);
										} catch (URISyntaxException e) {
											UiUtils.makeToast(context,
													"URISyntaxException",
													mainLayout);
										} catch (JSONException e) {
											UiUtils.makeToast(context,
													"JSONException", mainLayout);
										} catch (NotOkResponseException e) {
											UiUtils.makeToast(context,
													e.getMessage(), mainLayout);
										} catch (Exception e) {
											UiUtils.makeToast(
													context,
													DebugUtils
															.serializeException(e),
													mainLayout);
										} finally {
											list();
										}
									}
								}, "NO", null);
					}
				}, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT, 1.0f), 0, space, 0, space));
		optionsLayout.addView(UiUtils.createImage(context, R.drawable.edit,
				new Runnable() {

					@Override
					public void run() {
						edit();
					}
				}, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT, 1.0f), 0, space, 0, space));

		mainLayout.addView(MainActivity.createHeaderLayout(this, activityName));

		cardsLayout = new LinearLayout(this);
		cardsLayout.setBackgroundColor(bgColor);
		cardsLayout.setOrientation(LinearLayout.VERTICAL);
		mainLayout.addView(UiUtils.createScrollView(context, cardsLayout,
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
			final List<Card> cards = MainActivity.mobileSdk.listCards();
			mainLayout.post(new Runnable() {

				@Override
				public void run() {
					cardList.clear();
					int space = 50;
					cardsLayout.removeAllViews();
					cardsLayout.addView(UiUtils.createSpace(context, 10, space));
					if (cards.isEmpty()) {
						cardsLayout.addView(UiUtils.createSpace(context, 10,
								space));
						cardsLayout.addView(UiUtils.createTextView(context,
								"Empty list", bgColor, Color.GRAY,
								MainActivity.textSize, true,
								new LinearLayout.LayoutParams(
										LayoutParams.MATCH_PARENT,
										LayoutParams.WRAP_CONTENT, 1.0f)));
					} else {
						for (int i = 0; i < cards.size(); i++) {
							CardBox box = new CardBox(context, bgColor,
									MainActivity.textSize,
									MainActivity.smallTextSize);
							box.setCard(cards.get(i));
							cardList.add(box);
							cardsLayout.addView(box);
							cardsLayout.addView(UiUtils.createSpace(context,
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

				final EditText nameField = UiUtils.createEditText(context,
						"Name", "", true, true, Color.rgb(255, 249, 232),
						MainActivity.textSize, new LinearLayout.LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT));
				nameField.setWidth(width);
				layout.addView(nameField);
				layout.addView(UiUtils.createSpace(context, 10, padding));

				final EditText numberField = UiUtils.createEditText(context,
						"Number", "", true, true, Color.rgb(255, 249, 232),
						MainActivity.textSize, new LinearLayout.LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT));
				numberField.setWidth(width);
				layout.addView(numberField);
				layout.addView(UiUtils.createSpace(context, 10, padding));

				Button addBut = UiUtils.createButton(context, "ADD",
						new Runnable() {

							@Override
							public void run() {
								Card c = new Card();
								c.setName("" + nameField.getText());
								c.setNumber("" + numberField.getText());
								try {
									MainActivity.mobileSdk.addCard(c);
									dialog.dismiss();
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

	private void edit() {
		int checkedCnt = 0;
		for (int i = 0; i < cardList.size(); i++) {
			if (cardList.get(i).isChecked()) {
				checkedCnt++;
			}
		}
		if (checkedCnt != 1) {
			UiUtils.makeToast(context, "Please select one card to edit",
					mainLayout);
			return;
		}
		mainLayout.post(new Runnable() {

			@Override
			public void run() {
				CardBox selectedCard = null;
				for (int i = 0; i < cardList.size(); i++) {
					if (cardList.get(i).isChecked()) {
						selectedCard = cardList.get(i);
						break;
					}
				}

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

				final EditText nameField = UiUtils.createEditText(context,
						"Name", "", true, true, Color.rgb(255, 249, 232),
						MainActivity.textSize, new LinearLayout.LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT));
				nameField.setWidth(width);
				nameField.setText(selectedCard.getCard().getName());
				layout.addView(nameField);
				layout.addView(UiUtils.createSpace(context, 10, padding));

				final EditText numberField = UiUtils.createEditText(context,
						"Number", "", true, true, Color.rgb(255, 249, 232),
						MainActivity.textSize, new LinearLayout.LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT));
				numberField.setWidth(width);
				numberField.setText(selectedCard.getCard().getNumber());
				layout.addView(numberField);
				layout.addView(UiUtils.createSpace(context, 10, padding));

				Button editBut = UiUtils.createButton(
						context,
						"EDIT",
						new Runnable() {

							@Override
							public void run() {
								Card c = new Card();
								for (int i = 0; i < cardList.size(); i++) {
									if (cardList.get(i).isChecked()) {
										Card.copy(cardList.get(i).getCard(), c);
										break;
									}
								}
								c.setName("" + nameField.getText());
								c.setNumber("" + numberField.getText());
								try {
									MainActivity.mobileSdk.editCard(c);
									dialog.dismiss();
									list();
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
				editBut.setWidth(width);
				layout.addView(editBut);
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
