package com.example.demo.card;

public class Card {
    public Card(String title,String author,String id,String img_url,int width,int height){
        this.title=title;
        this.img_url=img_url;
        this.width=width;
        this.height=height;
        this.id=id;
        this.author=author;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    private String title;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    private String author;
    private String img_url;
    private int width;
    private int height;


    // 构造方法和getter/setter方法省略
}
