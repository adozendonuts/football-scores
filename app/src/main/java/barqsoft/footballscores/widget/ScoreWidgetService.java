package barqsoft.footballscores.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;

public class ScoreWidgetService extends IntentService {

    // service for updating widget

    // projection
    private static final String[] FOOTBALL_DATA_COLUMNS = {
            DatabaseContract.scores_table.DATE_COL,
            DatabaseContract.scores_table.HOME_COL,
            DatabaseContract.scores_table.HOME_GOALS_COL,
            DatabaseContract.scores_table.AWAY_COL,
            DatabaseContract.scores_table.AWAY_GOALS_COL,
            DatabaseContract.scores_table.TIME_COL
    };

    // variables representing indices in FOOTBALL_DATA_COLUMNS array
    static final int COL_DATE = 0;
    static final int COL_HOME_TEAM = 1;
    static final int COL_HOME_GOALS = 2;
    static final int COL_AWAY_TEAM = 3;
    static final int COL_AWAY_GOALS = 4;
    static final int COL_GAME_TIME= 5;

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

        // Tell the AppWidgetManager to perform an update on the current app widget
        if (data != null) {
            // update each visible instance of a widget
            for (int appWidgetId : appWidgetIds) {

                // if dataset is empty, hide all views and show "no games" message.
                if (!data.moveToFirst()) {
//                    Log.v("ONUPDATE", "DATA NOT MOVETOFIRST" + " BRUH< THIS  IS EMPTY");
                    remoteViews.setViewVisibility(R.id.widget_away_home_titles, View.INVISIBLE);
                    remoteViews.setViewVisibility(R.id.widget_team_names, View.INVISIBLE);
                    remoteViews.setViewVisibility(R.id.widget_scores, View.INVISIBLE);
                    remoteViews.setTextViewText(R.id.widget_date_textview, getString(R.string.no_games));
                    appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
                    data.close();
                    return;
                } else {
//                    Log.v("ONUPDATE", "DATA NOT EMPTY!");
                    // data is not null or empty, assign column values to variables
                    String time = data.getString(COL_GAME_TIME);
                    String date = data.getString(COL_DATE);
                    String home_team = data.getString(COL_HOME_TEAM);
                    String away_team = data.getString(COL_AWAY_TEAM);
                    // db shows -1 if no record/value is available for that field. Changed to N/A to make it more meaningful
                    String home_score = (data.getString(COL_HOME_GOALS).equals("-1")) ? "No scores available" : data.getString(COL_HOME_GOALS);
                    String away_score = (data.getString(COL_AWAY_GOALS).equals("-1")) ? "No scores available" : data.getString(COL_AWAY_GOALS);

                    String scoresDesc;
                    if (data.getString(COL_HOME_GOALS).equals("-1") || data.getString(COL_AWAY_GOALS).equals("-1")) {
                        scoresDesc = "No Scores Available. Game starts " + date + " at " + time;
                    } else {
                        scoresDesc = away_score + " to " + home_score;
                    }

                    String contentDescription = String.format(getString(R.string.contentDescription), away_team, home_team, scoresDesc);

                    remoteViews.setTextViewText(R.id.widget_date_textview, date + " at " + time);
                    remoteViews.setTextViewText(R.id.widget_home_team, home_team);
                    remoteViews.setTextViewText(R.id.widget_home_score, home_score);
                    remoteViews.setTextViewText(R.id.widget_away_team, away_team);
                    remoteViews.setTextViewText(R.id.widget_away_score, away_score);

                    Log.v("ONUPDATE", date + home_team + home_score + away_team + away_score);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                        remoteViews.setContentDescription(R.id.app_logo, contentDescription);
                    }

                    appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
                }
            }
            data.close();
        }
    }
}
