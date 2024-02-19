package com.example.demo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.demo.R;
import com.tencent.tencentmap.mapsdk.maps.MapRenderLayer;
import com.tencent.tencentmap.mapsdk.maps.TencentMapInitializer;
import com.tencent.tencentmap.mapsdk.maps.TencentMapOptions;

public class MapFragment extends Fragment {

    private MapRenderLayer mapView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        TencentMapInitializer.setAgreePrivacy(true);
        // 创建地图视图
        TencentMapOptions mapOptions = new TencentMapOptions();
        mapView = new MapRenderLayer(getContext(), mapOptions);

        // 将地图视图添加到布局容器中
        FrameLayout mapContainer = rootView.findViewById(R.id.map_container);
        mapContainer.addView(mapView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 地图生命周期绑定
        if (mapView != null) {
            mapView.onStart();
            mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // 地图生命周期绑定
        if (mapView != null) {
            mapView.onPause();
            mapView.onStop();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 销毁地图视图
        if (mapView != null) {
            mapView.onDestroy();
            mapView = null;
        }
    }
}
