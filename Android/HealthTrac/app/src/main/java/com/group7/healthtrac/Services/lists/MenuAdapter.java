package com.group7.healthtrac.services.lists;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.group7.healthtrac.CreateGoalActivity;
import com.group7.healthtrac.CreateGroupActivity;
import com.group7.healthtrac.EnterActivityActivity;
import com.group7.healthtrac.FeedActivity;
import com.group7.healthtrac.InvitesAndChallengesActivity;
import com.group7.healthtrac.R;
import com.group7.healthtrac.ViewActiveChallenges;
import com.group7.healthtrac.ViewProfileActivity;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    private static final int TYPE_HEADER = 0;

    private static final int TYPE_ITEM = 1;

    private String[] mNavTitles;
    private int[] mIconIds;
    private String name;
    private Drawable profilePicture;
    private String email;
    private String source;
    private Activity activity;
    private DrawerLayout drawer;

    public MenuAdapter(String[] titles, int[] iconIds, String name, String email, Drawable profilePicture, String source, Activity activity, DrawerLayout drawer){
        mNavTitles = titles;
        mIconIds = iconIds;
        this.name = name;
        this.email = email;
        this.profilePicture = profilePicture;
        this.source = source;
        this.activity = activity;
        this.drawer = drawer;
    }

    @Override
    public MenuAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false);

            return new ViewHolder(v, viewType, activity, source, drawer);

        } else if (viewType == TYPE_HEADER) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.header, parent, false);

            return new ViewHolder(v,viewType, activity, source, drawer);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(MenuAdapter.ViewHolder holder, int position) {
        if(holder.holderId == 1) {
            holder.textView.setText(mNavTitles[position - 1]);
            holder.imageView.setImageResource(mIconIds[position - 1]);
        }
        else{
            holder.profile.setImageDrawable(profilePicture);
            holder.Name.setText(name);
            holder.email.setText(email);
        }
    }

    @Override
    public int getItemCount() {
        return mNavTitles.length + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;
        }

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        int holderId;
        TextView textView;
        ImageView imageView;
        ImageView profile;
        TextView Name;
        TextView email;

        public ViewHolder(View itemView, int viewType, final Activity activity, final String source, final DrawerLayout drawerLayout) {
            super(itemView);
            itemView.setClickable(true);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = null;

                    switch (getPosition()) {
                        case 1:
                            if (!source.equals("feeds")) {
                                intent = new Intent(activity, FeedActivity.class);
                            }
                            break;
                        case 2:
                            if (!source.equals("viewOwnProfile")) {
                                intent = new Intent(activity, ViewProfileActivity.class);
                                intent.putExtra("isCurrentUser", true);
                            }
                            break;
                        case 3:
                            if (!source.equals("activities")) {
                                intent = new Intent(activity, EnterActivityActivity.class);
                            }
                            break;
                        case 4:
                            if (!source.equals("createGoal")) {
                                intent = new Intent(activity, CreateGoalActivity.class);
                            }
                            break;
                        case 5:
                            if (!source.equals("createGroup")) {
                                intent = new Intent(activity, CreateGroupActivity.class);
                            }
                            break;
                        case 6:
                            if (!source.equals("activeChallenges")) {
                                intent = new Intent(activity, ViewActiveChallenges.class);
                            }
                            break;
                        case 7:
                            if (!source.equals("invites")) {
                                intent = new Intent(activity, InvitesAndChallengesActivity.class);
                            }
                            break;
                    }

                    drawerLayout.closeDrawers();
                    if (intent != null) {
                        activity.startActivity(intent);
                    }
                }
            });

            if(viewType == TYPE_ITEM) {
                textView = (TextView) itemView.findViewById(R.id.row_text);
                imageView = (ImageView) itemView.findViewById(R.id.row_icon);
                holderId = 1;
            }
            else{
                Name = (TextView) itemView.findViewById(R.id.name);
                email = (TextView) itemView.findViewById(R.id.email);
                profile = (ImageView) itemView.findViewById(R.id.profile_image);
                holderId = 0;
            }
        }
    }

}
