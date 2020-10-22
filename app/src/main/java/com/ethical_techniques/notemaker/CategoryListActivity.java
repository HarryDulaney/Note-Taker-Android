package com.ethical_techniques.notemaker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ethical_techniques.notemaker.CategoryRecycleAdapter.CategoryViewHolder;
import com.ethical_techniques.notemaker.DAL.DBUtil;
import com.ethical_techniques.notemaker.decorators.SpacingItemDecoration;
import com.ethical_techniques.notemaker.model.Category;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;


/**
 * The type Category list activity controls the behavior of the list of categories.
 */
public class CategoryListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = this.getClass().getName();
    private static List<Category> categories;
    /**
     * The Recycle adapter.
     */
    CategoryRecycleAdapter recycleAdapter;
    /**
     * The Recycler view.
     */
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout_categ_list);
        setTitle(R.string.category_list_title);
        //Handle Toolbar
        Toolbar toolbar = findViewById(R.id.action_bar_top);
        setSupportActionBar(toolbar);

        //Handle create new note floating button
        FloatingActionButton floatingActionButton = findViewById(R.id.new_category_float_button);
        floatingActionButton.setOnLongClickListener((v) -> {
            Snackbar.make(v,
                    "Create a new category", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            return true;
        });

        floatingActionButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, CreateCategoryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

//        //Retrieve Sort By from sp
//        String sortBy = sp.getString("sort.by.preference", getString(R.string.SORT_BY_DATE));
//        //Retrieves Sort Order from sp
//        String sortOrder = sp.getString("sort.order.preference", getString(R.string.SORT_ORDER_HIGH_TO_LOW));

        try {

            categories = DBUtil.getCategories(this);

        } catch (Exception e) {
            Log.e(TAG, "Error in onResume, inspect DataSource . ");
            Toast.makeText(this, "Error retrieving categories, please try reloading. ", Toast.LENGTH_LONG).show();
        }
        recyclerView = findViewById(R.id.recycleListCategory);
        recycleAdapter = new CategoryRecycleAdapter(categories);
        recycleAdapter.setLongClickListener((view, position) -> {
            Intent intent = new Intent(this, CreateCategoryActivity.class);
            intent.putExtra(getString(R.string.CATEGORY_ID_KEY), recycleAdapter.getItemId(position));
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        });
        recycleAdapter.setShortClickListener((view, position) -> {
            Toast.makeText(this, "Hold a long click on the list item to open the Category for editing.", Toast.LENGTH_LONG).show();
        });
        recyclerView.addItemDecoration(new SpacingItemDecoration(1, false, true));
        recyclerView.setAdapter(recycleAdapter);

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
        // Handle action bar item clicks here.
        int id = item.getItemId();
        if (id == R.id.action_bar_settings) {
            //Open the note_list_settings activity
            Intent i = new Intent(this, SettingsActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            return true;

        } else if (id == R.id.action_bar_editSwitch) {
            // Turn on edit mode for the Category Fragments in the RecyclerView by
            // showing the Edit button on each list item.
            for (int i = 0; i < recycleAdapter.getItemCount(); i++) {
                CategoryViewHolder viewHolder = (CategoryViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                if (viewHolder != null) {
                    if (viewHolder.name.toString().equals(Category.NONE_NAME)) continue;

                        if (viewHolder.deleteButton.getVisibility() == View.VISIBLE) {
                        viewHolder.deleteButton.setVisibility(View.INVISIBLE);
                    } else {
                        viewHolder.deleteButton.setVisibility(View.VISIBLE);
                    }
                    Log.i(TAG, "Edit button on Category list item " + i + "set to visible");
                } else {
                    Log.e(TAG, " Error occurred at RecycleAdapter position: " + i);
                }

            }
        }
        return super.onOptionsItemSelected(item);

    }
}