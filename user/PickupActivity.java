/**
 * Created by Yitian on 24/02/2017.
 */

package com.aimyplus.consumer.activity.user;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import com.aimyplus.consumer.R;
import com.aimyplus.consumer.adapter.ContactListAdapter;
import com.aimyplus.consumer.app.GlobalApplication;
import com.aimyplus.consumer.base.BaseResponse;
import com.aimyplus.consumer.model.Constant.ContactTypeId;
import com.aimyplus.consumer.model.user.ContactModel;
import com.aimyplus.consumer.utils.ConnUtil;

import java.util.ArrayList;
import java.util.List;

public class PickupActivity extends AppCompatActivity {

    private static final String TAG = ContactActivity.class.getSimpleName();
    public static final String PARAM_KEY_TYPE = "Type";
    private ContactListAdapter listAdapter;
    ListView listview;
    MenuItem btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup);

        initialAppBar();
        initialList();
    }

    private void initialList() {

        List<ContactModel> contactItems = new ArrayList<>();
        listAdapter = new ContactListAdapter(this, contactItems);

        listview = (ListView) findViewById(R.id.list);
        if (listview != null) {
            listview.setAdapter(listAdapter);

        }

        ConnUtil.GetPickup(TAG, new BaseResponse<ContactModel[]>(this) {

            @Override
            public void onSuccess(Object sender, ContactModel[] models) {
                super.onSuccess(sender, models);

                listAdapter.setItems(models);
                listAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initialAppBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(GlobalApplication.getResString(R.string.pickups));
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
                Intent intent = new Intent(PickupActivity.this, EditContactActivity.class);
                intent.putExtra(PARAM_KEY_TYPE, ContactTypeId.NonAuthorisedPickup.getValue());
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
