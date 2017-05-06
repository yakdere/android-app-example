package com.yaprakakdere.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;
import com.yaprakakdere.myapplication.model.Restaurant;
import com.yaprakakdere.myapplication.service.MResultReceiver;
import com.yaprakakdere.myapplication.service.RequestIntentService;
import com.yaprakakdere.myapplication.storage.Preferences;

import java.util.ArrayList;

/**
 * Created by yaprakakdere on 5/4/17.
 */

public class DiscoverDetailsFragment extends Fragment implements MResultReceiver.Receiver {

    private static final String RES_ID = "res_id";
    private static final String RES_OBJECT = "res_obj";
    private ImageView imageView;
    private TextView details, status, rating;
    private Button addToFavButton;
    private String resID;
    private Restaurant restaurant;
    private MResultReceiver mResultReceiver;
    private Gson gson;
    private DetailsButtonActionListener listener;

    public static DiscoverDetailsFragment newInstance(String id) {
        Bundle bundle = new Bundle();
        bundle.putString(RES_ID, id);
        DiscoverDetailsFragment discoverDetailsFragment = new DiscoverDetailsFragment();
        discoverDetailsFragment.setArguments(bundle);
        return discoverDetailsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mResultReceiver = ((MainActivity) getActivity()).getReceiver();
        listener = (DetailsButtonActionListener) getActivity();
        // retain this fragment when activity is re-initialized
        setRetainInstance(true);
    }

    public interface DetailsButtonActionListener {
        void onButtonClicked(String restaurant, boolean isFav);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.discover_details_fragment, parent, false);
        imageView = (ImageView) view.findViewById(R.id.ivResImage);
        details = (TextView) view.findViewById(R.id.tvDetails);
        status = (TextView) view.findViewById(R.id.tvDetailsStatus);
        rating = (TextView) view.findViewById(R.id.tvDetailsRating);
        addToFavButton = (Button) view.findViewById(R.id.buttonAddFavs);
        gson = new GsonBuilder().disableHtmlEscaping().create();
        if (savedInstanceState != null) {
            resID = savedInstanceState.getString(RES_ID);
            restaurant = gson.fromJson(savedInstanceState.getString(RES_OBJECT), Restaurant.class);
        } else if (getArguments() != null) {
            resID = getArguments().getString(RES_ID);
        }
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        mResultReceiver.setReceiver(this);

        if (restaurant == null) {
            RequestIntentService.startActionFetchResDetails(getContext(), resID, mResultReceiver);
        } else {
            setUI(restaurant);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mResultReceiver.setReceiver(null);
        listener = null;
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        if (resultData.getInt(RequestIntentService.RESULT_FIELD_TYPE) == RequestIntentService.RESULT_TYPE_DISCOVER_DETAILS) {
            if (resultData.getInt("status") == RequestIntentService.STATUS_SUCCESS) {
                String encodedRestaurant = resultData.getString(RequestIntentService.RESULT_ACTUAL_RESULT);
                restaurant = gson.fromJson(encodedRestaurant, Restaurant.class);
                setUI(restaurant);
            }
        }
    }

    View.OnClickListener removeFromFavClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Preferences.getInstance(getContext()).removeFromFav(resID);
            Toast.makeText(getContext(), getString(R.string.success_remove), Toast.LENGTH_SHORT).show();
            updateFavButton();
            if (listener != null) {
                listener.onButtonClicked(gson.toJson(restaurant), false);
            }
        }
    };

    View.OnClickListener addToFavClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Preferences.getInstance(getContext()).addResToFavs(resID, gson.toJson(restaurant));
            Toast.makeText(getContext(), getString(R.string.success_add), Toast.LENGTH_SHORT).show();
            if (listener != null) {
                listener.onButtonClicked(gson.toJson(restaurant), true);
            }
            updateFavButton();
        }
    };

    public void setUI(Restaurant restaurant) {
        if (restaurant == null) {
            return;
        }
        Picasso.with(getContext()).load(restaurant.getCover_img_url()).into(imageView);
        details.setText(restaurant.getDescription());
        status.setText(restaurant.getStatus());
        rating.setText(restaurant.getAverage_rating());
        updateFavButton();
    }

    private void updateFavButton() {
        final String buttonNewText;
        if (Preferences.getInstance(getContext()).isFav(resID)) {
            buttonNewText = getString(R.string.remove_from_fav);
            addToFavButton.setOnClickListener(removeFromFavClickListener);
        } else {
            buttonNewText = getString(R.string.add_favs);
            addToFavButton.setOnClickListener(addToFavClickListener);
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addToFavButton.setText(buttonNewText);
                addToFavButton.setVisibility(View.VISIBLE);
            }
        });
    }
}
