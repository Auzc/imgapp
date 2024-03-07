package com.example.demo.data;

public class MyColor {
    private String chineseName;
    private String englishName;
    private String rgb;

    public MyColor(String chineseName, String englishName, String rgb) {
        this.chineseName = chineseName;
        this.englishName = englishName;
        this.rgb = rgb;
    }

    public String getChineseName() {
        return chineseName;
    }

    public void setChineseName(String chineseName) {
        this.chineseName = chineseName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getRgb() {
        return rgb;
    }

    public void setRgb(String rgb) {
        this.rgb = rgb;
    }

    @Override
    public String toString() {
        return "MyColor{" +
                "chineseName='" + chineseName + '\'' +
                ", englishName='" + englishName + '\'' +
                ", rgb='" + rgb + '\'' +
                '}';
    }
}
