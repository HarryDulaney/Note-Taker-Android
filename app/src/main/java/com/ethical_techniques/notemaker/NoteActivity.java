package com.ethical_techniques.notemaker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class NoteActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    private Note currentNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Bundle , if initialized, comes from ListActivity.onItemClicked
        Bundle extras = getIntent().getExtras();

        if(extras != null){
            //Load the note uses the noteID
            initNote(extras.getInt("noteid"));
        }else {
            //create a new blank note
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

    /**
     * Opens a connection to the database and uses getSpecificNote() to retrieve the
     * Note and then sets the TextViews for with the current notes values
     * @param id identifier for the note
     */
    private void initNote(int id){

        NoteDataSource ds = new NoteDataSource(NoteActivity.this);
        try{
            ds.open();
            currentNote = ds.getSpecificNote(id);
            ds.close();

        } catch (Exception e) {
            Log.w(TAG,e);
            Snackbar.make(findViewById(R.id.note_activity),
                    "Something went wrong loading your note, please try again",
                    Snackbar.LENGTH_INDEFINITE);
        }

        EditText editName = findViewById(R.id.editTitle);
        EditText editSubject = findViewById(R.id.editSubject);
        EditText editNote = (EditText) findViewById(R.id.editNotes);
        TextView dateCreated = findViewById(R.id.dateCreatedText);


        editName.setText(currentNote.getNoteName());
        editSubject.setText(currentNote.getSubject());
        editNote.setText(currentNote.getContent());
        dateCreated.setText(DateFormat.format("MM/dd/yyyy",currentNote.getDateCreated()));

    }

    /**
     * Sets event listener TextWatcher to each of the input fields in the NoteActivity
     */
    private void initTextChangedEvents() {
        final EditText etNoteName = findViewById(R.id.editTitle);
        etNoteName.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            /**
             * Updates the current Note with the users input
             * @param editable reference to the editTitle EditText
             */

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

    final EditText etNoteBody =(EditText) findViewById(R.id.editNotes);
        etNoteBody.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            currentNote.setNoteContent(etNoteBody.getText().toString());

        }
    });


}

        private void initSaveButton () {

            Button saveButton = findViewById(R.id.saveButton);
            saveButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    if (currentNote.getNoteName() == null || currentNote.getContent() == null) {
                        Toast.makeText(getBaseContext(), "Make sure to fill in the name and the " +
                                "\n note content fields of the note before saving", Toast.LENGTH_LONG).show();
                    }else {

                        currentNote.setDateCreated(Calendar.getInstance());

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
                            Log.w(TAG, e1);
                            wasSuccess = false;

                        }

                        if (wasSuccess) {
                            Toast.makeText(getBaseContext(), "Success, Your new note was saved. " +
                                    "\nClick on the List icon on the navigation bar to manage your notes. ", Toast.LENGTH_LONG).show();
                           }
                        }
                    }
            });

        }

        private void hideKeyboard(){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            EditText editName = findViewById(R.id.editTitle);
            assert imm != null;
            imm.hideSoftInputFromWindow(editName.getWindowToken(), 0);
            EditText editSubject = findViewById(R.id.editSubject);
            imm.hideSoftInputFromWindow(editSubject.getWindowToken(), 0);
            EditText editNote = (EditText) findViewById(R.id.editNotes);
            imm.hideSoftInputFromWindow(editNote.getWindowToken(), 0);

        }
    }

