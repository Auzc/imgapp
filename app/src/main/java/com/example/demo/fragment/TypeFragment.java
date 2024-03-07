package com.example.demo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.demo.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;


public class TypeFragment extends Fragment {

    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_type, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager = view.findViewById(R.id.view_pager);
        tabLayout = view.findViewById(R.id.tab_layout);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        // Set icons for each tab
        //int[] tabIcons = {R.drawable.love, R.drawable.love,R.drawable.love, R.drawable.love};
//        for (int i = 0; i < 4; i++) {
//            //tabLayout.getTabAt(i).setIcon(tabIcons[i]);
//            tabLayout.getTabAt(i).setText("666");
//        }
        tabLayout.getTabAt(0).setText("色彩划分");
        tabLayout.getTabAt(1).setText("特征划分");
        tabLayout.getTabAt(2).setText("地点划分");
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getChildFragmentManager());

        adapter.addFragment(new ColorFragment(), R.drawable.love);
        adapter.addFragment(new AllocFragment(),  R.drawable.love);
        adapter.addFragment(new AllocFragment(), R.drawable.love);
//        adapter.addFragment(new UserFragment(),  R.drawable.love);

        viewPager.setAdapter(adapter);
    }

    private static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> fragmentList = new ArrayList<>();
        //private final List<String> fragmentTitleList = new ArrayList<>();
        private final List<Integer> fragmentIconList = new ArrayList<>();

        public Adapter(FragmentManager fragmentManager) {
            super(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            Fragment fragment = fragmentList.get(position);
            int iconResId = fragmentIconList.get(position);
            Bundle args = new Bundle();
            args.putInt("icon_res_id", iconResId);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        public void addFragment(Fragment fragment, int iconResId) {
            fragmentList.add(fragment);

            fragmentIconList.add(iconResId);
        }
    }
}