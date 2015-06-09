package com.group7.healthtrac.services.lists;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.group7.healthtrac.R;
import com.group7.healthtrac.ViewFeedItemActivity;
import com.group7.healthtrac.models.Badge;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BadgeAdapter extends ArrayAdapter<Badge> {

    private List<Badge> mBadges;
    private Context mContext;

    public BadgeAdapter(Context context, List<Badge> badges, int badgeListId, Activity activity, String userId, View view, String userName) {
        super(context, R.layout.item_view, badges);

        mContext = context;
        mBadges = badges;
        setClickCallback(badgeListId, activity, userId, view, userName);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;

        // Ensure that the view is not null
        if(itemView == null) {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.item_view, parent, false);
        }

        // Find the group to use
        Badge currentBadge = mBadges.get(position);

        // Fill view
        ImageView imageView = (ImageView) itemView.findViewById(R.id.item_image);
        imageView.setVisibility(View.VISIBLE);
        Picasso.with(getContext())
                .load(currentBadge.getImageUrl())
                .error(R.drawable.checkmark_40px)
                .into(imageView);

        // set the group Name
        TextView badgeName = (TextView) itemView.findViewById(R.id.item_primary_information);
        badgeName.setText(currentBadge.getName());

        // set the group desc
        TextView badgeDesc = (TextView) itemView.findViewById(R.id.item_secondary_information);
        badgeDesc.setText(currentBadge.getDescription());

        return itemView;
    }

    /**
     * Changes the functionality of the list item's onClick to redirect a user to a page
     * displaying the badge information and a list of friends with that badge of the
     * user whose page the badge was clicked on
     * @param id The id of the list view whose items' onClick property should be changed
     * @param activity The activity that this list view is accessed from
     * @param userId The id of the user who's page the badge was displayed on
     */
    public void setClickCallback(int id, final Activity activity, final String userId, View view, final String userName) {
        ListView listView = (ListView) view.findViewById(id);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Badge badge = mBadges.get(position);
                String[] info = new String[4];
                info[3] = badge.getDescription();
                info[2] = badge.getName();
                info[1] = "";
                info[0] = userName;

                Intent intent = new Intent(activity, ViewFeedItemActivity.class);
                intent.putExtra("badge", badge);
                intent.putExtra("infoToDisplay", info);
                intent.putExtra("userId", userId);
                intent.putExtra("title", badge.getName());
                mContext.startActivity(intent);
            }
        });
    }
}
