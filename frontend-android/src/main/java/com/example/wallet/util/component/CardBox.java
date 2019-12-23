package com.example.wallet.util.component;

import com.example.wallet.R;
import com.example.wallet.sdk.model.Card;
import com.example.wallet.util.UiUtils;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author Masoud Taghavian (https://github.com/mtaghavian)
 */

public class CardBox extends LinearLayout {

	private boolean checked = false;
	private ImageView imageView;
	private Card card;
	private TextView nameView, numberView;

	public CardBox(Context context, int bgColor, int textSize, int smallTextSize) {
		super(context);
		setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		setGravity(Gravity.CENTER_VERTICAL);
		setBackgroundColor(bgColor);
		setOrientation(LinearLayout.HORIZONTAL);

		addView(UiUtils.createSpace(context, 40, 10));

		imageView = new ImageView(context);
		imageView.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		updateImage();
		addView(imageView);
		addView(UiUtils.createSpace(context, 40, 10));

		nameView = new TextView(context);
		nameView.setGravity(Gravity.CENTER_VERTICAL);
		nameView.setTextSize(textSize);
		nameView.setTextColor(Color.BLACK);
		nameView.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		addView(nameView);

		numberView = new TextView(context);
		numberView.setGravity(Gravity.CENTER_VERTICAL);
		numberView.setTextSize(smallTextSize);
		numberView.setTextColor(Color.GRAY);
		numberView.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		addView(numberView);

		setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramAnonymousView) {
				checked = !checked;
				updateImage();
			}
		});
	}

	private void updateImage() {
		imageView.setImageResource(checked ? R.drawable.chk : R.drawable.unchk);
	}

	public void setChecked(boolean ch) {
		checked = ch;
		updateImage();
	}

	public boolean isChecked() {
		return checked;
	}

	public void setCard(Card card) {
		this.card = card;
		nameView.setText(card.getName());
		numberView.setText("  (" + card.getNumber() + ")");
	}

	public Card getCard() {
		return card;
	}
}
