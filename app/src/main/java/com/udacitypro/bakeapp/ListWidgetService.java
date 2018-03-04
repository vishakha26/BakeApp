package com.udacitypro.bakeapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacitypro.bakeapp.provider.RecipeContentProvider;
import com.udacitypro.bakeapp.provider.RecipeContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListWidgetService extends RemoteViewsService{
    private static final String TAG = "ListWidgetService";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        String item = intent.getStringExtra("recipe");
        Log.d(TAG, "onGetViewFactory: " + item);
        return new ListViewRemoteViewsFactory(this, item);
    }

}

class ListViewRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final String TAG = "ListViewRemoteViewsFact";

    private Context mContext;
    private String mRecipe;
    private ArrayList<String> mIngrList = new ArrayList<>();

    public ListViewRemoteViewsFactory(Context context, String recipe){
        this.mContext = context;
        this.mRecipe = recipe;
    }
    @Override
    public void onCreate() {

    }

    @Override
    public int getCount() {
        if(mIngrList == null) return 0;
        return mIngrList.size();
    }

    @Override
    public void onDataSetChanged() {


        String[] projection = new String[]{RecipeContract.RecipeEntry.COLUMN_INGREDIENTS_ARRAY};
        Cursor cursor = mContext.getContentResolver().query(RecipeContentProvider.RecipeItemTable.RECIPE_CONTENT_URI,
                projection,
                RecipeContract.RecipeEntry.COLUMN_NAME + "='" + mRecipe + "'",
                null,
                null);
        while (cursor.moveToNext()) {
            try {
                JSONArray jsonArray = new JSONArray(cursor.getString(cursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_INGREDIENTS_ARRAY)));
                for (int ingr_int = 0; ingr_int < jsonArray.length(); ingr_int++) {
                    JSONObject ingredients = jsonArray.getJSONObject(ingr_int);
                    String item = ingredients.getString("ingredient");
                    mIngrList.add(item);
                }

            } catch (JSONException e) {
                Log.e(TAG, "onDataSetChanged: Json parse error", e);
            }
        }
        cursor.close();

    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if(mIngrList == null || mIngrList.size() == 0)return null;

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.list_view_item_widget);

        String item = mIngrList.get(position);
        views.setTextViewText(R.id.list_item_widget_row, item);
        return views;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}

