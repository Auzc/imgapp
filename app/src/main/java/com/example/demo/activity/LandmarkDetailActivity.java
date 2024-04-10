package com.example.demo.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.example.demo.R;
import com.example.demo.adapter.SimilarityStaggeredGridAdapter;
import com.example.demo.data.Card;
import com.example.demo.data.CardSimilarity;
import com.example.demo.data.Landmark;
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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LandmarkDetailActivity extends AppCompatActivity {
    private static final String url = "jdbc:mysql://rm-2ze740g8q9yaf3v06co.mysql.rds.aliyuncs.com:3296/mydesign" +
            "?useUnicode=true&characterEncoding=utf8&useSSL=false";    // mysql 数据库连接 url
    private static String user = "au";    // 用户名
    private static String password = "Jzc4211315"; // 密码
    private ImageView imageView,wiji;
    private TextView text,title,tip;
    private Landmark landmark;
    MapView mapView;
    TencentMap tencentMap;

    DecimalFormat decimalFormat = new DecimalFormat("0.00%");
    private RecyclerView mRecyclerView;
    private SimilarityStaggeredGridAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landmark_detail);
        imageView = findViewById(R.id.imageView);
        wiji=findViewById(R.id.wiji);
        text = findViewById(R.id.text);
        title = findViewById(R.id.title);
        tip = findViewById(R.id.tip);
        mRecyclerView = findViewById(R.id.recyclerView);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new SimilarityStaggeredGridAdapter(this, 20);
        mRecyclerView.setAdapter(mAdapter);
        // 获取传入的Intent
        Intent intent = getIntent();

        // 检查Intent和附加的数据是否为空
        if (intent != null && intent.hasExtra("landmark")) {
            // 从Intent中获取Landmark对象
            landmark = intent.getParcelableExtra("landmark");

            // 显示地标信息
            displayLandmarkInfo(landmark);
        }
        wiji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LandmarkDetailActivity.this, MyWebViewActivity.class);
                // 在这里可以传递卡片的信息到详情界面

                intent.putExtra("url", landmark.getCategory());
                startActivity(intent);
            }
        });
        TencentMapInitializer.setAgreePrivacy(true);
        TencentLocationManager.setUserAgreePrivacy(true);
        mapView = findViewById(R.id.mapView);
        tencentMap = mapView.getMap();

        Marker marker = tencentMap.addMarker(new MarkerOptions(new LatLng(landmark.getLatitude(), landmark.getLongitude())));
        double latitude = landmark.getLatitude();
        double longitude = landmark.getLongitude();
        TextView satellite1 = findViewById(R.id.satellite1);
        if (latitude >= 18.0 && latitude <= 54.0 && longitude >= 73.0 && longitude <= 135.0) {
            // 经纬度在中国范围内
            tencentMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 18));
            tencentMap.setMapType(TencentMap.MAP_TYPE_SATELLITE);
            satellite1.setText("取消切换");
        } else {
            // 经纬度不在中国范围内
            tencentMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 4));
        }

        VisualLayer mVisualLayer = tencentMap.addVisualLayer(new VisualLayerOptions("03097e5d0ff8") //xxxxxxxx为官网配置生成对应图层的图层id
                .newBuilder()
                .setAlpha(1)
                .setLevel(OverlayLevel.OverlayLevelAboveBuildings)
                .setZIndex(10)
                .setTimeInterval(15)
                .build());

        ImageButton satellite = findViewById(R.id.satellite);
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



        DatabaseTask4 databaseTask4 = new DatabaseTask4();
        databaseTask4.execute();



    }

    // 显示地标信息的方法
    private void displayLandmarkInfo(Landmark landmark) {
        // 查找布局中的TextView
        TextView landmarkIdTextView = findViewById(R.id.landmark_id);
        TextView latitudeTextView = findViewById(R.id.latitude);
        TextView longitudeTextView = findViewById(R.id.longitude);
        // 添加其他文本视图以显示更多地标信息

        Glide.with(this)
                .load(landmark.getImageUrl())
                .error(R.drawable.rounded_box) // 加载失败显示的图像
                .into(imageView);
        // 将地标信息设置到文本视图中
        landmarkIdTextView.setText("Landmark ID: " + landmark.getLandmarkId());
        title.setText(landmark.getLocation());
        text.setText("Supercategory: "+landmark.getSupercategory()+"\nHierarchicalLabel: "+landmark.getHierarchicalLabel()+"\n"+landmark.getNaturalOrHumanMade()+"\n"+landmark.getInstanceOf()+"\n"+landmark.getOperator()+"\n"+landmark.getInception());
        latitudeTextView.setText("Latitude: " + landmark.getLatitude());
        longitudeTextView.setText("Longitude: " + landmark.getLongitude());
        // 添加其他文本视图以显示更多地标信息
    }

    private class DatabaseTask4 extends AsyncTask<Void, Void, List<CardSimilarity>> {
        @Override
        protected List<CardSimilarity> doInBackground(Void... voids) {
            List<CardSimilarity> cardSimilarityList = new ArrayList<>();

            try (Connection connection = DriverManager.getConnection(url, user, password)) {
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM Images WHERE landmark_id = ? limit 10");
                statement.setString(1, landmark.getLandmarkId());
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    String id = resultSet.getString("id");
                    String imgurl = resultSet.getString("url");
                    String landmarkId = resultSet.getString("landmark_id");
                    int width = resultSet.getInt("width");
                    int height = resultSet.getInt("height");
                    String author = resultSet.getString("author");
                    String title = resultSet.getString("title");

                    // Process title
                    if (title.length() > 5) {
                        title = title.substring(5);
                    }
                    if (title.length() > 4) {
                        title = title.substring(0, title.length() - 4);
                    }

                    Card card1 = new Card(title, author, id, imgurl, width, height, landmarkId);
                    cardSimilarityList.add(new CardSimilarity(card1, (float) 100));


                }
            } catch (SQLException e) {
                Log.e("getData", "Error getData", e);
            }
            Collections.shuffle(cardSimilarityList);
            return cardSimilarityList;

        }

        @Override
        protected void onPostExecute(List<CardSimilarity> cardSimilarityList) {
            super.onPostExecute(cardSimilarityList);
            if (cardSimilarityList != null && !cardSimilarityList.isEmpty()) {
                updateUI5(cardSimilarityList);
            } else {
                updateUI6();

            }
        }
    }

    private void updateUI5(List<CardSimilarity> myCardSimilarityList) {
        mAdapter.setCardsSimilaritys(myCardSimilarityList);
        tip.setText("");
    }
    private void updateUI6() {
        tip.setText("暂无相关推荐");

    }
}
