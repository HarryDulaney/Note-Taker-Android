package com.ethical_techniques.notemaker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    Note currentNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle extras = getIntent().getExtras();

        if(extras != null){
            initNote(extras.getInt("noteID"));
        }else {

            currentNote = new Note();
        }


        initNotesButton();
        initListButton();
        initSettingsButton();
    }


    private void initNotesButton() {
        ImageButton noteButton = findViewById(R.id.imageButtonNote);
        noteButton.setEnabled(false);

    }
    private void initListButton(){
        ImageButton listButton = findViewById(R.id.imageButtonList);
        listButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }


        });

    }
    private void initSettingsButton(){
        ImageButton settingButton = findViewById(R.id.imageButtonSettings);
        settingButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


    }

    private void initNote(int id){

        NoteDataSource ds = new NoteDataSource(MainActivity.this);
        try{
            ds.open();
            currentNote = ds.getSpecificContact(id);
            ds.close();

        } catch (Exception e) {
            Toast.makeText(this,"Load Note Failed",Toast.LENGTH_LONG).show();
        }

        EditText editName = findViewById(R.id.editTitle);
        EditText editSubject = findViewById(R.id.editSubject);
        EditText editNote = findViewById(R.id.editNotes);


        editName.setText(currentNote.getNoteName());
        editSubject.setText(currentNote.getSubject());
        editNote.setText(currentNote.getContent());

    }
    private void initSaveButton(){
        Button saveButton = (Button)findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view){
                hideKeyboard();
                boolean wasSuccess = false;
                NoteDataSource dataSource = new NoteDataSource(MainActivity.this);
                try{
                    dataSource.open();

                    if(currentNote.getNoteID() == -1) {
                        wasSuccess = dataSource.insertNote(currentNote);

                        if(wasSuccess) {
                            int newId = dataSource.getLastContactId();
                            currentNote.setNoteID(newId);}

                    }else {
                        wasSuccess = dataSource.updateContact(currentNote);

                    }
                    dataSource.close();
                }catch(Exception e1){
                    wasSuccess = false;

                }
            }
        });
    }
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        EditText editName = (EditText) findViewById(R.id.editTitle);
        assert imm != null;
        imm.hideSoftInputFromWindow(editName.getWindowToken(), 0);
        EditText editSubject = findViewById(R.id.editSubject);
        imm.hideSoftInputFromWindow(editSubject.getWindowToken(),0);
        EditText editNote = findViewById(R.id.editNotes);
        imm.hideSoftInputFromWindow(editNote.getWindowToken(),0);

    }
}
