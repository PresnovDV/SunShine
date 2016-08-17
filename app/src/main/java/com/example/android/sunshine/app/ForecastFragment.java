package com.example.android.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.android.sunshine.R;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Dzianis_Prasnou on 8/10/2016.
 */
public class ForecastFragment extends Fragment {
    private ArrayAdapter<String> fcAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    private void updateWeather() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = settings.getString(getString(R.string.pref_location_key),getString(R.string.pref_location_default));
        new FetchWeatherTask(getActivity(),fcAdapter).execute(location);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            updateWeather();
            return true;
        }

        if (id == R.id.action_mapview) {
            showMap();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showMap() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = settings.getString(getString(R.string.pref_location_key),getString(R.string.pref_location_default));

        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.encodedPath("geo:0,0");
        uriBuilder.appendQueryParameter("q",location+"(Here!)");
        uriBuilder.appendQueryParameter("z","5");
        Uri geoLocation = Uri.parse(uriBuilder.toString());

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        String[] strings = {};
        ArrayList<String> itemsList = new ArrayList<>(Arrays.asList(strings));
        fcAdapter = new ArrayAdapter<String>(getActivity(),R.layout.list_item_forecast,R.id.list_item_forecast_textview,itemsList);
        listView.setAdapter(fcAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String forecast = fcAdapter.getItem(position);
                Intent detailIntent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, forecast);
                startActivity(detailIntent);
            }
        });

        return rootView;
    }

}
