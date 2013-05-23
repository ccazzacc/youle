package com.youle.managerUi;

import com.youle.R;
import com.youle.view.SlidingMenu;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;


public class MeActivity extends SlidingMeActivity {

    private SlidingMenu mSlider;
    private int mWidthPixels;
    
    /*
     * slide offset for rightView and leftView in pixels
     */
    private final static int SLIDE_OFFEST = 80;//60
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mSlider = this.getSlidingMenu();

        setViewAttrs();
    }

    private void setViewAttrs() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.me_center_fragment);

        this.setLeftContentView(R.layout.me_left_fragment);
        mSlider.setLeftOffset(SLIDE_OFFEST);
        mSlider.setLeftShadowDrawable(R.drawable.left_shadow);
        mSlider.setLeftMenuEnable(true);

//        this.setRightContentView(R.layout.main_right_fragment);
//        mSlider.setRightOffset(SLIDE_OFFEST);
//        mSlider.setRightShadowDrawable(R.drawable.right_shadow);
        mSlider.setRightMenuEnable(false);

        mSlider.setShadowWidth(32);
        mSlider.setWindowWidth(getWindowPix(), SLIDE_OFFEST);
    }
    
    /**
     * 得到屏幕宽度
     * @return
     */
    private int  getWindowPix() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        int widthPixels = dm.widthPixels;
//        int heightPixels = dm.heightPixels;
//        float density = dm.density;
        return mWidthPixels;
    }
}
