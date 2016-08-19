package com.example.android.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.app.data.WeatherContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private static final String FORECAST_SHARE_HASHTAG = "#SunshineApp";
    private final int FORECAST_DETAIL_LOADER_ID = 1;
    private String mForecastStr;
    ShareActionProvider mShareActionProvider;

    private static final String[] FORECAST_DETAIL_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG
    };

    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_WEATHER_COLUMN_HUMIDITY = 5;
    static final int COL_WEATHER_COLUMN_WIND_SPEED = 6;
    static final int COL_WEATHER_COLUMN_DEGREES = 7;
    static final int COL_WEATHER_COLUMN_PRESSURE = 8;
    static final int COL_LOCATION_SETTING = 9;
    static final int COL_WEATHER_CONDITION_ID = 10;
    static final int COL_COORD_LAT = 11;
    static final int COL_COORD_LONG = 12;


    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(FORECAST_DETAIL_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.detailfragment,menu);
        // Share provider
        MenuItem mShare = menu.findItem(R.id.action_share);

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(mShare);
        if(mForecastStr != null) {
            mShareActionProvider.setShareIntent(createShareIntent());
        }
    }

    private Intent createShareIntent(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                mForecastStr + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();
        String uriStr = intent.getDataString();

        return new CursorLoader(getActivity(), Uri.parse(uriStr), FORECAST_DETAIL_COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.moveToFirst()) {

            // left column
            // date
            long date = data.getLong(COL_WEATHER_DATE);
            TextView dayView = (TextView) getActivity().findViewById(R.id.list_item_day_textview);
            dayView.setText(Utility.getDayName(getActivity(), date));
            TextView dateView = (TextView) getActivity().findViewById(R.id.list_item_date_textview);
            dateView.setText(Utility.getFormattedMonthDay(getActivity(), date));
            // high / low temp
            boolean isMetric = Utility.isMetric(getActivity());

            double high = data.getDouble(COL_WEATHER_MAX_TEMP);
            TextView highTempView = (TextView) getActivity().findViewById(R.id.list_item_high_textview);
            highTempView.setText(Utility.formatTemperature(getContext(), high, isMetric));

            double low = data.getDouble(COL_WEATHER_MIN_TEMP);
            TextView lowTempView = (TextView) getActivity().findViewById(R.id.list_item_low_textview);
            lowTempView.setText(Utility.formatTemperature(getContext(), low, isMetric));

            // Humidity
            float humidity = data.getFloat(COL_WEATHER_COLUMN_HUMIDITY);
            TextView humidityView = (TextView) getActivity().findViewById(R.id.list_item_humidity_textview);
            humidityView.setText(getContext().getString(R.string.format_humidity, humidity));

            // Wind
            float wind_speed = data.getFloat(COL_WEATHER_COLUMN_WIND_SPEED);
            float wind_dir = data.getFloat(COL_WEATHER_COLUMN_DEGREES);
            TextView windView = (TextView) getActivity().findViewById(R.id.list_item_wind_textview);
            windView.setText(Utility.getFormattedWind(getContext(),wind_speed,wind_dir));

            // Pressure
            float pressure = data.getFloat(COL_WEATHER_COLUMN_PRESSURE);
            TextView pressureView = (TextView) getActivity().findViewById(R.id.list_item_pressure_textview);
            pressureView.setText(getContext().getString(R.string.format_pressure, pressure));

            // right column
            // icon
            int weatherId = data.getInt(COL_WEATHER_ID);
            ImageView iconView = (ImageView) getActivity().findViewById(R.id.list_item_icon);
            iconView.setImageResource(R.drawable.ic_weather); // todo

            // weather forecast
            String forecast = data.getString(COL_WEATHER_DESC);
            TextView descriptionView = (TextView) getActivity().findViewById(R.id.list_item_forecast_textview);
            descriptionView.setText(forecast);
        }

//            if (mShareActionProvider != null) {
//                mShareActionProvider.setShareIntent(createShareIntent());
//            }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}