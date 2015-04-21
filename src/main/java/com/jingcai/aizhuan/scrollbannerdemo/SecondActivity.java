package com.jingcai.aizhuan.scrollbannerdemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by cfy on 2015/4/21.
 */
public class SecondActivity extends Activity {

    private ViewPager mViewPager;
    private LayoutInflater mInflater;
    private ImageView[] mIndicatorImgs = new ImageView[7];

    String[] urls = {

            "http://a.hiphotos.baidu.com/image/pic/item/3bf33a87e950352ad6465dad5143fbf2b2118b6b.jpg",
            "http://a.hiphotos.baidu.com/image/pic/item/c8177f3e6709c93d002077529d3df8dcd0005440.jpg",
            "http://f.hiphotos.baidu.com/image/pic/item/7aec54e736d12f2ecc3d90f84dc2d56285356869.jpg",
            "http://e.hiphotos.baidu.com/image/pic/item/9c16fdfaaf51f3de308a87fc96eef01f3a297969.jpg",
            "http://d.hiphotos.baidu.com/image/pic/item/f31fbe096b63f624b88f7e8e8544ebf81b4ca369.jpg",
            "http://h.hiphotos.baidu.com/image/pic/item/11385343fbf2b2117c2dc3c3c88065380cd78e38.jpg",
            "http://c.hiphotos.baidu.com/image/pic/item/3801213fb80e7bec5ed8456c2d2eb9389b506b38.jpg"

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        List<View> list = new ArrayList<>();
        mInflater = LayoutInflater.from(this);

        /*
        * 创建多个item 每一条都是一个item
        * 从服务器获取完数据(文章标题、url地址)后，在设置适配器
        * */
        View item = null;
         for(int i=0; i<urls.length; i++){
            item = mInflater.inflate(R.layout.view_pager_item,null);
             ((TextView)item.findViewById(R.id.text_view)).setText("第" + i +"个" );
             list.add(item);
        }

        //创建适配器，把组装完的组件传递进去
        MyAdapter myAdapter = new MyAdapter(this,list);
        mViewPager.setAdapter(myAdapter);

        //绑定监听,Viewpager的页面监听
        mViewPager.setOnPageChangeListener(new MyListener());

        //初始化指示器(下方给出代码)
        //将第一个点点选中，其他的不选中
        initIndicator();

        //设置定时器,延时两秒后，每两秒进行一次滚动
        Timer timer = new Timer();
        timer.schedule( new TimerTask(){

            @Override
            public void run() {
                handler.sendEmptyMessage(0x123);
            }
        },2000,2000);
     }

    Handler handler = new Handler(){
        private boolean mIsPause = false;
        @Override
        public void handleMessage(Message msg) {

            if(msg.what == 0x123) {
                //滚动
                if (!mIsPause) {
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                }
            }else if(msg.what == 0x234){
                //停止滚动
                mIsPause = true;
            }else if(msg.what == 0x345){
                //开始滚动
                mIsPause = false;
            }
        }
    };

    /**
     * 初始化引导图标
     * 动态创建多个小圆点，然后组装到线性布局里
     */
    private void initIndicator() {
        ImageView imageView;
        View v = findViewById(R.id.indicator);
        for(int i = 0 ; i< mIndicatorImgs.length; i++){
            imageView = new ImageView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(10,10);
            layoutParams.setMargins(7,10,7,10);
            imageView.setLayoutParams(layoutParams);
            mIndicatorImgs[i] = imageView;
            if(i==0){
                mIndicatorImgs[i].setBackgroundResource(R.drawable.indicator_selected);
            }else{
                mIndicatorImgs[i].setBackgroundResource(R.drawable.indicator);
            }
            ((ViewGroup)v).addView(mIndicatorImgs[i]);
        }
    }

    private class MyListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            int cur_position = position%urls.length;
            for(int i=0; i<mIndicatorImgs.length; i++){
                mIndicatorImgs[i].setBackgroundResource(R.drawable.indicator);
            }
            mIndicatorImgs[cur_position].setBackgroundResource(R.drawable.indicator_selected);

        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if(ViewPager.SCROLL_STATE_DRAGGING == state){
                //用户碰到了
                handler.sendEmptyMessage(0x234);
            }else{
                //用户离开了
                handler.sendEmptyMessage(0x345);
            }
        }
    }


    private class MyAdapter extends PagerAdapter{

        //这个列表中的View是实例化后的view_pager_item,
        // 只加载了文字，尚未加载图片，因为图片采用异步的方式加载
        private List<View> mList;
        //Volley的请求队列
        private RequestQueue mRequestQueue;
        //上下文，这个建立请求必须
        private Context mContext;


        public MyAdapter(Context context,List<View> list){
            mContext = context;
            //建立requestQueue
            mRequestQueue = Volley.newRequestQueue(context);
            mList = list;
        }

        @Override
        public Object instantiateItem(final ViewGroup container, final int position) {
            //取余才能正确获取到urls和mList
            final int cur_position = position%urls.length;
            //实例化ImageLoader，其中CustomeImageCache为自定义的Cache，下面会给出
            ImageLoader loader = new ImageLoader(mRequestQueue,new CustomeImageCache());
            //发送请求，如果有缓存，则直接返回，如果没有则发送网络请求加载图片
            ImageLoader.ImageContainer imageContainer = loader.get(urls[cur_position], new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    //isImmediate 为 true 可以设置默认图片
                    View view = mList.get(cur_position);
                    ImageView image = (ImageView) view.findViewById(R.id.image);
                    image.setImageBitmap(response.getBitmap());
                    //先移除，在添加，不然就会报出 已存在父容器的异常
                    container.removeView(mList.get(cur_position));
                    container.addView(mList.get(cur_position));
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    //这里可以加载错误图片
                }
            });
            //如果有缓存
            if(imageContainer.getBitmap() != null){
                View view = mList.get(cur_position);
                ImageView image = (ImageView) view.findViewById(R.id.image);
                image.setImageBitmap(imageContainer.getBitmap());
                container.removeView(mList.get(cur_position));
                container.addView(mList.get(cur_position));
            }
            return mList.get(cur_position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            final int cur_position = position%urls.length;
            container.removeView(mList.get(cur_position));
        }

        @Override
        public int getCount() {
            //让ViewPager无限滚动
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }
    }

    static class CustomeImageCache implements ImageLoader.ImageCache{

        LruCache<String,Bitmap> mCache;
        public CustomeImageCache(){
            int max =1024 * 1024 * 1024;
            mCache = new LruCache<>(max);
        }

        @Override
        public Bitmap getBitmap(String url) {
            return mCache.get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            mCache.put(url,bitmap);
        }
    }
}
