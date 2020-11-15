package com.ethical_techniques.notemaker;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.widget.Toolbar;

import com.ethical_techniques.notemaker.DAL.DBUtil;
import com.ethical_techniques.notemaker.auth.BaseActivity;
import com.ethical_techniques.notemaker.model.NoteCategory;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;



/**
 * The type Create noteCategory activity.
 *
 * @author Harry Dulaney
 */
public class CreateCategoryActivity extends BaseActivity {

    private final String TAG = getClass().getName();
    private NoteCategory currentNoteCategory;

    @Override
    public void onCreate(Bundle saveInstanceBundle) {
        super.onCreate(saveInstanceBundle);
        setContentView(R.layout.activity_categ_create);


        /* Check and Load info based from calling activity */
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            //Load the note using the noteID
            initCategory(extras.getInt(getString(R.string.CATEGORY_ID_KEY)));
        } else {
            //create a new blank note
            currentNoteCategory = new NoteCategory();

        }
        //Initialize the Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get the Toolbar back as an ActionBar and initialize the back button (Up/Home Button)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            Log.e(TAG, "ActionBar was not created properly...");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    /**
     * Opens a connection to the database and uses getSpecificCategory() to retrieve the
     * NoteCategory and then sets the local views with its values.
     *
     * @param categoryId identifier for the noteCategory
     */
    private void initCategory(int categoryId) {

        try {
            currentNoteCategory = DBUtil.findCategory(this, categoryId);

        } catch (Exception e) {
            Log.w(TAG, e);
            Snackbar.make(findViewById(R.id.category_activity),
                    Objects.requireNonNull(e.getMessage()),
                    Snackbar.LENGTH_INDEFINITE);
        }

        EditText editName = findViewById(R.id.editCategoryName);
        editName.setText(currentNoteCategory.getName());

        ImageButton colorPickButton = findViewById(R.id.colorPickerView);
        colorPickButton.setColorFilter(currentNoteCategory.getColor());


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar_back_button, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (currentNoteCategory.getName() != null) {
            //handle Dialog to save changes

        }
        super.onBackPressed();
    }


    /**
     * Handle save noteCategory.
     *
     * @param view SaveButton view object
     */
    public void handleSaveCategory(View view) {
        NoteCategory noteCategory = new NoteCategory();
        EditText nameInput = findViewById(R.id.editCategoryName);
        if (nameInput.getText().toString().isEmpty()) {
            Snackbar.make(view, "Please input a name for the NoteCategory before saving. ", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else {
            noteCategory.setName(nameInput.getText().toString());
//            noteCategory.setColor();
            try {
                DBUtil.saveCategory(this, noteCategory);
            } catch (Exception e) {
                Snackbar.make(view, "The NoteCategory name is already used for another NoteCategory, " +
                        "please delete the other NoteCategory or revise the name", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                e.printStackTrace();

            }
        }
    }
}
