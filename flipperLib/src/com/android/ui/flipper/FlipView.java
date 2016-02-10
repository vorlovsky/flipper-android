package com.android.ui.flipper;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

/**
 *
 */
class FlipView implements AnimationListener {

	private FlipSegment flipSegmentBackUpper = null;
	private FlipSegment flipSegmentBackLower = null;
	private FlipSegment flipSegmentFrontUpper = null;
	private FlipSegment flipSegmentFrontLower = null;

	private Context context;
	private Animation animation1;
	private Animation animation2;
	private int id;
	private OnAnimationComplete completeListener;

    private String[] stringValues = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

	private int animateTo = 0;
	private int animateFrom = 0;
	private boolean ascending = true;

	public interface OnAnimationComplete {
		public void onComplete(int id);
	}

    public void setStringValues(String[] values) {
        this.stringValues = values;
    }

	public FlipView(Context context, int id, View view, OnAnimationComplete completeListener) {
		super();

		this.context = context;
		this.id = id;
		this.completeListener = completeListener;

		flipSegmentBackUpper = (FlipSegment) view.findViewById(R.id.flip_spinner_back_upper);
		flipSegmentBackLower = (FlipSegment) view.findViewById(R.id.flip_spinner_back_lower);
		flipSegmentFrontUpper = (FlipSegment) view.findViewById(R.id.flip_spinner_front_upper);
		flipSegmentFrontLower = (FlipSegment) view.findViewById(R.id.flip_spinner_front_lower);

		init();
	}

    private int getStringIndex(String value) {
        for (int i = 0; i < stringValues.length; i++) {
            if(stringValues[i].equals(value)) {
                return i;
            }
        }

        return -1;
    }

    private String getStringByIndex(int index) {
        return stringValues[index];
    }

	public void setValue(int value, boolean withAnimation, boolean ascending) {
		this.ascending = ascending;

		animateTo = value;

		if(animateTo == -1) {
			animateTo = 0;
		}

		if(animateTo >= stringValues.length) {
			animateTo = (stringValues.length - 1);
		}

		if (withAnimation)
			animateString(true);
		else
			setStringByIndexToAllViews(animateTo);
	}

    public void setString(String value, boolean withAnimation, boolean ascending) {
		setValue(getStringIndex(value), withAnimation, ascending);
	}

    public String getString() {
        return stringValues[animateTo];
    }

	private void animateString(boolean isUpper) {
		animateFrom = getLastIndex(isUpper);

		startAnimation();
	}

	private void init() {
		flipSegmentBackUpper.setTag(0);
		flipSegmentBackLower.setTag(0);
		flipSegmentFrontUpper.setTag(0);
		flipSegmentFrontLower.setTag(0);

		animation1 = AnimationUtils.loadAnimation(context, R.anim.flip_point_to_middle);
		animation1.setAnimationListener(this);
		animation2 = AnimationUtils.loadAnimation(context, R.anim.flip_point_from_middle);
		animation2.setAnimationListener(this);
	}

	private void startAnimation() {
		if (animateTo == animateFrom) {
			if (completeListener != null)
				completeListener.onComplete(id);
		} else {
			startStringAnimation(true);
		}
	}

	private void startStringAnimation(boolean isUpper) {
		if (isUpper) {
			flipSegmentFrontUpper.clearAnimation();
			flipSegmentFrontUpper.setAnimation(animation1);
			flipSegmentFrontUpper.startAnimation(animation1);

		} else {
			flipSegmentFrontLower.clearAnimation();
			flipSegmentFrontLower.setAnimation(animation2);
			flipSegmentFrontLower.startAnimation(animation2);
		}
	}

	private void updateIndexFromCode() {
		animateFrom = normalizeIndex(ascending ? (animateFrom + 1) : (animateFrom - 1));
	}

	private int getLastIndex(boolean isUpper) {
		int index = 0;
		try {
			if (isUpper)
				index = (Integer) flipSegmentFrontUpper.getTag();
			else
				index = (Integer) flipSegmentFrontLower.getTag();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return normalizeIndex(index);
	}

	private int getStringToShow() {
        int index = 0;

		if(animateTo == animateFrom) {
            index = animateTo;
		}
		else if(ascending) {
            index = animateFrom + 1;
		}
		else {
            index = animateFrom - 1;
		}

        return normalizeIndex(index);
	}

	private int normalizeIndex(int index) {
		if (index >= stringValues.length) {
			return 0;
		}
		if (index < 0) {
			return (stringValues.length - 1);
		}

		return index;
	}

	private void setStringByIndexToAllViews(int index) {
        setStringByIndexToView(index, flipSegmentBackUpper);
        setStringByIndexToView(index, flipSegmentBackLower);
        setStringByIndexToView(index, flipSegmentFrontUpper);
        setStringByIndexToView(index, flipSegmentFrontLower);
	}

	private void setStringByIndexToView(int index, FlipSegment image) {
		image.setTag(index);

		image.setText(getStringByIndex(index));
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		if (animation == animation1) {
			flipSegmentFrontUpper.setVisibility(View.INVISIBLE);

			startStringAnimation(false);
		} else if (animation == animation2) {
			flipSegmentFrontUpper.setVisibility(View.VISIBLE);

            setStringByIndexToView(getStringToShow(), flipSegmentFrontUpper);
            setStringByIndexToView(getStringToShow(), flipSegmentBackLower);

			updateIndexFromCode();

			animateString(false);
		}
	}

	@Override
	public void onAnimationRepeat(Animation arg0) {

	}

	@Override
	public void onAnimationStart(Animation animation) {
		if (animation == animation1) {
			flipSegmentFrontLower.setVisibility(View.INVISIBLE);
			flipSegmentFrontUpper.setVisibility(View.VISIBLE);

            setStringByIndexToView(getStringToShow(), flipSegmentFrontLower);
            setStringByIndexToView(getStringToShow(), flipSegmentBackUpper);

		} else if (animation == animation2) {
			flipSegmentFrontLower.setVisibility(View.VISIBLE);
		}
	}
}
