package com.ethical_techniques.notemaker;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.ethical_techniques.notemaker.DAL.DBUtil;
import com.ethical_techniques.notemaker.note.Category;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.time.Duration;

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

    /*
     * Called when an item in the navigation menu is selected.
     *
     * @param item The selected item
     * @return true to display the item as the selected item

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_new_note:
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.nav_my_notes:
                Intent intent1 = new Intent(CreateCategoryActivity.this, ListActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent1);
                break;
            case R.id.nav_share:
                //Open share prompt with options to share a note or a list of notes TODO

                break;
            case R.id.nav_send:
                //Open send prompt with options to send a note or a list of notes TODO

                break;
            default:
                throw new IllegalStateException("Unexpected value: " + item.getItemId());
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout_list);
        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }
    */


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
            }catch (Exception e) {
                e.printStackTrace();

            }
        }


    }
}
