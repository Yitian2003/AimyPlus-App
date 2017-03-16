/**
 * Created by Yitian on 24/02/2017.
 */

package com.aimyplus.consumer.activity.user;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aimyplus.consumer.R;
import com.aimyplus.consumer.adapter.ChildListAdapter;
import com.aimyplus.consumer.adapter.ViewPagerPickupAdapter;
import com.aimyplus.consumer.app.GlobalApplication;
import com.aimyplus.consumer.base.BaseResponse;
import com.aimyplus.consumer.fragment.ChildDetailFragment;
import com.aimyplus.consumer.fragment.PickupDetailFragment;
import com.aimyplus.consumer.model.user.ChildModel;
import com.aimyplus.consumer.model.user.ContactModel;
import com.aimyplus.consumer.utils.ConnUtil;

import java.util.ArrayList;
import java.util.List;

public class ManagePickupActivity extends AppCompatActivity {

    private static final String TAG = ChildActivity.class.getSimpleName();
    public static final String PARAM_KEY_MODEL = "Model";
    public static final String PARAM_KEY_ID = "Child Id";

    private ChildListAdapter childListAdapter;
    RecyclerView recyclerViewChildren;
    private List<ChildModel> childModelList;
    private int childId = -1;
    private ViewPager viewPagerContact;
    private ViewPagerPickupAdapter pickupViewPagerAdapter;

    ChildModel childModel;
    ContactModel contactModel;
    LinearLayout childLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_pickup);

        initActionBar();
        initialView();
    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    private void initActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(GlobalApplication.getResString(R.string.manage_pickup));
        }
    }

    private void initialView() {

        childLayout = (LinearLayout) findViewById(R.id.mainView);
        viewPagerContact = (ViewPager) findViewById(R.id.viewpager);

        recyclerViewChildren = (RecyclerView) findViewById(R.id.childrenRecyclerView);
        childModelList = new ArrayList<>();
        childListAdapter = new ChildListAdapter(this, childModelList, recyclerViewChildren);
        pickupViewPagerAdapter = new ViewPagerPickupAdapter(getSupportFragmentManager());
        pickupViewPagerAdapter.clearFrag();

        if (recyclerViewChildren != null) {

            LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(ManagePickupActivity.this, LinearLayoutManager.HORIZONTAL, false);
            recyclerViewChildren.setLayoutManager(horizontalLayoutManager);
            recyclerViewChildren.setAdapter(childListAdapter);

            // get children models
            ConnUtil.getChildren(TAG, new BaseResponse<ChildModel[]>(this) {

                @Override
                public void onSuccess(Object sender, ChildModel[] models) {
                    super.onSuccess(sender, models);

                    childListAdapter.setItems(models);
                    childListAdapter.notifyDataSetChanged();

                    childId = models[0].getId();
                    childModel = models[0];

                    // setup child detail fragment
                    for (int i = 0; i < models.length; i++) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(PARAM_KEY_ID, models[i].getId());
                        PickupDetailFragment fragment = new PickupDetailFragment();
                        fragment.setArguments(bundle);
                        pickupViewPagerAdapter.addFrag(fragment);
                    }

                    if (viewPagerContact.getAdapter() == null)
                        viewPagerContact.setAdapter(pickupViewPagerAdapter);
                    else
                        pickupViewPagerAdapter.notifyDataSetChanged();
                }
            });

            ConnUtil.GetContactByChild(TAG, childId, new BaseResponse<ContactModel[]>(this) {
                @Override
                public void onSuccess(Object sender, ContactModel[] models) {
                    super.onSuccess(sender, models);

                    pickupViewPagerAdapter.setItems(models);
                    pickupViewPagerAdapter.notifyDataSetChanged();
                }
            });
        }

        viewPagerContact.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                recyclerViewChildren.smoothScrollToPosition(position);
                childListAdapter.setAlphaBackground(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        childListAdapter.setOnItemClickListener(new ChildListAdapter.onItemClickListener(){
            @Override
            public void onItemClick(View view, int position) {
                viewPagerContact.setCurrentItem(position);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
