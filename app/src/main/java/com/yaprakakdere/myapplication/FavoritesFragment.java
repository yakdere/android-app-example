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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
    private Gson gson;

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
        outState.putString(RESTAURANTS, gson.toJson(restaurants, type) );
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            listState = savedInstanceState.getParcelable(LIST_STATE_KEY);
            String encodedRes = savedInstanceState.getString(RESTAURANTS);
            Type type = new TypeToken<ArrayList<Restaurant>>(){}.getType();
            restaurants = gson.fromJson(encodedRes, type);
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
        gson = new GsonBuilder().disableHtmlEscaping().create();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listState != null) {
            layoutManager.onRestoreInstanceState(listState);
        }

        if (!Preferences.getInstance(getContext()).getFavsRes().isEmpty()) {
            for (String s : Preferences.getInstance(getContext()).getFavsRes().values()) {
                Restaurant restaurant = gson.fromJson(s, Restaurant.class);
                restaurants.add(restaurant);
            }
        }

        if (restaurants != null && restaurants.size() > 0) {
            RestaurantsAdapter adapter = new RestaurantsAdapter(getContext(), restaurants);
            // Attach the adapter to the recyclerview to populate items
            recyclerView.setAdapter(adapter);
        } else {
            emptyList.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    public void onFavButtonClicked(String restaurant, boolean isFav) {
        Restaurant modifiedRes = gson.fromJson(restaurant, Restaurant.class);
        if (isFav) {
            if (restaurants == null) {
                restaurants = new ArrayList<Restaurant>();
                restaurants.add(modifiedRes);
                recyclerView.getAdapter().notifyDataSetChanged();
            } else {
                restaurants.add(0, modifiedRes);
                recyclerView.getAdapter().notifyItemInserted(0);
            }
        } else if (restaurants != null && restaurants.contains(modifiedRes)) {
            int pos = restaurants.indexOf(modifiedRes);
            restaurants.remove(modifiedRes);
            recyclerView.getAdapter().notifyItemRemoved(pos);
        }
    }
}
