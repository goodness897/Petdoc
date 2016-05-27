package com.petdoc;

/**
 * Created by STU on 2016-05-27.
 */
public class ReviewItem {

    private int doc_id;
    private String user_id;
    private String content;
    private String dateTime;

    public ReviewItem(int doc_id, String user_id, String content, String dateTime) {
        this.content = content;
        this.dateTime = dateTime;
        this.doc_id = doc_id;
        this.user_id = user_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public int getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(int doc_id) {
        this.doc_id = doc_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
