package io.agora.openvcall.ui;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import io.agora.openvcall.model.ConstantApp;

public class SettingsActivity extends AppCompatActivity {
    private VideoProfileAdapter mVideoProfileAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_settings);

        //initUi();
    }

    private void initUi() {
//        RecyclerView v_profiles = (RecyclerView) findViewById(R.id.profiles);
//        v_profiles.setHasFixedSize(true);
//
//        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
//        int prefIndex = pref.getInt(ConstantApp.PrefManager.PREF_PROPERTY_PROFILE_IDX, ConstantApp.DEFAULT_PROFILE_IDX);
//
//        mVideoProfileAdapter = new VideoProfileAdapter(this, prefIndex);
//        mVideoProfileAdapter.setHasStableIds(true);
//
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        v_profiles.setLayoutManager(layoutManager);
//
//        v_profiles.setAdapter(mVideoProfileAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      return true;
    }

    @SuppressLint("NewApi") private void doSaveProfile() {
        int profileIndex = mVideoProfileAdapter.getSelected();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(ConstantApp.PrefManager.PREF_PROPERTY_PROFILE_IDX, profileIndex);
        editor.apply();
    }
}
