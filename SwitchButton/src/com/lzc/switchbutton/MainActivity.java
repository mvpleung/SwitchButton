package com.lzc.switchbutton;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.lzc.switchbutton.widget.SwitchButton;
import com.lzc.switchbutton.widget.SwitchButton.OnChangeListener;

public class MainActivity extends Activity {

	SwitchButton mSwitchButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mSwitchButton = (SwitchButton) findViewById(R.id.switchButton);
		mSwitchButton.setOnChangeListener(new OnChangeListener() {

			@Override
			public void onChange(int position) {
				// TODO Auto-generated method stub
				Toast.makeText(MainActivity.this, "点击了第" + position + "项目", Toast.LENGTH_SHORT).show();
			}
		});
	}

	public void add(View view) {
		mSwitchButton.setSwitchCount(mSwitchButton.getSwitchCount() + 1);
		String[] mTexts = new String[mSwitchButton.getSwitchCount()];
		for (int i = 0; i < mSwitchButton.getSwitchCount(); i++) {
			mTexts[i] = "" + i;
		}
		/** 根据实际需要赋值，文本个数必须与SwitchButton个数相同 */
		mSwitchButton.setTextArray(mTexts);
		mSwitchButton.initialize();
	}

	public void reduction(View view) {
		mSwitchButton.setSwitchCount(mSwitchButton.getSwitchCount() - 1);
		String[] mTexts = new String[mSwitchButton.getSwitchCount()];
		for (int i = 0; i < mSwitchButton.getSwitchCount(); i++) {
			mTexts[i] = "" + i;
		}
		/** 根据实际需要赋值，文本个数必须与SwitchButton个数相同 */
		mSwitchButton.setTextArray(mTexts);
		mSwitchButton.initialize();
	}
}
