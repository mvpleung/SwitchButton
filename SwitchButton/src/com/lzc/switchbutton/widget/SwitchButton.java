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
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.lzc.switchbutton.R;

/**
 * @description 选项卡
 * @author LiangZiChao
 * @Date 2014-8-28下午3:19:45
 * @Package com.xiaobai.xbtrip.view
 */
@SuppressLint("Recycle")
public class SwitchButton extends RadioGroup implements OnCheckedChangeListener {

	private static final int[] ATTRS = new int[] { android.R.attr.orientation };

	/** 默认选项卡数量 */
	private final static int DEFAULT_SWITCH_COUNT = 2;

	// 选择器
	private int mShapeCenter, mShapeLeft, mShapeRight;

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
	private SparseIntArray mSparseIds;

	private OnChangeListener changeListener;

	/**
	 * @param context
	 */
	public SwitchButton(Context context) {
		super(context, null);
	}

	public SwitchButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
		setOrientation(a.getInt(0, HORIZONTAL));
		a.recycle();
		a = context.obtainStyledAttributes(attrs, R.styleable.switchButton);
		setTextColor(a.getColorStateList(R.styleable.switchButton_android_textColor));
		setTextArray(a.getTextArray(R.styleable.switchButton_textArray));
		setShapeLeft(a.getResourceId(R.styleable.switchButton_shapeLeft, 0));
		setShapeRight(a.getResourceId(R.styleable.switchButton_shapeRight, 0));
		setShapeCenter(a.getResourceId(R.styleable.switchButton_shapeCenter, 0));
		setSwitchCount(a.getInteger(R.styleable.switchButton_switchCount, DEFAULT_SWITCH_COUNT));
		setSwitchStyle(a.getResourceId(R.styleable.switchButton_switchStyle, 0));
		a.recycle();
		setOnCheckedChangeListener(this);
	}

	/**
	 * 初始化UI
	 * 
	 * @param context
	 */
	private void initUI(Context context) {
		if (mTexts != null && mTexts.length != switchCount) {
			throw new IllegalArgumentException("The textArray's length must equal to the switchCount");
		}
		RadioGroup.LayoutParams mParams = null;
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
			if (mTextColor != null)
				mRadioButton.setTextColor(mTextColor);
			if (i == 0) {
				mRadioButton.setBackgroundResource(mShapeLeft);
			} else if (i == switchCount - 1) {
				mRadioButton.setBackgroundResource(mShapeRight);
			} else {
				mRadioButton.setBackgroundResource(mShapeCenter);
			}
			if (mTexts != null)
				mRadioButton.setText(mTexts[i]);
			if (!isExists) {
				int id = getViewId();
				if (mSparseIds == null)
					mSparseIds = new SparseIntArray();
				mSparseIds.put(i, id);
				mRadioButton.setId(id);
			}
			addView(mRadioButton, i);
			mRadioArrays.put(i, mRadioButton);
		}
		isStyleChanged = false;
		isMeasureChanged = false;
	}

	/**
	 * Button的个数跟随文本的数量，文本为空时，使用原始数量
	 */
	public void notifyDataSetChange() {
		removeAllViews();
		switchCount = mTexts != null ? mTexts.length : switchCount;
		initUI(getContext());
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (!isMeasure) {
			mParentWidth = widthMeasureSpec;
			mParentHeight = heightMeasureSpec;
			initUI(getContext());
			isMeasure = !isMeasure;
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		if (changeListener != null)
			changeListener.onChange(mSparseIds.indexOfValue(checkedId));
	}

	/**
	 * 设置当前选中项
	 * 
	 * @param selectedPosition
	 */
	public void setCheckedPosition(int selectedPosition) {
		if (selectedPosition >= 0 && selectedPosition <= switchCount) {
			check(mSparseIds.get(mSparseIds.keyAt(selectedPosition)));
		}
	}

	public void setShapeCenter(int mShapeCenter) {
		this.mShapeCenter = mShapeCenter;
	}

	public void setShapeLeft(int mShapeLeft) {
		this.mShapeLeft = mShapeLeft;
	}

	public void setShapeRight(int mShapeRight) {
		this.mShapeRight = mShapeRight;
	}

	public ColorStateList getTextColor() {
		return mTextColor;
	}

	public void setTextColor(ColorStateList mTextColor) {
		this.mTextColor = mTextColor;
	}

	public int getSwitchStyle() {
		return mRadioStyle;
	}

	public void setSwitchStyle(int mSwitchStyle) {
		isStyleChanged = true;
		this.mRadioStyle = mSwitchStyle;
	}

	public CharSequence[] getTexts() {
		return mTexts;
	}

	public void setTextArray(CharSequence[] mTexts) {
		this.mTexts = mTexts;
	}

	public int getSwitchCount() {
		return switchCount;
	}

	public int getParentWidth() {
		return mParentWidth;
	}

	public void setParentWidth(int mParentWidth) {
		this.mParentWidth = mParentWidth;
		isMeasureChanged = true;
	}

	public int getParentHeight() {
		return mParentHeight;
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
