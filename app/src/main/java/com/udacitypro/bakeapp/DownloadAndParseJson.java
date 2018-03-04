package com.udacitypro.bakeapp;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.udacitypro.bakeapp.provider.RecipeContentProvider;
import com.udacitypro.bakeapp.provider.RecipeContract;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadAndParseJson extends AsyncTask<String, Void, String>{

    private static final String TAG = "DownloadAndParseJson";

    enum DownloadStatus {OK, UNKNOWN_FAILED, UNKNOWN_INVALID_JSON, UNKNOWN_STATUS_CODE }
    private DownloadStatus status;
    private Context mContext;

    private OnDownloadComplete mCallback;
    interface OnDownloadComplete {
        void onDownloadComplete(DownloadStatus status);
    }

    DownloadAndParseJson(OnDownloadComplete callback, Context context) {
        this.mCallback = callback;
        this.mContext = context;
    }

    @Override
    protected String doInBackground(String... params) {
        try{
            URL url = new URL(params[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            int statusCode = connection.getResponseCode();

            if(statusCode == 200){
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder builder = new StringBuilder();
                String line;
                while((line = reader.readLine()) != null){
                    builder.append(line);
                }
                status = DownloadStatus.OK;
                return builder.toString();
            }else{
                status = DownloadStatus.UNKNOWN_STATUS_CODE;
                return null;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        parseData(result);
        if(mCallback != null){
            mCallback.onDownloadComplete(status);
        }
    }

    private void parseData(String data){
        if(data == null)return;
        try{
            JSONArray array = new JSONArray(data);
            for(int main_int = 0; main_int < array.length(); main_int++){
                JSONObject recipe = array.getJSONObject(main_int);
                int id = recipe.getInt("id");
                String name = recipe.getString("name");
                String servings = recipe.getString("servings");
                String images = recipe.getString("image");
                String ingredients_array = recipe.getJSONArray("ingredients").toString();
                String steps_array = recipe.getJSONArray("steps").toString();

                ContentValues cv = new ContentValues();
                cv.put(RecipeContract.RecipeEntry.COLUMN_ID, id);
                cv.put(RecipeContract.RecipeEntry.COLUMN_NAME, name);
                cv.put(RecipeContract.RecipeEntry.COLUMN_SERVINGS, servings);
                cv.put(RecipeContract.RecipeEntry.COLUMN_IMAGES, images);
                cv.put(RecipeContract.RecipeEntry.COLUMN_INGREDIENTS_ARRAY, ingredients_array);
                cv.put(RecipeContract.RecipeEntry.COLUMN_STEPS_ARRAY, steps_array);

                mContext.getContentResolver().insert(RecipeContentProvider.RecipeItemTable.RECIPE_CONTENT_URI, cv);
            }
        }catch (Exception e){
            status = DownloadStatus.UNKNOWN_INVALID_JSON;
            e.printStackTrace();
            Log.e(TAG, "parseData: Error parsing json");
        }
    }
}
