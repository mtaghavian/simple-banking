package com.example.wallet.util.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.widget.TextView;

/**
 * @author Masoud Taghavian (https://github.com/mtaghavian)
 */

public class Filler extends TextView {

	private Paint paint;
	private RectF rect;

	public Filler(Context context, int bgColor) {
		super(context);
		paint = new Paint();
		paint.setColor(bgColor);
		paint.setStyle(Paint.Style.FILL);
		rect = new RectF(0, 0, getWidth(), getHeight());
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		rect.set(0, 0, getWidth(), getHeight());
		canvas.drawRoundRect(rect, 10, 10, paint);
	}
}
