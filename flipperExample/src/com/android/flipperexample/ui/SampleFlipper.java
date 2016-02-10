package com.android.flipperexample.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.android.flipperexample.R;
import com.android.ui.flipper.FlipSpinner;

/**
 *
 */
public class SampleFlipper extends LinearLayout {
	private static final int NUM_DIGITS = 2;

//	private String mCurrentValue;
//	private int animationCompleteCounter = 0;

	private FlipSpinner[] mSpinners;

	public SampleFlipper(Context context) {
		super(context);

		initialize();
	}

	public SampleFlipper(Context context, AttributeSet attrs) {
		super(context, attrs);

		initialize();
	}

	private void initialize() {
		mSpinners = new FlipSpinner[NUM_DIGITS];

		// Inflate the view from the layout resource.
		String infService = Context.LAYOUT_INFLATER_SERVICE;

		LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
		li.inflate(R.layout.widget_flipper, this, true);

		mSpinners[0] = (FlipSpinner) findViewById(R.id.widget_flip_spinner_1);
		mSpinners[1] = (FlipSpinner) findViewById(R.id.widget_flip_spinner_2);

        String[] values = new String[100];
        for (int i = 0; i < values.length; i++) {
            values[i] = String.valueOf(i);
        }

        FlipSpinner spinner;
        for (int i = 0; i < mSpinners.length; i++) {
            spinner = mSpinners[i];

            spinner.setStringValues(values);
        }
	}

    public void setString(String value, boolean withAnimation) {
        setString(value, withAnimation, true);
    }

	// XXX:
	public void setString(String value, boolean withAnimation, boolean ascending) {
		value = normalizeString(value, 4);

        FlipSpinner spinner;
        for (int i = 0; i < mSpinners.length; i++) {
            spinner = mSpinners[mSpinners.length - i - 1];

			spinner.setString(String.valueOf(Integer.parseInt(value.substring(i*2, i*2+2))), withAnimation, ascending);
//			changeAnimationCompleteCounter(withAnimation);
		}

	}

	private String normalizeString(String value, int length) {
		while(value.length() < length) {
			value = "0" + value;
		}
		value = value.substring(0, length);

		return value;
	}

//	private synchronized int changeAnimationCompleteCounter(Boolean increment) {
//		if (increment == null)
//			return animationCompleteCounter;
//		else if (increment)
//			return ++animationCompleteCounter;
//		else
//			return --animationCompleteCounter;
//	}

	/**
	 * @return
	 */
	public String getString() {
        String value = "";

        FlipSpinner spinner;
        for (int i = (mSpinners.length - 1); i >= 0; i--) {
            spinner = mSpinners[i];

            value += spinner.getString();
        }

        return value;
	}

	public interface OnValueChangeListener {
		abstract void onValueChange(SampleFlipper sender, int newValue);
	}

}
