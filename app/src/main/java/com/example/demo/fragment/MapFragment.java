package com.example.demo.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.demo.R;
import com.example.demo.activity.CardDetailActivity;
import com.example.demo.activity.LandmarkDetailActivity;
import com.example.demo.data.Card;
import com.example.demo.data.CardSimilarity;
import com.example.demo.data.Landmark;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.sdk.comps.vis.VisualLayer;
import com.tencent.map.sdk.comps.vis.VisualLayerOptions;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.MapRenderLayer;
import com.tencent.tencentmap.mapsdk.maps.MapView;
import com.tencent.tencentmap.mapsdk.maps.Projection;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.TencentMapInitializer;
import com.tencent.tencentmap.mapsdk.maps.TencentMapOptions;
import com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptor;
import com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptorFactory;
import com.tencent.tencentmap.mapsdk.maps.model.CameraPosition;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.LatLngBounds;
import com.tencent.tencentmap.mapsdk.maps.model.Marker;
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions;
import com.tencent.tencentmap.mapsdk.maps.model.OverlayLevel;
import com.tencent.tencentmap.mapsdk.maps.model.TencentMapGestureListenerList;
import com.tencent.tencentmap.mapsdk.maps.model.VisibleRegion;

import org.w3c.dom.Text;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapFragment extends Fragment {


    private MapView mapView;
    private TencentMap tencentMap;
    private TencentLocationManager mLocationManager;

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Map<Landmark, Integer> landmark_hashMap = new HashMap<>();
    private LatLng nearLeft ,nearRight ,farLeft, farRight ;
    private ImageButton satellite;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String url = "jdbc:mysql://rm-2ze740g8q9yaf3v06co.mysql.rds.aliyuncs.com:3296/mydesign" +
            "?useUnicode=true&characterEncoding=utf8&useSSL=false";
    private static String user = "au";    // 用户名
    private static String password = "Jzc4211315"; // 密码
    public  static TencentLocation mylocation = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        TencentMapInitializer.setAgreePrivacy(true);
        TencentLocationManager.setUserAgreePrivacy(true);
        mapView = view.findViewById(R.id.mapView);
        mLocationManager = TencentLocationManager.getInstance(getActivity());
// 检查是否已经授予定位权限
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 如果没有授予权限，则向用户请求定位权限
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            //mLocationManager.requestSingleFreshLocation(null, locationListener, Looper.getMainLooper());
        } else {
            // 如果已经授予了定位权限，请执行相应的操作
            // do something...
            //mLocationManager.requestSingleFreshLocation(null, locationListener, Looper.getMainLooper());
        }





        tencentMap = mapView.getMap();
        VisualLayer mVisualLayer = tencentMap.addVisualLayer(new VisualLayerOptions("03097e5d0ff8") //xxxxxxxx为官网配置生成对应图层的图层id
                .newBuilder()
                .setAlpha(1)
                .setLevel(OverlayLevel.OverlayLevelAboveBuildings)
                .setZIndex(10)
                .setTimeInterval(15)
                .build());

        tencentMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(34.7952, 114.3076), 1));
        //tencentMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng( 33.266700, -114.000000), 9));

        ImageButton myLocationButton = view.findViewById(R.id.my_location_button);
//        // 获取定位管理器实例

        mLocationManager = TencentLocationManager.getInstance(getActivity());




        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 1.2f, 1f, 1.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(500); //动画持续时间为500毫秒
        scaleAnimation.setRepeatCount(1); //设置重复次数为1
        scaleAnimation.setRepeatMode(Animation.REVERSE); //设置重复模式为REVERSE，即反向播放

        myLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myLocationButton.startAnimation(scaleAnimation);
                // 发起单次定位请求
                mLocationManager.requestSingleFreshLocation(null, locationListener, Looper.getMainLooper());

// 获取当前屏幕地图的视野范围
                Projection projection = tencentMap.getProjection();
                VisibleRegion region = projection.getVisibleRegion();
                //Toast.makeText(getActivity(), ("当前地图的视野范围：" + new Gson().toJson(region)), Toast.LENGTH_LONG).show();
            }
        });
        TextView satellite1 = view.findViewById(R.id.satellite1);
        satellite = view.findViewById(R.id.satellite);
        tencentMap.setBuilding3dEffectEnable(true);

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




        //initMarker();

        tencentMap.setOnInfoWindowClickListener(new TencentMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                for (Map.Entry<Landmark, Integer> entry : landmark_hashMap.entrySet()) {
                    Landmark landmark = entry.getKey();

                    // 检查地标ID是否与标记的标题匹配
                    if (landmark.getLandmarkId().equals(marker.getTitle())) {

//                        String landmarkInfo = "Landmark ID: " + landmark.getLandmarkId() +
//                                "\nLatitude: " + landmark.getLatitude() +
//                                "\nLongitude: " + landmark.getLongitude() +
//                                "\nCategory: " + landmark.getCategory() +
//                                "\nSupercategory: " + landmark.getSupercategory() +
//                                "\nHierarchical Label: " + landmark.getHierarchicalLabel() +
//                                "\nNatural or Human Made: " + landmark.getNaturalOrHumanMade() +
//                                "\nInstance Of: " + landmark.getInstanceOf() +
//                                "\nLocation: " + landmark.getLocation() +
//                                "\nOperator: " + landmark.getOperator() +
//                                "\nInception: " + landmark.getInception() +
//                                "\nImage URL: " + landmark.getImageUrl();
//
//// 使用Toast显示地标信息
//                        Toast.makeText(getActivity(), landmarkInfo, Toast.LENGTH_LONG).show();
// 创建一个Intent对象
                        Intent intent = new Intent(getActivity(), LandmarkDetailActivity.class);

// 将Landmark对象放入Intent中
                        intent.putExtra("landmark", landmark);

// 启动LandmarkDetailActivity
                        startActivity(intent);

                        break; // 如果只需要找到第一个匹配，可以在这里退出循环
                    }
                }



            }

            @Override
            public void onInfoWindowClickLocation(int width, int height, int x, int y) {
                Log.i("TAG","当InfoWindow点击时，点击点的回调");
            }
        });
        tencentMap.setOnCameraChangeListener(new TencentMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                // 当地图视野变化时触发
            }

            @Override
            public void onCameraChangeFinished(CameraPosition cameraPosition) {
                // 当地图视野变化完成时触发
                checkVisibleRegionSize();
            }
        });




        return view;


    }

    private void checkVisibleRegionSize() {
        Projection projection = tencentMap.getProjection();
        VisibleRegion region = projection.getVisibleRegion();

        // 计算地图视野范围的面积，你可以根据自己的需求定义“小于某个数量级”的条件
        double visibleRegionArea = calculateVisibleRegionArea(region);

        // 设置你定义的阈值
        double threshold = 3; // 举例阈值为1000

        if (visibleRegionArea < threshold) {
            // 当地图视野范围小于阈值时，显示 Toast 消息
            //Toast.makeText(getActivity(), new Gson().toJson(region), Toast.LENGTH_LONG).show();

            nearLeft = region.nearLeft;
            nearRight = region.nearRight;
            farLeft = region.farLeft;
            farRight = region.farRight;
            DatabaseTask databaseTask = new DatabaseTask();
            databaseTask.execute();


        }
    }

    private double calculateVisibleRegionArea(VisibleRegion region) {
        // 这里是一个简单的计算地图视野范围面积的示例，你可以根据需要实现更复杂的计算方法
        // 假设地图视野范围是一个矩形，可以使用矩形面积公式进行计算
        LatLngBounds bounds = region.latLngBounds;
        double width = Math.abs(bounds.northeast.longitude - bounds.southwest.longitude);
        double height = Math.abs(bounds.northeast.latitude - bounds.southwest.latitude);
        return width * height;
    }

    private class DatabaseTask extends AsyncTask<Void, Void, List<Landmark>> {
        @Override
        protected List<Landmark> doInBackground(Void... voids) {
            List<Landmark> landmarkList = new ArrayList<>();
            try (Connection connection = DriverManager.getConnection(url, user, password)) {
                // 添加日志记录SQL查询语句

                String sqlQuery = "SELECT * FROM landmark " +
                        "WHERE latitude BETWEEN " + nearLeft.latitude + " AND " + farRight.latitude +
                        " AND longitude BETWEEN " + nearLeft.longitude + " AND " + farRight.longitude;
                Log.d("DatabaseTask", "Executing SQL Query: " + sqlQuery);
                PreparedStatement statement = connection.prepareStatement(sqlQuery);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    // 读取每个地标并添加到列表中
                    Landmark landmark = new Landmark(
                            resultSet.getString("landmark_id"),
                            resultSet.getFloat("latitude"),
                            resultSet.getFloat("longitude"),
                            resultSet.getInt("id_count"),
                            resultSet.getString("category"),
                            resultSet.getString("supercategory"),
                            resultSet.getString("hierarchical_label"),
                            resultSet.getString("natural_or_human_made"),
                            resultSet.getString("Instance_of"),
                            resultSet.getString("Location"),
                            resultSet.getString("operator"),
                            resultSet.getString("inception"),
                            resultSet.getString("Image_URL")
                    );

                    landmarkList.add(landmark);
                    if (landmark_hashMap.get(landmark) == null ) {
                        landmark_hashMap.put(landmark, 0);
                        Log.d("DatabaseTask", "landmark: " + landmark.getLandmarkId());
                    }
                }
            } catch (Exception e) {
                Log.e("getData", "Error getData", e);
            }
            return landmarkList;
        }

        @Override
        protected void onPostExecute(List<Landmark> landmarkList) {
            super.onPostExecute(landmarkList);

            if (landmarkList != null && !landmarkList.isEmpty()) {
                // 更新UI
                updateUI();
            } else {
                // 添加日志记录如果地标列表为空
                Log.d("DatabaseTask", "No landmarks retrieved from database.");
            }
        }
    }

    private void updateUI() {
        for (Map.Entry<Landmark, Integer> entry : landmark_hashMap.entrySet()) {

            Integer value = entry.getValue();
            if (value == 1) {
                continue;
            }
            // 对值不为1的条目执行操作
            entry.setValue(1);
            Landmark landmark = entry.getKey();
            LatLng position = new LatLng(landmark.getLatitude(), landmark.getLongitude());
            MarkerOptions options = new MarkerOptions(position);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ip);
            bitmap = Bitmap.createScaledBitmap(bitmap, 48, 48, false);
            BitmapDescriptor custom = BitmapDescriptorFactory.fromBitmap(bitmap);
            options.infoWindowEnable(true);//默认为true
            options.title(landmark.getLandmarkId())//标注的InfoWindow的标题
                    .icon(custom)
                    .snippet(landmark.getLocation()+"\n"+landmark.getLatitude()+"，"+landmark.getLongitude());
            Marker marker = tencentMap.addMarker(options);
            marker.setClickable(true);

//设置Marker支持点击

            // 添加日志记录已在地图上添加标记的地标
            Log.d("DatabaseTask", "Added marker for landmark: " + landmark.getLandmarkId());
        }
    }


    TencentLocationListener locationListener = new TencentLocationListener() {
        @Override
        public void onLocationChanged(TencentLocation location, int error, String reason) {
            if (error == TencentLocation.ERROR_OK) {
                mylocation = location;
                // 定位成功
                String result = "您的位置：\n" +
                        "经度：" + location.getLongitude() + "\n" +
                        "纬度：" + location.getLatitude();
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                tencentMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 9));
                Resources res = getResources();

            } else {
                // 定位失败
                String result = "定位失败：" + reason;
                Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        public void onStatusUpdate(String name, int status, String desc) {
            // do nothing
        }
    };
    TencentLocationListener locationListener1 = new TencentLocationListener() {
        @Override
        public void onLocationChanged(TencentLocation location, int error, String reason) {
            if (error == TencentLocation.ERROR_OK) {
                mylocation = location;
                // 定位成功
                String result = "您的位置：\n" +
                        "经度：" + location.getLongitude() + "\n" +
                        "纬度：" + location.getLatitude();
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                tencentMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8));
                Resources res = getResources();

            } else {
                // 定位失败
                String result = "定位失败：" + reason;
                Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        public void onStatusUpdate(String name, int status, String desc) {
            // do nothing
        }
    };

    /**
     * MapView的生命周期管理
     */
    public void initMarker(){

    }
    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
        // 停止定位
        mLocationManager.removeUpdates(locationListener);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mapView = null;
    }


    // 处理用户响应
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户已授予定位权限
                // do something...
            } else {
                // 用户拒绝了定位权限
                // do something...
            }
        }
    }



}

