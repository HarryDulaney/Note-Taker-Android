package com.ethical_techniques.notemaker;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.ethical_techniques.notemaker.DAL.DBUtil;
import com.ethical_techniques.notemaker.auth.BaseActivity;
import com.ethical_techniques.notemaker.listeners.TextWatcherImpl;
import com.ethical_techniques.notemaker.model.Note;
import com.ethical_techniques.notemaker.model.NoteCategory;
import com.ethical_techniques.notemaker.model.PRIORITY;
import com.ethical_techniques.notemaker.utils.DialogUtil;
import com.google.android.material.snackbar.Snackbar;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;


/**
 * The type Main activity.
 * Contains the UI for creating and editing the Notes. <p>As well, for assigning/ re-assigning
 * / creating new categories </p>
 *
 * <p> Main Activity also includes the Navigation Drawer</p>
 *
 * @author Harry Dulaney
 */
public class NoteActivity extends BaseActivity {

    private final String TAG = this.getClass().getSimpleName();
    private Note currentNote;
    private List<NoteCategory> categories;
    private NoteCategory currentNoteCategory;
    Spinner dropDownSpinner;
    private MenuItem priorityStar;
    ArrayAdapter<String> categoryArrayAdapter;
    private boolean editMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        //Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_note);
        setSupportActionBar(toolbar);
        // Get the Toolbar back as an ActionBar and initialize the back button (Up/Home Button)
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        dropDownSpinner = findViewById(R.id.categorySpinner);

        /* Check and Load info based from previous activity */
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            //Load the note using the noteID
            initNote(extras.getInt(getString(R.string.NOTE_ID_KEY)));
        } else {
            //create a new note
            currentNote = new Note();

        }

        //Initialize listeners on text input fields
        initTextChangedEvents();

    }


    @Override
    protected void onResume() {
        super.onResume();
        //Refresh dropdown Spinner
        if (!refreshCategories()) {
            Snackbar.make(findViewById(R.id.note_activity), "We had an issue loading categories, please try reloading the current screen.", Snackbar.LENGTH_LONG);
        }
    }

    @Override
    public void onBackPressed() {
        boolean success = saveNote(currentNote);
        if (success) {
            Toast.makeText(this, "Success, Your new note was saved. " +
                            "\nClick on the List icon on the navigation bar to manage your notes. ",
                    Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(this, "Something went wrong, Your note could not be saved. " +
                            "\nClick on the List icon on the navigation bar to manage your notes. ",
                    Toast.LENGTH_LONG).show();

        }

        Intent intent2 = new Intent(NoteActivity.this, ListActivity.class);
        startActivity(intent2);
    }

    /**
     * Displays a save dialog to warn the user to save changes the note before
     * moving back up to a previous activity
     */
    protected void backClickedSavePrompt() {

        List<View> views = new ArrayList<>();
        final EditText title = findViewById(R.id.editTitle);
        final EditText notes = findViewById(R.id.editNotes);
        hideKeyboard(this, title, notes);

        if (title.length() != 0 || notes.length() != 0) {
            DialogUtil.makeAndShow(this,
                    "Unsaved changes detected",
                    "Hit SAVE to save changes before going back or DON'T SAVE to continue to your Notes list without saving.",
                    "SAVE", "DON'T SAVE",
                    () -> {
                        handleSaveNote();
                        NoteActivity.super.onBackPressed();
                    },
                    NoteActivity.super::onBackPressed
            );
        } else {
            NoteActivity.super.onBackPressed();
        }

    }


    /**
     * Initializes and refreshes the categories list, by accessing saved categories.
     *
     * @return true if operation was successful
     */
    private boolean refreshCategories() {
        try {
            categories = DBUtil.getCategories(this);

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        initDropDown();
        return true;
    }

    /**
     * Init drop down.
     */
    void initDropDown() {
        List<String> catDnames = NoteCategory.getDisplayNames(categories);

        categoryArrayAdapter = new ArrayAdapter<>(this,
                R.layout.dropdown_item_simple, catDnames);
        dropDownSpinner.setAdapter(categoryArrayAdapter);

        if (currentNote.getNoteID() > -1) {
            dropDownSpinner.setSelection(NoteCategory.getPosition(currentNoteCategory.getName(), catDnames));
        } else {
            dropDownSpinner.setSelection(0);
        }
        dropDownSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String checkedTextView = (String) parent.getItemAtPosition(position);
                System.out.println("checkedTextView = " + checkedTextView);
                currentNote.setNoteCategory(categories.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * Opens a connection to the database and uses getSpecificNote() to retrieve the
     * Note and then sets the TextViews with the current note values
     *
     * @param id identifier for the note
     */
    private void initNote(int id) {
        try {
            currentNote = DBUtil.findNote(this, id);
            currentNoteCategory = DBUtil.findCategory(this, currentNote.getNoteCategory().getId());
            editMode = true;
        } catch (Exception e) {
            Log.w(TAG, e);
            Snackbar.make(findViewById(R.id.note_activity),
                    Objects.requireNonNull(e.getMessage()),
                    Snackbar.LENGTH_INDEFINITE);
        }

        EditText editName = findViewById(R.id.editTitle);
        EditText editNote = findViewById(R.id.editNotes);

        editName.setText(currentNote.getNoteName());
        editNote.setText(currentNote.getContent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar_top_menu, menu);
        priorityStar = menu.findItem(R.id.star);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem star = menu.findItem(R.id.star);
        if (editMode) {
            //Set the star to the current note's priority level
            handleTogglePriorityStar(star, currentNote.getPRIORITY_LEVEL().equals(PRIORITY.high()));
            priorityStar = star;
        }

        return true;
    }

    /**
     * Switches the priority star on and off
     */
    @SuppressWarnings("deprecation")
    private void handleTogglePriorityStar(MenuItem item, boolean colorTheStar) {
        Drawable starDrawable = item.getIcon();
        if (starDrawable != null) {
            starDrawable.mutate();
            if (colorTheStar) {
                starDrawable.setColorFilter(ContextCompat.getColor(this, R.color.colorPriorityHigh), PorterDuff.Mode.SRC_ATOP);

                Toast.makeText(this, "The current note is set to high priority",
                        Toast.LENGTH_SHORT).show();
            } else {
                starDrawable.setColorFilter(null);
                Toast.makeText(this, "The current note is set to regular priority",
                        Toast.LENGTH_SHORT).show();

            }
        }
    }


    /**
     * @param item that was selected
     * @return true if the item is recognized
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks.
        switch (item.getItemId()) {
            case R.id.save: {
                handleSaveNote();
                return true;
            }
            case R.id.star: {
                if (currentNote.getPRIORITY_LEVEL().equals(PRIORITY.high())) {
                    currentNote.setPRIORITY_LEVEL(PRIORITY.low());
                    handleTogglePriorityStar(priorityStar, false);
                } else {
                    currentNote.setPRIORITY_LEVEL(PRIORITY.high());
                    handleTogglePriorityStar(priorityStar, true);
                }
                return true;
            }
            case android.R.id.home: {
                backClickedSavePrompt();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }


    /**
     * Sets event listener TextWatcher to each of the input fields in the NoteActivity
     */
    private void initTextChangedEvents() {
        final EditText etNoteName = findViewById(R.id.editTitle);
        etNoteName.addTextChangedListener(new TextWatcherImpl() {
            /**
             * Updates the current Note with the users input
             * @param editable reference to the editTitle EditText
             */

            @Override
            public void afterTextChanged(Editable editable) {
                currentNote.setNoteName(etNoteName.getText().toString());

            }
        });

        final EditText etNoteBody = findViewById(R.id.editNotes);
        etNoteBody.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable editable) {
                currentNote.setNoteContent(etNoteBody.getText().toString());
            }
        });
    }

    /**
     * Save button clicked
     */
    public void handleSaveNote() {
        hideKeyboard(this, findViewById(R.id.editTitle), findViewById(R.id.editNotes));

        boolean success = saveNote(currentNote);

        if (success) {
            Toast.makeText(this, "Success, Your new note was saved. " +
                            "\nClick on the List icon on the navigation bar to manage your notes. ",
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Something went wrong while saving the note. " +
                            "\nPlease try again by creating a new note. ",
                    Toast.LENGTH_LONG).show();
        }
        Intent intent2 = new Intent(NoteActivity.this, ListActivity.class);
        startActivity(intent2);
    }

    private boolean saveNote(Note note) {
        note.setDateCreated(Calendar.getInstance());

        boolean success = false;
        try {
            success = DBUtil.saveNote(NoteActivity.this, note);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return success;

    }
}
