package com.ethical_techniques.notemaker;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.ethical_techniques.notemaker.auth.BaseActivity;
import com.ethical_techniques.notemaker.frags.SettingsFragment;

/**
 * The type Settings activity.
 *
 * @author Harry Dulaney
 */
public class SettingsActivity extends BaseActivity {

    private final String TAG = getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();

        //Initialize the Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get the Toolbar back as an ActionBar and initialize the back button (Up/Home Button)
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        } else {
            Log.e(TAG, "ActionBar was not created properly...");
        }
    }

    /**
     * Initializes the top app bar.
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar_back_button, menu);
        return true;
    }

    /**
     * @param item that was selected
     * @return true if the item is recognized
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_bar_back_button) {
//            switch (callingContext) {
//                case R.integer.list_activity:
//                    //Return to list activity
//                    break;
//                case R.integer.categ_list_activty:
//                    //Return to categoryListActivity
//                    break;
//                case R.integer.main_activity:
//                    //Return to main activity
//
//            }
//
//            return true;
//        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
//    public static void setCallingActivity(int contextKey) {
//        SettingsActivity.callingContext = contextKey;
//    }


}
