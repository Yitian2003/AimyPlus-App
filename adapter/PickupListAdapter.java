package com.aimyplus.consumer.adapter;

/**
 * Created by bruce on 27/02/2017.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aimyplus.consumer.R;
import com.aimyplus.consumer.app.GlobalApplication;
import com.aimyplus.consumer.model.user.ContactModel;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.aimyplus.consumer.model.Constant.ContactTypeId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PickupListAdapter extends BaseAdapter {

    private Context context;
    private List<ContactModel> items;
    private ImageLoader imageLoader;
    protected int DELETE_POS = -1;
    List<ContactModel> list;
    private final float min_opacity = 0.4f;
    private final float max_opacity = 1.0f;

    public PickupListAdapter(Context context) {
        this.context = context;
        this.items = new ArrayList<>();
        list = new ArrayList(Arrays.asList(items));
        imageLoader = GlobalApplication.getInstance().getImageLoader();
    }

    public PickupListAdapter(Context context, List<ContactModel> items) {

        this.context = context;
        this.items = items;
        list = new ArrayList(Arrays.asList(items));
        imageLoader = GlobalApplication.getInstance().getImageLoader();
    }

    public void setItems(List<ContactModel> items) {
        this.items = items;
    }

    public void setItems(ContactModel[] items) {
        this.items = Arrays.asList(items);
    }

    public void removeItem(int position) {

        list.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public ContactModel getItem(int location) {
        return items.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.item_pickup, parent, false);

        NetworkImageView profilePhoto = (NetworkImageView) convertView.findViewById(R.id.profilePhoto);
        TextView tvName     = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvType     = (TextView) convertView.findViewById(R.id.tvType);
        LinearLayout layoutPickup = (LinearLayout) convertView.findViewById(R.id.layoutPickup);

        // getting contact data for the row
        ContactModel item = items.get(position);

        // user profile pic
        profilePhoto.setDefaultImageResId(R.drawable.profile_circular_border_imageview);
        profilePhoto.setImageUrl(item.getImageUrl(), imageLoader);

        tvName.setText(item.getFirstName() + " " + item.getLastName());
        if (item.getCanPickup() == null) {
            tvType.setText(" ");
        } else if(!item.getCanPickup()) {
            tvType.setText(R.string.non_auth_pickup);
        } else {
            tvType.setText(R.string.auth_pickup);
        }

        if (item.getCanPickup() == null) {
            layoutPickup.setAlpha(min_opacity);
        } else {
            layoutPickup.setAlpha(max_opacity);
        }

        return convertView;
    }
}
