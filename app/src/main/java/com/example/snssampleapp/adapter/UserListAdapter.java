package com.example.snssampleapp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.snssampleapp.FirebaseHelper;
import com.example.snssampleapp.PostInfo;
import com.example.snssampleapp.R;
import com.example.snssampleapp.UserInfo;
import com.example.snssampleapp.activity.PostActivity;
import com.example.snssampleapp.activity.WritePostActivity;
import com.example.snssampleapp.listener.OnPostListener;
import com.example.snssampleapp.view.ReadContentsView;
import com.google.android.exoplayer2.SimpleExoPlayer;

import java.util.ArrayList;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    private ArrayList<UserInfo> localDataSet;
    private Activity activity;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;

        public ViewHolder(CardView view) {
            super(view);
            cardView = view;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView.
     */
    public UserListAdapter(Activity activity, ArrayList<UserInfo> dataSet) {
        this.activity = activity;
        this.localDataSet = dataSet;
    }

    @Override
    public int getItemViewType(int position){
        return position;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        CardView cardView = (CardView) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_user_list, viewGroup, false);
        final ViewHolder viewHolder = new ViewHolder(cardView);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
            }
        });

        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder,int position) {
        CardView cardView = viewHolder.cardView;
        ImageView photo = cardView.findViewById(R.id.photoimageView);
        TextView nameTextView = cardView.findViewById(R.id.nameTextView);
        TextView addressTextView = cardView.findViewById(R.id.addressTextView);

        UserInfo userInfo = localDataSet.get(position);
        if(localDataSet.get(position).getPhotoUrl() != null){
            Glide.with(activity)
                    .load(localDataSet.get(position).getPhotoUrl())
                    .centerCrop()
                    .override(500)
                    .into(photo);
        }
        nameTextView.setText(userInfo.getName());
        addressTextView.setText(userInfo.getAddress());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

}
