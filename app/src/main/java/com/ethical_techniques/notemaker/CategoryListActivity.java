package com.ethical_techniques.notemaker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ethical_techniques.notemaker.note.Category;
import com.ethical_techniques.notemaker.note.Note;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class CategoryListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = this.getClass().getName();
    private static List<Category> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout_categ_list);


        //Handle Toolbar
        Toolbar toolbar = findViewById(R.id.action_bar_top);
        setSupportActionBar(toolbar);

        //Handle create new note floating button
        FloatingActionButton floatingActionButton = findViewById(R.id.new_category_float_button);
        floatingActionButton.setOnLongClickListener((v) -> {
            Snackbar.make(v,
                    "Create a new Categorey", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            return true;
        });

        floatingActionButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, CreateCategoryActivity.class);
            startActivity(intent);

        });
        //Handle Nav Drawer
        DrawerLayout navDrawer = findViewById(R.id.drawer_layout_list);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, navDrawer,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        navDrawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        //Handle NavigationView
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(CategoryListActivity.this);
    }


    /**
     * @param item the MenuItem that was clicked
     * @return boolean success indicator
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_new_note:
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.nav_my_notes:
                Intent intent1 = new Intent(CategoryListActivity.this, ListActivity.class);
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout_list);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar_top_list_menu, menu);
        return true;
    }

    /**
     * @param item that was selected
     * @return true if the item is recognized
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_bar_settings) {
            //Open the settings activity
            Intent i = new Intent(this, SettingsActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    public void handleCategoryEdit(View view) {
        Intent intent = new Intent(this, CreateCategoryActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}