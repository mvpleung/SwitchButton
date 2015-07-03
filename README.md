# SwitchButton
Android仿IOS的SwitchButton

如何使用：

	<com.lzc.switchbutton.widget.SwitchButton
        android:id="@+id/switchButton"
        android:layout_width="match_parent"
        android:layout_height="45dp"
        android:layout_margin="15dp"
        android:textColor="@color/switch_textcolor_selector"
        app:shapeCenter="@drawable/switch_middle_selector"
        app:shapeLeft="@drawable/switch_left_corner_selector"
        app:shapeRight="@drawable/switch_right_corner_selector"
        app:switchCount="4"
        app:textArray="@array/test" />

属性说明：
		
		<attr name="android:textColor" />                               //文本颜色，可以是Selector
        <attr name="android:textSize" />                                //文本大小
        <attr name="textArray" format="string" />						//文本数组（array）
        <attr name="shapeCenter" format="color|reference" />            //中间Button的背景样式，可以是Selector
        <attr name="shapeLeft" format="color|reference" />              //左边或者上边（垂直绘制时）Button的背景样式，可以是Selector
        <attr name="shapeRight" format="color|reference" />             //右边或者下边（垂直绘制时）Button的背景样式，可以是Selector
        <attr name="switchCount" format="integer" />                    //Button数目
        <attr name="switchStyle" format="reference" />                  //Button 样式

 ![image](https://github.com/mvpleung/SwitchButton/blob/master/SwitchButton/switchbutton.gif)
