# SwitchButton
Android仿IOS的Segmented Control

如何使用：

	<com.lzc.switchbutton.widget.SwitchButton
        android:id="@+id/switchButton"
        android:layout_width="match_parent"
        android:layout_height="45dp"
        android:layout_margin="15dp"
        android:textColor="@color/switch_textcolor_selector"
        app:sw_checkedColor="@color/green"
        app:sw_strokeColor="@color/green"
        app:sw_switchCount="4"
        app:sw_textArray="@array/test" />

 注：layout_width 只能固定宽度或者使用 match_parent
		
属性说明：
		
		<attr name="android:textColor" />                               //文本颜色，可以是Selector
        <attr name="android:textSize" />                                //文本大小
        <attr name="sw_textArray" format="string" />						//文本数组（array）
        <attr name="sw_switchCount" format="integer" />                    //Button数目
        <attr name="sw_ThemeStyle" format="reference" />                  //Button 样式
        <attr name="sw_CornerRadius" format="dimension" />                //圆角弧度
        <attr name="sw_checkedColor" format="color|reference" />          //选中的颜色
        <attr name="sw_unCheckedColor" format="color|reference" />        //未选中的颜色
        <attr name="sw_strokeColor" format="color|reference" />           //边框颜色
        <attr name="sw_strokeWidth" format="dimension" />                 //边框粗细

 ![image](https://github.com/mvpleung/SwitchButton/blob/master/art/switchbutton.gif)
