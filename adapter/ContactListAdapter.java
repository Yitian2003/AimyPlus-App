/**
 * Created by Yitian on 18/02/2017.
 */

package com.aimyplus.consumer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aimyplus.consumer.R;
import com.aimyplus.consumer.app.GlobalApplication;
import com.aimyplus.consumer.model.user.ContactModel;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ContactListAdapter  extends BaseAdapter {

    private Context context;
    private List<ContactModel> items;
    private ImageLoader imageLoader;
    protected int DELETE_POS = -1;
    List<ContactModel> list;

    public ContactListAdapter(Context context) {
        this.context = context;
        this.items = new ArrayList<>();
        list = new ArrayList(Arrays.asList(items));
        imageLoader = GlobalApplication.getInstance().getImageLoader();
    }

    public ContactListAdapter(Context context, List<ContactModel> items) {

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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false);

        NetworkImageView profilePhoto = (NetworkImageView) convertView.findViewById(R.id.profilePhoto);
        TextView tvName     = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvType     = (TextView) convertView.findViewById(R.id.tvType);
        /*TextView tvLandline = (TextView) convertView.findViewById(R.id.tvLandline);
        TextView tvMobile   = (TextView) convertView.findViewById(R.id.tvMobile);
        TextView tvOffice   = (TextView) convertView.findViewById(R.id.tvOffice);*/

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

        /*tvLandline.setText(item.getLandline());

        tvOffice.setText(item.getOffice());
        tvMobile.setText(item.getMobile());*/

        return convertView;
    }
}
