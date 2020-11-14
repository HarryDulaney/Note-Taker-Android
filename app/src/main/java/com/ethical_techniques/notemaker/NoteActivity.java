package com.ethical_techniques.notemaker;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;

import com.ethical_techniques.notemaker.DAL.DBUtil;
import com.ethical_techniques.notemaker.auth.AppFlowActivity;
import com.ethical_techniques.notemaker.auth.BaseActivity;
import com.ethical_techniques.notemaker.listeners.TextWatcherImpl;
import com.ethical_techniques.notemaker.model.Category;
import com.ethical_techniques.notemaker.model.Note;
import com.ethical_techniques.notemaker.model.PRIORITY;
import com.ethical_techniques.notemaker.utils.DialogAction;
import com.ethical_techniques.notemaker.utils.DialogUtil;
import com.google.android.gms.common.util.Strings;
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
    private List<Category> categories;
    private Category currentNoteCategory;
    Spinner dropDownSpinner;
    MenuItem priorityStar;
    ArrayAdapter<String> categoryArrayAdapter;


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


        /* Check and Load info based from previous activity */
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            //Load the note using the noteID
            initNote(extras.getInt(getString(R.string.NOTE_ID_KEY)));
        } else {
            //create a new blank note
            currentNote = new Note();
            //Refresh dropdown Spinner
            dropDownSpinner = findViewById(R.id.categorySpinner);
            if (initCategories()) {
                initDropDown(dropDownSpinner);
            }

        }//Initialize listeners on text input fields
        initTextChangedEvents();

    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        backClickedSavePrompt();
    }

    /**
     * Displays a save dialog to warn the user to save changes the note before
     * moving back up to a previous activity
     */
    protected void backClickedSavePrompt() {

        List<View> views = new ArrayList<>();
        final EditText title = findViewById(R.id.editTitle);
        final EditText notes = findViewById(R.id.editNotes);
        views.add(title);
        views.add(notes);
        hideKeyboard(this, views);

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
     * Initializes the categories list.
     * Handles adding the default category (Un-Categorized) in the event that
     * it has not been persisted into database yet.
     *
     * @return true if operation was successful
     */
    private boolean initCategories() {
        try {
            categories = DBUtil.getCategories(this);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Init drop down.
     *
     * @param spinner the spinner
     */
    void initDropDown(Spinner spinner) {
        List<String> categoryStrings = new ArrayList<>();
        // Add all Categories names to the list for the dropdown spinner
        for (Category category : categories) {
            categoryStrings.add(category.getName());
        }

        categoryArrayAdapter = new ArrayAdapter<>(this,
                R.layout.dropdown_item_simple, categoryStrings);
        spinner.setAdapter(categoryArrayAdapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String checkedTextView = (String) parent.getItemAtPosition(position);
                System.out.println("checkedTextView = " + checkedTextView);
                currentNote.setCategory(categories.get(position).getId());
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
            currentNoteCategory = DBUtil.findCategory(this, currentNote.getCategory());

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
        dropDownSpinner = findViewById(R.id.categorySpinner);

        if (initCategories()) {
            initDropDown(dropDownSpinner);
        }
        int currentPosition = 0;
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getName().equals(currentNoteCategory.getName())) {
                currentPosition = i;
                break;
            }

        }
        // Set the priority level ie color in star
        initPriorityStar(currentNote);
    }

    /**
     * Set's RatingBar to the Note's saved value
     *
     * @param note populating the Activity fields
     */
    private void initPriorityStar(Note note) {
        if (priorityStar != null) {
            if (note.getPRIORITY_LEVEL().equals(PRIORITY.HIGH.getString())) {
                handleTogglePriorityStar(priorityStar, true);
            } else {
                handleTogglePriorityStar(priorityStar, false);
            }
        }
    }

    /**
     * Initializes the top app bar.
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar_top_menu, menu);
        priorityStar = menu.findItem(R.id.action_bar_priority_star);
        return true;
    }

    /**
     * Switches the priority star on and off
     */
    private void handleTogglePriorityStar(MenuItem menuItem, boolean colorTheStar) {
        Drawable starDrawable = menuItem.getIcon();
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
            case R.id.action_bar_save_button: {
                handleSaveNote();
                return true;
            }
            case R.id.action_bar_priority_star: {
                handleTogglePriorityStar(item, currentNote.getPRIORITY_LEVEL().equals(PRIORITY.HIGH.getString()));
                if (currentNote.getPRIORITY_LEVEL().equals(PRIORITY.HIGH.getString())) {
                    currentNote.setPRIORITY_LEVEL(PRIORITY.LOW.getString());
                } else {
                    currentNote.setPRIORITY_LEVEL(PRIORITY.HIGH.getString());
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
        List<View> views = new ArrayList<>();
        views.add(findViewById(R.id.editTitle));
        views.add(findViewById(R.id.editNotes));

        if (views.size() > 0) {
            hideKeyboard(this, views);
        }

        if (currentNote.getNoteName() == null || currentNote.getContent() == null) {
            Toast.makeText(NoteActivity.this, "Make sure to fill in the name and the " +
                    "\n note content fields of the note before saving", Toast.LENGTH_LONG).show();
        } else {
            currentNote.setDateCreated(Calendar.getInstance());

            boolean success = false;
            try {
                success = DBUtil.saveNote(NoteActivity.this, currentNote);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (success) {
                Toast.makeText(this, "Success, Your new note was saved. " +
                                "\nClick on the List icon on the navigation bar to manage your notes. ",
                        Toast.LENGTH_LONG).show();

                Intent intent2 = new Intent(NoteActivity.this, ListActivity.class);
                startActivity(intent2);
            }
        }
    }
}
