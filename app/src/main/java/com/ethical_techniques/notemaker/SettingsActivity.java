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

    /**
     * Initializes the sortBy RadioGroup. Sets listener to check
     * the users choice and then commits it to persistent memory
     */
    private void initSortByClick(){
        RadioGroup rgSortBy = findViewById(R.id.radioGroupSortBy);
        rgSortBy.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {


            @SuppressLint("ApplySharedPref")
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                RadioButton rbPriority = findViewById(R.id.radioPriorityLevel);
                //RadioButton rbDate = findViewById();


                if (rbPriority.isChecked()) {
                    getSharedPreferences("NoteMakerPreferences",
                            Context.MODE_PRIVATE).edit()
                            .putString("sortfield", "priority").commit();
                } else {
                    getSharedPreferences("NoteMakerPreferences",
                            Context.MODE_PRIVATE).edit()
                            .putString("sortfield", "datecreated").commit();
                }
            }

        });
    }

    /**
     * Initializes the sort order RadioGroup.
     * User selection is checked then committed to persistent memory
     */
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

    /**
     * Reads in Shared Preferences from memory and sets the RadioButtons to the
     * user specific settings
     */
    private void initSettings() {
        //Creates string to hold default value 'priority' for sortBy.
        String sortBy = getSharedPreferences("NoteMakerPreferences",
                Context.MODE_PRIVATE).getString("sortfield", "priority");
        //Default String value for sortOrder 'Ascending'
        String sortOrder = getSharedPreferences("NoteMakerPreferences",
                Context.MODE_PRIVATE).getString("sortorder", "ASC");


        RadioButton rbPriority = findViewById(R.id.radioPriorityLevel);
        RadioButton rbDate = findViewById(R.id.radioSetByDate);

        if (sortBy.equalsIgnoreCase("priority")) {
            rbPriority.setChecked(true);

        } else {
            rbDate.setChecked(true);
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
