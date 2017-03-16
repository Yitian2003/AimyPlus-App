// Modified by Yitian Feb 6, 2017
package com.aimyplus.consumer.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aimyplus.consumer.R;
import com.aimyplus.consumer.activity.user.ChildActivity;
import com.aimyplus.consumer.app.GlobalApplication;
import com.aimyplus.consumer.model.user.ChildModel;
import com.aimyplus.consumer.ui.CircularNetworkImageView;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ChildListAdapter extends RecyclerView.Adapter<ChildListAdapter.MyViewHolder> {

    private Context context;
    private List<ChildModel> items;
    private ImageLoader imageLoader;
    private final float min_opacity = 0.4f;
    private final float max_opacity = 1.0f;
    private RecyclerView recyclerView;
    private int lastCheckedPos = 0;

    public onItemClickListener mListener;

    public ChildListAdapter(Context context) {

        this.context = context;
        this.items = new ArrayList<>();
        imageLoader = GlobalApplication.getInstance().getImageLoader();
    }

    public ChildListAdapter(Context context, List<ChildModel> items, RecyclerView recyclerView) {

        this.context = context;
        this.items = items;
        imageLoader = GlobalApplication.getInstance().getImageLoader();
        this.recyclerView = recyclerView;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_child, parent, false);

        return new MyViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final ChildModel item = items.get(position);

        //holder.ivPhoto.setDefaultImageResId(R.drawable.ic_action_profile);
        holder.ivPhoto.setImageUrl(item.getImageUrl(), imageLoader);
        holder.tvName.setText(item.getFirstName());
        holder.tvName.setTextColor(ContextCompat.getColor(context, R.color.color_field_value));

       if (position != lastCheckedPos) {
            holder.ivPhoto.setAlpha(min_opacity);
           holder.tvName.setTextColor(ContextCompat.getColor(context, R.color.color_field_title));
       }

        holder.layoutChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    int position = holder.getLayoutPosition();
                    mListener.onItemClick(holder.layoutChild, position);

                    //holder.ivPhoto.getLayoutPosition();
                    holder.ivPhoto.setAlpha(max_opacity);
                    holder.tvName.setTextColor(ContextCompat.getColor(context, R.color.color_field_value));

                    if (lastCheckedPos != holder.getLayoutPosition()) {
                        MyViewHolder holder = (MyViewHolder) recyclerView.findViewHolderForLayoutPosition(lastCheckedPos);

                        if (holder != null) {
                            holder.ivPhoto.setAlpha(min_opacity);
                            holder.tvName.setTextColor(ContextCompat.getColor(context, R.color.color_field_title));
                        }
                    }

                    lastCheckedPos = holder.getLayoutPosition();
                    notifyItemChanged(lastCheckedPos);
                }
            }
        });
    }

    public void setActiveUserPosition(int position) {
        if (lastCheckedPos != position) {
            int oldPosition = lastCheckedPos;
            lastCheckedPos = position;
            notifyItemChanged(oldPosition);
            notifyItemChanged(position);
        }
    }

    public void setAlphaBackground(int position) {

        //notifyItemChanged(position);
        MyViewHolder currentHolder = (MyViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
        MyViewHolder previousHolder = (MyViewHolder) recyclerView.findViewHolderForAdapterPosition(lastCheckedPos);
        currentHolder.ivPhoto.setAlpha(max_opacity);
        previousHolder.ivPhoto.setAlpha(min_opacity);
        currentHolder.tvName.setTextColor(ContextCompat.getColor(context, R.color.color_field_value));
        previousHolder.tvName.setTextColor(ContextCompat.getColor(context, R.color.color_field_title));
        lastCheckedPos = position;
        notifyItemChanged(position);
    }

    public interface onItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        mListener = listener;
    }

    /*public void onItemHolderClick(MyViewHolder holder) {
        if (mListener != null)
            mListener.onItemClick(holder.itemView, holder.getAdapterPosition());
    }*/

    @Override
    public int getItemCount()
    {
        return items.size();
    }

    public void setItems(ChildModel[] items) {
        this.items = Arrays.asList(items);
    }

    public ChildModel getItem(int location) {
        return items.get(location);
    }


    //-------------------------------------------------------

    public class MyViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {

        private ChildListAdapter mAdapter;
        CircularNetworkImageView ivPhoto;
        TextView tvName;
        LinearLayout layoutChild;
        //private View view;

        public MyViewHolder(View view, final ChildListAdapter mAdapter) {
            super(view);

            ivPhoto = (CircularNetworkImageView) view.findViewById(R.id.childPicture);
            tvName = (TextView) view.findViewById(R.id.lblName);
            layoutChild = (LinearLayout) view.findViewById(R.id.layoutChild);

            this.mAdapter = mAdapter;
            //this.view = view;

            //view.setOnClickListener(this);
        }

        /*@Override
        public void onClick(View view) {
            lastCheckedPos = getAdapterPosition();
            notifyItemRangeChanged(0, items.size());
            mAdapter.onItemHolderClick(MyViewHolder.this);
        }*/

    }
}
