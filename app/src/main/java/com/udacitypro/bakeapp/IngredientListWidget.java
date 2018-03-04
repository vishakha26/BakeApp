package com.udacitypro.bakeapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import static com.google.android.exoplayer2.mediacodec.MediaCodecInfo.TAG;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link IngredientListWidgetConfigureActivity IngredientListWidgetConfigureActivity}
 */
public class IngredientListWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = IngredientListWidgetConfigureActivity.loadTitlePref(context, appWidgetId);

        RemoteViews views = getIngredientRemoteViews(context, widgetText.toString());

        views.setTextViewText(R.id.appwidget_text, widgetText + " Ingredients");
        Log.d(TAG, "updateAppWidget: " + widgetText);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            IngredientListWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }

    private static RemoteViews getIngredientRemoteViews(Context context, String recipe_name){
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredient_list_widget);

        Intent intent = new Intent(context, ListWidgetService.class);
        intent.putExtra("recipe", recipe_name);
        views.setRemoteAdapter(R.id.widget_list_view, intent);

        Intent appIntent = new Intent(context, DisplayMainList.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widget_list_view, appPendingIntent);

        views.setEmptyView(R.id.widget_list_view, R.id.empty_view);

        return views;
    }
}

