package com.example.demo.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment {


    private MapView mapView;
    private TencentMap tencentMap;
    private TencentLocationManager mLocationManager;

    private ViewPager viewPager;
    private TabLayout tabLayout;

    private ImageButton satellite;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

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

//                BmobQuery<FootballField> query = new BmobQuery<>();
//                query.findObjects(new FindListener<FootballField>() {
//                    @Override
//                    public void done(List<FootballField> list, BmobException e) {
//                        if (e == null) {
//                            for(int i=0;i<list.size();i++){
//                                if(list.get(i).getName().toString().equals(marker.getTitle().toString())){
//                                    Intent intent =new Intent();
//                                    intent.setClass(getActivity(), MessageOfFieldActivity.class);
//                                    intent.putExtra("name", list.get(i).getName());
//                                    intent.putExtra("avatar_file", list.get(i).getFile().getFileUrl());
//                                    intent.putExtra("lat",list.get(i).getAddress_lat());
//                                    intent.putExtra("lng",list.get(i).getAddress_lng());
//                                    intent.putExtra("addressName",list.get(i).getAddress_name());
//                                    intent.putExtra("field_size",list.get(i).getField_size());
//                                    intent.putExtra("character",list.get(i).getCharacter());
//                                    startActivity(intent);
//                                }
//
//                            }
//
//                        } else {
//                            // 查询失败
//                        }
//                    }
//                });
//                Log.i("TAG","InfoWindow被点击时回调函数");
            }
            //  windowWidth - InfoWindow的宽度
            //windowHigh - InfoWindow的高度
            // x - 点击点在InfoWindow的x坐标点
            //y - 点击点在InfoWindow的y坐标点
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
        double threshold = 10; // 举例阈值为1000

        if (visibleRegionArea < threshold) {
            // 当地图视野范围小于阈值时，显示 Toast 消息
            Toast.makeText(getActivity(), new Gson().toJson(region), Toast.LENGTH_LONG).show();

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
                tencentMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 6));
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

