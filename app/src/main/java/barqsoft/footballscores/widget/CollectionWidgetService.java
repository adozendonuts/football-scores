package barqsoft.footballscores.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;


public class CollectionWidgetService extends RemoteViewsService {

    // projection
    private static final String[] FOOTBALL_DATA_COLUMNS = {
            DatabaseContract.scores_table._ID,
            DatabaseContract.scores_table.HOME_COL,
            DatabaseContract.scores_table.HOME_GOALS_COL,
            DatabaseContract.scores_table.AWAY_COL,
            DatabaseContract.scores_table.AWAY_GOALS_COL,
            DatabaseContract.scores_table.DATE_COL
    };

    // variables representing indices in FOOTBALL_DATA_COLUMNS array
    static final int ROW_ID = 0;
    static final int COL_HOME_TEAM = 1;
    static final int COL_HOME_GOALS = 2;
    static final int COL_AWAY_TEAM = 3;
    static final int COL_AWAY_GOALS = 4;
    static final int COL_DATE = 5;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.v("ONUPDATECOLLECTION", "onGetViewFactory called");
        return new CollectionRemoteViewsFactory(this.getBaseContext(), intent);
    }

    public class CollectionRemoteViewsFactory implements RemoteViewsFactory {
        private Cursor data = null;

        public CollectionRemoteViewsFactory(Context context, Intent intent) {
        }

        @Override
        public void onCreate() {
        }

        @Override
        public void onDataSetChanged() {
            Log.v("ONUPDATECOLLECTION", "onDataSetChanged called");
            if (data != null) {
                data.close();
            }

            final long identityToken = Binder.clearCallingIdentity();
            Uri scoreUri = DatabaseContract.BASE_CONTENT_URI;
            data = getContentResolver().query(scoreUri,                 //content authority
                    FOOTBALL_DATA_COLUMNS,                              //projection
                    null,                                               //selection
                    null,                                               //selectionargs[]
                    DatabaseContract.scores_table.DATE_COL + " ASC");   //sort by date, ascending
            Binder.restoreCallingIdentity(identityToken);
        }


        @Override
        public void onDestroy() {
            Log.v("ONUPDATECOLLECTION", "onDestroy called");
            if (data != null) {
                data.close();
                data = null;
            }
        }

        @Override
        public RemoteViews getViewAt(int position) {
            Log.v("ONUPDATECOLLECTION", "getViewAt called");
            if (position == AdapterView.INVALID_POSITION
                    || data == null
                    || !data.moveToPosition(position)) {
                return null;
            }
            RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.appwidget_item);

            String date = data.getString(COL_DATE);
            String home_team = data.getString(COL_HOME_TEAM);
            String home_score = data.getString(COL_HOME_GOALS);
            String away_team = data.getString(COL_AWAY_TEAM);
            String away_score = data.getString(COL_AWAY_GOALS);

            remoteViews.setTextViewText(R.id.widget_date_textview, date);
            remoteViews.setTextViewText(R.id.widget_home_team, home_team);
            remoteViews.setTextViewText(R.id.widget_home_score, home_score);
            remoteViews.setTextViewText(R.id.widget_away_team, away_team);
            remoteViews.setTextViewText(R.id.widget_away_score, away_score);

            final Intent fillInIntent = new Intent();
            Uri scoreUri = DatabaseContract.scores_table.buildScoreWithDate();
            fillInIntent.setData(scoreUri);
            remoteViews.setOnClickFillInIntent(R.id.appwidget_item, fillInIntent);

            return remoteViews;
        }

        @Override
        public int getCount() {
            return data == null ? 0 : data.getCount();
        }

        @Override
        public RemoteViews getLoadingView() {
            return new RemoteViews(getPackageName(), R.layout.appwidget_item);
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            if (data.moveToPosition(position)) {
                return data.getLong(ROW_ID);
            }
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }


    }

    public class WidgetItem {
        int position;
        String date;
        String home_team;
        String home_score;
        String away_team;
        String away_score;

        public WidgetItem(int position, String date, String home_team,
                          String home_score, String away_team, String away_score) {
            this.position = position;
            this.date = date;
            this.home_team = home_team;
            this.home_score = home_score;
            this.away_team = away_team;
            this.away_score = away_score;
        }
    }
}
