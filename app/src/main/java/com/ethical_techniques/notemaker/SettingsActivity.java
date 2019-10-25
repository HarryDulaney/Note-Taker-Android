package com.ethical_techniques.notemaker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initSortOrderClick();
        initSortByClick();
        initListButton();
        initNotesButton();
        initSettingsButton();
        initSettings();
    }

    private void initSortByClick(){
        RadioGroup rgSortBy = (RadioGroup) findViewById(R.id.radioGroupSortBy);
        rgSortBy.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {


            @SuppressLint("ApplySharedPref")
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                RadioButton rbHigh = (RadioButton) findViewById(R.id.radioHigh);
                RadioButton rbMedium = (RadioButton) findViewById(R.id.radioMedium);


                if (rbHigh.isChecked()) {
                    getSharedPreferences("NoteMakerPreferences",
                            Context.MODE_PRIVATE).edit()
                            .putString("sortfield", "high").commit();
                } else if (rbMedium.isChecked()) {
                    getSharedPreferences("NoteMakerPreferences",
                            Context.MODE_PRIVATE).edit()
                            .putString("sortfield", "medium").commit();
                } else {
                    getSharedPreferences("NoteMakerPreferences",
                            Context.MODE_PRIVATE).edit()
                            .putString("sortfield", "low").commit();
                }
            }

        });
    }
    private void initSortOrderClick(){
        RadioGroup rgSortOrder = findViewById(R.id.radioGroupSortOrder);
        rgSortOrder.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

            @SuppressLint("ApplySharedPref")
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rbAscending = findViewById(R.id.radioAscending);
                if (rbAscending.isChecked()){
                    getSharedPreferences("NoteMakerPreferences",
                            Context.MODE_PRIVATE).edit()
                            .putString("sortorder","ASC").commit();
                }else {
                    getSharedPreferences("NoteMakerPreferences",
                            Context.MODE_PRIVATE).edit()
                            .putString("sortorder","DESC").commit();

                }

            }
        });
    }

    private void initSettings() {
        String sortBy = getSharedPreferences("NoteMakerPreferences",
                Context.MODE_PRIVATE).getString("sortfield", "prioritylevel");

        String sortOrder = getSharedPreferences("NoteMakerPreferences",
                Context.MODE_PRIVATE).getString("sortorder", "ASC");


        RadioButton rbHigh = findViewById(R.id.radioHigh);
        RadioButton rbMedium = findViewById(R.id.radioMedium);
        RadioButton rbLow = findViewById(R.id.radioLow);

        if (sortBy.equalsIgnoreCase("high")) {
            rbHigh.setChecked(true);

        } else if (sortBy.equalsIgnoreCase("medium")) {
            rbMedium.setChecked(true);
        } else {
            rbLow.setChecked(true);
        }

        RadioButton rbAscending = findViewById(R.id.radioAscending);
        RadioButton rbDescending = findViewById(R.id.radioDescending);

        if (sortOrder.equalsIgnoreCase(("ASC"))) {
            rbAscending.setChecked(true);

        } else {
            rbDescending.setChecked(true);
        }

    }




    private void initNotesButton() {
        ImageButton noteButton = findViewById(R.id.imageButtonNote);
        noteButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, NoteActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
    private void initListButton(){
        ImageButton listButton = findViewById(R.id.imageButtonList);
        listButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                Intent intent = new Intent(SettingsActivity.this,ListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

    }
    private void initSettingsButton(){
        ImageButton settingButton = findViewById(R.id.imageButtonSettings);
        settingButton.setEnabled(false);
    }
}
