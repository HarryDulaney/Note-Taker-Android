package com.ethical_techniques.notemaker;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ethical_techniques.notemaker.DAL.DBUtil;
import com.ethical_techniques.notemaker.adapters.NoteRecycleAdapter;
import com.ethical_techniques.notemaker.auth.BaseActivity;
import com.ethical_techniques.notemaker.auth.UpdateProfileActivity;
import com.ethical_techniques.notemaker.auth.UserLoginActivity;
import com.ethical_techniques.notemaker.exceptions.NullHandlerException;
import com.ethical_techniques.notemaker.model.Note;
import com.ethical_techniques.notemaker.model.NoteCategory;
import com.ethical_techniques.notemaker.model.PRIORITY;
import com.ethical_techniques.notemaker.utils.DialogUtil;
import com.ethical_techniques.notemaker.utils.PictureUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The Main Activity containing the navigation drawer, most other activities,excluding authentication activities
 * return here have back buttons that will return here.
 * TODO: Implement NoteFragment for creating the RecyclerView / Note list
 *
 * @author Harry Dulaney
 */
public class ListActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = this.getClass().getName();

    private List<Note> currentNotes;
    private List<Note> allNotes;

    RecyclerView recyclerView;
    NoteRecycleAdapter noteRecycleAdapter;

    private boolean editModeActive;
    private List<NoteCategory> categories;
    private Spinner spinner;
    private NoteCategory activeNoteCategory;
    private String CURRENT_CATEGORY_KEY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout_list);

        CURRENT_CATEGORY_KEY = getString(R.string.CURR_CATEGORY_IN_LIST_KEY);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //initialize Categories Dropdown (i.e. Spinner)
        spinner = findViewById(R.id.list_activity_category_spinner);
        recyclerView = findViewById(R.id.recycleList);

        //Handle create new note floating button
        FloatingActionButton floatingActionButton = findViewById(R.id.new_note_float_button);
        floatingActionButton.setOnLongClickListener((v) -> {
            Snackbar.make(v, "Create a new note", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            return true;
        });

        floatingActionButton.setOnClickListener(view -> {
            Intent intent = new Intent(ListActivity.this, NoteActivity.class);
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
        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.nav_my_notes).setEnabled(false);

        navigationView.setNavigationItemSelectedListener(ListActivity.this);

        //Replace User Avatar Icon in drawer with users img
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fUser != null) {
            initUserAvatar(fUser, navigationView);
        }
        menu.findItem(R.id.nav_sync).setEnabled(fUser == null);
        menu.findItem(R.id.nav_logout).setEnabled(fUser != null);
        menu.findItem(R.id.nav_update).setEnabled(fUser != null);

        currentNotes = new ArrayList<>();

        try {
            allNotes = DBUtil.findNotes(this, SORT_BY_PREFERENCE, SORT_ORDER_PREFERENCE); //Fill with All Notes
        } catch (Exception e) {
            Log.e(TAG, "Error in onResume, inspect DataSource. ");
            Toast.makeText(ListActivity.this, "Error retrieving notes, please try reloading. ", Toast.LENGTH_LONG).show();
        }

        checkMessages(savedInstanceState);

        if (allNotes.isEmpty()) {
            toggleEmptyListMessage(View.GONE, View.VISIBLE, View.VISIBLE);
        } else {
            toggleEmptyListMessage(View.VISIBLE, View.GONE, View.GONE);
            initNoteList();
        }
    }

    /* load bundle to get message */
    private void checkMessages(Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(CURRENT_CATEGORY_KEY)) {
                activeNoteCategory = savedInstanceState.getParcelable(CURRENT_CATEGORY_KEY);

            } else {
                activeNoteCategory = NoteCategory.getMain(); //Default to "All Notes"

            }
            if (savedInstanceState.getBoolean(getString(R.string.launch_key))) {
                showSyncDecisionDialog();
            }

            if (savedInstanceState.containsKey(getString(R.string.SIGNED_OUT_RESTART_ACTIVITY))) {
                Snackbar.make(spinner.getRootView(), Objects.requireNonNull(savedInstanceState.getString(getString(R.string.SIGNED_OUT_RESTART_ACTIVITY))),
                        Snackbar.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Display choice dialog prompting the user to login to their cloud account
     */
    private void showSyncDecisionDialog() {
        DialogUtil.makeAndShow(this,
                "Welcome back!",
                "Click SYNC to sign into your account and synchronize your notes now or CONTINUE to " +
                        "take notes now.",
                "SIGN-IN",
                "CONTINUE",
                () -> {
                    Intent i = new Intent(this, UserLoginActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);

                },
                () -> {

                });

    }

//    @Override
//    protected void onSaveInstanceState(@NonNull Bundle outState) {
//        outState.putParcelable(CURRENT_CATEGORY_KEY, activeNoteCategory);
//        super.onSaveInstanceState(outState);
//
//    }

    private void initUserAvatar(FirebaseUser fUser, NavigationView nv) {
        final int proPicWidth = 48;
        final int proPicHeight = 48;

        View navHeaderView = nv.getHeaderView(0);
        TextView displayName = navHeaderView.findViewById(R.id.textViewUserNameDrawer);
        ImageView profPicImageView = navHeaderView.findViewById(R.id.userPic);

        if (fUser.getPhotoUrl() != null && fUser.getPhotoUrl().getPath() != null) {
            profPicImageView.getDrawable().mutate();

            File picFile = new File(fUser.getPhotoUrl().getPath());
            Bitmap unscaledBitmap = BitmapFactory.decodeFile(picFile.getAbsolutePath());
            Bitmap scaledBitmap = PictureUtil.getScaledBitmap(unscaledBitmap, proPicWidth, proPicHeight);

            profPicImageView.setImageBitmap(scaledBitmap);
        }

        if (fUser.getDisplayName() != null && !fUser.getDisplayName().isEmpty()) {
            displayName.setText(fUser.getDisplayName());
        } else {
            displayName.setText(R.string.nav_drawer_username_default);
        }

    }

    /**
     * Initialize the noteCategory dropdown menu
     */
    private void initDropdownMenu() {
        try {
            categories = DBUtil.getCategories(this);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "SQLException while loading 'categories' @initDropDown()");
        }

        ArrayAdapter<NoteCategory> categoryArrayAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item_simple);
        categoryArrayAdapter.addAll(categories);
        spinner.setAdapter(categoryArrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                NoteCategory noteCategory = categories.get(position);
                refreshListWithByCategory(noteCategory);

                Toast.makeText(ListActivity.this,
                        noteCategory.getName(),
                        Toast.LENGTH_SHORT).show();
                Log.i("checkedTextView = " + noteCategory.getName(), TAG);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (activeNoteCategory == NoteCategory.getMain()) {
            currentNotes.addAll(allNotes);
            noteRecycleAdapter.notifyDataSetChanged();
        } else {
            int pos = categories.indexOf(activeNoteCategory);
            spinner.setSelection(pos);
        }

    }

    /**
     * Load and re-load the list of notes and define listener events
     */
    private void initNoteList() {
        noteRecycleAdapter = new NoteRecycleAdapter(currentNotes);

        /* Set listener event behavior for long click on list item event */
        noteRecycleAdapter.setNoteLongClickListener((view, position) -> {

            int noteId = (int) noteRecycleAdapter.getItemId(position);
            NoteRecycleAdapter.ViewHolder viewHolder = (NoteRecycleAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(noteId);
            if (viewHolder != null) {
                FragmentManager fragManager = getSupportFragmentManager();
                String fragmentTag = viewHolder.noteViewPopupDialog.getTag();
                viewHolder.noteViewPopupDialog.show(fragManager, fragmentTag);
            } else {
                throw new NullHandlerException("NoteRecycleAdapter.ViewHolder was not instantiated.", TAG);
            }

        });

        /* Set listener event behavior for regular (short) click on list item */
        noteRecycleAdapter.setNoteClickListener((view, position) -> Toast.makeText(ListActivity.this,
                "Long click to open the note the editing"
                , Toast.LENGTH_LONG).show());

        /* Define the behavior for clicking on the Star  */
        noteRecycleAdapter.setPriorityStarListener((priorityView, position) -> {
            String toast = "";
            boolean saved = false;
            Note note = currentNotes.get(position);
            if (note.getPRIORITY_LEVEL().equals(PRIORITY.HIGH.getString())) { //Note is currently high priority and user desires to change to low priority
                note.setPRIORITY_LEVEL(PRIORITY.LOW.getString());

                try {
                    saved = DBUtil.saveNote(this, note);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "Exception trying to save note with ID# " + note.getNoteID() + " and title " + note.getNoteName());
                }
                if (saved) {
                    handleTogglePriorityStar(priorityView, false);
                    toast = "This note is set to " + note.getPRIORITY_LEVEL() + " priority";

                } else {
                    toast = "Something went wrong we could not save the note to " + note.getPRIORITY_LEVEL() + " priority";
                }

            } else { //Note is currently Low priority and the user is pressing the star to change it to High priority
                note.setPRIORITY_LEVEL(PRIORITY.HIGH.getString());

                try {
                    saved = DBUtil.saveNote(this, note);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "Exception trying to save note with ID# " + note.getNoteID() + " and title " + note.getNoteName());
                }

                if (saved) {
                    handleTogglePriorityStar(priorityView, true);
                    toast = "This note is set to " + note.getPRIORITY_LEVEL() + " priority";

                } else {
                    toast = "Something went wrong we could not save the note to " + note.getPRIORITY_LEVEL() + " priority";
                }
            }

            Toast.makeText(ListActivity.this,
                    toast,
                    Toast.LENGTH_SHORT).show();

            noteRecycleAdapter.notifyItemChanged(position);
        });
        /* Set listener event behavior for regular click on delete button */
        noteRecycleAdapter.setDeleteButtonListener((view, position) -> {
            Note note = currentNotes.get(position);

            DialogUtil.makeAndShow(this,
                    "Confirmation Popup",
                    "Are you sure you want to permanently delete: " + note.getNoteName(),
                    "delete",
                    "cancel",
                    () -> {
                        try {
                            DBUtil.deleteNote(this, note.getNoteID());
                            Toast.makeText(this, "The Note titled " + note.getNoteName() + " was deleted.", Toast.LENGTH_LONG).show();
                            currentNotes.remove(position);
                            noteRecycleAdapter.notifyItemRemoved(position);

                        } catch (Exception e) {
                            Toast.makeText(this, "An error occurred and the note could note be deleted :(", Toast.LENGTH_LONG).show();
                            Log.e(TAG, "Exception @ delete(int noteID)..NoteId = " + note.getNoteID() +
                                    " Exception's message: " + e.getMessage(), e);
                        }
                    });
        });
        //Set the adapter to the RecyclerView
        recyclerView.setAdapter(noteRecycleAdapter);
        initDropdownMenu();

    }


    private void refreshListWithByCategory(NoteCategory noteCategory) {
        currentNotes.clear();
        try {
            allNotes = DBUtil.findNotes(this, SORT_BY_PREFERENCE, SORT_ORDER_PREFERENCE);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "SQLException on findNotes in refreshListByCategory(NoteCategory)");
        }
        if (noteCategory.equals(NoteCategory.getMain())) {
            currentNotes.addAll(allNotes);
        } else {
            for (Note note : allNotes) {
                if (note.getNoteCategory().equals(noteCategory)) {
                    currentNotes.add(note);
                }
            }
        }
        noteRecycleAdapter.notifyDataSetChanged();

    }

    /**
     * @param item the MenuItem that was clicked
     * @return boolean success indicator
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_new_note:
                //Open create new note activity
                Intent intent = new Intent(this, NoteActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_edit_categories:
                //Edit the note categories
                Intent i2 = new Intent(this, CategoryListActivity.class);
                startActivity(i2);
                break;

            case R.id.nav_settings:
                //Open the settings activity
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                break;

            case R.id.nav_sync:
                //Open user login activity
                Intent intent1 = new Intent(this, UserLoginActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent1);
                break;
            case R.id.nav_update:
                //Open update user profile activity
                Intent i3 = new Intent(this, UpdateProfileActivity.class);
                startActivity(i3);
                break;
            case R.id.nav_logout:
                String message = "You are now signed out of your account. " +
                        "Please continue using Notes For Android and sign in to sync notes later on..";
                ;
                DialogUtil.makeAndShow(this,
                        "Confirm Sign Out",
                        "Are you sure you want to sign out of your account?",
                        "YES, SIGN OUT",
                        "NO, GO BACK",
                        () -> {
                            //Sign out
                            FirebaseAuth.getInstance().signOut();
                            //Restart the list-activity
                            Intent intentRes = Intent.makeRestartActivityTask(getIntent().getComponent());
                            intentRes.putExtra(getString(R.string.SIGNED_OUT_RESTART_ACTIVITY), message);
                            startActivity(intentRes);
                        });
                break;
//         TODO:   case R.id.nav_share:
//                DialogUtil.makeAndShow(this,
//                        "Share Options",
//                        "",
//                        "","");
//
//                break;
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
        } else if (id == R.id.list_activity_category_spinner) {
            String categoryName = (String) spinner.getSelectedItem();
            if (categoryName != NoteCategory.MAIN_NAME) {
                Toast.makeText(this, "NoteCategory by the name of " + categoryName + " was selected", Toast.LENGTH_SHORT).show();
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

    void toggleEmptyListMessage(int recyView, int emptImage, int emptyMessage) {
        //Empty List state vies
        TextView emptyListMessage = findViewById(R.id.empty_view1);
        ImageView emptyListImage = findViewById(R.id.empty_view2);

        recyclerView.setVisibility(recyView);
        emptyListMessage.setVisibility(emptyMessage);
        emptyListImage.setVisibility(emptImage);

    }


    /**
     * Switches the priority star on and off
     */
    private void handleTogglePriorityStar(ImageButton starView, boolean colorTheStar) {
        Drawable starDrawable = starView.getDrawable();
        if (starDrawable != null) {
            starDrawable.mutate();
            if (colorTheStar) {
                starDrawable.setColorFilter(ContextCompat.getColor(this, R.color.colorPriorityHigh), PorterDuff.Mode.SRC_ATOP);

            } else {
                starDrawable.setColorFilter(null);


            }
        }
    }
}