package com.group7.healthtrac.services.lists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.group7.healthtrac.R;
import com.group7.healthtrac.models.User;
import com.group7.healthtrac.services.utilities.Tuple;
import com.squareup.picasso.Picasso;

import java.util.List;

public class LeaderBoardAdapter extends ArrayAdapter<Tuple> {

    private List<Tuple> mLeaders;
    private Context mContext;
    private String mCategory;

    public LeaderBoardAdapter(Context context, List<Tuple> leaders, String category) {
        super(context, R.layout.item_view, leaders);

        mLeaders = leaders;
        mContext = context;
        mCategory = category;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;

        // Ensure that the view is not null
        if(itemView == null) {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.item_view, parent, false);
        }

        // Find the user to use
        final User currentLeader = mLeaders.get(position).getUser();

        // Fill view
        ImageView imageView = (ImageView) itemView.findViewById(R.id.item_image_circle);
        imageView.setVisibility(View.VISIBLE);
        Picasso.with(getContext())
                .load(currentLeader.getImageUrl())
                .error(R.drawable.account_box)
                .noFade()
                .into(imageView);

        // set the user name
        TextView groupName = (TextView) itemView.findViewById(R.id.item_primary_information);
        groupName.setText(currentLeader.getFullName());

        String label = "";

        switch (mCategory) {
            case "steps":
                label = Double.toString(mLeaders.get(position).getValue()) + " steps";
                break;
            case "distance":
                label = Double.toString((double) (Math.round(mLeaders.get(position).getValue() * 100)) / 100) + " miles";
                break;
            case "duration":
                label = Double.toString(Math.round(mLeaders.get(position).getValue() / 6) / 10) + " minutes";
                break;
        }

        // set the user location
        TextView groupDesc = (TextView) itemView.findViewById(R.id.item_secondary_information);
        //todo store the value of the user, probably in a second list or in a tuple?
        groupDesc.setText(label);

        return itemView;
    }
}
