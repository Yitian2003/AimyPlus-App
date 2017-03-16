// create by yitian on Feb 7, 2017
package com.aimyplus.consumer.activity.user;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import com.aimyplus.consumer.R;
import com.aimyplus.consumer.adapter.ConditionListAdapter;
import com.aimyplus.consumer.adapter.ContactListAdapter;
import com.aimyplus.consumer.app.GlobalApplication;
import com.aimyplus.consumer.base.BaseResponse;
import com.aimyplus.consumer.model.user.ConditionModel;
import com.aimyplus.consumer.model.user.ContactModel;
import com.aimyplus.consumer.utils.ConnUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ChildConditionActivity extends AppCompatActivity {

    private static final String TAG = ChildConditionActivity.class.getSimpleName();
    private ConditionListAdapter listAdapter;
    private List<ConditionModel> conditionItems;
    private ExpandableListView listview;
    //List<String> groupTitleList;
    public String PARAM_KEY_CHILDE_ID = "Child Id";
    public static final String PARAM_KEY_IS_NEW_CONDITION = "New Condition";
    private int childId = -1;
    MenuItem btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_condition);

        childId = getIntent().getIntExtra(PARAM_KEY_CHILDE_ID, -1);

        initialAppBar();
        initialList();
        setOnlinstener();
    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    private void initialList() {

        conditionItems = new ArrayList<>();
        listAdapter = new ConditionListAdapter(this, conditionItems);

        listview = (ExpandableListView) findViewById(R.id.childConditionList);
        if (listview != null) {
            listview.setAdapter(listAdapter);
        }

        ConnUtil.GetCondition(TAG, childId, new BaseResponse<ConditionModel[]>(this) {

            @Override
            public void onSuccess(Object sender, ConditionModel[] models) {
                super.onSuccess(sender, models);

                listAdapter.setItems(models);
                listAdapter.notifyDataSetChanged();
            }

            // go to the test data
            @Override
            public void onFailure(Object sender, String response) {
                super.onFailure(sender, response);

                addMockupData(); // mockup data, delete later
            }
        });
    }

    private void setOnlinstener() {
        listview.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                int len = listAdapter.getGroupCount();
                for (int i = 0; i < len; i++) {
                    if (i != groupPosition) {
                        listview.collapseGroup(i);
                    }
                }
            }
        });

        listview.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
            }
        });

        listview.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                return false;
            }
        });
    }

    private void initialAppBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(GlobalApplication.getResString(R.string.conditions));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_add:
                Intent intent = new Intent(ChildConditionActivity.this, EditMedicalConditionActivity.class);
                intent.putExtra(PARAM_KEY_IS_NEW_CONDITION, true);
                intent.putExtra(PARAM_KEY_CHILDE_ID, childId);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addMockupData(){
        ConditionModel[] models = new ConditionModel[6];
        ConditionModel model = new ConditionModel();

        List<String> title = Arrays.asList("A.D.H.D", "Asthma", "Diabetes", "Epilepsy", "Wheat Allergy", "Peanut Allergy");

        for (int i = 0 ; i < 6; i++) { // mock up data, delete later
            models[i] = new ConditionModel();
            models[i].setId(i);
            models[i].setName(title.get(i));
            models[i].setSeverity("Low");
            models[i].setSymptons("Can lose focus sometimes");
            models[i].setTreatment("Get child to focus on an object or a person");
            models[i].setDoctorName("Gary Wu");
            models[i].setDoctorContact("09-4093345");
        }

        listAdapter.setItems(models);
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_add, menu);
        btnAdd = menu.findItem(R.id.action_add);

        return true;
    }
}
