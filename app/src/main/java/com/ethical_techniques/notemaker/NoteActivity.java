package com.ethical_techniques.notemaker;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.ethical_techniques.notemaker.DAL.DBUtil;
import com.ethical_techniques.notemaker.auth.BaseActivity;
import com.ethical_techniques.notemaker.model.Category;
import com.ethical_techniques.notemaker.model.Note;
import com.ethical_techniques.notemaker.model.PRIORITY;
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
 */
public class NoteActivity extends BaseActivity {

    private final String TAG = this.getClass().getSimpleName();
    private Note currentNote;
    private List<Category> categories;
    private Category currentNoteCategory;
    Spinner dropDownSpinner;
    MenuItem priorityStar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        //Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.action_bar_create_note);
        setSupportActionBar(toolbar);

        // Get the Toolbar back as an ActionBar and initialize the back button (Up/Home Button)

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        /* Check and Load info based from previous activity */
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            //Load the note using the noteID
            initNote(extras.getInt(getString(R.string.NOTE_ID_KEY)));
        } else {
            //create a new blank note
            currentNote = new Note();
            //Initialize the Category chooser dropdown Spinner
            dropDownSpinner = findViewById(R.id.categorySpinner);
            if (initCategories()) {
                initDropDown(dropDownSpinner);
            }
        }
        //Initialize listeners on text input fields
        initTextChangedEvents();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //Refresh dropdown Spinner
        dropDownSpinner = findViewById(R.id.categorySpinner);
        if (initCategories()) {
            initDropDown(dropDownSpinner);
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

        ArrayAdapter<String> categoryArrayAdapter = new ArrayAdapter<>(this,
                R.layout.dropdown_item_simple, categoryStrings);
        spinner.setAdapter(categoryArrayAdapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String checkedTextView = (String) parent.getItemAtPosition(position);
                System.out.println("checkedTextView = " + checkedTextView);
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
            handleTogglePriorityStar(priorityStar, note.getPRIORITY_LEVEL().equals(PRIORITY.HIGH.getString()));
        }
    }

    @Override
    public void onBackPressed() {
        handleSaveNote();
        super.onBackPressed();
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
                        Toast.LENGTH_LONG).show();
            } else {
                starDrawable.setColorFilter(null);
                Toast.makeText(this, "The current note is set to regular priority",
                        Toast.LENGTH_LONG).show();

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
        int id = item.getItemId();
        if (id == R.id.action_bar_save_button) {
            handleSaveNote();
        } else if (id == R.id.action_bar_priority_star) {
            handleTogglePriorityStar(item, !(currentNote.getPRIORITY_LEVEL().equals(PRIORITY.HIGH.getString())));
            if (currentNote.getPRIORITY_LEVEL().equals(PRIORITY.HIGH.getString())) {
                currentNote.setPRIORITY_LEVEL(PRIORITY.LOW.getString());
            } else {
                currentNote.setPRIORITY_LEVEL(PRIORITY.HIGH.getString());
            }
        }
        return true;
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
                currentNote.setNoteContent(etNoteBody.getText().toString());
            }
        });
    }

    /**
     * Save button clicked
     */
    public void handleSaveNote() {

        if (currentNote.getNoteName() == null || currentNote.getContent() == null) {
            Toast.makeText(NoteActivity.this, "Make sure to fill in the name and the " +
                    "\n note content fields of the note before saving", Toast.LENGTH_LONG).show();
        } else {

            currentNote.setDateCreated(Calendar.getInstance());
            List<View> views = new ArrayList<>();
            views.add(findViewById(R.id.editTitle));
            views.add(findViewById(R.id.editNotes));
            views.add(findViewById(R.id.editTitle));

            if (views.size() > 0) {
                hideKeyboard(views);
            }

            boolean success = false;
            try {
                success = DBUtil.saveNote(NoteActivity.this, currentNote);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (success) {
                Toast.makeText(getBaseContext(), "Success, Your new note was saved. " +
                                "\nClick on the List icon on the navigation bar to manage your notes. ",
                        Toast.LENGTH_LONG).show();

                Intent intent2 = new Intent(NoteActivity.this, ListActivity.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent2);
            }
        }
    }
}