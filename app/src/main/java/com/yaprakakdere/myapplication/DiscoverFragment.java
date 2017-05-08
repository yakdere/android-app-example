package com.yaprakakdere.myapplication;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.yaprakakdere.myapplication.adapters.RestaurantsAdapter;
import com.yaprakakdere.myapplication.model.Restaurant;
import com.yaprakakdere.myapplication.service.MResultReceiver;
import com.yaprakakdere.myapplication.service.RequestIntentService;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by yaprakakdere on 5/4/17.
 */

public class DiscoverFragment extends Fragment implements MResultReceiver.Receiver {

    public static final String DISCOVER_FRAGMENT_TAG = DiscoverFragment.class.getCanonicalName() + ".TAG";
    public final static String LIST_STATE_KEY = "recycler_list_state";
    public final static String RESTAURANTS = "restaurants_instance";

    public static Fragment newInstance() {
        DiscoverFragment discoverFragment = new DiscoverFragment();
        return discoverFragment;
    }

    private RecyclerView recyclerView;
    private Parcelable listState;
    private LinearLayoutManager layoutManager;
    private ArrayList<Restaurant> restaurants = new ArrayList<>();
    private MResultReceiver mResultReceiver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mResultReceiver = ((MainActivity) getActivity()).getReceiver();
        // In many cases, we can avoid problems when an Activity is
        // re-created by simply using fragments. If your views and state are within a fragment,
        // we can easily have the fragment be retained when the activity is re-created
        setRetainInstance(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        listState = layoutManager.onSaveInstanceState();
        outState.putParcelable(LIST_STATE_KEY, listState);
        Type type = new TypeToken<ArrayList<Restaurant>>(){}.getType();
        //the disableHtmlEscaping() method tells Gson not to escape HTML characters such as <, >, &, =, and '
        outState.putString(RESTAURANTS, MyApplication.getGson().toJson(restaurants, type) );
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            listState = savedInstanceState.getParcelable(LIST_STATE_KEY);
            String encodedRes = savedInstanceState.getString(RESTAURANTS);
            Type type = new TypeToken<ArrayList<Restaurant>>(){}.getType();
            restaurants = MyApplication.getGson().fromJson(encodedRes, type);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.discover_fragment, parent, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.rvRestaurants);
        // Set layout manager to position the items
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        RestaurantsAdapter adapter = new RestaurantsAdapter(getContext(), restaurants);
        // Attach the adapter to the recyclerview to populate items
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mResultReceiver.setReceiver(this);

        if (listState != null) {
            layoutManager.onRestoreInstanceState(listState);
        }

        if (restaurants.size() == 0) {
            RequestIntentService.startActionFetchRes(this.getContext(), mResultReceiver);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mResultReceiver.setReceiver(null);
    }


    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        if (resultData.getInt(RequestIntentService.RESULT_FIELD_TYPE) == RequestIntentService.RESULT_TYPE_DISCOVER) {
            if (resultData.getInt("status") == RequestIntentService.STATUS_SUCCESS) {
                String encodedRestaurants = resultData.getString(RequestIntentService.RESULT_ACTUAL_RESULT);

                Type type = new TypeToken<ArrayList<Restaurant>>(){}.getType();
                restaurants = MyApplication.getGson().fromJson(encodedRestaurants, type);
                ((RestaurantsAdapter) recyclerView.getAdapter()).updateData(restaurants);
            }
        }
    }
}
