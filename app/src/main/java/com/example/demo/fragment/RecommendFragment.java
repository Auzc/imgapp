package com.example.demo.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.demo.R;
import com.example.demo.activity.CardDetailActivity;
import com.example.demo.activity.SQLTestActivity;
import com.example.demo.data.Card;
import com.example.demo.data.ImageData;
import com.example.demo.data.Landmark;
import com.google.android.material.tabs.TabLayout;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.sdk.comps.vis.VisualLayer;
import com.tencent.map.sdk.comps.vis.VisualLayerOptions;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.MapView;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.TencentMapInitializer;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.Marker;
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions;
import com.tencent.tencentmap.mapsdk.maps.model.OverlayLevel;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class RecommendFragment extends Fragment {
    private ImageView search_btn;
    private static String driver = "com.mysql.cj.jdbc.Driver";
    private static String url = "jdbc:mysql://rm-2ze740g8q9yaf3v06co.mysql.rds.aliyuncs.com:3296/mydesign"
            + "?useUnicode=true&characterEncoding=utf8";    // mysql 数据库连接 url
    private static String user = "au";    // 用户名
    private static String password = "Jzc4211315"; // 密码
    private ImageView img;
    private TextView card_author,title,featuresTagTextView1,featuresTagTextView2,featuresTagTextView3,ResNetFeaturesTagTextView1,ResNetFeaturesTagTextView2,ResNetFeaturesTagTextView3,tagTextView1,tagTextView2,tagTextView3,colorTextView1,colorTextView2,colorTextView3;
    private static List<Card> mylist = new ArrayList<>();
    private TextView location;

    private Stack<Card> cardStack = new Stack<>();
    private int currentOffset = new Random().nextInt(2000);
    DecimalFormat decimalFormat = new DecimalFormat("0.00%");
    private ImageData imageData;
    private String cardId,landmarkid;
    private Landmark landmark;
    private MapView mapView;
    private TencentMap tencentMap;
    private TencentLocationManager mLocationManager;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    public  static TencentLocation mylocation = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend, container, false);
        location = view.findViewById(R.id.location);
        // Features Tag TextViews
        featuresTagTextView1 = view.findViewById(R.id.featuresTagTextView1);
        featuresTagTextView2 = view.findViewById(R.id.featuresTagTextView2);
        featuresTagTextView3 = view.findViewById(R.id.featuresTagTextView3);

// ResNet Features Tag TextViews
        ResNetFeaturesTagTextView1 = view.findViewById(R.id.ResNetFeaturesTagTextView1);
        ResNetFeaturesTagTextView2 = view.findViewById(R.id.ResNetFeaturesTagTextView2);
        ResNetFeaturesTagTextView3 = view.findViewById(R.id.ResNetFeaturesTagTextView3);

// Tag TextViews
        tagTextView1 = view.findViewById(R.id.tagTextView1);
        tagTextView2 = view.findViewById(R.id.tagTextView2);
        tagTextView3 = view.findViewById(R.id.tagTextView3);

// Color TextViews
        colorTextView1 = view.findViewById(R.id.colorTextView1);
        colorTextView2 = view.findViewById(R.id.colorTextView2);
        colorTextView3 = view.findViewById(R.id.colorTextView3);
        search_btn = view.findViewById(R.id.search_btn);

        TextView satellite1 = view.findViewById(R.id.satellite1);
        ImageButton satellite = view.findViewById(R.id.satellite);
        satellite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (satellite1.getText().equals("切换卫星图")){
                    tencentMap.setMapType(TencentMap.MAP_TYPE_SATELLITE);
                    satellite1.setText("取消切换");
                }else{
                    tencentMap.setMapType(TencentMap.MAP_TYPE_NORMAL);
                    satellite1.setText("切换卫星图");
                }

            }
        });
        satellite1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (satellite1.getText().equals("切换卫星图")){
                    tencentMap.setMapType(TencentMap.MAP_TYPE_SATELLITE);
                    satellite1.setText("取消切换");
                }else{
                    tencentMap.setMapType(TencentMap.MAP_TYPE_NORMAL);
                    satellite1.setText("切换卫星图");
                }
            }
        });
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), SQLTestActivity.class);
                startActivity(intent);
            }
        });

        if (mylist.isEmpty()) {
            new RecommendFragment.InfoAsyncTask().execute();
        }
        img = view.findViewById(R.id.img);



        TencentMapInitializer.setAgreePrivacy(true);
        TencentLocationManager.setUserAgreePrivacy(true);
        mapView = view.findViewById(R.id.mapView);
        tencentMap = mapView.getMap();
        VisualLayer mVisualLayer = tencentMap.addVisualLayer(new VisualLayerOptions("03097e5d0ff8") //xxxxxxxx为官网配置生成对应图层的图层id
                .newBuilder()
                .setAlpha(1)
                .setLevel(OverlayLevel.OverlayLevelAboveBuildings)
                .setZIndex(10)
                .setTimeInterval(15)
                .build());




        return view;
    }

    public List<Card> getData(int offset, int limit) {
        List<Card> dataInfoList = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT * FROM Images LIMIT ? OFFSET ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, limit);
            statement.setInt(2, offset);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String url = resultSet.getString("url");
                String landmarkId = resultSet.getString("landmark_id");
                int width = resultSet.getInt("width");
                int height = resultSet.getInt("height");
                String author = resultSet.getString("author");
                String title = resultSet.getString("title");
                if (title.length() > 5) {
                    title = title.substring(5);
                }
                if (title.length() > 4) {
                    title = title.substring(0, title.length() - 4);
                }
                System.out.println("ID: " + id + ", URL: " + url + ", Landmark ID: " + landmarkId + ", Width: " + width + ", Height: " + height + ", Author: " + author + ", Title: " + title);

                Card sizeInfo = new Card(title,author,id,url,width,height,landmarkId);
                dataInfoList.add(sizeInfo);
            }
        } catch (Exception e) {
            Log.e("getData", "Error getData", e);
        }
        return dataInfoList;
    }
    @SuppressLint("StaticFieldLeak")
    public class InfoAsyncTask extends AsyncTask<Void, Void, List<Card>> {
        @Override
        protected List<Card> doInBackground(Void... voids) {
            mylist.addAll(getData(currentOffset, 10));
            currentOffset += 10; // Update the offset for the next query


            return null;
        }

        @Override
        protected void onPostExecute(List<Card> stackItems) {
            updateDataAndRefresh(stackItems);

        }
    }
    private void updateDataAndRefresh(List<Card> sizeInfoList) {
        Card card = mylist.get(0);
        Glide.with(this)
                .load(card.getImg_url())
                .error(R.drawable.rounded_box) // 加载失败显示的图像
                .into(img);
        cardId=mylist.get(0).getId();
        landmarkid=mylist.get(0).getLandmark_id();
        DatabaseTask2 databaseTask2 = new DatabaseTask2();
        databaseTask2.execute();
        DatabaseTask3 databaseTask3 = new DatabaseTask3();
        databaseTask3.execute();

    }

    private class DatabaseTask2 extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try (Connection connection = DriverManager.getConnection(url, user, password)) {
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM image_data WHERE image_id = ?");
                statement.setString(1, cardId);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String imageId = resultSet.getString("image_id");
                    String typeId1Inception = resultSet.getString("type_id1_inception");
                    String type1Inception = resultSet.getString("type1_inception");
                    float confidence1Inception = resultSet.getFloat("confidence1_inception");
                    String typeId2Inception = resultSet.getString("type_id2_inception");
                    String type2Inception = resultSet.getString("type2_inception");
                    float confidence2Inception = resultSet.getFloat("confidence2_inception");
                    String typeId3Inception = resultSet.getString("type_id3_inception");
                    String type3Inception = resultSet.getString("type3_inception");
                    float confidence3Inception = resultSet.getFloat("confidence3_inception");
                    String typeId1Resnet = resultSet.getString("type_id1_resnet");
                    String type1Resnet = resultSet.getString("type1_resnet");
                    float confidence1Resnet = resultSet.getFloat("confidence1_resnet");
                    String typeId2Resnet = resultSet.getString("type_id2_resnet");
                    String type2Resnet = resultSet.getString("type2_resnet");
                    float confidence2Resnet = resultSet.getFloat("confidence2_resnet");
                    String typeId3Resnet = resultSet.getString("type_id3_resnet");
                    String type3Resnet = resultSet.getString("type3_resnet");
                    float confidence3Resnet = resultSet.getFloat("confidence3_resnet");
                    String typeId4Resnet = resultSet.getString("type_id4_resnet");
                    String type4Resnet = resultSet.getString("type4_resnet");
                    float confidence4Resnet = resultSet.getFloat("confidence4_resnet");
                    String typeId5Resnet = resultSet.getString("type_id5_resnet");
                    String type5Resnet = resultSet.getString("type5_resnet");
                    float confidence5Resnet = resultSet.getFloat("confidence5_resnet");
                    String dominantColor1 = resultSet.getString("dominant_color1");
                    String dominantColor2 = resultSet.getString("dominant_color2");
                    String dominantColor3 = resultSet.getString("dominant_color3");
                    // Create an ImageData object with retrieved data
                    imageData = new ImageData(imageId, typeId1Inception, type1Inception, confidence1Inception,
                            typeId2Inception, type2Inception, confidence2Inception,
                            typeId3Inception, type3Inception, confidence3Inception,
                            typeId1Resnet, type1Resnet, confidence1Resnet,
                            typeId2Resnet, type2Resnet, confidence2Resnet,
                            typeId3Resnet, type3Resnet, confidence3Resnet,
                            typeId4Resnet, type4Resnet, confidence4Resnet,
                            typeId5Resnet, type5Resnet, confidence5Resnet,
                            dominantColor1, dominantColor2, dominantColor3);
                }
            } catch (Exception e) {
                Log.e("getData", "Error getData", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (imageData != null) {
                updateUI3(imageData);
                //Toast.makeText(CardDetailActivity.this, cardId, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Error occurred", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private class DatabaseTask3 extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try (Connection connection = DriverManager.getConnection(url, user, password)) {
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM landmark WHERE landmark_id = ?");
                statement.setString(1, landmarkid);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    String landmarkId = resultSet.getString("landmark_id");
                    Float latitude = resultSet.getFloat("Latitude");
                    Float longitude = resultSet.getFloat("Longitude");
                    Integer idCount = resultSet.getInt("id_count");
                    String category = resultSet.getString("category");
                    String supercategory = resultSet.getString("supercategory");
                    String hierarchicalLabel = resultSet.getString("hierarchical_label");
                    String naturalOrHumanMade = resultSet.getString("natural_or_human_made");
                    String instanceOf = resultSet.getString("Instance_of");
                    String location = resultSet.getString("Location");
                    String operator = resultSet.getString("Operator");
                    String inception = resultSet.getString("Inception");
                    String imageUrl = resultSet.getString("Image_URL");

                    // Create a Landmark object with retrieved data
                    landmark = new Landmark(landmarkId, latitude, longitude, idCount, category, supercategory,
                            hierarchicalLabel, naturalOrHumanMade, instanceOf, location, operator, inception, imageUrl);
                }
            } catch (Exception e) {
                Log.e("getData", "Error getData", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (landmark != null) {
                updateUI4(landmark);
                //Toast.makeText(CardDetailActivity.this, card.getLandmark_id(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Error occurred", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateUI3(ImageData imageData1) {

        featuresTagTextView1.setText(imageData1.getType1Inception()+" | "+decimalFormat.format(imageData1.getConfidence1Inception()));
        featuresTagTextView2.setText(imageData1.getType2Inception()+" | "+decimalFormat.format(imageData1.getConfidence2Inception()));
        featuresTagTextView3.setText(imageData1.getType3Inception()+" | "+decimalFormat.format(imageData1.getConfidence3Inception()));
        ResNetFeaturesTagTextView1.setText(imageData1.getType1Resnet()+" | "+decimalFormat.format(imageData1.getConfidence1Resnet()));
        ResNetFeaturesTagTextView2.setText(imageData1.getType2Resnet()+" | "+decimalFormat.format(imageData1.getConfidence2Resnet()));
        ResNetFeaturesTagTextView3.setText(imageData1.getType3Resnet()+" | "+decimalFormat.format(imageData1.getConfidence3Resnet()));
        tagTextView1.setText(imageData1.getDominantColor1());
        tagTextView2.setText(imageData1.getDominantColor2());
        tagTextView3.setText(imageData1.getDominantColor3());
        String dominantColorHex1 = imageData1.getDominantColor1();
        int color1 = Color.parseColor(dominantColorHex1);
        colorTextView1.setBackgroundColor(color1);
        String dominantColorHex2 = imageData1.getDominantColor2();
        int color2 = Color.parseColor(dominantColorHex2);
        colorTextView1.setBackgroundColor(color2);
        String dominantColorHex3 = imageData1.getDominantColor3();
        int color3 = Color.parseColor(dominantColorHex3);
        colorTextView1.setBackgroundColor(color3);

    }

    private void updateUI4(Landmark landmark) {
        location.setText(landmark.getLocation());
        tencentMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(landmark.getLatitude(), landmark.getLongitude()), 4));
        LatLng position = new LatLng(landmark.getLatitude(), landmark.getLongitude());
        Marker marker = tencentMap.addMarker(new MarkerOptions(position));
    }
}