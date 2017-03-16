/**
 * Created by Yitian on 22/02/2017.
 */

package com.aimyplus.consumer.activity.user;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.aimyplus.consumer.R;

public class ChooseConditionPhotoActivity extends AppCompatActivity {

    public static final String PARAM_KEY_IS_NEW_CHILD = "New Child";

    Button btnBack;
    Button btnAdd;
    Button btnUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_condition_photo);

        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnBack = (Button)findViewById(R.id.btnBack);
        btnUpload = (Button)findViewById(R.id.btnUpload);

        setOnClickListen();
    }

    private void setOnClickListen() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseConditionPhotoActivity.this, EditMedicalConditionActivity.class);
                intent.putExtra(PARAM_KEY_IS_NEW_CHILD, true);
                startActivity(intent);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseConditionPhotoActivity.this, ChildActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseConditionPhotoActivity.this, UploadChildPhotoActivity.class);
                startActivity(intent);
            }
        });
    }
}
