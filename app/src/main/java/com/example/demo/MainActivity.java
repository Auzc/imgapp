package com.example.demo;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.demo.fragment.AddFragment;
import com.example.demo.fragment.HomeFragment;
import com.example.demo.fragment.ListFragment;
import com.example.demo.fragment.MapFragment;
import com.example.demo.fragment.RecommendFragment;
import com.example.demo.fragment.UserFragment;
import com.example.demo.fragment.TypeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    private HomeFragment homeFragment;
    private MapFragment mapFragment;
    private UserFragment userFragment;
    private TypeFragment typeFragment;
    private ListFragment listFragment;
    private RecommendFragment recommendFragment;
    private AddFragment addFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化底部导航栏和Fragment
        bottomNavigationView = findViewById(R.id.bottom_navigation);
//        homeFragment = new HomeFragment();
        recommendFragment = new RecommendFragment();
        mapFragment = new MapFragment();
        userFragment = new UserFragment();
        listFragment = new ListFragment();
//        addFragment = new AddFragment();
        typeFragment = new TypeFragment();
        // 默认显示HomeFragment
        setFragment(recommendFragment);

        // 底部导航栏选中项变化的监听器
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_recommend:
                        setFragment(recommendFragment);
                        return true;
                    case R.id.action_list:
                        setFragment(listFragment);
                        return true;
                    case R.id.action_type:
                        setFragment(typeFragment);
//                        Intent intent =new Intent();
//                        intent.setClass(MainActivity.this, AddActivity.class);
//                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this);
//                        startActivity(intent, options.toBundle());
//                        //overridePendingTransition(R.anim.slide_up_enter, R.anim.slide_up_exit);
                        return true;
//                    case R.id.action_add:
//                        setFragment(addFragment);
//                        return true;
                    case R.id.action_messages:
                        setFragment(mapFragment);
                        return true;
                    case R.id.action_settings:
                        setFragment(userFragment);
                        return true;
                }
                return false;
            }
        });
    }

    // 切换Fragment
    private void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }
}