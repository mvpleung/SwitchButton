/*
 * Copyright (c) 2014. mvpleung@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lzc.switchbutton.widget;

import java.util.concurrent.atomic.AtomicInteger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.lzc.switchbutton.R;

/**
 * 选项卡
 * 
 * @author LiangZiChao created on 2014-8-28下午3:19:45
 */
@SuppressLint("Recycle")
public class SwitchButton extends RadioGroup implements OnCheckedChangeListener {

	private final int[] CHECKED_STATE = { android.R.attr.state_checked }, UNCHECKED_STATE = { -android.R.attr.state_checked };

	/** 默认选项卡数量 */
	private final static int DEFAULT_SWITCH_COUNT = 2;

	private ColorStateList mTextColor;

	private int mParentWidth, mParentHeight;

	private int mRadioStyle;

	private CharSequence[] mTexts;

	private int switchCount;

	// 是否测量完毕
	private boolean isMeasure;

	private boolean isStyleChanged, isMeasureChanged;

	private SparseArray<RadioButton> mRadioArrays;
	private SparseArray<Drawable> mButtonDrawables;
	private SparseArray<StateListDrawable> mStateDrawables;
	private SparseIntArray mSparseIds;

	private int mCurrentPosition;

	private OnChangeListener changeListener;

	private float cornerRadius;

	private int checkedColor, unCheckedColor, strokeColor, strokeWidth;

	/**
	 * @param context
	 */
	public SwitchButton(Context context) {
		super(context, null);
	}

	public SwitchButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs, new int[] { android.R.attr.orientation, android.R.attr.layout_height });
		setOrientation(a.getInt(0, LinearLayout.HORIZONTAL));
		mParentHeight = a.getDimensionPixelSize(1, 0);
		a.recycle();
		a = context.obtainStyledAttributes(attrs, R.styleable.switchButton);
		setTextColor(a.getColorStateList(R.styleable.switchButton_android_textColor));
		setTextArray(a.getTextArray(R.styleable.switchButton_sw_textArray));
		setSwitchCount(a.getInteger(R.styleable.switchButton_sw_switchCount, DEFAULT_SWITCH_COUNT));
		setSwitchStyle(a.getResourceId(R.styleable.switchButton_sw_ThemeStyle, 0));
		setCornerRadius(a.getDimension(R.styleable.switchButton_sw_CornerRadius, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, getResources().getDisplayMetrics())));
		setCheckedColor(a.getColor(R.styleable.switchButton_sw_checkedColor, Color.GREEN));
		setUnCheckedColor(a.getColor(R.styleable.switchButton_sw_unCheckedColor, Color.WHITE));
		setStrokeColor(a.getColor(R.styleable.switchButton_sw_strokeColor, Color.GREEN));
		setStrokeWidth((int) a.getDimension(R.styleable.switchButton_sw_strokeWidth, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, getResources().getDisplayMetrics())));
		a.recycle();
		setOnCheckedChangeListener(this);
	}

	/**
	 * 初始化UI
	 * 
	 * @param context
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private void initUI(Context context) {
		if (mTexts != null && mTexts.length != switchCount) {
			throw new IllegalArgumentException("The textArray's length must equal to the switchCount");
		}
		LayoutParams mParams = null;
		if (mParentWidth == 0)
			return;
		for (int i = 0; i < switchCount; i++) {
			if (mRadioArrays == null)
				mRadioArrays = new SparseArray<RadioButton>();
			RadioButton mRadioButton = mRadioArrays.get(i);
			boolean isExists = !isStyleChanged && mRadioButton != null;
			if (mParams == null) {
				mParams = (isExists && !isMeasureChanged) ? (LayoutParams) mRadioButton.getLayoutParams() : new LayoutParams(mParentWidth / switchCount, mParentHeight, 1);
			}
			if (!isExists) {
				mRadioButton = new RadioButton(getContext(), null, mRadioStyle > 0 ? mRadioStyle : android.R.attr.radioButtonStyle);
				if (mRadioStyle == 0) {
					mRadioButton.setGravity(Gravity.CENTER);
					mRadioButton.setEllipsize(TruncateAt.END);
				}
			}
			mRadioButton.setLayoutParams(mParams);
			Drawable mButtonDrawable = mButtonDrawables != null ? mButtonDrawables.get(i) : null;
			mRadioButton.setButtonDrawable(mButtonDrawable != null ? mButtonDrawable : new ColorDrawable());
			mRadioButton.setButtonDrawable(new ColorDrawable());
			if (Build.VERSION.SDK_INT >= 16) {
				mRadioButton.setBackground(getStateDrawable(i));
			} else {
				mRadioButton.setBackgroundDrawable(getStateDrawable(i));
			}
			if (mTextColor != null)
				mRadioButton.setTextColor(mTextColor);
			if (mTexts != null)
				mRadioButton.setText(mTexts[i]);
			if (!isExists) {
				int id = getViewId();
				if (mSparseIds == null)
					mSparseIds = new SparseIntArray();
				mSparseIds.put(i, id);
				mRadioButton.setId(id);
			} else {
				removeView(mRadioButton);
			}
			mRadioButton.setChecked(mCurrentPosition == i);
			addView(mRadioButton, i);
			mRadioArrays.put(i, mRadioButton);
		}
		isStyleChanged = false;
		isMeasureChanged = false;
	}

	private Drawable getStateDrawable(int i) {
		if (mStateDrawables == null)
			mStateDrawables = new SparseArray<StateListDrawable>();
		StateListDrawable mStateListDrawable = mStateDrawables.size() >= i + 1 && (i != switchCount - 1 || i == switchCount - 1) ? null : mStateDrawables.get(i);
		if (mStateListDrawable == null) {
			float leftRadius = i == 0 ? cornerRadius : 0;
			float rightRadius = i == 0 ? 0 : i == switchCount - 1 ? cornerRadius : 0;
			float[] cRadius = { leftRadius, leftRadius, rightRadius, rightRadius, rightRadius, rightRadius, leftRadius, leftRadius };
			mStateListDrawable = new StateListDrawable();
			GradientDrawable cornerDrawable = new GradientDrawable();
			cornerDrawable.setColor(checkedColor);
			cornerDrawable.setCornerRadii(cRadius);
			mStateListDrawable.addState(CHECKED_STATE, cornerDrawable);
			cornerDrawable = new GradientDrawable();
			cornerDrawable.setColor(unCheckedColor);
			cornerDrawable.setStroke(strokeWidth, strokeColor);
			cornerDrawable.setCornerRadii(cRadius);
			mStateListDrawable.addState(UNCHECKED_STATE, cornerDrawable);
			mStateDrawables.put(i, mStateListDrawable);
		}
		return mStateListDrawable;
	}

	@Deprecated
	public void initialize() {
		notifyDataSetChange();
	}

	/**
	 * 刷新数据（Button数量跟随刷新的文本数据变化）
	 */
	public void notifyDataSetChange() {
		removeAllViews();
		switchCount = mTexts != null ? mTexts.length : switchCount;
		initUI(getContext());
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (!isMeasure) {
			initUI(getContext());
			isMeasure = !isMeasure;
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mParentWidth = widthMeasureSpec;
		mParentHeight = mParentHeight == 0 ? heightMeasureSpec : mParentHeight;
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if (changeListener != null)
			changeListener.onChange(mSparseIds.indexOfValue(checkedId));
	}

	/**
	 * 设置当前选中项
	 * 
	 * @param selectedPosition
	 */
	public void setCurrentPosition(int selectedPosition) {
		if (selectedPosition >= 0 && selectedPosition <= switchCount) {
			mCurrentPosition = selectedPosition;
		}
	}

	/**
	 * 设置选中项
	 * 
	 * @param selectedPosition
	 */
	public void setCheckedPosition(int selectedPosition) {
		if (selectedPosition >= 0 && selectedPosition <= switchCount) {
			mCurrentPosition = selectedPosition;
			if (mSparseIds != null)
				check(mSparseIds.get(mSparseIds.keyAt(selectedPosition)));
		}
	}

	public void setTextColor(ColorStateList mTextColor) {
		this.mTextColor = mTextColor;
	}

	public void setSwitchStyle(int mSwitchStyle) {
		isStyleChanged = true;
		this.mRadioStyle = mSwitchStyle;
	}

	public void setTextArray(CharSequence[] mTexts) {
		this.mTexts = mTexts;
	}

	public int getSwitchCount() {
		return switchCount;
	}

	public void setParentWidth(int mParentWidth) {
		this.mParentWidth = mParentWidth;
		isMeasureChanged = true;
	}

	public void setParentHeight(int mParentHeight) {
		this.mParentHeight = mParentHeight;
		isMeasureChanged = true;
	}

	public void setSwitchCount(int switchCount) {
		this.switchCount = switchCount < 2 ? DEFAULT_SWITCH_COUNT : switchCount;
		if (mButtonDrawables == null)
			mButtonDrawables = new SparseArray<Drawable>();
	}

	public void setCornerRadius(float cornerRadius) {
		this.cornerRadius = cornerRadius;
	}

	public void setCheckedColor(int checkedColor) {
		this.checkedColor = checkedColor;
	}

	public void setUnCheckedColor(int unCheckedColor) {
		this.unCheckedColor = unCheckedColor;
	}

	public void setStrokeColor(int strokeColor) {
		this.strokeColor = strokeColor;
	}

	public void setStrokeWidth(int strokeWidth) {
		this.strokeWidth = strokeWidth;
	}

	public void setSwitchButton(int position, int mDrawableResId) {
		mButtonDrawables.put(position, getResources().getDrawable(mDrawableResId));
	}

	public void setOnChangeListener(OnChangeListener eventListener) {
		this.changeListener = eventListener;
	}

	public interface OnChangeListener {
		public void onChange(int position);
	}

	private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

	public int getViewId() {
		for (;;) {
			final int result = sNextGeneratedId.get();
			// aapt-generated IDs have the high byte nonzero; clamp to the range
			// under that.
			int newValue = result + 1;
			if (newValue > 0x00FFFFFF)
				newValue = 1; // Roll over to 1, not 0.
			if (sNextGeneratedId.compareAndSet(result, newValue)) {
				return result;
			}
		}
	}
}
