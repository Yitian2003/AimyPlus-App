// Modified by Yitian Feb 6, 2017
package com.aimyplus.consumer.activity.user;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aimyplus.consumer.Manifest;
import com.aimyplus.consumer.R;
import com.aimyplus.consumer.activity.booking.BookProgramActivity;
import com.aimyplus.consumer.activity.timeline.AddFeedActivity;
import com.aimyplus.consumer.adapter.ChildListAdapter;
import com.aimyplus.consumer.app.GlobalApplication;
import com.aimyplus.consumer.base.BaseResponse;
import com.aimyplus.consumer.fragment.ChildDetailFragment;
import com.aimyplus.consumer.model.user.ChildModel;
import com.aimyplus.consumer.ui.CustomViewPager;
import com.aimyplus.consumer.utils.ConnUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ChildActivity extends AppCompatActivity {

    private static final String TAG = ChildActivity.class.getSimpleName();
    public static final String PARAM_KEY_MODEL = "Model";
    public static final String PARAM_KEY_ID = "Child Id";

    private ChildListAdapter adapter;
    RecyclerView childrenRecyclerView;
    private List<ChildModel> childList;
    private int childId = -1;
    private ViewPager viewPager;
    private ViewPagerAdapter mPagerAdapter;

    TextView tvKnownName;
    TextView tvFullName;
    TextView tvDoB;
    TextView tvGender;
    MenuItem btnAdd;
    ChildModel childModel;
    LinearLayout childLayout;
    LinearLayoutManager horizontalLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);

        initialView();
        initialAppBar();
    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    private void initialView() {

        tvKnownName    = (TextView) findViewById(R.id.tvKnownName);
        tvFullName     = (TextView) findViewById(R.id.tvFullName);
        //tvLastName     = (TextView) findViewById(R.id.tvLastName);
        tvDoB          = (TextView) findViewById(R.id.tvDateOfBirth);
        tvGender       = (TextView) findViewById(R.id.tvGender);
        childLayout    = (LinearLayout) findViewById(R.id.mainView) ;
        viewPager      = (ViewPager) findViewById(R.id.viewpager);

        childrenRecyclerView= (RecyclerView) findViewById(R.id.childrenRecyclerView);
        childList = new ArrayList<>();
        adapter = new ChildListAdapter(this, childList, childrenRecyclerView);
        mPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mPagerAdapter.clearFrag();

        if (childrenRecyclerView != null) {

            horizontalLayoutManager = new LinearLayoutManager(ChildActivity.this, LinearLayoutManager.HORIZONTAL, false);
            childrenRecyclerView.setLayoutManager(horizontalLayoutManager);
            childrenRecyclerView.setAdapter(adapter);

            // get children models
            ConnUtil.getChildren(TAG, new BaseResponse<ChildModel[]>(this) {

                @Override
                public void onSuccess(Object sender, ChildModel[] models) {
                    super.onSuccess(sender, models);

                    adapter.setItems(models);
                    mPagerAdapter.setItems(models);
                    adapter.notifyDataSetChanged();
                    mPagerAdapter.notifyDataSetChanged();

                    childId = models[0].getId();
                    childModel = models[0];

                    // setup child detail fragment
                    for (int i = 0; i < models.length; i++) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(PARAM_KEY_MODEL, models[i]);
                        ChildDetailFragment fragment = new ChildDetailFragment();
                        fragment.setArguments(bundle);
                        mPagerAdapter.addFrag(fragment);
                    }

                    if (viewPager.getAdapter() == null)
                        viewPager.setAdapter(mPagerAdapter);
                    else
                        mPagerAdapter.notifyDataSetChanged();
                }
            });
        }

        childrenRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
           public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dx > 70) {
                    int currentItem = viewPager.getCurrentItem();
                    viewPager.setCurrentItem(currentItem + 1);
                    adapter.setAlphaBackground(currentItem + 1);
                }

            }
        });

        // view pager set on scroll listener
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                horizontalLayoutManager.scrollToPositionWithOffset(position, 230);
                adapter.setAlphaBackground(position);

            }

            @Override
            public void onPageSelected(int position) {

                childrenRecyclerView.smoothScrollToPosition(position);
                adapter.setAlphaBackground(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        adapter.setOnItemClickListener(new ChildListAdapter.onItemClickListener(){
            @Override
            public void onItemClick(View view, int position) {
                //adapter.notifyItemChanged(position);
                viewPager.setCurrentItem(position);
            }
        });
    }

    private void initialAppBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(GlobalApplication.getResString(R.string.children));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_add, menu);
        btnAdd = menu.findItem(R.id.action_add);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_add:
                Intent intent = new Intent(ChildActivity.this, ChildProfileActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private class ViewPagerAdapter extends FragmentStatePagerAdapter {
        // fragments to load
        private final List<ChildDetailFragment> mFragmentList = new ArrayList<>();
        private List<ChildModel> childList;

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        public void setItems(ChildModel[] items) {
            childList = Arrays.asList(items);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
        }

        public void addFrag(ChildDetailFragment fragment) {
            mFragmentList.add(fragment);
        }

        public void clearFrag() {
            mFragmentList.clear();
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }

        @Override
        public int getItemPosition(Object object) {

            return PagerAdapter.POSITION_NONE;
        }
    }
}
