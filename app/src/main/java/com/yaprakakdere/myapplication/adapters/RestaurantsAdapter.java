package com.yaprakakdere.myapplication.adapters;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yaprakakdere.myapplication.DiscoverDetailsFragment;
import com.yaprakakdere.myapplication.R;
import com.yaprakakdere.myapplication.model.Restaurant;

import java.util.ArrayList;

import static com.yaprakakdere.myapplication.DiscoverDetailsFragment.RES_DETAILS_FRAGMENT_TAG;

/**
 * Created by yaprakakdere on 5/4/17.
 */

public class RestaurantsAdapter extends RecyclerView.Adapter<RestaurantsAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Restaurant> restaurants;

    public RestaurantsAdapter(Context context, ArrayList<Restaurant> restaurants) {
        this.context = context;
        this.restaurants = restaurants;
    }

    public void updateData(ArrayList<Restaurant> res) {
        restaurants.clear();
        restaurants.addAll(res);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.restaurant_row_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Restaurant restaurant = restaurants.get(position);
        Picasso.with(context).load(restaurant.getCover_img_url())
                .resize(100, 100)
                .centerInside()
                .error(R.drawable.ic_menu_gallery)
                .into(holder.imageView);
        holder.title.setText(restaurant.getName());
        holder.description.setText(restaurant.getDescription());
        holder.status.setText(restaurant.getStatus());
        holder.llitemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                if (fm.findFragmentByTag(RES_DETAILS_FRAGMENT_TAG) != null) {
                    fragmentTransaction.replace(R.id.flContent, fm.findFragmentByTag(RES_DETAILS_FRAGMENT_TAG));

                } else {
                    DiscoverDetailsFragment fragment = DiscoverDetailsFragment.newInstance(restaurant.getId(), restaurant.getName());
                    fragmentTransaction.replace(R.id.flContent, fragment);
                    fragmentTransaction.addToBackStack(RES_DETAILS_FRAGMENT_TAG);
                }
                fragmentTransaction.commitAllowingStateLoss();
            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title;
        TextView description;
        TextView status;
        View llitemView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.ivIcon);
            title = (TextView) itemView.findViewById(R.id.tvTitle);
            description = (TextView) itemView.findViewById(R.id.tvDescription);
            status = (TextView) itemView.findViewById(R.id.tvStatus);
            llitemView = itemView.findViewById(R.id.llItemView);
        }
    }
}
