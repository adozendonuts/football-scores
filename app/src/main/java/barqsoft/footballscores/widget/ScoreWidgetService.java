package barqsoft.footballscores.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.widget.RemoteViews;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;

/**
 * Created by SandD on 11/22/2015.
 */
public class ScoreWidgetService extends IntentService {

    // service for updating widget

    // projection
    private static final String[] FOOTBALL_DATA_COLUMNS = {
            DatabaseContract.scores_table.DATE_COL,
            DatabaseContract.scores_table.HOME_COL,
            DatabaseContract.scores_table.HOME_GOALS_COL,
            DatabaseContract.scores_table.AWAY_COL,
            DatabaseContract.scores_table.AWAY_GOALS_COL,
    };

    // variables representing indices in FOOTBALL_DATA_COLUMNS array
    static final int COL_DATE = 0;
    static final int COL_HOME_TEAM = 1;
    static final int COL_AWAY_TEAM = 2;
    static final int COL_HOME_GOALS = 3;
    static final int COL_AWAY_GOALS = 4;

    public ScoreWidgetService() {
        super("ScoreWidgetService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager
                .getAppWidgetIds(new ComponentName(this, ScoreWidgetProvider.class));

        // get database info, sort by date, ascending
        Uri scoreUri = DatabaseContract.BASE_CONTENT_URI;
        Cursor data = getContentResolver().query(scoreUri, FOOTBALL_DATA_COLUMNS, null, null,
                DatabaseContract.scores_table.DATE_COL + " ASC");
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.appwidget);
        ;

        // Tell the AppWidgetManager to perform an update on the current app widget

        if (data == null) {
            return;
        }
        if (!data.moveToFirst()) {
            remoteViews.setViewVisibility(R.id.widget_home_score_box, View.INVISIBLE);
            remoteViews.setViewVisibility(R.id.widget_away_score_box, View.INVISIBLE);
            remoteViews.setTextViewText(R.id.widget_date_textview, getString(R.string.no_games));

            data.close();
        } else {
            // data is not null or empty, assign column values to variables
            String date = data.getString(COL_DATE);
            String home_team = data.getString(COL_HOME_TEAM);
            String home_score = Integer.toString(data.getInt(COL_HOME_GOALS));
            String away_team = data.getString(COL_AWAY_TEAM);
            String away_score = Integer.toString(data.getInt(COL_AWAY_GOALS));

            // update each visible instance of a widget
            for (int appWidgetId : appWidgetIds){
                remoteViews.setTextViewText(R.id.widget_date_textview, date);
                remoteViews.setTextViewText(R.id.widget_home_team, home_team);
                remoteViews.setTextViewText(R.id.widget_home_score, home_score);
                remoteViews.setTextViewText(R.id.widget_away_team, away_team);
                remoteViews.setTextViewText(R.id.widget_away_score, away_score);

                appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
            }
        }
    }


}
