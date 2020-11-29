package com.ethical_techniques.notemaker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.ethical_techniques.notemaker.DAL.DBUtil;
import com.ethical_techniques.notemaker.adapters.CategoryRecycleAdapter;
import com.ethical_techniques.notemaker.adapters.CategoryRecycleAdapter.CategoryViewHolder;
import com.ethical_techniques.notemaker.auth.BaseActivity;
import com.ethical_techniques.notemaker.model.NoteCategory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;


/**
 * The type NoteCategory list activity controls the behavior of the list of categories.
 *
 * @author Harry Dulaney
 */
public class CategoryListActivity extends BaseActivity {

    private final String TAG = this.getClass().getName();
    private static List<NoteCategory> categories;
    private static boolean allNotesPopupShown;
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
        setContentView(R.layout.activity_categ_list);
        setTitle(R.string.category_list_title);
        //Handle Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Get the Toolbar back as an ActionBar and initialize the back button (Up/Home Button)
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        } else {
            Log.e(TAG, "ActionBar was not created properly...");
        }

        //Handle create new note floating button
        FloatingActionButton floatingActionButton = findViewById(R.id.new_category_float_button);
        floatingActionButton.setOnLongClickListener((v) -> {
            Snackbar.make(v,
                    "Create a new noteCategory", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            return true;
        });

        floatingActionButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, CreateCategoryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        });

    }

    @Override
    public void onResume() {
        super.onResume();

//        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

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
            if (position != 0) {
                int categoryId = (int) recycleAdapter.getItemId(position);
                Intent intent = new Intent(this, CreateCategoryActivity.class);
                intent.putExtra(getString(R.string.CATEGORY_ID_KEY), categoryId);
                startActivity(intent);
            } else {
                Toast.makeText(this, "This is a default noteCategory and is necessary for grouping un-categorized Notes. " +
                        "Please try creating a new noteCategory with the button below.", Toast.LENGTH_LONG).show();
            }

        });
        recycleAdapter.setShortClickListener((view, position) -> {
            Toast.makeText(this, "Hold a long click on the list item to open the NoteCategory for editing.", Toast.LENGTH_LONG).show();
        });
//        recyclerView.addItemDecoration(new SpacingItemDecoration(1, false, true));
        recyclerView.setAdapter(recycleAdapter);
        recycleAdapter.notifyDataSetChanged();

    }

//    /**
//     * @param item the MenuItem that was clicked
//     * @return boolean success indicator
//     */
//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//
//        switch (item.getItemId()) {
//            case R.id.nav_new_note:
//                Intent intent = new Intent(this, NoteActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//                break;
//
//            case R.id.nav_my_notes:
//                Intent intent1 = new Intent(CategoryListActivity.this, ListActivity.class);
//                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent1);
//                break;
//
//            case R.id.nav_settings:
//                SettingsActivity.setCallingActivity(R.integer.categ_list_activty);
//                //Open the settings activity
//                Intent i = new Intent(this, SettingsActivity.class);
//                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(i);
//                break;
//
//            case R.id.nav_share:
//                //Open share prompt with options to share a note or a list of notes TODO
//
//                break;
//            case R.id.nav_send:
//                //Open send prompt with options to send a note or a list of notes TODO
//
//                break;
//            default:
//                throw new IllegalStateException("Unexpected value: " + item.getItemId());
//        }
//
//        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout_list);
//        drawerLayout.closeDrawer(GravityCompat.START);
//
//        return true;
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar_top_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * @param item that was selected
     * @return true if the item is recognized
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();
        if (id == R.id.action_bar_editSwitch) {
            // Turn on edit mode for the NoteCategory Fragments in the RecyclerView by
            // showing the Edit button on each list item.
            for (int i = 0; i < recycleAdapter.getItemCount(); i++) {
                CategoryViewHolder viewHolder = (CategoryViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                if (viewHolder != null) {
                    if (NoteCategory.MAIN_NAME.contentEquals(viewHolder.name.getText())) {
                        continue;
                    } else if (viewHolder.deleteButton.getVisibility() == View.VISIBLE) {
                        viewHolder.deleteButton.setVisibility(View.INVISIBLE);
                    } else if (viewHolder.deleteButton.getVisibility() == View.INVISIBLE) {
                        viewHolder.deleteButton.setVisibility(View.VISIBLE);
                    }
                    Log.i(TAG, "Edit button on NoteCategory list item " + i + "set to visible");
                } else {
                    Log.e(TAG, " Error occurred at RecycleAdapter position: " + i);
                }

            }
            return true;
        }
        return super.onOptionsItemSelected(item);

    }
}