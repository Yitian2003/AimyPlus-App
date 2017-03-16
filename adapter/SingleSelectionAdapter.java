package com.aimyplus.consumer.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aimyplus.consumer.R;
import com.aimyplus.consumer.activity.booking.ChangeSiteActivity;
import com.aimyplus.consumer.model.SiteModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SoK on 24/11/2016.
 */


public class SingleSelectionAdapter {//extends RecyclerView.Adapter<SingleSelectionAdapter.ViewHolder> {

    /*private List<SiteModel> items;
    private String selected_label;
    private SiteModel selectedSite;
    private AppCompatCheckBox selectedCheckBox;
    private Context context;
    private boolean communicateWithHost;

    public static final String ACTION = "action";
    public static final String DATA = "data";

    public SingleSelectionAdapter(Context context) {
        this.context = context;
    }

    public SingleSelectionAdapter(Context context, List<SiteModel> items, boolean communicateWithHost) {
        this.items = items;
        this.context = context;
        this.communicateWithHost = communicateWithHost;
    }

    public SingleSelectionAdapter(Context c, List<SiteModel> items) {
        this.context = c;
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SingleSelectionAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.single_selection_list,parent,false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        String site = items.get(position).getName();

        holder.label.setText(site);

        holder.main_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedSite = items.get(holder.getAdapterPosition());

                selected_label = selectedSite.getName();

                AppCompatCheckBox check_box = ((AppCompatCheckBox)((LinearLayout)v).getChildAt(0));
                if (selectedCheckBox == check_box)
                    return;
                if (selectedCheckBox != null)
                    selectedCheckBox.setChecked(false);
                check_box.setChecked(true);
                selectedCheckBox = check_box;

                *//*if (communicateWithHost) {
                    Intent intent = new Intent();
                    intent.setAction(ACTION);
                    intent.putExtra(DATA,items.get(holder.getAdapterPosition()));
                    context.sendBroadcast(intent);
                }*//*

            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public String getSelectedLabel() {
        return selected_label;
    }

    public void setItems(ArrayList<SiteModel> response) {
        items = new ArrayList<>(response);
    }

    public SiteModel getSelectedSite() {
        return selectedSite;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView label;
        public AppCompatCheckBox check_box;
        public LinearLayout main_layout;

        public ViewHolder(View itemView) {
            super(itemView);
            label = (TextView) itemView.findViewById(R.id.site_name);
            check_box = (AppCompatCheckBox) itemView.findViewById(R.id.check);
            check_box.setVisibility(View.GONE);
            main_layout = (LinearLayout) itemView.findViewById(R.id.main_layout);
        }
    }*/
}
