// Create by Yitian Jan21, 2017
package com.aimyplus.consumer.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.aimyplus.consumer.R;
import com.aimyplus.consumer.app.GlobalApplication;
import com.aimyplus.consumer.model.user.ChildModel;
import com.aimyplus.consumer.model.user.UserProfileModel;

public class EditChildProfileItemActivity extends AppCompatActivity {

    private String fieldName;
    public static final String PARAM_KEY_MODEL = "Model";
    public static final String PARAM_KEY_FIELD ="FieldName";
    public static final String PARAM_VALUE_FIELD = "EditString";

    DatePicker datePickerDB;
    MenuItem btnSave;
    ChildModel passedModel;
    String toolBarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_child_profile_item);

        datePickerDB = (DatePicker) findViewById(R.id.editDB);

        Intent intent = getIntent();
        if (intent != null) {
            fieldName = intent.getStringExtra(PARAM_KEY_FIELD);
            String editString = intent.getStringExtra(PARAM_VALUE_FIELD);
            passedModel = (ChildModel) intent.getSerializableExtra(PARAM_KEY_MODEL);

            switch (fieldName) {

                case "Date Of Birth":
                    if (editString == null) {
                        editString = "";
                    }

                    if (!editString.isEmpty()) {
                        String[] parts = editString.split("-");
                        int monthInit = Integer.parseInt(parts[1]) - 1;
                        int dayInit = Integer.parseInt(parts[2].substring(0, 2));
                        int yearInit = Integer.parseInt(parts[0]);
                        datePickerDB.init(yearInit, monthInit, dayInit, new DatePicker.OnDateChangedListener() {
                            @Override
                            public void onDateChanged(DatePicker view, int year, int month, int day) {
                                btnSave.setEnabled(true);
                            }
                        });
                    } else {
                        datePickerDB.init(2010, 06, 15, new DatePicker.OnDateChangedListener() {
                            @Override
                            public void onDateChanged(DatePicker view, int year, int month, int day) {
                                btnSave.setEnabled(true);
                            }
                        });
                    }

                    datePickerDB.setVisibility(View.VISIBLE);
                    toolBarTitle = "Change Date of Birth";
                    break;
                default:
                    break;
            }
        }

        initActionBar();
    }

    private void initActionBar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(toolBarTitle);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_save_profile, menu);
        btnSave = menu.findItem(R.id.action_profile_save);
        btnSave.setEnabled(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_profile_save:

                btnSave.setEnabled(false);
                Intent intent = new Intent(EditChildProfileItemActivity.this, ChildProfileActivity.class);
                switch (fieldName) {

                    case "Date Of Birth":
                        String date = "";
                        date = datePickerDB.getYear() + "-" + (datePickerDB.getMonth() + 1) + "-" + datePickerDB.getDayOfMonth()
                                + " 00:00:00";
                        intent.putExtra(PARAM_VALUE_FIELD, date);
                        setResult(ChildProfileActivity.RESULT_OK, intent);
                        finish();
                        break;
                    default:
                        break;
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
