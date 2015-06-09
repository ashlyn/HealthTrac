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
import com.squareup.picasso.Picasso;

import java.util.List;

public class FriendAdapter extends ArrayAdapter<User> {

    private final static String TAG = "FriendAdapter";
    private List<User> mFriends;
    private Context mContext;

    public FriendAdapter(Context context, List<User> friends) {
        super(context, R.layout.item_view, friends);

        mFriends = friends;
        mContext = context;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;

        // Ensure that the view is not null
        if(itemView == null) {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.item_view, parent, false);
        }

        // Find the friend to use
        User currentFriend = mFriends.get(position);

        // Fill view
        ImageView imageView = (ImageView) itemView.findViewById(R.id.item_image_circle);
        imageView.setVisibility(View.VISIBLE);
        Picasso.with(getContext())
                .load(currentFriend.getImageUrl())
                .error(R.drawable.account_box)
                .noFade()
                .into(imageView);

        // set the user name
        TextView friendName = (TextView) itemView.findViewById(R.id.item_primary_information);
        friendName.setText(currentFriend.getFullName());

        // hide the secondary info of the friend
        TextView temp = (TextView) itemView.findViewById(R.id.item_secondary_information);
        temp.setVisibility(View.GONE);

        return itemView;
    }
}
