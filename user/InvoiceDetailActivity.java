package com.aimyplus.consumer.activity.user;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aimyplus.consumer.R;
import com.aimyplus.consumer.base.BaseResponse;
import com.aimyplus.consumer.model.user.InvoiceLineModel;
import com.aimyplus.consumer.model.user.InvoiceModel;
import com.aimyplus.consumer.utils.ConnUtil;

import java.util.List;

public class InvoiceDetailActivity extends AppCompatActivity {

    public static final String PARAM_KEY_ID = "id";
    private static final String TAG = InvoiceDetailActivity.class.getSimpleName();

    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_detail);

        Intent intent = getIntent();
        if (intent != null) {
            id = intent.getIntExtra(PARAM_KEY_ID, 0);
        }

        if (id > 0) {
            populateInvoiceDetails();
        }
    }

    private void populateInvoiceDetails(){

        ConnUtil.getInvoice(TAG, id, new BaseResponse<InvoiceModel>(this) {

            @Override
            public void onSuccess(Object sender, InvoiceModel model) {
                super.onSuccess(sender, model);

                initialAppBar(model.getInvoiceCode());

                TextView tvAmountDue = (TextView) findViewById(R.id.tvAmountDue);
                if (tvAmountDue != null) {
                    tvAmountDue.setText(model.getAmountDueString());
                }

                TextView tvDueDate = (TextView) findViewById(R.id.tvDueDate);
                if (tvDueDate != null) {
                    tvDueDate.setText(model.getLocalDueDate());
                }

                TextView tvCreatedOn = (TextView) findViewById(R.id.tvCreatedOn);
                if (tvCreatedOn != null) {
                    tvCreatedOn.setText(model.getLocalInvoiceDate());
                }

                TextView tvReference = (TextView) findViewById(R.id.tvReference);
                if (tvReference != null) {
                    tvReference.setText(model.getReference());
                }

                TextView tvStatus = (TextView) findViewById(R.id.tvStatus);
                if (tvStatus != null) {
                    tvStatus.setText(model.getStatusString());
                }

                if (model.getInvoiceLines() != null && model.getInvoiceLines().size() > 0) {
                    setupInvoiceLines(model.getInvoiceLines());
                }
            }
        });
    }

    private void setupInvoiceLines(List<InvoiceLineModel> invoiceLines){

        LinearLayout invoiceLineLayout = (LinearLayout) findViewById(R.id.layoutInvoiceLine);

        for (int i = 0; i < invoiceLines.size(); i++) {

            LayoutInflater inflater = getLayoutInflater();

            View view = inflater.inflate(R.layout.item_invoice_line, invoiceLineLayout, false);

            TextView tvChildName   = (TextView)view.findViewById(R.id.invoice_line_childname);
            TextView tvProgramName = (TextView)view.findViewById(R.id.invoice_line_programename);
            TextView tvLineAmount  = (TextView)view.findViewById(R.id.invoice_line_amount);
            TextView tvDescription = (TextView)view.findViewById(R.id.invoice_line_invlinedesc);

            InvoiceLineModel invoiceLine = invoiceLines.get(i);
            if (invoiceLine != null) {
                tvChildName.setText(invoiceLine.getChildName());
                tvProgramName.setText(invoiceLine.getProgramName());
                tvLineAmount.setText(invoiceLine.getAmountString());
                if(invoiceLine.getDescription() != null){
                    tvDescription.setText(invoiceLine.getDescription().trim());
                }
            }

            if (invoiceLineLayout != null) {
                invoiceLineLayout.addView(view);
            }
        }
    }

    private void initialAppBar(String title) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(title);
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
}
