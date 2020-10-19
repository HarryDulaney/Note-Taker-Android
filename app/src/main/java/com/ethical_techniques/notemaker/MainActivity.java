package com.ethical_techniques.notemaker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.ethical_techniques.notemaker.DAL.DBUtil;
import com.ethical_techniques.notemaker.model.Category;
import com.ethical_techniques.notemaker.model.Note;
import com.ethical_techniques.notemaker.model.PRIORITY;
import com.google.android.material.navigation.NavigationView;
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
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = this.getClass().getSimpleName();
    private Note currentNote;
    private List<Category> categories;
    private Category currentNoteCategory;
    ImageButton priorityStar;
    Spinner dropDownSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout_main);
        //Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.action_bar_top);
        setSupportActionBar(toolbar);
        //Initialize navigation drawer
        DrawerLayout navDrawer = findViewById(R.id.drawer_layout_main);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, navDrawer,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //Set toggle on nav drawer
        navDrawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        //Initialize NavigationView
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

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
        //Re-initialize dropdown Spinner
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
        // Set the priority level ie color in star or not
        initPriorityStar(currentNote);
    }

    /**
     * Set's RatingBar to the Note's saved value
     *
     * @param note populating the Activity fields
     */
    private void initPriorityStar(Note note) {
        priorityStar = findViewById(R.id.highPriorityStar);
        if (note.getPRIORITY_LEVEL().equals("high")) {
            priorityStar.setColorFilter(getResources().getColor(R.color.colorPriorityHigh));
        } else {
            priorityStar.getDrawable().setColorFilter(null);
        }
    }


    /**
     * @param item the MenuItem that was clicked
     * @return boolean success indicator
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_my_notes: {
                Intent intent2 = new Intent(this, ListActivity.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent2);

            }
            break;
            case R.id.nav_edit_categories: {
                Intent i3 = new Intent(this, CategoryListActivity.class);
                i3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i3);
            }
            break;


            case R.id.nav_share: {
                //Open share prompt with options to share a note or a list of notes TODO
            }
            break;
            case R.id.nav_send: {
                //Open send prompt with options to send a note or a list of notes TODO
            }
            break;
            default:
                throw new IllegalStateException("Unexpected value: " + item.getItemId());
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout_main);
        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_main);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    /**
     * Initializes the top app bar.
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar_top_menu, menu);
        return true;
    }

    /**
     * @param item that was selected
     * @return true if the item is recognized
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_bar_settings) {
            //Open the note_list_settings activity
            Intent i = new Intent(this, SettingsActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);

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

    /**
     * Save button clicked
     *
     * @param view the view parent of the button that was clicked
     */
    public void handleSaveNote(View view) {

        if (currentNote.getNoteName() == null || currentNote.getContent() == null) {
            Toast.makeText(getBaseContext(), "Make sure to fill in the name and the " +
                    "\n note content fields of the note before saving", Toast.LENGTH_LONG).show();
        } else {

            currentNote.setDateCreated(Calendar.getInstance());
            hideKeyboard();

            boolean success = false;
            try {
                success = DBUtil.saveNote(MainActivity.this, currentNote);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (success) {
                Toast.makeText(getBaseContext(), "Success, Your new note was saved. " +
                                "\nClick on the List icon on the navigation bar to manage your notes. ",
                        Toast.LENGTH_LONG).show();

                Intent intent2 = new Intent(MainActivity.this, ListActivity.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent2);
            }
        }
    }

    /**
     * Switches the priority star on and off
     *
     * @param imgButtonView the view parent of the priority star
     */
    public void toggleNotePriority(View imgButtonView) {
        priorityStar = findViewById(R.id.highPriorityStar);
        if (currentNote.getPRIORITY_LEVEL().equals(PRIORITY.HIGH.getString())) {

            priorityStar.getDrawable().setColorFilter(null);
            currentNote.setPRIORITY_LEVEL(PRIORITY.LOW.getString());
            Toast.makeText(this, "The current note is set to regular priority",
                    Toast.LENGTH_LONG).show();

        } else {

            priorityStar.setColorFilter(getResources().getColor(R.color.colorPriorityHigh));
            currentNote.setPRIORITY_LEVEL(PRIORITY.HIGH.getString());
            Toast.makeText(this, "The current note is set to high priority",
                    Toast.LENGTH_LONG).show();

        }

    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        EditText editName = findViewById(R.id.editTitle);
        assert imm != null;
        imm.hideSoftInputFromWindow(editName.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(priorityStar.getWindowToken(), 0);
        EditText editNote = (EditText) findViewById(R.id.editNotes);
        imm.hideSoftInputFromWindow(editNote.getWindowToken(), 0);

    }
}