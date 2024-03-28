    package com.example.demo.fragment;

    import android.content.Intent;
    import android.graphics.Color;
    import android.os.AsyncTask;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.ImageButton;
    import android.widget.ImageView;
    import android.widget.LinearLayout;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.fragment.app.Fragment;
    import androidx.recyclerview.widget.RecyclerView;
    import androidx.viewpager2.widget.ViewPager2;

    import com.bumptech.glide.Glide;
    import com.example.demo.DatabaseHelper;
    import com.example.demo.R;
    import com.example.demo.activity.ImagesActivity;
    import com.example.demo.data.Card;
    import com.example.demo.data.ImageData;
    import com.example.demo.data.Landmark;
    import com.example.demo.data.RecommendationData;
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
    import java.text.DecimalFormat;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.Random;

    public class RecommendationFragment extends Fragment {

        private ViewPager2 viewPager;
        private String userId = "user123";
        private int currentPageIndex = 0;
        private static String url = "jdbc:mysql://rm-2ze740g8q9yaf3v06co.mysql.rds.aliyuncs.com:3296/mydesign"
                + "?useUnicode=true&characterEncoding=utf8";    // mysql 数据库连接 url
        private static String user = "au";    // 用户名
        private static String password = "Jzc4211315"; // 密码

        private int currentOffset = new Random().nextInt(2000);
        private List<RecommendationData> mydataInfoList = new ArrayList<>();
        private View view1;

        private ImageView point;


        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_recommendation, container, false);

            viewPager = rootView.findViewById(R.id.viewPager);
            view1 = rootView.findViewById(R.id.view1);
            point = rootView.findViewById(R.id.point);


            DatabaseTask databaseTask = new DatabaseTask();
            databaseTask.execute();
            viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                    // 根据滑动的偏移量计算缩放比例
                    float scale = 1 - positionOffset;
                    // 设置 View1 的宽度缩放动画
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view1.getLayoutParams();
                    layoutParams.width = (int) (240 * scale); // 初始宽度 * 缩放比例
                    view1.setLayoutParams(layoutParams);

                    float alpha = 1 - positionOffset;
                    // 设置点的透明度
                    point.setAlpha(alpha);
                }
            });

            return rootView;
        }


        @Override
        public void onDestroyView() {
            super.onDestroyView();
            // 清除Glide加载任务
            Glide.with(requireContext()).clear(viewPager);
        }

        private static class RecommendationPagerAdapter extends RecyclerView.Adapter<RecommendationPagerAdapter.ViewHolder> {

            private List<RecommendationData> data;

            public RecommendationPagerAdapter(List<RecommendationData> data) {
                this.data = data;
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommendation_page, parent, false);
                return new ViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                RecommendationData pageData = data.get(position);


                Glide.with(holder.itemView.getContext().getApplicationContext())
                        .load(pageData.getCard().getImg_url())
                        .error(R.drawable.rounded_box) // 加载失败显示的图像
                        .into(holder.img);

                DecimalFormat decimalFormat = new DecimalFormat("0.00%");
                holder.featuresTagTextView1.setText(pageData.getImageData().getType1Inception()+" | "+decimalFormat.format(pageData.getImageData().getConfidence1Inception()));
                holder.featuresTagTextView2.setText(pageData.getImageData().getType2Inception()+" | "+decimalFormat.format(pageData.getImageData().getConfidence2Inception()));
                holder.featuresTagTextView3.setText(pageData.getImageData().getType3Inception()+" | "+decimalFormat.format(pageData.getImageData().getConfidence3Inception()));
                holder.ResNetFeaturesTagTextView1.setText(pageData.getImageData().getType1Resnet()+" | "+decimalFormat.format(pageData.getImageData().getConfidence1Resnet()));
                holder.ResNetFeaturesTagTextView2.setText(pageData.getImageData().getType2Resnet()+" | "+decimalFormat.format(pageData.getImageData().getConfidence2Resnet()));
                holder.ResNetFeaturesTagTextView3.setText(pageData.getImageData().getType3Resnet()+" | "+decimalFormat.format(pageData.getImageData().getConfidence3Resnet()));

                holder.card_love.setImageResource(R.drawable.lovewhite);
                holder.card_love.setTag(R.drawable.lovewhite);
                holder.card_store.setImageResource(R.drawable.store);
                holder.card_comment.setImageResource(R.drawable.comment);

                holder.card_love.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 获取当前的图片资源ID
                        int currentImageResource = (Integer) holder.card_love.getTag();
                        // 检查当前的图片资源ID，根据不同的状态设置不同的图片
                        if (currentImageResource == R.drawable.lovewhite) {

//                // 在这里调用 AsyncTask 来执行数据库操作
                            DatabaseHelper.likeContent(pageData.getCard().getId(), "user123", v.getContext());

                            //insert(userId,contentId);
                            holder.card_love.setImageResource(R.drawable.loved);
                            // 更新标签以便下次点击知道当前状态
                            holder.card_love.setTag(R.drawable.loved);
                        } else {
                            holder.card_love.setImageResource(R.drawable.lovewhite);
                            // 更新标签以便下次点击知道当前状态
                            holder.card_love.setTag(R.drawable.lovewhite);
                        }

                    }
                });
                holder.tagTextView1.setText(pageData.getImageData().getDominantColor1());
                holder.tagTextView2.setText(pageData.getImageData().getDominantColor2());
                holder.tagTextView3.setText(pageData.getImageData().getDominantColor3());
                String dominantColorHex1 = pageData.getImageData().getDominantColor1();
                int color1 = Color.parseColor(dominantColorHex1);
                holder.colorTextView1.setBackgroundColor(color1);
                String dominantColorHex2 = pageData.getImageData().getDominantColor2();
                int color2 = Color.parseColor(dominantColorHex2);
                holder.colorTextView1.setBackgroundColor(color2);
                String dominantColorHex3 = pageData.getImageData().getDominantColor3();
                int color3 = Color.parseColor(dominantColorHex3);
                holder.colorTextView1.setBackgroundColor(color3);
                holder.location.setText(pageData.getLandmark().getLocation());
                holder.tencentMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(pageData.getLandmark().getLatitude(),pageData.getLandmark().getLongitude()), 4));
                holder.img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(holder.itemView.getContext().getApplicationContext(), ImagesActivity.class);
                        intent.putExtra("card", pageData.getCard());
                        holder.itemView.getContext().startActivity(intent);
                    }
                });
                Marker marker = holder.tencentMap.addMarker(new MarkerOptions(new LatLng(pageData.getLandmark().getLatitude(), pageData.getLandmark().getLongitude())));
            }

            @Override
            public int getItemCount() {
                return data.size();
            }

            public static class ViewHolder extends RecyclerView.ViewHolder {

                TextView featuresTagTextView1,featuresTagTextView2,featuresTagTextView3,ResNetFeaturesTagTextView1,ResNetFeaturesTagTextView2,ResNetFeaturesTagTextView3,tagTextView1,tagTextView2,tagTextView3,colorTextView1,colorTextView2,colorTextView3;
                TextView location;
                MapView mapView;

                TencentMap tencentMap;

                private ImageView img,card_love,card_store,card_comment;
                public ViewHolder(@NonNull View view) {
                    super(view);
                    location = view.findViewById(R.id.location);

                    card_love = view.findViewById(R.id.card_love);
                    card_store = view.findViewById(R.id.card_store);
                    card_comment = view.findViewById(R.id.card_comment);

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

                }
            }
        }

         private class DatabaseTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                try (Connection connection = DriverManager.getConnection(url, user, password)) {
                    String sql = "SELECT * FROM Images LIMIT ? OFFSET ?";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setInt(1, 20);
                    statement.setInt(2, currentOffset);
                    ResultSet resultSet = statement.executeQuery();
                    while (resultSet.next()) {
                        String id = resultSet.getString("id");
                        ImageData imageData = new ImageData();
                        Landmark landmark = new Landmark();
                        PreparedStatement statementimageData = connection.prepareStatement("SELECT * FROM image_data WHERE image_id = ?");
                        statementimageData.setString(1, id);
                        ResultSet resultSetimageData = statementimageData.executeQuery();
                        if (resultSetimageData.next()) {
                            String imageId = resultSetimageData.getString("image_id");
                            String typeId1Inception = resultSetimageData.getString("type_id1_inception");
                            String type1Inception = resultSetimageData.getString("type1_inception");
                            float confidence1Inception = resultSetimageData.getFloat("confidence1_inception");
                            String typeId2Inception = resultSetimageData.getString("type_id2_inception");
                            String type2Inception = resultSetimageData.getString("type2_inception");
                            float confidence2Inception = resultSetimageData.getFloat("confidence2_inception");
                            String typeId3Inception = resultSetimageData.getString("type_id3_inception");
                            String type3Inception = resultSetimageData.getString("type3_inception");
                            float confidence3Inception = resultSetimageData.getFloat("confidence3_inception");
                            String typeId1Resnet = resultSetimageData.getString("type_id1_resnet");
                            String type1Resnet = resultSetimageData.getString("type1_resnet");
                            float confidence1Resnet = resultSetimageData.getFloat("confidence1_resnet");
                            String typeId2Resnet = resultSetimageData.getString("type_id2_resnet");
                            String type2Resnet = resultSetimageData.getString("type2_resnet");
                            float confidence2Resnet = resultSetimageData.getFloat("confidence2_resnet");
                            String typeId3Resnet = resultSetimageData.getString("type_id3_resnet");
                            String type3Resnet = resultSetimageData.getString("type3_resnet");
                            float confidence3Resnet = resultSetimageData.getFloat("confidence3_resnet");
                            String typeId4Resnet = resultSetimageData.getString("type_id4_resnet");
                            String type4Resnet = resultSetimageData.getString("type4_resnet");
                            float confidence4Resnet = resultSetimageData.getFloat("confidence4_resnet");
                            String typeId5Resnet = resultSetimageData.getString("type_id5_resnet");
                            String type5Resnet = resultSetimageData.getString("type5_resnet");
                            float confidence5Resnet = resultSetimageData.getFloat("confidence5_resnet");
                            String dominantColor1 = resultSetimageData.getString("dominant_color1");
                            String dominantColor2 = resultSetimageData.getString("dominant_color2");
                            String dominantColor3 = resultSetimageData.getString("dominant_color3");
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

                        String url = resultSet.getString("url");
                        String landmarkId = resultSet.getString("landmark_id");
                        PreparedStatement statementlandmarkId = connection.prepareStatement("SELECT * FROM landmark WHERE landmark_id = ?");
                        statementlandmarkId.setString(1, landmarkId);
                        ResultSet resultSetlandmarkId = statementlandmarkId.executeQuery();

                        if (resultSetlandmarkId.next()) {
                            String landmark_Id = resultSetlandmarkId.getString("landmark_id");
                            Float latitude = resultSetlandmarkId.getFloat("Latitude");
                            Float longitude = resultSetlandmarkId.getFloat("Longitude");
                            Integer idCount = resultSetlandmarkId.getInt("id_count");
                            String category = resultSetlandmarkId.getString("category");
                            String supercategory = resultSetlandmarkId.getString("supercategory");
                            String hierarchicalLabel = resultSetlandmarkId.getString("hierarchical_label");
                            String naturalOrHumanMade = resultSetlandmarkId.getString("natural_or_human_made");
                            String instanceOf = resultSetlandmarkId.getString("Instance_of");
                            String location = resultSetlandmarkId.getString("Location");
                            String operator = resultSetlandmarkId.getString("Operator");
                            String inception = resultSetlandmarkId.getString("Inception");
                            String imageUrl = resultSetlandmarkId.getString("Image_URL");

                            // Create a Landmark object with retrieved data
                            landmark = new Landmark(landmark_Id, latitude, longitude, idCount, category, supercategory,
                                    hierarchicalLabel, naturalOrHumanMade, instanceOf, location, operator, inception, imageUrl);
                        }

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
                        //System.out.println("ID: " + id + ", URL: " + url + ", Landmark ID: " + landmarkId + ", Width: " + width + ", Height: " + height + ", Author: " + author + ", Title: " + title);
                        Card card = new Card(title,author,id,url,width,height,landmarkId);

                        RecommendationData recommendationData = new RecommendationData(card,imageData,landmark);
                        mydataInfoList.add(recommendationData);
                    }


                } catch (Exception e) {
                    Log.e("getData", "Error getData", e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                if (mydataInfoList != null) {
                    updateUI(mydataInfoList);
                    //Toast.makeText(CardDetailActivity.this, card.getLandmark_id(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Error occurred", Toast.LENGTH_SHORT).show();
                }
            }


        }
        private void updateUI(List<RecommendationData> mydataList) {

            RecommendationPagerAdapter adapter = new RecommendationPagerAdapter(mydataList);

            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(currentPageIndex, false);

            viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    currentPageIndex = position;
                }
            });

        }


    }
