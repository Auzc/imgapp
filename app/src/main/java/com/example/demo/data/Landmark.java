package com.example.demo.data;

import java.math.BigDecimal;
import android.os.Parcel;
import android.os.Parcelable;

public class Landmark implements Parcelable {
    private String landmarkId;
    private Float latitude;
    private Float longitude;
    private Integer idCount;
    private String category;
    private String supercategory;
    private String hierarchicalLabel;
    private String naturalOrHumanMade;
    private String instanceOf;
    private String location;
    private String operator;
    private String inception;

    public String getLandmarkId() {
        return landmarkId;
    }

    public void setLandmarkId(String landmarkId) {
        this.landmarkId = landmarkId;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public Integer getIdCount() {
        return idCount;
    }

    public void setIdCount(Integer idCount) {
        this.idCount = idCount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSupercategory() {
        return supercategory;
    }

    public void setSupercategory(String supercategory) {
        this.supercategory = supercategory;
    }

    public String getHierarchicalLabel() {
        return hierarchicalLabel;
    }

    public void setHierarchicalLabel(String hierarchicalLabel) {
        this.hierarchicalLabel = hierarchicalLabel;
    }

    public String getNaturalOrHumanMade() {
        return naturalOrHumanMade;
    }

    public void setNaturalOrHumanMade(String naturalOrHumanMade) {
        this.naturalOrHumanMade = naturalOrHumanMade;
    }

    public String getInstanceOf() {
        return instanceOf;
    }

    public void setInstanceOf(String instanceOf) {
        this.instanceOf = instanceOf;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getInception() {
        return inception;
    }

    public void setInception(String inception) {
        this.inception = inception;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    private String imageUrl;

    // Constructors
    public Landmark() {
    }

    public Landmark(String landmarkId, Float latitude, Float longitude, Integer idCount, String category, String supercategory, String hierarchicalLabel, String naturalOrHumanMade, String instanceOf, String location, String operator, String inception, String imageUrl) {
        this.landmarkId = landmarkId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.idCount = idCount;
        this.category = category;
        this.supercategory = supercategory;
        this.hierarchicalLabel = hierarchicalLabel;
        this.naturalOrHumanMade = naturalOrHumanMade;
        this.instanceOf = instanceOf;
        this.location = location;
        this.operator = operator;
        this.inception = inception;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters (omitted for brevity)

    // Parcelable implementation
    protected Landmark(Parcel in) {
        landmarkId = in.readString();
        latitude = new Float(in.readString());
        longitude = new Float(in.readString());
        idCount = in.readInt();
        category = in.readString();
        supercategory = in.readString();
        hierarchicalLabel = in.readString();
        naturalOrHumanMade = in.readString();
        instanceOf = in.readString();
        location = in.readString();
        operator = in.readString();
        inception = in.readString();
        imageUrl = in.readString();
    }

    public static final Creator<Landmark> CREATOR = new Creator<Landmark>() {
        @Override
        public Landmark createFromParcel(Parcel in) {
            return new Landmark(in);
        }

        @Override
        public Landmark[] newArray(int size) {
            return new Landmark[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(landmarkId);
        parcel.writeString(latitude.toString());
        parcel.writeString(longitude.toString());
        parcel.writeInt(idCount);
        parcel.writeString(category);
        parcel.writeString(supercategory);
        parcel.writeString(hierarchicalLabel);
        parcel.writeString(naturalOrHumanMade);
        parcel.writeString(instanceOf);
        parcel.writeString(location);
        parcel.writeString(operator);
        parcel.writeString(inception);
        parcel.writeString(imageUrl);
    }
}
