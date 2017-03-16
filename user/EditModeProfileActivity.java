package com.aimyplus.consumer.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.aimyplus.consumer.R;
import com.aimyplus.consumer.app.GlobalApplication;
import com.aimyplus.consumer.base.IResponse;
import com.aimyplus.consumer.model.user.UserProfileModel;
import com.aimyplus.consumer.utils.ConnUtil;
import com.aimyplus.consumer.utils.UIUtil;

public class EditModeProfileActivity extends AppCompatActivity {

    public static final String FIELD_FIRST_NAME = "firstName";
    public static final String FIELD_LAST_NAME = "lastName";
    public static final String FIELD_DATE_OF_BIRTH = "birthday";
    public static final String FIELD_MOBILE = "mobile";
    public static final String FIELD_LAND_LINE= "landline";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_ADDRESS = "address";
    public static final String FIELD_BILLING_ADDRESS = "firstName";
    public static final String FIELD_OSCAR = "oscar";


    // Log tag
    private static final String TAG = EditModeProfileActivity.class.getSimpleName();

    private String name;
    private String lastName;
    private String dateBirthday;
    private String userName;

    TextView textViewName;
    TextView textViewDB;
    TextView textViewLastName;
    TextView textViewUserName;
    TextView textViewTitleUserName;
    TextView textViewTitleName;
    TextView textViewTitleLastName;
    TextView textViewTitleDateBirthday;

//
//    String strCurrentDate = dateBirthday;
//    SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss Z");
//    Date newDate = format.parse(strCurrentDate);
//
//    format = new SimpleDateFormat("YY-MM-DD");
//    String date = format.format(newDate);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        /*textViewTitleUserName = (TextView) findViewById(R.id.txtTitleUserName);
        textViewTitleName = (TextView) findViewById(R.id.txtTitleName);
        textViewTitleLastName = (TextView) findViewById(R.id.txtTitleLastName);
        textViewTitleDateBirthday = (TextView) findViewById(R.id.txtTitleDateBirthday);*/
        textViewName = (TextView) findViewById(R.id.editName);
        textViewUserName = (TextView) findViewById(R.id.editUsername);
        textViewLastName = (TextView) findViewById(R.id.editLastName);
//        textViewDB = (TextView) findViewById(R.id.b);

        Intent intent = getIntent();
        if(intent != null) {
            String fieldName = intent.getStringExtra("FieldName");

            switch (fieldName) {
                case "Username" :
                    textViewUserName.setVisibility(View.VISIBLE);
                    //textViewTitleUserName.setVisibility(View.VISIBLE);
                    break;
                case "Name":
                    textViewName.setVisibility(View.VISIBLE);
                    //textViewTitleName.setVisibility(View.VISIBLE);
                    break;
                case "Date Birthday":
                    textViewDB.setVisibility(View.VISIBLE);
                    //textViewTitleDateBirthday.setVisibility(View.VISIBLE);
                    break;
                case "Last Name":
                    textViewLastName.setVisibility(View.VISIBLE);
                    //textViewTitleLastName.setVisibility(View.VISIBLE);
                default:
                    break;
            }

        }

        int userId = GlobalApplication.getInstance().getCurrentUser().getId();



        ConnUtil.getUserProfile(EditModeProfileActivity.class.getSimpleName(), userId, new IResponse<UserProfileModel>() {

            @Override
            public void onBegin(Object sender) {

            }

            @Override
            public void onSuccess(Object sender, UserProfileModel response) {
                Toast.makeText(UIUtil.getContext(), "success", Toast.LENGTH_SHORT).show();

                //Getting name
                name = response.getFirstName();
                textViewName.setText(name);

                //Getting Lastname
                lastName = response.getLastName();
                textViewLastName.setText(lastName);

                //Getting Date Birthday
                dateBirthday = response.getDateOfBirth();
                textViewDB.setText(dateBirthday);

                //Getting UserName
                userName = response.getEmail();
                textViewUserName.setText(userName);

            }

            @Override
            public void onFailure(Object sender, String response) {

                Toast.makeText(UIUtil.getContext(), response, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ....");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ...");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ...");
    }
}

