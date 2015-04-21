package com.jingcai.aizhuan.scrollbannerdemo;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;

import com.jingcai.aizhuan.scrollbannerdemo.view.ImageBean;
import com.jingcai.aizhuan.scrollbannerdemo.view.ScrollBannerAdapter;

import java.util.ArrayList;
import java.util.List;


public class Main extends ActionBarActivity {

    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewPager = (ViewPager) findViewById(R.id.vp_main);
        List<ImageBean> beans = new ArrayList<>();
        beans.add(new ImageBean("呵呵","http://img3.imgtn.bdimg.com/it/u=1147849988,399167347&fm=21&gp=0.jpg"));
        beans.add(new ImageBean("呵呵","http://img3.imgtn.bdimg.com/it/u=3979340985,1400351320&fm=21&gp=0.jpg"));
        beans.add(new ImageBean("呵呵","http://img1.imgtn.bdimg.com/it/u=734872794,3773294519&fm=21&gp=0.jpg"));
        beans.add(new ImageBean("呵呵","http://img4.imgtn.bdimg.com/it/u=2361228013,439763247&fm=21&gp=0.jpg"));
        mViewPager.setAdapter(new ScrollBannerAdapter(this,beans));

    }

}
