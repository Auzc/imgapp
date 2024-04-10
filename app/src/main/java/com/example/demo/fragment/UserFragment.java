package com.example.demo.fragment;

import android.content.Intent;
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
import com.example.demo.activity.CardDetailActivity;
import com.example.demo.activity.HistoryActivity;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;


public class UserFragment extends Fragment {
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private View history;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        history = view.findViewById(R.id.history);
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HistoryActivity.class);
                startActivity(intent);

            }
        });
        return view;
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
        tabLayout.getTabAt(0).setText("兴趣");
        tabLayout.getTabAt(1).setText("赞过");

    }

    private void setupViewPager(ViewPager viewPager) {
        UserFragment.Adapter adapter = new UserFragment.Adapter(getChildFragmentManager());

        adapter.addFragment(new ChartFragment(), R.drawable.love);
        adapter.addFragment(new MyLikeFragment(),  R.drawable.love);

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