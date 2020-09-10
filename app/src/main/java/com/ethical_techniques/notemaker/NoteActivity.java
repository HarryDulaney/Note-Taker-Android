package com.ethical_techniques.notemaker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ethical_techniques.notemaker.model.Note;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;

public class NoteActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private Note currentNote;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
         * Load info on user actions from resource bundle
         */
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            //Load the note using the noteID
            initNote(extras.getInt("noteid"));
        } else {
            //create a new blank note
            currentNote = new Note();
        }
        toolbar = findViewById(R.id.action_bar_top);
        initToolBar();

        initTextChangedEvents();
        initRadioButtons();
        initSaveButton();
    }

    private void initToolBar() {
        if (toolbar != null) {
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.action_bar_settings:
                            // User chose the "Settings" item, show the app settings UI...
                            Intent intent = new Intent(NoteActivity.this, SettingsActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            return true;

                        case R.id.action_bar_list:
                            Intent intent2 = new Intent(NoteActivity.this, ListActivity.class);
                            intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent2);
                            return true;

                        case R.id.action_bar_new:
                            return true;

                        default:
                            return false;

                    }

                }
            });

        }
    }

    /**
     * Opens a connection to the database and uses getSpecificNote() to retrieve the
     * Note and then sets the TextViews with the current note values
     *
     * @param id identifier for the note
     */
    private void initNote(int id) {

        NoteDataSource ds = new NoteDataSource(NoteActivity.this);
        try {
            ds.open();
            currentNote = ds.getSpecificNote(id);
            ds.close();

        } catch (Exception e) {
            Log.w(TAG, e);
            Snackbar.make(findViewById(R.id.note_activity),
                    "Something went wrong loading your note, please try again",
                    Snackbar.LENGTH_INDEFINITE);
        }

        EditText editName = findViewById(R.id.editTitle);
        EditText editSubject = findViewById(R.id.editSubject);
        EditText editNote = findViewById(R.id.editNotes);
        RadioButton rbHigh = findViewById(R.id.radioHigh);
        RadioButton rbMedium = findViewById(R.id.radioMedium);
        RadioButton rbLow = findViewById(R.id.radioLow);


        editName.setText(currentNote.getNoteName());
        editSubject.setText(currentNote.getSubject());
        editNote.setText(currentNote.getContent());


        //Check the correct radiobutton for the Note being loaded
        if (currentNote.getPriorityLevel() == 3) {
            rbHigh.setChecked(true);
        } else if (currentNote.getPriorityLevel() == 2) {
            rbMedium.setChecked(true);
        } else {
            rbLow.setChecked(true);
        }


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

        final EditText etNoteBody = (EditText) findViewById(R.id.editNotes);
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

    private void initRadioButtons() {

        final RadioGroup radioButtons = findViewById(R.id.radioGroupPriority);
        final RadioButton rbHigh = findViewById(R.id.radioHigh);
        final RadioButton rbMedium = findViewById(R.id.radioMedium);
        radioButtons.setOnCheckedChangeListener((group, checkedId) -> {

            RadioButton selectedButton = findViewById(checkedId);

            if (selectedButton.getId() == rbHigh.getId()) {
                currentNote.setPriorityLevel(3);

            } else if (selectedButton.getId() == rbMedium.getId()) {
                currentNote.setPriorityLevel(2);

            } else {
                currentNote.setPriorityLevel(1);
            }
        });

    }

    /**
     * Initializes the Save button and sets the behavior to call on
     * the Database Object. Checks if the Note is a new note using the default ID
     * value of -1.
     */
    private void initSaveButton() {

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {

                if (currentNote.getNoteName() == null || currentNote.getContent() == null) {
                    Toast.makeText(getBaseContext(), "Make sure to fill in the name and the " +
                            "\n note content fields of the note before saving", Toast.LENGTH_LONG).show();
                } else {

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
                                        "\nClick on the List icon on the navigation bar to manage your notes. ",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }

    private void hideKeyboard() {
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

