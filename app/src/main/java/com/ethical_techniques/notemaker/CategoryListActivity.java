package com.ethical_techniques.notemaker;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.ethical_techniques.notemaker.note.Category;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

/* <h3>Positions in RecyclerView:</h3>
 * <p>
 * RecyclerView introduces an additional level of abstraction between the {@link Adapter} and
 * {@link LayoutManager} to be able to detect data set changes in batches during a layout
 * calculation. This saves LayoutManager from tracking adapter changes to calculate animations.
 * It also helps with performance because all view bindings happen at the same time and unnecessary
 * bindings are avoided.
 * <p>
 * For this reason, there are two types of <code>position</code> related methods in RecyclerView:
 * <ul>
 *     <li>layout position: Position of an item in the latest layout calculation. This is the
 *     position from the LayoutManager's perspective.</li>
 *     <li>adapter position: Position of an item in the adapter. This is the position from
 *     the Adapter's perspective.</li>
 * </ul>
 * <p>
 * These two positions are the same except the time between dispatching <code>adapter.notify*
 * </code> events and calculating the updated layout.
 * <p>
 * Methods that return or receive <code>*LayoutPosition*</code> use position as of the latest
 * layout calculation (e.g. {@link CategoryViewHolder#getLayoutPosition()},
 * {@link #findViewHolderForLayoutPosition(int)}). These positions include all changes until the
 * last layout calculation. You can rely on these positions to be consistent with what user is
 * currently seeing on the screen. For example, if you have a list of items on the screen and user
 * asks for the 5<sup>th</sup> element, you should use these methods as they'll match what user
 * is seeing.
 * <p>
 * The other set of position related methods are in the form of
 * <code>*AdapterPosition*</code>. (e.g. {@link CategoryViewHolder#getAdapterPosition()},
 * {@link #findViewHolderForAdapterPosition(int)}) You should use these methods when you need to
 * work with up-to-date adapter positions even if they may not have been reflected to layout yet.
 * For example, if you want to access the item in the adapter on a CategoryViewHolder click, you should use
 * {@link CategoryViewHolder#getAdapterPosition()}. Beware that these methods may not be able to calculate
 * adapter positions if {@link Adapter#notifyDataSetChanged()} has been called and new layout has
 * not yet been calculated. For this reasons, you should carefully handle {@link #NO_POSITION} or
 * <code>null</code> results from these methods.*/
//

public class CategoryListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private final String TAG = this.getClass().getName();
    private static List<Category> categories;
    CategoryRecycleAdapter recycleAdapter;
    RecyclerView recyclerView;

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
                    if (viewHolder.chooseEditCategoryButton.getVisibility() == View.VISIBLE) {
                        viewHolder.chooseEditCategoryButton.setVisibility(View.INVISIBLE);
                    } else {
                        viewHolder.chooseEditCategoryButton.setVisibility(View.VISIBLE);
                    }
                    Log.i(TAG, "Edit button on Category list item " + i + "set to visible");
                } else {
                    Log.e(TAG, " Error occured at RecycleAdapter position: " + i);
                }

            }
        }
        return super.onOptionsItemSelected(item);

    }


    public void handleCategoryEdit(View view) {
        int position = recyclerView.getChildAdapterPosition(view);
        CategoryViewHolder viewHolder = (CategoryViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
        if (viewHolder != null) {
            Log.i(TAG, "Category list item " + position + " selected to edit fields.");

            Intent intent = new Intent(this, CreateCategoryActivity.class);
            intent.putExtra(getString(R.string.CATEGORY_ID_KEY), viewHolder.category.getId());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}