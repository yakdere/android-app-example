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
import com.yaprakakdere.myapplication.storage.Preferences;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by yaprakakdere on 5/4/17.
 */

public class FavoritesFragment extends Fragment {

    public static final String FAVORITES_FRAGMENT_TAG = FavoritesFragment.class.getCanonicalName() + ".TAG";
    public final static String LIST_STATE_KEY = "recycler_list_state";
    public final static String RESTAURANTS = "restaurants_instance";

    private View emptyList;
    private RecyclerView recyclerView;
    private Parcelable listState;
    private LinearLayoutManager layoutManager;
    private ArrayList<Restaurant> restaurants = new ArrayList<>();

    public static Fragment newInstance() {
        FavoritesFragment myFragment = new FavoritesFragment();
        return myFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment when activity is re-initialized
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
            this.restaurants = MyApplication.getGson().fromJson(encodedRes, type);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.favorites_fragment, parent, false);
        emptyList = view.findViewById(R.id.tvEmpty);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView = (RecyclerView) view.findViewById(R.id.rvRestaurants);
        recyclerView.setLayoutManager(layoutManager);
        RestaurantsAdapter restaurantsAdapter = new RestaurantsAdapter(getContext(), restaurants);
        recyclerView.setAdapter(restaurantsAdapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listState != null) {
            layoutManager.onRestoreInstanceState(listState);
        }

        if (!Preferences.getInstance(getContext()).getFavsRes().isEmpty()) {
            ArrayList<Restaurant> cachedRes = new ArrayList<>();
            for (String s : Preferences.getInstance(getContext()).getFavsRes().values()) {
                Restaurant r = MyApplication.getGson().fromJson(s, Restaurant.class);
                cachedRes.add(r);
            }
            if (cachedRes.size() != this.restaurants.size()) {
                this.restaurants = cachedRes;
                ((RestaurantsAdapter) recyclerView.getAdapter()).updateData(this.restaurants);
            } else {
                //TODO compare with local cache with cached data in shared pref only update modified items for performance
            }
        } else {
            this.restaurants.clear();
        }

       if (this.restaurants.size() == 0){
           emptyList.setVisibility(View.VISIBLE);
           recyclerView.setVisibility(View.GONE);
       } else {
           emptyList.setVisibility(View.GONE);
           recyclerView.setVisibility(View.VISIBLE);
       }
    }
}
