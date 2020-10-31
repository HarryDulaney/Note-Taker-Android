package com.ethical_techniques.notemaker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ethical_techniques.notemaker.DAL.DBUtil;
import com.ethical_techniques.notemaker.adapters.NoteRecycleAdapter;
import com.ethical_techniques.notemaker.model.Note;
import com.ethical_techniques.notemaker.model.PRIORITY;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

/**
 * The Parent/ Main Activity containing the navigation drawer. Other activities return here.
 */
public class ListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = this.getClass().getName();
    private static List<Note> notes;
    RecyclerView recyclerView;
    NoteRecycleAdapter noteRecycleAdapter;
    boolean editModeActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get Preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        setContentView(R.layout.drawer_layout_list);

        //Handle Toolbar
        Toolbar toolbar = findViewById(R.id.action_bar_top);
        setSupportActionBar(toolbar);

        //Handle create new note floating button
        FloatingActionButton floatingActionButton = findViewById(R.id.new_note_float_button);
        floatingActionButton.setOnLongClickListener((v) -> {
            Snackbar.make(v, "Create a new note", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            return true;
        });

        floatingActionButton.setOnClickListener(view -> {
            Intent intent = new Intent(ListActivity.this, NoteActivity.class);
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
        navigationView.setNavigationItemSelectedListener(ListActivity.this);

    }

    /**
     * @param item the MenuItem that was clicked
     * @return boolean success indicator
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_new_note:
                Intent intent = new Intent(this, NoteActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.nav_edit_categories:
                Intent i2 = new Intent(this, CategoryListActivity.class);
                i2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i2);
                break;

            case R.id.nav_settings:
                //Open the settings activity
                Intent i = new Intent(this, SettingsActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                break;

            case R.id.nav_share:
                //TODO:Open share prompt with options to share a note or a list of notes

                break;
            case R.id.nav_sync:
                //TODO: If user isn't logged in, show login screen, else run sync and ask to auto sync

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
        if (id == R.id.action_bar_editSwitch) {
            toggleDeleteIcon();
            //Toggle the icons
            if (!editModeActive) {
                item.setIcon(R.drawable.ic_playlist_check_white_48dp);
                editModeActive = true;
            } else {
                item.setIcon(R.drawable.ic_playlist_edit_white_48dp);
                editModeActive = false;
            }
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    private void toggleDeleteIcon() {
        for (int i = 0; i < noteRecycleAdapter.getItemCount();
             i++) {
            NoteRecycleAdapter.ViewHolder viewHolder = (NoteRecycleAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
            if (viewHolder != null) {
                if (viewHolder.deleteButton.getVisibility() == View.VISIBLE) {
                    viewHolder.deleteButton.setVisibility(View.GONE);
                    viewHolder.priorityStar.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.priorityStar.setVisibility(View.GONE);
                    viewHolder.deleteButton.setVisibility(View.VISIBLE);
                }
                Log.i(TAG, "Edit button on list item " + i + "set to visible");
            } else {
                Log.e(TAG, " The ViewHolder is null");
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        //Retrieve Sort By from sp
        String sortBy = sp.getString("sort.by.preference", getString(R.string.SORT_BY_DATE));
        //Retrieves Sort Order from sp
        String sortOrder = sp.getString("sort.order.preference", getString(R.string.SORT_ORDER_HIGH_TO_LOW));

        try {
            notes = DBUtil.findNotes(this, sortBy, sortOrder);

        } catch (Exception e) {
            Log.e(TAG, "Error in onResume, inspect DataSource. ");
            Toast.makeText(this, "Error retrieving notes, please try reloading. ", Toast.LENGTH_LONG).show();
        }

        recyclerView = findViewById(R.id.recycleList);
        TextView emptyListMessageTopHalf = findViewById(R.id.empty_view1);
        TextView emptyListBottomHalf = findViewById(R.id.empty_view2);
        noteRecycleAdapter = new NoteRecycleAdapter(notes);

//        recyclerView.addItemDecoration(new SpacingItemDecoration(10, true, true));

        if (notes.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyListMessageTopHalf.setVisibility(View.VISIBLE);
            emptyListBottomHalf.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyListMessageTopHalf.setVisibility(View.GONE);
            emptyListBottomHalf.setVisibility(View.GONE);
        }

        /* Set listener event behavior for long click on list item event */
        noteRecycleAdapter.setNoteLongClickListener((view, position) -> {
            int noteId = (int) noteRecycleAdapter.getItemId(position);
            Intent intent = new Intent(ListActivity.this, NoteActivity.class);
            intent.putExtra(getString(R.string.NOTE_ID_KEY), noteId);
            ListActivity.this.startActivity(intent);

        });
        /* Set listener event behavior for regular (short) click on list item */
        noteRecycleAdapter.setNoteClickListener((view, position) -> Toast.makeText(ListActivity.this, "Hold down a long click to open the note the editing"
                , Toast.LENGTH_LONG).show());
        noteRecycleAdapter.setPriorityStarListener((priorityView, position) -> {
            Note priorityNote = notes.get(position);
            if (priorityNote.getPRIORITY_LEVEL().equals(PRIORITY.HIGH.getString())) {
                if (priorityView instanceof ImageButton) {
                    ImageButton priorityStar = (ImageButton) priorityView;
                    priorityStar.setColorFilter(R.color.colorPriorityHigh);
                }
                priorityNote.setPRIORITY_LEVEL(PRIORITY.LOW.getString());
                Toast.makeText(this, "The current note is set to regular priority",
                        Toast.LENGTH_LONG).show();

            }
        });


        /* Set listener event behavior for regular click on delete button */
        noteRecycleAdapter.setDeleteButtonListener((view, position) -> {
            Note note = notes.get(position);
            final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle("Confirm")
                    .setMessage("Are you sure you want to permanently delete the Note: " + note.getNoteName())
                    .setNegativeButton("CANCEL", (dialog, which) -> {
                        Toast.makeText(ListActivity.this, "Cancelled delete Note operation.", Toast.LENGTH_LONG).show();
                        dialog.cancel();
                    })
                    .setPositiveButton("DELETE", (dialog, usersChoice) -> {
                        //Handle deleting the Note
                        try {
                            DBUtil.deleteNote(ListActivity.this, note.getNoteID());
                            Toast.makeText(ListActivity.this, "The Note titled " + note.getNoteName() + " was deleted.", Toast.LENGTH_LONG).show();
                            notes.remove(position);
                            noteRecycleAdapter.notifyDataSetChanged();

                        } catch (Exception e) {
                            Toast.makeText(ListActivity.this, "An error occurred and the note could note be deleted :(", Toast.LENGTH_LONG).show();
                            Log.e(TAG, "Exception @ delete(int noteID)..NoteId = " + note.getNoteID() +
                                    " Exception's message: " + e.getMessage(), e);
                        }
                    });
            builder.create().show();
        });

        recyclerView.setAdapter(noteRecycleAdapter);
    }

    public void handleToggleStar(View starView) {
        if (starView instanceof ImageButton) {
            ImageButton starButton = (ImageButton) starView;
            if (starButton.getDrawable().getCurrent() == ContextCompat.getDrawable(this, android.R.drawable.btn_star_big_off)) {
                starButton.setImageDrawable(ContextCompat.getDrawable(this, android.R.drawable.btn_star_big_on));

            } else {
                starButton.setImageDrawable(ContextCompat.getDrawable(this, android.R.drawable.btn_star_big_off));
            }
        }
    }

}