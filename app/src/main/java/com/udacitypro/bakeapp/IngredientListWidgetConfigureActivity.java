package com.udacitypro.bakeapp;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.udacitypro.bakeapp.provider.RecipeContentProvider;
import com.udacitypro.bakeapp.provider.RecipeContract;

import java.util.ArrayList;

/**
 * The configuration screen for the {@link IngredientListWidget IngredientListWidget} AppWidget.
 */
public class IngredientListWidgetConfigureActivity extends Activity {

    private static final String TAG = "IngredientListWidgetCon";
    private static final String PREFS_NAME = "com.udacitypro.bakeapp.IngredientListWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    public IngredientListWidgetConfigureActivity() {
        super();
    }

    static void saveTitlePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.apply();
    }

    static String loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return context.getString(R.string.appwidget_text);
        }
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);


        setResult(RESULT_CANCELED);

        setContentView(R.layout.ingredient_list_widget_configure);

        final ArrayList<String> list = new ArrayList<>();

        String[] projection = new String[] {RecipeContract.RecipeEntry.COLUMN_NAME};

        Cursor c = getContentResolver().query(RecipeContentProvider.RecipeItemTable.RECIPE_CONTENT_URI,
                projection,
                null,
                null,
                null);

        while (c.moveToNext()){
            String recipe = c.getString(c.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_NAME));
            list.add(recipe);
        }

        ListView mListView = (ListView)findViewById(R.id.widget_configure_list_view);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        if(mListView == null){
            Log.d(TAG, "onCreate: null");
            return;
        }
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String widgetText = list.get(position);
                saveTitlePref(getApplicationContext(), mAppWidgetId, widgetText);

                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
                IngredientListWidget.updateAppWidget(getApplicationContext(), appWidgetManager, mAppWidgetId);

                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                setResult(RESULT_OK, resultValue);
                finish();
            }
        });

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

    }
}

