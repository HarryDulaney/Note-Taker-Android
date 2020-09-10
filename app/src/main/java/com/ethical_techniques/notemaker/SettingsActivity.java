package com.ethical_techniques.notemaker;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ethical_techniques.notemaker.model.SettingsFragment;

import java.util.Set;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();

        toolbar = findViewById(R.id.action_bar_top);
        initToolBar();


    }

    private void initToolBar() {
        if (toolbar != null) {
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.action_bar_settings:
                            return true;

                        case R.id.action_bar_list:
                            Intent intent = new Intent(SettingsActivity.this, ListActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            return true;

                        case R.id.action_bar_new:
                            Intent intent2 = new Intent(SettingsActivity.this, NoteActivity.class);
                            intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent2);
                            return true;

                        default:
                            return false;

                    }

                }
            });

        }
    }

}
