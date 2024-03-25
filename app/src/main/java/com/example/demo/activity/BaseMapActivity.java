package com.example.demo.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.demo.R;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.TencentMapInitializer;
import com.tencent.tencentmap.mapsdk.maps.TencentMapOptions;
import com.tencent.tencentmap.mapsdk.maps.TextureMapView;

public class BaseMapActivity extends AppCompatActivity {
    private TextureMapView mapView;
    private TencentMap tencentMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_map);
        TencentMapOptions mTencentMapOptions =new TencentMapOptions();
        mTencentMapOptions.setMapKey("KGVBZ-63FKH-UQBDK-WWTWJ-XIZEO-SRBOG");

        TencentMapInitializer.setAgreePrivacy(true);

        mapView = findViewById(R.id.mapView);

        tencentMap = mapView.getMap();
    }

    /**
     * mapview的生命周期管理
     */
    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mapView.onRestart();
    }

}
