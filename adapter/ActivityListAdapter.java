package com.aimyplus.consumer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aimyplus.consumer.R;
import com.aimyplus.consumer.app.GlobalApplication;
import com.aimyplus.consumer.model.program.HolidayActivityModel;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActivityListAdapter extends BaseAdapter {

    private Context context;
    private List<HolidayActivityModel> items;
    private ImageLoader imageLoader;

    public ActivityListAdapter(Context context) {

        this.context = context;
        this.items = new ArrayList<>();
        imageLoader = GlobalApplication.getInstance().getImageLoader();
    }

    public ActivityListAdapter(Context context, List<HolidayActivityModel> items) {

        this.context = context;
        this.items = items;
        imageLoader = GlobalApplication.getInstance().getImageLoader();
    }

    public ActivityListAdapter(Context context, HolidayActivityModel[] items) {

        this.context = context;
        this.items = Arrays.asList(items);
        imageLoader = GlobalApplication.getInstance().getImageLoader();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.item_activity, parent, false);

        TextView tvName            = (TextView)         convertView.findViewById(R.id.tvActivityName);
        TextView tvDescription     = (TextView)         convertView.findViewById(R.id.tvActivityDescription);
        TextView tvDate            = (TextView)         convertView.findViewById(R.id.tvActivityDate);
        NetworkImageView imageView = (NetworkImageView) convertView.findViewById(R.id.ivActivityImage);

        HolidayActivityModel item = items.get(position);

        imageView.setDefaultImageResId(R.drawable.google_thumb);
        imageView.setImageUrl(item.getImageUrl(), imageLoader);

        tvName.setText(item.getName());
        tvDescription.setText(item.getDescription());
        tvDate.setText(item.getLocalDate());

        return convertView;
    }
}