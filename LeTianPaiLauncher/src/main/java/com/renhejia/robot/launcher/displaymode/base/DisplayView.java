package com.renhejia.robot.launcher.displaymode.base;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;



/**
 * 显示模式
 */
public class DisplayView extends ViewPager {

    public DisplayView(@NonNull Context context) {
        super(context);
    }

    public DisplayView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


}
