/**
 * Created by Yitian on 20/02/2017.
 */

package com.aimyplus.consumer.activity.user;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.aimyplus.consumer.R;
import com.aimyplus.consumer.app.GlobalApplication;
import com.aimyplus.consumer.base.BaseResponse;
import com.aimyplus.consumer.model.user.ConditionModel;
import com.aimyplus.consumer.utils.ConnUtil;

import java.util.zip.Inflater;

public class EditMedicalConditionActivity extends AppCompatActivity {

    private static final String TAG = EditMedicalConditionActivity.class.getSimpleName();
    public static final String PARAM_KEY_MODEL = "Model";
    public static final String PARAM_KEY_CHILDE_ID = "Child Id";
    public static final String PARAM_KEY_IS_NEW_CHILD = "New Child";
    public static final String PARAM_KEY_IS_NEW_CONDITION = "New Condition";
    final Context context = this;

    MenuItem btnSave;
    ConditionModel model;
    TextView tvMedicalName;
    TextView tvSeverity;
    EditText editSymptoms;
    EditText editTreatment;
    LinearLayout layoutMedicalName;
    LinearLayout layoutSeverity;
    BottomSheetBehavior behavior;
    View dialogView;
    String[] medNameArray;
    ListView listView;
    int prePosition;
    boolean isNewChild;
    boolean isNewCondition;
    int childId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_medical_condition);

        tvMedicalName = (TextView) findViewById(R.id.tvMedicalName);
        tvSeverity = (TextView) findViewById(R.id.tvSeverity);
        editSymptoms = (EditText) findViewById(R.id.editSymptoms);
        editTreatment = (EditText) findViewById(R.id.editTreatment);
        layoutMedicalName = (LinearLayout) findViewById(R.id.layoutMedicalName);
        layoutSeverity = (LinearLayout) findViewById(R.id.layoutSeverity);

        dialogView = findViewById(R.id.layoutNameList);
        behavior = BottomSheetBehavior.from(dialogView);
        listView = (ListView) findViewById(R.id.medNameList);
        medNameArray = getResources().getStringArray(R.array.medNames);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        prePosition = -1;

        Intent intent = getIntent();
        model = new ConditionModel();

        isNewChild = intent.getBooleanExtra(PARAM_KEY_IS_NEW_CHILD, false);
        isNewCondition = intent.getBooleanExtra(PARAM_KEY_IS_NEW_CONDITION, false);
        childId = intent.getIntExtra(PARAM_KEY_CHILDE_ID, 0);

        if (isNewChild || isNewCondition) {
            if (isNewCondition) {
                model.setChildId(childId);
            }
            initialNewConditionView();
        } else {
            model = (ConditionModel) intent.getSerializableExtra(PARAM_KEY_MODEL);
            initialView();
        }

        setClickListener();
        setTextChangeListener();
    }

    private void initialView() {

        initActionBar();

        String med = tvMedicalName.getText().toString();

        if (model.getConditionId() > 0) {
            tvMedicalName.setText(model.getName());
        } else {
            tvMedicalName.setText(model.getOtherName());
        }
        tvSeverity.setText(model.getSeverity());
        editSymptoms.setText(model.getSymptons());
        editTreatment.setText(model.getTreatment());
        editSymptoms.setSelection(editSymptoms.getText().length());
        tvMedicalName.requestFocus();

        listView.setAdapter(new ArrayAdapter<String>(EditMedicalConditionActivity.this,
                android.R.layout.simple_list_item_1, medNameArray));
        listView.setItemChecked(getConditionPosition(med), true);

        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
    }

    private void initialNewConditionView() {
        initNewActionBar();

        listView.setAdapter(new ArrayAdapter<String>(EditMedicalConditionActivity.this,
                android.R.layout.simple_list_item_1, medNameArray));

        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    private void initNewActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(GlobalApplication.getResString(R.string.add_medical_condition));
        }
    }

    private void initActionBar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(GlobalApplication.getResString(R.string.edit_medical_condition));
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
                if ((tvMedicalName.getText().toString()).isEmpty()) {
                    btnSave.setEnabled(true);
                    Toast.makeText(context, R.string.medical_name_required, Toast.LENGTH_SHORT).show();
                } else {
                    if (isNewChild) {
                        // new child, so save data, go to upload photo page
                        Intent intent = new Intent(EditMedicalConditionActivity.this, ChooseConditionPhotoActivity.class);
                        // need to pass id
                        startActivity(intent);
                        finish();
                    } else {
                        /*if (isNewCondition) {

                            model.setConditionId(getConditionTypeId(tvMedicalName.getText().toString()));
                            model.setSeverity(tvSeverity.getText().toString());
                            model.setSymptons(editSymptoms.getText().toString());
                            model.setTreatment(editTreatment.getText().toString());
                            updateConditionModel();
                            finish();

                            Intent intent = new Intent(EditMedicalConditionActivity.this, ChildConditionActivity.class);
                            startActivity(intent);
                        }*/
                        model.setConditionId(getConditionTypeId(tvMedicalName.getText().toString()));
                        model.setSeverity(tvSeverity.getText().toString());
                        model.setSymptons(editSymptoms.getText().toString());
                        model.setTreatment(editTreatment.getText().toString());
                        updateConditionModel();
                        finish();
                    }

                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateConditionModel() {
        ConnUtil.postChildCondition(TAG, model, new BaseResponse<ConditionModel>(this) {

            @Override
            public void onSuccess(Object sender, ConditionModel response) {
                super.onSuccess(sender, response);
                Toast.makeText(EditMedicalConditionActivity.this, "pass condition model success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Object sender, String response) {
                super.onFailure(sender, response);
                Toast.makeText(EditMedicalConditionActivity.this, "pass condition model fail", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setTextChangeListener() {
        editSymptoms.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnSave.setEnabled(true);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        editTreatment.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnSave.setEnabled(true);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setClickListener() {
        layoutMedicalName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }

                View childTextView = null;
                int position = listView.getCheckedItemPosition();
                if (position != prePosition) {
                    childTextView = getViewByPosition(position, listView);
                    childTextView.setBackgroundColor(getResources().getColor(R.color.__picker_selected_bg));
                    if (prePosition != -1) {
                        childTextView = getViewByPosition(prePosition, listView);
                        childTextView.setBackgroundColor(getResources().getColor(android.R.color.white));
                    }
                    prePosition = position;
                }

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        if (i != 12) {
                            if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            } else {
                                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                            }
                            String secletedName = (String) adapterView.getAdapter().getItem(i);
                            tvMedicalName.setText(secletedName);
                            btnSave.setEnabled(true);
                        } else {
                            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(EditMedicalConditionActivity.this);
                            LayoutInflater inflater = getLayoutInflater();
                            View dialogView = inflater.inflate(R.layout.dialog_edit_others, null);
                            dialogBuilder.setView(dialogView);
                            dialogBuilder.setTitle(R.string.other_med_name);
                            final AlertDialog alertDialog = dialogBuilder.create();
                            final EditText editOthers = (EditText) dialogView.findViewById(R.id.editOthers);
                            final Button btnDone = (Button) dialogView.findViewById(R.id.btnDone);
                            Button btnCancel = (Button) dialogView.findViewById(R.id.btnCancel);
                            btnDone.setEnabled(false);

                            btnDone.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                                        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                                    } else {
                                        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                    }
                                    String secletedName = editOthers.getText().toString();
                                    tvMedicalName.setText(secletedName);
                                    alertDialog.dismiss();
                                    btnSave.setEnabled(true);
                                }
                            });

                            btnCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    alertDialog.dismiss();
                                }
                            });

                            alertDialog.show();

                            editOthers.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                }

                                @Override
                                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                    btnDone.setEnabled(true);
                                }

                                @Override
                                public void afterTextChanged(Editable editable) {

                                }
                            });
                        }

                    }
                });
            }
        });

        layoutSeverity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(EditMedicalConditionActivity.this);
                dialogBuilder.setTitle(R.string.severity);

                int setItemPosition;

                if (model.getSeverity() != null) {
                    setItemPosition = getSeverityInt();
                } else {
                    setItemPosition = 0;
                }

                dialogBuilder.setSingleChoiceItems(R.array.severities, setItemPosition, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            tvSeverity.setText(R.string.low);
                        } else if (which == 1) {
                            tvSeverity.setText(R.string.moderate);
                        } else if (which == 2) {
                            tvSeverity.setText(R.string.high);
                        }
                        btnSave.setEnabled(true);
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
            }
        });
    }

    private View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    private int getSeverityInt() {

        int severityInt = 0;

        switch (model.getSeverity()) {
            case "Low":
                severityInt = 0;
                break;
            case "Moderate":
                severityInt = 1;
                break;
            case "High":
                severityInt = 2;
                break;
            default:
                severityInt = 0;
                break;
        }

        return severityInt;
    }

    private int getConditionPosition(String name) {
        int position;

        switch (name) {
            case "Asthma":
                position = 0;
                break;
            case "A.D.H.D":
                position = 1;
                break;
            case "Epilepsy":
                position = 2;
                break;
            case "Haemophilia":
                position = 3;
                break;
            case "Heart Problems":
                position = 4;
                break;
            case "Peanut Allergy":
                position = 5;
                break;
            case "Tree Nut Allergy":
                position = 6;
                break;
            case "Bee Sting Allergy":
                position = 7;
                break;
            case "Medicine Allergy":
                position = 8;
                break;
            case "Diabetes":
                position = 9;
                break;
            case "Other":
                position = 10;
                break;
            default:
                position = -1;
                break;
        }
        return position;
    }

    private int getConditionTypeId(String name) {
        int conditionTypeId;

        switch (name) {
            case "Asthma":
                conditionTypeId = 1;
                break;
            case "A.D.H.D":
                conditionTypeId = 2;
                break;
            case "Epilepsy":
                conditionTypeId = 3;
                break;
            case "Haemophilia":
                conditionTypeId = 4;
                break;
            case "Heart Problems":
                conditionTypeId = 5;
                break;
            case "Peanut Allergy":
                conditionTypeId = 8;
                break;
            case "Tree Nut Allergy":
                conditionTypeId = 9;
                break;
            case "Bee Sting Allergy":
                conditionTypeId = 10;
                break;
            case "Medicine Allergy":
                conditionTypeId = 11;
                break;
            case "Diabetes":
                conditionTypeId = 12;
                break;
            case "Other":
                conditionTypeId = 13;
                break;
            case "Heart attack":
                conditionTypeId = 16;
                break;
            case "Dairy Allergy":
                conditionTypeId = 17;
                break;
            case "Wheat Allergy":
                conditionTypeId = 20;
                break;
            case "Rheumatic fever":
                conditionTypeId = 21;
                break;
            case "Celiac":
                conditionTypeId = 22;
                break;
            default:
                conditionTypeId = -1;
                break;
        }
        return conditionTypeId;
    }
}