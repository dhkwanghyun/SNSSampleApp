package com.example.snssampleapp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.snssampleapp.PostInfo;
import com.example.snssampleapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private ArrayList<PostInfo> localDataSet;
    private Activity activity;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;

        public ViewHolder(Activity activity,CardView view,PostInfo postInfo) {
            super(view);
            // Define click listener for the ViewHolder's View

            cardView = view;
            LinearLayout contentsLayout = cardView.findViewById(R.id.contentsLayout);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ArrayList<String> contentsList = postInfo.getContents();

            if(contentsLayout.getChildCount() == 0){
                for(int i=0; i< contentsList.size(); i++){
                    String contents = contentsList.get(i);
                    if(Patterns.WEB_URL.matcher(contents).matches()){
                        ImageView imageView = new ImageView(activity);
                        imageView.setLayoutParams(layoutParams);
                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        contentsLayout.addView(imageView);
                        Glide.with(activity)
                                .load(contents)
                                .centerCrop()
                                .override(1000)
                                .thumbnail(0.1f)
                                .into(imageView);
                    }else{
                        TextView textView = new TextView(activity);
                        textView.setLayoutParams(layoutParams);
                        textView.setText(contents);
                        contentsLayout.addView(textView);
                    }
                }
            }
        }

        public CardView getTextView() {
            return cardView;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView.
     */
    public MainAdapter(Activity activity, ArrayList<PostInfo> dataSet) {
        this.activity = activity;
        localDataSet = dataSet;
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
                .inflate(R.layout.item_post, viewGroup, false);

        final ViewHolder viewHolder = new ViewHolder(activity,cardView,localDataSet.get(viewType));
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder,int position) {
        CardView cardView = viewHolder.cardView;
        TextView titleTextView = cardView.findViewById(R.id.titleTextView);
        titleTextView.setText(localDataSet.get(position).getTitle());

        TextView creatAtTextView = cardView.findViewById(R.id.createAtTextView);
        creatAtTextView.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(localDataSet.get(position).getCreatedAt()));

        LinearLayout contentsLayout = cardView.findViewById(R.id.contentsLayout);
        ArrayList<String> contentsList = localDataSet.get(position).getContents();

        for(int i=0;i < contentsLayout.getChildCount();i++){
            contentsLayout.getChildAt(i);
            if(contentsLayout.getChildAt(i) instanceof ImageView){

            }else{
                contentsLayout.getChildAt(i)

            }
        }



        if(contentsLayout.getChildCount() == 0){
            for(int i=0; i< contentsList.size(); i++){
                String contents = contentsList.get(i);
                if(Patterns.WEB_URL.matcher(contents).matches()){
                    ImageView imageView = new ImageView(activity);
                    imageView.setLayoutParams(layoutParams);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    contentsLayout.addView(imageView);
                    Glide.with(activity)
                            .load(contents)
                            .centerCrop()
                            .override(1000)
                            .thumbnail(0.1f)
                            .into(imageView);
                }else{
                    TextView textView = new TextView(activity);
                    textView.setLayoutParams(layoutParams);
                    textView.setText(contents);
                    contentsLayout.addView(textView);
                }
            }
        }



    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
