package com.ethical_techniques.notemaker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

public class NoteActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    private Note currentNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle extras = getIntent().getExtras();

        if(extras != null){
            initNote(extras.getInt("noteid"));
        }else {

            currentNote = new Note();
        }


        initNotesButton();
        initListButton();
        initSettingsButton();
        initTextChangedEvents();
        initSaveButton();
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
                Intent intent = new Intent(NoteActivity.this,ListActivity.class);
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
                Intent intent = new Intent(NoteActivity.this,SettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


    }

    private void initNote(int id){

        NoteDataSource ds = new NoteDataSource(NoteActivity.this);
        try{
            ds.open();
            currentNote = ds.getSpecificNote(id);
            ds.close();

        } catch (Exception e) {
            Log.e(TAG,String.valueOf(e));
            Snackbar.make(findViewById(R.id.note_activity),
                    "Something went wrong loading your note, please try again",
                    Snackbar.LENGTH_INDEFINITE);
        }

        EditText editName = findViewById(R.id.editTitle);
        EditText editSubject = findViewById(R.id.editSubject);
        EditText editNote = findViewById(R.id.editNotes);


        editName.setText(currentNote.getNoteName());
        editSubject.setText(currentNote.getSubject());
        editNote.setText(currentNote.getContent());

    }

    private void initTextChangedEvents() {
        final EditText etNoteName = findViewById(R.id.editTitle);
        etNoteName.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                currentNote.setNoteName(etNoteName.getText().toString());

            }
        });


        final EditText etNoteSubject = findViewById(R.id.editSubject);
        etNoteSubject.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                currentNote.setSubject(etNoteSubject.getText().toString());

            }
        });

    final EditText etNoteBody = findViewById(R.id.editNotes);
        etNoteBody.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            currentNote.setSubject(etNoteBody.getText().toString());

        }
    });

}
        private void initSaveButton () {
            Button saveButton = (Button) findViewById(R.id.saveButton);
            saveButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    hideKeyboard();
                    boolean wasSuccess = false;
                    NoteDataSource dataSource = new NoteDataSource(NoteActivity.this);
                    try {
                        dataSource.open();

                        if (currentNote.getNoteID() == -1) {
                            wasSuccess = dataSource.insertNote(currentNote);

                            if (wasSuccess) {
                                int newId = dataSource.getLastNoteId();
                                currentNote.setNoteID(newId);
                            }

                        } else {
                            wasSuccess = dataSource.updateNote(currentNote);

                        }
                        dataSource.close();
                    } catch (Exception e1) {
                        Log.e(TAG, String.valueOf(e1));

                    }
                }
            });
        }
        private void hideKeyboard(){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            EditText editName = (EditText) findViewById(R.id.editTitle);
            assert imm != null;
            imm.hideSoftInputFromWindow(editName.getWindowToken(), 0);
            EditText editSubject = findViewById(R.id.editSubject);
            imm.hideSoftInputFromWindow(editSubject.getWindowToken(), 0);
            EditText editNote = findViewById(R.id.editNotes);
            imm.hideSoftInputFromWindow(editNote.getWindowToken(), 0);

        }
    }

