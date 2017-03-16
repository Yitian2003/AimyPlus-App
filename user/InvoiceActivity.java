package com.aimyplus.consumer.activity.user;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.aimyplus.consumer.R;
import com.aimyplus.consumer.adapter.InvoiceListAdapter;
import com.aimyplus.consumer.app.GlobalApplication;
import com.aimyplus.consumer.base.BaseResponse;
import com.aimyplus.consumer.model.user.InvoiceModel;
import com.aimyplus.consumer.utils.ConnUtil;

import java.util.ArrayList;
import java.util.List;

public class InvoiceActivity extends AppCompatActivity {

    private static final String TAG = InvoiceActivity.class.getSimpleName();

    private InvoiceListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        initialAppBar();
        initialInvoiceList();
    }

    private void initialAppBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(GlobalApplication.getResString(R.string.invoices));
        }
    }

    private void initialInvoiceList(){

        List<InvoiceModel> invoiceList = new ArrayList<>();
        adapter = new InvoiceListAdapter(this,invoiceList);

        ListView listView = (ListView) findViewById(R.id.invoice_list);
        if (listView != null) {
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    InvoiceModel invoice = adapter.getItem(position);
                    Intent intent = new Intent(InvoiceActivity.this, InvoiceDetailActivity.class);
                    intent.putExtra(InvoiceDetailActivity.PARAM_KEY_ID, invoice.getId());
                    startActivity(intent);
                }
            });
        }

        ConnUtil.getInvoiceList(TAG, new BaseResponse<InvoiceModel[]>(this){

            @Override
            public void onSuccess(Object sender, InvoiceModel[] models) {
                super.onSuccess(sender, models);
                adapter.setItems(models);
                adapter.notifyDataSetChanged();
            }
        });
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
}