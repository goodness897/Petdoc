package com.petdoc;

/**
 * Created by mu on 2016-05-30.
 */
public class PetcastItem {

    private int imageUrl;
    private String title;
    private String content;

    public PetcastItem(int imageUrl, String title, String content) {
        this.content = content;
        this.imageUrl = imageUrl;
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(int imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
