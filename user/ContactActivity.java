// modified by Yitian Jan 29, 2017
// Out of date Mar 13

package com.aimyplus.consumer.activity.user;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.aimyplus.consumer.R;
import com.aimyplus.consumer.adapter.ContactListAdapter;
import com.aimyplus.consumer.app.GlobalApplication;
import com.aimyplus.consumer.base.BaseResponse;
import com.aimyplus.consumer.model.user.ContactModel;
import com.aimyplus.consumer.utils.ConnUtil;

import java.util.ArrayList;
import java.util.List;

public class ContactActivity extends AppCompatActivity {

    private static final String TAG = ContactActivity.class.getSimpleName();
    private ContactListAdapter listAdapter;
    protected FloatingActionButton mFab;
    private GestureDetector gestureDetector;  // gesture detector
    View.OnTouchListener gestureListener;
    ListView listview;
    private final int INVALID = -1;
    protected int DELETE_POS = -1;
    Button delete;
    Long then;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        //then = 0;

        // gesture detection
        gestureDetector = new GestureDetector(this, new GestureListener());

        initialAppBar();
        initialList();
    }

    private void initialList() {

        List<ContactModel> contactItems = new ArrayList<>();
        listAdapter = new ContactListAdapter(this, contactItems);

        listview = (ListView) findViewById(R.id.list);
        if (listview != null) {
            listview.setAdapter(listAdapter);

            /*listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ContactModel contact = listAdapter.getItem(position);
                    Intent intent = new Intent(ContactActivity.this, EditContactActivity.class);  /////////////////////
                    intent.putExtra("Contact Model", contact); /////////////////////////
                    startActivity(intent);
                }
            });*/


            listview.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                   return gestureDetector.onTouchEvent(event);
                    /*if(event.getAction() == MotionEvent.ACTION_DOWN){
                        then = (Long) System.currentTimeMillis();
                    }
                    else if(event.getAction() == MotionEvent.ACTION_UP){
                        if(((Long) System.currentTimeMillis() - then) > 1200){
                            return true;
                        }
                    }
                    return false;*/
                }

            });
        }

        ConnUtil.GetContact(TAG, new BaseResponse<ContactModel[]>(this) {

            @Override
            public void onSuccess(Object sender, ContactModel[] models) {
                super.onSuccess(sender, models);

                listAdapter.setItems(models);
                listAdapter.notifyDataSetChanged();
            }
        });

        setupFab();
    }

    private void initialAppBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(GlobalApplication.getResString(R.string.contacts));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupFab() {

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(ContactActivity.this, EditContactActivity.class);/////////////////
                startActivity(intent);
            }
        });
    }

    class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                            hideDeleteButton(e1);
                        } else {
                            onSwipeLeft();
                            showDeleteButton(e1);
                        }
                    }
                    result = true;
                }
                else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        //onSwipeBottom();
                    } else {
                        //onSwipeTop();
                    }
                }
                result = true;

            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }

        @Override
        public void onLongPress(MotionEvent event) {
            Toast.makeText(ContactActivity.this, "Longpress detected", Toast.LENGTH_SHORT).show();
           /* View child=listview.findChildViewUnder(event.getX(),event.getY());
            if(child!=null && clicklistener!=null){
                clicklistener.onLongClick(child,recycleView.getChildAdapterPosition(child));
            }*/
            //super.onLongPress(event);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            Toast.makeText(ContactActivity.this, "Single tap", Toast.LENGTH_SHORT).show();
            int position = listview.pointToPosition((int)event.getX(), (int)event.getY());
            ContactModel contact = listAdapter.getItem(position);
            Intent intent = new Intent(ContactActivity.this, EditContactActivity.class);  /////////////////////
            intent.putExtra("Contact Model", contact); /////////////////////////
            startActivity(intent);
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            //Log.d(DEBUG_TAG, "onSingleTapConfirmed: " + event.toString());
            return true;
        }
    }

    public void onSwipeRight() {
        Toast.makeText(ContactActivity.this, "right", Toast.LENGTH_SHORT).show();
    }

    public void onSwipeLeft() {
        Toast.makeText(ContactActivity.this, "left", Toast.LENGTH_SHORT).show();
    }

    private boolean hideDeleteButton(MotionEvent event){
        int pos = listview.pointToPosition((int)event.getX(), (int)event.getY());
        return hideDeleteButton(pos);
    }

    private boolean hideDeleteButton(final int pos) {
        View child = listview.getChildAt(pos);
        if (child != null){
            //delete = (Button) child.findViewById(R.id.delete_button_id);
            if (delete != null)
                if (delete.getVisibility() == View.VISIBLE)
                    delete.setVisibility(View.GONE);
                else
                    delete.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

    private boolean showDeleteButton(MotionEvent e1) {
        int pos = listview.pointToPosition((int)e1.getX(), (int)e1.getY());
        return showDeleteButton(pos);
    }

    private boolean showDeleteButton(final int pos) {
        View child = listview.getChildAt(pos);
        if (child != null){
           // delete = (Button) child.findViewById(R.id.delete_button_id);
            if (delete != null)
                if (delete.getVisibility() == View.GONE)
                    delete.setVisibility(View.VISIBLE);
                else
                    delete.setVisibility(View.VISIBLE);
            delete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    deleteItem(pos);
                }
            });
            return true;
        }
        return false;
    }

    public void deleteItem(int pos) {
        //
        listAdapter.removeItem(pos);
        DELETE_POS = INVALID;
    }

}
