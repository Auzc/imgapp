package com.example.demo.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.View;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.example.demo.R;
import com.example.demo.adapter.SimilarityStaggeredGridAdapter;
import com.example.demo.data.Card;
import com.example.demo.data.CardSimilarity;
import com.example.demo.data.ImageData;
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


public class CardDetailActivity extends AppCompatActivity {
    ImageView back_btn;
    private static String driver = "com.mysql.cj.jdbc.Driver";
    private static String url = "jdbc:mysql://rm-2ze740g8q9yaf3v06co.mysql.rds.aliyuncs.com:3296/mydesign"
            + "?useUnicode=true&characterEncoding=utf8";    // mysql 数据库连接 url
    private static String user = "au";    // 用户名
    private static String password = "Jzc4211315"; // 密码
    private  Card mycard;
    private String cardId;
    private ImageView imageView,wiji;
    private TextView  tip,card_author,title,featuresTagTextView1,featuresTagTextView2,featuresTagTextView3,ResNetFeaturesTagTextView1,ResNetFeaturesTagTextView2,ResNetFeaturesTagTextView3,tagTextView1,tagTextView2,tagTextView3,colorTextView1,colorTextView2,colorTextView3;
    private TextView location;
    private ImageData imageData;

    MapView mapView;
    TencentMap tencentMap;
    private Landmark landmark;
    DecimalFormat decimalFormat = new DecimalFormat("0.00%");
    private RecyclerView mRecyclerView;
    private SimilarityStaggeredGridAdapter mAdapter;
    private List<Card> mCards;
    private List<CardSimilarity> myCardSimilarityList = new ArrayList<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_detail);
        mycard = getIntent().getParcelableExtra("card");

        mRecyclerView = findViewById(R.id.recyclerView);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new SimilarityStaggeredGridAdapter(this, 20);
        mRecyclerView.setAdapter(mAdapter);
//
//        // 模拟一些数据
//        List<CardSimilarity> cardSimilarityscardSimilaritys = generateDummyCards();
//        mAdapter.setCards(cardSimilarityscardSimilaritys);
        tip = findViewById(R.id.tip);

        imageView = findViewById(R.id.imageView);
        card_author = findViewById(R.id.card_author);
        title = findViewById(R.id.title);
        location = findViewById(R.id.location);
// Features Tag TextViews
        featuresTagTextView1 = findViewById(R.id.featuresTagTextView1);
        featuresTagTextView2 = findViewById(R.id.featuresTagTextView2);
        featuresTagTextView3 = findViewById(R.id.featuresTagTextView3);

// ResNet Features Tag TextViews
        ResNetFeaturesTagTextView1 = findViewById(R.id.ResNetFeaturesTagTextView1);
        ResNetFeaturesTagTextView2 = findViewById(R.id.ResNetFeaturesTagTextView2);
        ResNetFeaturesTagTextView3 = findViewById(R.id.ResNetFeaturesTagTextView3);

// Tag TextViews
        tagTextView1 = findViewById(R.id.tagTextView1);
        tagTextView2 = findViewById(R.id.tagTextView2);
        tagTextView3 = findViewById(R.id.tagTextView3);

// Color TextViews
        colorTextView1 = findViewById(R.id.colorTextView1);
        colorTextView2 = findViewById(R.id.colorTextView2);
        colorTextView3 = findViewById(R.id.colorTextView3);
        // 获取传递的卡片ID
        //cardId = getIntent().getStringExtra("card_id");

        if(mycard !=null){
            updateUI2(mycard);
            cardId = mycard.getId();
//            DatabaseTask databaseTask = new DatabaseTask();
//            databaseTask.execute();
            DatabaseTask2 databaseTask2 = new DatabaseTask2();
            databaseTask2.execute();
            DatabaseTask3 databaseTask3 = new DatabaseTask3();
            databaseTask3.execute();
            DatabaseTask4 databaseTask4 = new DatabaseTask4();
            databaseTask4.execute();
        }
        TencentMapInitializer.setAgreePrivacy(true);
        TencentLocationManager.setUserAgreePrivacy(true);
        mapView = findViewById(R.id.mapView);
        tencentMap = mapView.getMap();
        tencentMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(34.7952, 114.3076), 1));
        VisualLayer mVisualLayer = tencentMap.addVisualLayer(new VisualLayerOptions("03097e5d0ff8") //xxxxxxxx为官网配置生成对应图层的图层id
                .newBuilder()
                .setAlpha(1)
                .setLevel(OverlayLevel.OverlayLevelAboveBuildings)
                .setZIndex(10)
                .setTimeInterval(15)
                .build());
        TextView satellite1 = findViewById(R.id.satellite1);
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

        wiji=findViewById(R.id.wiji);
        wiji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CardDetailActivity.this, MyWebViewActivity.class);
                // 在这里可以传递卡片的信息到详情界面

                intent.putExtra("url", landmark.getCategory());
                startActivity(intent);
            }
        });


        //Toast.makeText(this, card.getImg_url(), Toast.LENGTH_SHORT).show();
//        if (!cardId.equals("")) {
//            new DatabaseTask().execute();
//        }
//        ViewPager viewPager = findViewById(R.id.viewPager);
//        List<String> imageUrls = new ArrayList<>();
//        imageUrls.add(card.getImg_url());
//// 添加更多图片 URL
//        ImagePagerAdapter adapter = new ImagePagerAdapter(this, imageUrls);
//        viewPager.setAdapter(adapter);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CardDetailActivity.this, ImagesActivity.class);
                // 在这里可以传递卡片的信息到详情界面
                //intent.putExtra("card_id", card.getId());
                intent.putExtra("card", mycard);
                startActivity(intent);
            }
        });
        back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // 创建 AsyncTask 来执行数据库操作
    private class DatabaseTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try (Connection connection1 = DriverManager.getConnection(url, user, password)) {
                PreparedStatement statement = connection1.prepareStatement("SELECT * FROM Images WHERE id = ?");
                statement.setString(1, cardId);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    String id = resultSet.getString("id");
                    String imgurl = resultSet.getString("url");
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
                    mycard = new Card(title,author,id,imgurl,width,height,landmarkId);

                }
            } catch (Exception e) {
                Log.e("getData", "Error getData", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (mycard != null) {

            } else {
                Toast.makeText(CardDetailActivity.this, "DatabaseTask Error occurred", Toast.LENGTH_SHORT).show();
            }
        }
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
                Toast.makeText(CardDetailActivity.this, "DatabaseTask2 Error occurred", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private class DatabaseTask3 extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try (Connection connection = DriverManager.getConnection(url, user, password)) {
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM landmark WHERE landmark_id = ?");
                statement.setString(1, mycard.getLandmark_id());
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
                Toast.makeText(CardDetailActivity.this, "DatabaseTask3 Error occurred", Toast.LENGTH_SHORT).show();
            }
        }
    }


    // 更新 UI
    private void updateUI(int width, int height, String imgurl, String author, String title) {

        card_author.setText(author);
        Glide.with(this)
                .load(imgurl)
                .error(R.drawable.rounded_box) // 加载失败显示的图像
                .into(imageView);
    }
    private void updateUI2(Card card) {

        title.setText(card.getTitle());
        String temptxt = card.getAuthor();
        if(temptxt.length()>20){
            temptxt=temptxt.substring(0, 15);
            temptxt+="...";
        }
        card_author.setText(temptxt);
        Glide.with(this)
                .load(card.getImg_url())
                .error(R.drawable.rounded_box) // 加载失败显示的图像
                .into(imageView);

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
        Marker marker = tencentMap.addMarker(new MarkerOptions(new LatLng(landmark.getLatitude(), landmark.getLongitude())));
        tencentMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(landmark.getLatitude(), landmark.getLongitude()), 4));


    }

    // 模拟一些数据的方法
    private List<CardSimilarity> generateDummyCards() {
        List<CardSimilarity> cardSimilaritys = new ArrayList<>();
        for (int i = 0; i < 10; i++) {

            cardSimilaritys.add(new CardSimilarity(mycard, 1.0F));
        }
        return cardSimilaritys;
    }

    private class DatabaseTask4 extends AsyncTask<Void, Void, List<CardSimilarity>> {
        @Override
        protected List<CardSimilarity> doInBackground(Void... voids) {
            List<CardSimilarity> cardSimilarityList = new ArrayList<>();

            try (Connection connection = DriverManager.getConnection(url, user, password)) {
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM imagesimilarit" +
                        " INNER JOIN Images ON imagesimilarity.Image_ID_2 = Images.id WHERE Image_ID_1 = ?");
                statement.setString(1, mycard.getId());
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    String id = resultSet.getString("id");
                    String imgurl = resultSet.getString("url");
                    String landmarkId = resultSet.getString("landmark_id");
                    int width = resultSet.getInt("width");
                    int height = resultSet.getInt("height");
                    String author = resultSet.getString("author");
                    String title = resultSet.getString("title");
                    float similarity = resultSet.getFloat("Similarity");

                    // Process title
                    if (title.length() > 5) {
                        title = title.substring(5);
                    }
                    if (title.length() > 4) {
                        title = title.substring(0, title.length() - 4);
                    }

                    Card card1 = new Card(title, author, id, imgurl, width, height, landmarkId);
                    cardSimilarityList.add(new CardSimilarity(card1, similarity));
                }
            } catch (SQLException e) {
                Log.e("getData", "Error getData", e);
            }
            try (Connection connection = DriverManager.getConnection(url, user, password)) {
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM imagesimilarity_color INNER JOIN Images ON imagesimilarity.Image_ID_2 = Images.id WHERE Image_ID_1 = ?");
                statement.setString(1, mycard.getId());
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    String id = resultSet.getString("id");
                    String imgurl = resultSet.getString("url");
                    String landmarkId = resultSet.getString("landmark_id");
                    int width = resultSet.getInt("width");
                    int height = resultSet.getInt("height");
                    String author = resultSet.getString("author");
                    String title = resultSet.getString("title");
                    float similarity = resultSet.getFloat("Similarity");

                    // Process title
                    if (title.length() > 5) {
                        title = title.substring(5);
                    }
                    if (title.length() > 4) {
                        title = title.substring(0, title.length() - 4);
                    }

                    Card card1 = new Card(title, author, id, imgurl, width, height, landmarkId);
                    cardSimilarityList.add(new CardSimilarity(card1, similarity));
                }
            } catch (SQLException e) {
                Log.e("getData", "Error getData", e);
            }


//            try (Connection connection = DriverManager.getConnection(url, user, password)) {
//                PreparedStatement statement = connection.prepareStatement("SELECT * FROM Images WHERE landmark_id = ? limit 10");
//                statement.setString(1, mycard.getLandmark_id());
//                ResultSet resultSet = statement.executeQuery();
//
//                while (resultSet.next()) {
//                    String id = resultSet.getString("id");
//                    String imgurl = resultSet.getString("url");
//                    String landmarkId = resultSet.getString("landmark_id");
//                    int width = resultSet.getInt("width");
//                    int height = resultSet.getInt("height");
//                    String author = resultSet.getString("author");
//                    String title = resultSet.getString("title");
//
//                    // Process title
//                    if (title.length() > 5) {
//                        title = title.substring(5);
//                    }
//                    if (title.length() > 4) {
//                        title = title.substring(0, title.length() - 4);
//                    }
//
//                    Card card1 = new Card(title, author, id, imgurl, width, height, landmarkId);
//                    if(mycard.equals(card1)){
//
//                    }else{
//                        cardSimilarityList.add(new CardSimilarity(card1, (float) 100));
//                    }
//
//                }
//            } catch (SQLException e) {
//                Log.e("getData", "Error getData", e);
//            }

            return cardSimilarityList;
        }

        @Override
        protected void onPostExecute(List<CardSimilarity> cardSimilarityList) {
            super.onPostExecute(cardSimilarityList);
            if (cardSimilarityList != null && !cardSimilarityList.isEmpty()) {
                updateUI5(cardSimilarityList);
            } else {
                updateUI6();
                //Toast.makeText(CardDetailActivity.this, "无相关推荐", Toast.LENGTH_SHORT).show();
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
