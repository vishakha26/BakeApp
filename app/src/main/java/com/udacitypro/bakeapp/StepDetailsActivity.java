package com.udacitypro.bakeapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.udacitypro.bakeapp.fragments.StepDetailFragment;

public class StepDetailsActivity extends AppCompatActivity
        implements StepDetailFragment.OnNextOrPreviousSelected{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(isIsInLandScapeMode()){
            getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
//            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_step_details);

        if(isIsInLandScapeMode()){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }


        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                if (findViewById(R.id.steps_detail_container)==null) {
                    if (fm.getBackStackEntryCount() > 1) {
                        //go back to "Recipe Detail" screen
                        fm.popBackStack("STACK_RECIPE_DETAIL", 0);
                    } else if (fm.getBackStackEntryCount() > 0) {
                        //go back to "Recipe" screen
                        finish();
                    }
                }
                else {
                    //go back to "Recipe" screen
                    finish();

                }

            }
        });

        Intent intent = getIntent();

        Bundle bundle = intent.getBundleExtra("bundle_object");

        if(savedInstanceState == null) {

            StepDetailFragment stepDetailFragment = new StepDetailFragment();
            stepDetailFragment.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.steps_detail_container, stepDetailFragment)
                    .commit();

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onButtonClicked(Bundle bundle) {
        if(!getResources().getBoolean(R.bool.isTablet)){
            StepDetailFragment stepDetailFragment = new StepDetailFragment();
            stepDetailFragment.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.steps_detail_container, stepDetailFragment)
                    .commit();
        }
    }

    private boolean isIsInLandScapeMode(){
        int i = this.getResources().getConfiguration().orientation;
        boolean mode;
        if(i == 2){
            mode = true;
        }else{
            mode = false;
        }
        return mode;
    }
}
