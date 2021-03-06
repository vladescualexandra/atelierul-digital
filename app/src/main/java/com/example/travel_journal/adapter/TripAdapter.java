package com.example.travel_journal.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travel_journal.DisplayActivity;
import com.example.travel_journal.MainActivity;
import com.example.travel_journal.R;
import com.example.travel_journal.TripActivity;
import com.example.travel_journal.async.Callback;
import com.example.travel_journal.database.model.Trip;
import com.example.travel_journal.database.service.TripService;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    private List<Trip> trips;
    private Context context;

    public static class TripViewHolder extends RecyclerView.ViewHolder {
        public static final String TRIP_KEY_UPDATE = "trip_key_update";
        public static final String TRIP_KEY_DISPLAY = "trip_key_display";
        private final ImageView iv_image;
        private final TextView tv_name;
        private final TextView tv_destination;
        private final TextView tv_price;
        private final RatingBar rb_rating;
        private final ImageButton ib_fav;
        private Trip trip;

        public TripViewHolder(View view) {
            super(view);
            iv_image = view.findViewById(R.id.home_row_image);
            tv_name = view.findViewById(R.id.home_row_trip_name);
            tv_destination = view.findViewById(R.id.home_row_trip_destination);
            tv_price = view.findViewById(R.id.home_row_trip_price);
            rb_rating = view.findViewById(R.id.home_row_trip_rating);
            ib_fav = view.findViewById(R.id.home_row_button_fav);

            view.setOnClickListener(displayTripEvent());
            view.setOnLongClickListener(updateTripEvent());

            ib_fav.setOnClickListener(favEvent(view));
        }

        private View.OnClickListener displayTripEvent() {
            return new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), DisplayActivity.class);
                    intent.putExtra(TRIP_KEY_DISPLAY, trip);
                    v.getContext().startActivity(intent);
                }
            };
        }

        void bind(@NonNull final Trip trip) {
            this.trip = trip;
        }

        private View.OnLongClickListener updateTripEvent() {
            return new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Intent intent = new Intent(v.getContext(), TripActivity.class);
                    intent.putExtra(TRIP_KEY_UPDATE, trip);
                    v.getContext().startActivity(intent);
                    return false;
                }
            };
        }

        @NotNull
        private View.OnClickListener favEvent(View view) {
            return new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    trip.setFav(!trip.isFav());
                    TripService tripService = new TripService(view.getContext());
                    tripService.update(trip, addToFavDbCallback(view));
                }
            };
        }

        private Callback<Trip> addToFavDbCallback(View view) {
            return new Callback<Trip>() {
                @Override
                public void runResultOnUIThread(Trip result) {
                    if (result != null) {
                        Intent intent = new Intent(view.getContext(), MainActivity.class);
                        view.getContext().startActivity(intent);
                    }
                }
            };
        }
    }



    public TripAdapter(List<Trip> trips, Context context) {
        this.trips = trips;
        this.context = context;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_row_item, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        setView(holder, trips.get(position));
    }

    private void setView(@NonNull TripViewHolder holder, Trip trip) {
        holder.bind(trip);

        switch (trip.getType()) {
            case Trip.SEA_SIDE:
                holder.iv_image.setImageResource(R.drawable.beach);
                break;
            case Trip.MOUNTAINS:
                holder.iv_image.setImageResource(R.drawable.mountains);
                break;
            default:
                holder.iv_image.setImageResource(R.drawable.city);
                break;
        }

        holder.tv_name.setText(trip.getName());
        holder.tv_destination.setText(trip.getDestination());
        holder.tv_price.setText(trip.getPrice() + "€");
        holder.rb_rating.setRating((float) trip.getRating());

        if (trip.isFav()) {
            holder.ib_fav.setImageResource(R.drawable.ic_fav);
        } else {
            holder.ib_fav.setImageResource(R.drawable.ic_not_fav);
        }
    }


    @Override
    public int getItemCount() {
        return trips.size();
    }


}
