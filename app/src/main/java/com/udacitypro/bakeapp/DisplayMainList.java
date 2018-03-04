package com.udacitypro.bakeapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.udacitypro.bakeapp.adapters.RecyclerViewAdapter;
import com.udacitypro.bakeapp.objects.Recipe;
import com.udacitypro.bakeapp.provider.RecipeContentProvider;
import com.udacitypro.bakeapp.provider.RecipeContract;
import com.udacitypro.bakeapp.utils.ItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DisplayMainList extends AppCompatActivity
        implements DownloadAndParseJson.OnDownloadComplete,
        LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = "DisplayMainList";

    private final String KEY_RECYCLER_STATE = "recycler_state";
    private static Bundle mBundleRecyclerViewState;


    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mRecyclerViewAdapter;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mErrorText;
    public static final int LOADER_ID = 57;
    private ArrayList<Recipe> mRecipes;
    private boolean loaderShouldBeStartedNew;
    private Parcelable listState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //listState=savedInstanceState.getParcelable("ListState");


        setContentView(R.layout.display_main_list);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("Bake App");


        mErrorText = (TextView) findViewById(R.id.error_text);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_to_refresh);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        mRecyclerView = (RecyclerView) findViewById(R.id.main_recycler_view);
        if (getResources().getBoolean(R.bool.isTablet)) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
        mRecyclerViewAdapter = new RecyclerViewAdapter(null, this);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerView.addOnItemTouchListener(new ItemClickListener(this, mRecyclerView, new ItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent recipe_intent = new Intent(DisplayMainList.this, MainActivity.class);
                Recipe recipe = mRecipes.get(position);
                Bundle b = new Bundle();
                b.putParcelable("parcelable_object", recipe);
                recipe_intent.putExtra("bundle_object", b);
                startActivity(recipe_intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRecipeData();
            }
        });

        getRecipeData();
    }


    @Override
    protected void onPause() {

        super.onPause(); // save RecyclerView state
        mBundleRecyclerViewState = new Bundle();
        listState = mRecyclerView.getLayoutManager().onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, listState);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBundleRecyclerViewState != null) {
            Parcelable listState = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
            mRecyclerView.getLayoutManager().onRestoreInstanceState(listState);
        }
        /*if(!loaderShouldBeStartedNew) {
            getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
        }*/
    }

    @Override
    public void onDownloadComplete(DownloadAndParseJson.DownloadStatus status) {
        if (status == DownloadAndParseJson.DownloadStatus.OK) {
            getSupportLoaderManager().initLoader(LOADER_ID, null, this);
            loaderShouldBeStartedNew = false;
        } else if (status == DownloadAndParseJson.DownloadStatus.UNKNOWN_INVALID_JSON) {
            showErrorText(getResources().getString(R.string.bad_json));
        } else if (status == DownloadAndParseJson.DownloadStatus.UNKNOWN_FAILED) {
            showErrorText(getResources().getString(R.string.unknown_error));
        } else if (status == DownloadAndParseJson.DownloadStatus.UNKNOWN_STATUS_CODE) {
            showErrorText(getResources().getString(R.string.unknown_status_code));
        } else {
            showErrorText(getResources().getString(R.string.unknown_error));
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader: here");
        return new CursorLoader(this,
                RecipeContentProvider.RecipeItemTable.RECIPE_CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(mRecipes != null){
            mRecipes.clear();
            Log.d(TAG, "onLoadFinished: here");
        }
        Log.d(TAG, "onLoadFinished: here2");
        mRecipes = convertCursorToRecipe(data);
        mRecyclerViewAdapter.swapData(mRecipes);
        showRecyclerView();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerViewAdapter.swapData(null);
    }

    private void showErrorText(String message) {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mErrorText.setVisibility(View.VISIBLE);
        mErrorText.setText(message);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void showRecyclerView() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorText.setVisibility(View.INVISIBLE);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void getRecipeData() {
        if(isDataBaseEmpty()){
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                new DownloadAndParseJson(this, this).execute(getResources().getString(R.string.url));
            } else {
                showErrorText(getResources().getString(R.string.no_internet));
            }
        }else{
            mSwipeRefreshLayout.setRefreshing(false);
            if(loaderShouldBeStartedNew){
                getSupportLoaderManager().initLoader(LOADER_ID, null, this);
            }else {
                getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
            }
            loaderShouldBeStartedNew = false;
        }
    }

    private boolean isDataBaseEmpty(){
        String [] projection = new String []{RecipeContract.RecipeEntry.COLUMN_NAME};
        Cursor cursor = getContentResolver().query(RecipeContentProvider.RecipeItemTable.RECIPE_CONTENT_URI,
                projection,
                null,
                null,
                null);
        if(cursor.getCount() == 0)return true;
        return false;
    }

    private ArrayList<Recipe> convertCursorToRecipe(Cursor cursor){
        ArrayList<Recipe> recipe_list = new ArrayList<>();
        while (cursor.moveToNext()){

            try {
                int id = cursor.getInt(cursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_NAME));
                String servings = cursor.getString(cursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_SERVINGS));

                JSONArray ingredients_array = new JSONArray(cursor.getString(cursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_INGREDIENTS_ARRAY)));
                JSONArray steps_array = new JSONArray(cursor.getString(cursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_STEPS_ARRAY)));

                ArrayList<String> ingr = new ArrayList<>();

                for(int ingr_int = 0; ingr_int < ingredients_array.length(); ingr_int++){
                    JSONObject ingredients = ingredients_array.getJSONObject(ingr_int);
                    ingr.add(ingredients.toString());
                }

                ArrayList<String> stp = new ArrayList<>();

                for (int stp_int = 0; stp_int < steps_array.length(); stp_int++){
                    JSONObject steps = steps_array.getJSONObject(stp_int);
                    stp.add(steps.toString());
                }
                Recipe recipe = new Recipe(id, name, servings, "", ingr, stp);
                recipe_list.add(recipe);

            }catch (JSONException e){
                Log.e(TAG, "convertCursorToRecipe: Error parsing json", e);
            }
        }
        return recipe_list;
    }
}
