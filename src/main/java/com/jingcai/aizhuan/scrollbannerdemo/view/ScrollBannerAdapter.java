package com.jingcai.aizhuan.scrollbannerdemo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.util.List;

/**
 * Created by cfy on 2015/4/20.
 */
public class ScrollBannerAdapter extends PagerAdapter {

    private Context mContext;
    private List<ImageBean> mImages;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private LayoutInflater inflater;
    private ImageView[] mImagesViews;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x123) {
                mImagesViews[msg.arg1] = new ImageView(mContext);
                mImagesViews[msg.arg1].setImageBitmap(((ImageLoader.ImageContainer)msg.obj).getBitmap());
                notifyDataSetChanged();
            }
        }
    };

    public ScrollBannerAdapter(Context context, List<ImageBean> images) {
        mContext = context;
        mImages = images;
        mRequestQueue = Volley.newRequestQueue(mContext);
        mImageLoader = new ImageLoader(mRequestQueue, new BitmapCache());
        inflater = LayoutInflater.from(context);
        mImagesViews = new ImageView[images.size()];

        for (int i = 0; i < images.size(); i++) {
            final int j = i;
            mImageLoader.get(images.get(i).getImageUrl(), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {

                    Message msg = new Message();
                    msg.what = 0x123;
                    msg.obj = response;
                    msg.arg1 = j;
                    handler.sendMessage(msg);
                    Log.i("Volley", "paint " + j + " has been loaded");
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        }
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {

//        ImageBean bean = mImages.get(position);   //获取bean
//        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.image_layout,null);  //实例布局
//        final ImageView imageView = (ImageView) v.findViewById(R.id.id_image);  //找到imageView
//        imageView.setImageResource(R.drawable.default_image);
//        mImageLoader.get(bean.getImageUrl(),new ImageLoader.ImageListener() {
//            @Override
//            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
//                mImagesViews[position] = imageView;
//                imageView.setImageBitmap(response.getBitmap());
//
//            }
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        });  //加载图片
//        container.addView(v);  //将布局加入ViewPager
        container.addView(mImagesViews[position]);
        return mImagesViews[position];
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mImagesViews[position]);
    }

    @Override
    public int getCount() {
        return mImages.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public class BitmapCache implements ImageLoader.ImageCache {

        private LruCache<String, Bitmap> mLruCache;

        public BitmapCache() {
            int maxSize = 10 * 1024 * 1024;
            mLruCache = new LruCache<String, Bitmap>(maxSize) {
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    return value.getRowBytes() * value.getHeight();
                }
            };
        }

        @Override
        public Bitmap getBitmap(String url) {

            return mLruCache.get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            mLruCache.put(url, bitmap);
        }
    }
}
