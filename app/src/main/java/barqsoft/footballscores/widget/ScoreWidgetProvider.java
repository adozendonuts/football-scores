package barqsoft.footballscores.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;


public class ScoreWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        context.startService(new Intent(context, ScoreWidgetService.class));
        super.onUpdate(context, appWidgetManager, appWidgetIds);
//        Log.v("ScoreWidgetProvider", "onUpdate ran!");
    }

//    @Override
//    public void onReceive(Context context, Intent intent) {
//        Log.v("ONUPDATECOLLECTION", "onReceive called");
//        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
//        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, getClass()));
//        appWidgetManager.up
//        super.onReceive(context, intent);
//    }

}

