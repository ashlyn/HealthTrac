package com.group7.healthtrac.services.lists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.group7.healthtrac.R;
import com.group7.healthtrac.models.Mood;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MoodAdapter extends ArrayAdapter<Mood> {

    private final static String TAG = "MoodAdapter";
    private Context mContext;
    private List<Mood> mMoods;

    public MoodAdapter(Context context, List<Mood> moods) {
        super(context, R.layout.mood_view, moods);
        mMoods = moods;
        mContext = context;
    }

    @Override
    public View getDropDownView(int position, View convertView,ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;

        // Ensure that the view is not null
        if(itemView == null) {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.mood_view, parent, false);
        }

        // Find the mood to use
        Mood currentMood = mMoods.get(position);

        // Fill view
        ImageView imageView = (ImageView) itemView.findViewById(R.id.mood_view_image);
        Picasso.with(getContext())
                .load(currentMood.getImageUrl())
                .error(R.drawable.checkmark_40px)
                .into(imageView);

        // set the mood Name
        TextView moodName = (TextView) itemView.findViewById(R.id.mood_view_text);
        moodName.setText(currentMood.getType());

        return itemView;
    }
}
