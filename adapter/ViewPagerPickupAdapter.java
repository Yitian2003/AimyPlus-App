package com.aimyplus.consumer.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.ViewGroup;

import com.aimyplus.consumer.fragment.ChildDetailFragment;
import com.aimyplus.consumer.fragment.PickupDetailFragment;
import com.aimyplus.consumer.model.user.ChildModel;
import com.aimyplus.consumer.model.user.ContactModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Yitian on 24/02/2017.
 */

public class ViewPagerPickupAdapter extends FragmentStatePagerAdapter {

    private final List<PickupDetailFragment> mPickupFragmentList = new ArrayList<>();
    private List<ContactModel> contactList;

    public ViewPagerPickupAdapter(FragmentManager manager) {
        super(manager);
    }

    public void setItems(ContactModel[] items) {
        contactList = Arrays.asList(items);
    }

    @Override
    public Fragment getItem(int position) {
        return mPickupFragmentList.get(position);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
    }

    public void addFrag(PickupDetailFragment fragment) {
        mPickupFragmentList.add(fragment);
    }

    public void clearFrag() {
        mPickupFragmentList.clear();
    }

    @Override
    public int getCount() {
        return mPickupFragmentList.size();
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
