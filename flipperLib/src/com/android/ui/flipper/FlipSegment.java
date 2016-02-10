package com.android.ui.flipper;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 *
 */
public class FlipSegment extends View {
    private String mText;
    private int mTextColor = Color.RED;
    private float mTextSize = 0;
    private float mTextPaddingBottom = 0;
    private float mTextPaddingTop = 0;

    private TextPaint mTextPaint;
    private float mTextWidth;
    private float mTextHeight;

    public FlipSegment(Context context) {
        super(context);
        init(null, 0);
    }

    public FlipSegment(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public FlipSegment(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.FlipSegment, defStyle, 0);

        mText = a.getString(
                R.styleable.FlipSegment_text);
        mTextColor = a.getColor(
                R.styleable.FlipSegment_text_color,
                mTextColor);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mTextSize = a.getDimension(
                R.styleable.FlipSegment_text_size,
                mTextSize);

        mTextPaddingBottom = a.getDimension(
                R.styleable.FlipSegment_text_padding_bottom,
                mTextPaddingBottom);

        mTextPaddingTop = a.getDimension(
                R.styleable.FlipSegment_text_padding_top,
                mTextPaddingTop);

        a.recycle();

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();
    }

    private void invalidateTextPaintAndMeasurements() {
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);
        mTextWidth = mTextPaint.measureText(mText);

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom;

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        Log.d("char", mText);

        canvas.drawText(mText,
                paddingLeft + (contentWidth - mTextWidth) / 2,
                paddingTop + mTextPaddingTop + (contentHeight - mTextPaddingBottom - mTextPaddingTop + mTextHeight) / 2,
                mTextPaint);
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
        invalidateTextPaintAndMeasurements();
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int exampleColor) {
        mTextColor = exampleColor;
        invalidateTextPaintAndMeasurements();
    }

    public float getTextSize() {
        return mTextSize;
    }

    public void setTextSize(float dimension) {
        mTextSize = dimension;
        invalidateTextPaintAndMeasurements();
    }
}
