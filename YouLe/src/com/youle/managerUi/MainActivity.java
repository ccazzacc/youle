package com.youle.managerUi;

import cn.sharesdk.framework.AbstractWeibo;

import com.youle.R;
import com.youle.managerData.SharedPref.SharedPref;
import com.youle.util.OtherUtil;
import com.youle.view.SlidingMenu;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;


public class MainActivity extends SlidingActivity {

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
        getLocation();
    }
    
    @Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		Log.i("test", "MainActivity onRestart");
	}

    private void setViewAttrs() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.main_center_fragment);

        this.setLeftContentView(R.layout.main_left_fragment);
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

    public void getLocation() {
        SharedPref sharedPref=new SharedPref(MainActivity.this);
        if(OtherUtil.isNullOrEmpty(sharedPref.getCity())){
            startActivity(new Intent(MainActivity.this,ChooseCity.class));
//            finish();
        }
    }
}
