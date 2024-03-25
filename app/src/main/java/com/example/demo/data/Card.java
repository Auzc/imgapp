package com.example.demo.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Card implements Parcelable {
    private String title;
    private String id;
    private String author;
    private String img_url;
    private int width;
    private int height;

    public String getLandmark_id() {
        return landmark_id;
    }

    public void setLandmark_id(String landmark_id) {
        this.landmark_id = landmark_id;
    }

    private String landmark_id;
    public Card(String title, String author, String id, String img_url, int width, int height, String landmark_id) {
        this.title = title;
        this.author = author;
        this.id = id;
        this.img_url = img_url;
        this.width = width;
        this.height = height;
        this.landmark_id = landmark_id;
    }



    protected Card(Parcel in) {
        title = in.readString();
        author = in.readString();
        id = in.readString();
        img_url = in.readString();
        width = in.readInt();
        height = in.readInt();
        landmark_id = in.readString();
    }

    public static final Creator<Card> CREATOR = new Creator<Card>() {
        @Override
        public Card createFromParcel(Parcel in) {
            return new Card(in);
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(author);
        dest.writeString(id);
        dest.writeString(img_url);
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeString(landmark_id);
    }
}
