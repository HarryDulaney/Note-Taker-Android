package com.ethical_techniques.notemaker;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.ethical_techniques.notemaker.DAL.DBUtil;
import com.ethical_techniques.notemaker.auth.BaseActivity;
import com.ethical_techniques.notemaker.listeners.TextWatcherImpl;
import com.ethical_techniques.notemaker.model.NoteCategory;
import com.ethical_techniques.notemaker.utils.DialogUtil;
import com.google.android.material.snackbar.Snackbar;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import top.defaults.colorpicker.ColorPickerPopup;


/**
 * The type Create noteCategory activity.
 *
 * @author Harry Dulaney
 */
public class CreateCategoryActivity extends BaseActivity {

    private final String TAG = getClass().getName();
    private NoteCategory currentNoteCategory;
    private ImageButton colorPickButton;
    private EditText editName;


    @Override
    public void onCreate(Bundle saveInstanceBundle) {
        super.onCreate(saveInstanceBundle);
        setContentView(R.layout.activity_categ_create);

        colorPickButton = findViewById(R.id.colorPickerView);
        editName = findViewById(R.id.editCategoryName);

        /* Check and Load info based from calling activity */
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            //Load the note using the noteID
            initCategory(extras.getInt(getString(R.string.CATEGORY_ID_KEY)));
        } else {
            //create a new blank note
            currentNoteCategory = new NoteCategory();
            setListeners();

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

        editName.setText(currentNoteCategory.getName());

        colorPickButton.getDrawable().mutate();
        if (currentNoteCategory.getColor() != Color.TRANSPARENT) {
            colorPickButton.getDrawable().setTint(currentNoteCategory.getColor());

        }

        setListeners();

    }

    private void setListeners() {
        colorPickButton.setOnClickListener(v -> {
            if (v.getId() == R.id.colorPickerView) {
                new ColorPickerPopup.Builder(this)
                        .initialColor(currentNoteCategory.getColor())
                        .enableBrightness(true)
                        .okTitle("Choose")
                        .cancelTitle("Cancel")
                        .showIndicator(true)
                        .build()
                        .show(v, new ColorPickerPopup.ColorPickerObserver() {
                            @Override
                            public void onColorPicked(int color) {
                                currentNoteCategory.setColor(color);
                                colorPickButton.getDrawable().mutate();
                                colorPickButton.getDrawable().setTint(color);
                            }
                        });

            }
        });
        editName.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable s) {
                currentNoteCategory.setName(editName.getText().toString());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.save:
                handleSaveCategory();
                startActivity(new Intent(this, CategoryListActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar_top_menu, menu);
        menu.removeItem(R.id.star);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (currentNoteCategory.getName() != null) {
            //handle Dialog to save change
            DialogUtil.makeAndShow(this,
                    "Do you want to save your changes?",
                    "Click SAVE to save changes or click DONT SAVE to " +
                            "continue without saving.",
                    "SAVE",
                    "DONT SAVE",
                    () -> {
                        handleSaveCategory();
                        super.onBackPressed();
                    });

        } else {
            super.onBackPressed();
        }
    }


    /**
     * Handle save noteCategory.
     */
    public void handleSaveCategory() {
        //            noteCategory.setColor();

        if (editName.getText().toString().isEmpty()) {
            Snackbar.make(editName.getRootView(), "Please input a name for the NoteCategory before saving. ", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else if (currentNoteCategory.getId() == -1) {
            List<String> catNamesExisting = new ArrayList<>();
            try {
                List<NoteCategory> categories = DBUtil.getCategories(this);
                for (NoteCategory noteCategory : categories) {
                    catNamesExisting.add(noteCategory.getName());
                }
                if (catNamesExisting.contains(currentNoteCategory.getName())) {
                    Snackbar.make(editName.getRootView(), "The NoteCategory name is already used for another NoteCategory, " +
                            "please delete the other NoteCategory or revise the name", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    try {
                        DBUtil.saveCategory(this, currentNoteCategory);
                        Toast.makeText(this, "The new Category " + currentNoteCategory.getName() + " has been saved", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, " An error occurred while saving the NoteCategory. The Category may not have been saved. ",
                                Toast.LENGTH_LONG).show();

                    }


                }

            } catch (Exception e) {
                Log.e(TAG, "Error pulling the categories from the db.");
                Toast.makeText(this, "Error retrieving categories, please try reloading. ", Toast.LENGTH_LONG).show();
            }

        } else { //overwrite existing note using update (the category has an id that is != -1 so it must existing in the db already)
            try {
                DBUtil.updateCategory(this, currentNoteCategory);
                Toast.makeText(this, "Your changes to the note have been saved.",
                        Toast.LENGTH_LONG).show();
            } catch (SQLException exp) {
                exp.printStackTrace();
                Toast.makeText(this, "An error occurred trying to open the database, the note category may not have been updated correctly.",
                        Toast.LENGTH_LONG).show();

            }

        }

    }
}
