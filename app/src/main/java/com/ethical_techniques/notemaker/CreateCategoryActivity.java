package com.ethical_techniques.notemaker;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.ethical_techniques.notemaker.DAL.DBUtil;
import com.ethical_techniques.notemaker.note.Category;
import com.google.android.material.snackbar.Snackbar;


public class CreateCategoryActivity extends AppCompatActivity {

    private View currView;

    @Override
    public void onCreate(Bundle saveInstanceBundle) {
        super.onCreate(saveInstanceBundle);
        setContentView(R.layout.app_bar_categ_create);
        currView = getCurrentFocus();

        //Handle Toolbar
        Toolbar toolbar = findViewById(R.id.action_bar_top);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar_back_button, menu);
        return true;
    }

    @Override
    public void onBackPressed() {


        super.onBackPressed();
    }


    /**
     * @param view SaveButton view object
     */
    public void handleSaveCategory(View view) {
        Category category = new Category();
        EditText nameInput = findViewById(R.id.editCategoryName);
        if (nameInput.getText().toString().isEmpty()) {
            Snackbar.make(currView, "Please input a name for the Category before saving. ", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else {
            category.setName(nameInput.getText().toString());
//            category.setColor();
            try {
                DBUtil.saveCategory(this, category);
            } catch (Exception e) {
                Snackbar.make(currView, "The Category name is already used for another Category, " +
                        "please delete the other Category or revise the name", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                e.printStackTrace();

            }
        }


    }
}
