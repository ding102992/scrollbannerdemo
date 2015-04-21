package com.jingcai.aizhuan.scrollbannerdemo.view;

/**
 * Created by cfy on 2015/4/20.
 */
public class ImageBean {
    private String imageName;
    private String imageUrl;

    public ImageBean() {
    }

    public ImageBean(String imageName, String imageUrl) {
        this.imageName = imageName;
        this.imageUrl = imageUrl;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
