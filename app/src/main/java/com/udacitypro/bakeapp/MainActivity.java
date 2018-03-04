package com.udacitypro.bakeapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.udacitypro.bakeapp.fragments.RecipeStepListFragment;
import com.udacitypro.bakeapp.fragments.StepDetailFragment;

public class MainActivity extends AppCompatActivity
        implements RecipeStepListFragment.OnRecipeClickListener,
        StepDetailFragment.OnNextOrPreviousSelected{

    private static final String TAG = "MainActivity";

    private boolean mTwoPane;
    private Bundle mBundle;
    private int positionInt = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!getResources().getBoolean(R.bool.isTablet)) {

            Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
            setSupportActionBar(myToolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Bake App");

            myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fm = getSupportFragmentManager();
                    if (findViewById(R.id.recipe_detail_list_container) == null) {
                        if (fm.getBackStackEntryCount() > 1) {
                            fm.popBackStack("STACK_RECIPE_DETAIL", 0);
                        } else if (fm.getBackStackEntryCount() > 0) {
                            finish();
                        }
                    } else {
                        finish();

                    }

                }
            });
        }

        Intent intent = getIntent();
        mBundle = intent.getBundleExtra("bundle_object");

        if(savedInstanceState == null) {

            RecipeStepListFragment recipeStepListFragment = new RecipeStepListFragment();
            recipeStepListFragment.setArguments(mBundle);
            FragmentManager stepListFragmentManager = getSupportFragmentManager();

            stepListFragmentManager.beginTransaction()
                    .add(R.id.recipe_detail_list_container, recipeStepListFragment)
                    .commit();

        }else{
            positionInt = savedInstanceState.getInt("position_key");
        }

        if (getResources().getBoolean(R.bool.isTablet)) {
            mTwoPane = true;

            Log.d(TAG, "onCreate: positionInt = " + positionInt);
            mBundle.putInt("position", positionInt);
            if(savedInstanceState == null) {

                StepDetailFragment stepDetailFragment = new StepDetailFragment();
                stepDetailFragment.setArguments(mBundle);
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .add(R.id.steps_detail_container, stepDetailFragment)
                        .commit();
            }

        } else {
            mTwoPane = false;
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
    public void onRecipeStepSelected(int position) {
        positionInt = position;
        mBundle.putInt("position", positionInt);
        if (mTwoPane) {
            StepDetailFragment stepDetailFragment = new StepDetailFragment();
            stepDetailFragment.setArguments(mBundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.steps_detail_container, stepDetailFragment)
                    .commit();
        } else {
            Intent detail_intent = new Intent(MainActivity.this, StepDetailsActivity.class);
            detail_intent.putExtra("bundle_object", mBundle);
            startActivity(detail_intent);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("position_key", positionInt);
        Log.d(TAG, "onSaveInstanceState: " + positionInt);
    }


    @Override
    public void onButtonClicked(Bundle bundle) {
        Log.d(TAG, "onButtonClicked: here");
    }
}
