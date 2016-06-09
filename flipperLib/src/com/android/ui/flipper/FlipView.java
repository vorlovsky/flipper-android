package com.android.ui.flipper;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

/**
 *
 */
class FlipView {

	private FlipSegment flipSegmentBackUpper = null;
	private FlipSegment flipSegmentBackLower = null;
	private FlipSegment flipSegmentFrontUpper = null;
	private FlipSegment flipSegmentFrontLower = null;

	private Context context;
	private int id;

	private Animation animationUpper;
	private Animation animationLower;
	private Interpolator interpolatorUpper;
	private Interpolator interpolatorLower;

	private OnAnimationComplete completeListener;

	private String[] stringValues = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

	private int animateTo = 0;
	private int animateFrom = 0;

	private boolean ascending = true;

	public boolean reverseDescendingAnimation = false;

	public interface OnAnimationComplete {
		public void onComplete(int id);
	}

	private AnimationListener forwardAnimationListener = new AnimationListener() {
		@Override
		public void onAnimationStart(Animation animation) {
			if (animation == animationUpper) {
				flipSegmentFrontLower.setVisibility(View.INVISIBLE);
				flipSegmentFrontUpper.setVisibility(View.VISIBLE);

				int index = getIndexToShow();

				setStringByIndexToView(index, flipSegmentFrontLower);
				setStringByIndexToView(index, flipSegmentBackUpper);

			} else if (animation == animationLower) {
				flipSegmentFrontLower.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			if (animation == animationUpper) {
				flipSegmentFrontUpper.setVisibility(View.INVISIBLE);

				startSegmentAnimation(false);
			} else if (animation == animationLower) {
				flipSegmentFrontUpper.setVisibility(View.VISIBLE);

				int index = getIndexToShow();

				setStringByIndexToView(index, flipSegmentFrontUpper);
				setStringByIndexToView(index, flipSegmentBackLower);

				animateString(false);
			}
		}

		@Override
		public void onAnimationRepeat(Animation animation) {}
	};

	private AnimationListener backwardAnimationListener = new AnimationListener() {
		@Override
		public void onAnimationStart(Animation animation) {
			if (animation == animationLower) {
				flipSegmentFrontUpper.setVisibility(View.INVISIBLE);
				flipSegmentFrontLower.setVisibility(View.VISIBLE);

				int index = getIndexToShow();

				setStringByIndexToView(index, flipSegmentFrontUpper);
				setStringByIndexToView(index, flipSegmentBackLower);

			} else if (animation == animationUpper) {
				flipSegmentFrontUpper.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			if (animation == animationLower) {
				flipSegmentFrontLower.setVisibility(View.INVISIBLE);

				startSegmentAnimation(true);
			} else if (animation == animationUpper) {
				flipSegmentFrontLower.setVisibility(View.VISIBLE);

				int index = getIndexToShow();

				setStringByIndexToView(index, flipSegmentFrontLower);
				setStringByIndexToView(index, flipSegmentBackUpper);

				animateString(true);
			}
		}

		@Override
		public void onAnimationRepeat(Animation animation) {}
	};

	private class ReverseInterpolator implements Interpolator{
		private Interpolator interpolator;

		public ReverseInterpolator(Interpolator interpolator){
			this.interpolator = interpolator;
		}

		@Override
		public float getInterpolation(float input) {
			return 1 - interpolator.getInterpolation(input);
		}
	}

	private boolean getNeedReverseAnimations() {
		return (!ascending && reverseDescendingAnimation);
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
			if (stringValues[i].equals(value)) {
				return i;
			}
		}

		return -1;
	}

	private String getStringByIndex(int index) {
		return stringValues[index];
	}

	public void setStringIndex(int index, boolean withAnimation, boolean ascending) {
		this.ascending = ascending;

		animateTo = index;

		if (animateTo == -1) {
			animateTo = 0;
		}

		if (animateTo >= stringValues.length) {
			animateTo = (stringValues.length - 1);
		}

		if (withAnimation) {
			if(getNeedReverseAnimations()) {
				setupAnimations(backwardAnimationListener, new ReverseInterpolator(interpolatorUpper), new ReverseInterpolator(interpolatorLower));

				animateString(false);
			}
			else {
				setupAnimations(forwardAnimationListener, interpolatorUpper, interpolatorLower);

				animateString(true);
			}
		}
		else {
			setStringByIndexToAllViews(animateTo);
		}
	}

	public void setString(String value, boolean withAnimation, boolean ascending) {
		setStringIndex(getStringIndex(value), withAnimation, ascending);
	}

	private void setupAnimations(AnimationListener animationListener, Interpolator interpolatorUpper, Interpolator interpolatorLower) {
		animationUpper.setAnimationListener(animationListener);
		animationLower.setAnimationListener(animationListener);

		animationUpper.setInterpolator(interpolatorUpper);
		animationLower.setInterpolator(interpolatorLower);
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

		animationUpper = AnimationUtils.loadAnimation(context, R.anim.flip_point_to_middle);
		interpolatorUpper = animationUpper.getInterpolator();
		animationLower = AnimationUtils.loadAnimation(context, R.anim.flip_point_from_middle);
		interpolatorLower = animationLower.getInterpolator();
	}

	private void startAnimation() {
		if (animateTo == animateFrom) {
			if (completeListener != null)
				completeListener.onComplete(id);
		} else {
			startSegmentAnimation(!getNeedReverseAnimations());
		}
	}

	private void startSegmentAnimation(boolean isUpper) {
		if (isUpper) {
			flipSegmentFrontUpper.clearAnimation();
			flipSegmentFrontUpper.setAnimation(animationUpper);
			flipSegmentFrontUpper.startAnimation(animationUpper);

		} else {
			flipSegmentFrontLower.clearAnimation();
			flipSegmentFrontLower.setAnimation(animationLower);
			flipSegmentFrontLower.startAnimation(animationLower);
		}
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

	private int getIndexToShow() {
		int index = 0;

		if (animateTo == animateFrom) {
			index = animateTo;
		} else if (ascending) {
			index = animateFrom + 1;
		} else {
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
}