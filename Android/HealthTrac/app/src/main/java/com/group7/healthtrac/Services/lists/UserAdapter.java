package com.group7.healthtrac.services.lists;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.group7.healthtrac.R;
import com.group7.healthtrac.ViewProfileActivity;
import com.group7.healthtrac.events.groupevents.BanMemberEvent;
import com.group7.healthtrac.models.Membership;
import com.group7.healthtrac.models.User;
import com.group7.healthtrac.services.api.IApiCaller;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserAdapter extends ArrayAdapter<User> {

    private List<User> mUsers;
    private Context mContext;
    private Activity mParentActivity;
    private User mUser;
    private int mUserStatus;
    private IApiCaller mApiCaller;
    public UserAdapter(Context context, List<User> users, Activity activity, int userListId, User currentUser, int userStatus, IApiCaller apiCaller) {
        super(context, R.layout.item_view, users);

        mUsers = users;
        mContext = context;
        mParentActivity = activity;
        mUser = currentUser;
        mUserStatus = userStatus;
        mApiCaller = apiCaller;

        if (userListId != -1) {
            setClickCallback(userListId);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;

        // Ensure that the view is not null
        if(itemView == null) {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.item_view, parent, false);
        }

        // Find the user to use
        final User currentUser = mUsers.get(position);

        //create button
        Button banButton = (Button) itemView.findViewById(R.id.ban_button);
        if (mUserStatus == Membership.ADMIN) {
            banButton.setVisibility(View.VISIBLE);
        }

        // Fill view
        ImageView imageView = (ImageView) itemView.findViewById(R.id.item_image_circle);
        imageView.setVisibility(View.VISIBLE);
        Picasso.with(getContext())
                .load(currentUser.getImageUrl())
                .error(R.drawable.account_box)
                .noFade()
                .into(imageView);

        // set the user name
        TextView groupName = (TextView) itemView.findViewById(R.id.item_primary_information);
        if (currentUser.getFullName().length() > 30) {
            groupName.setText(currentUser.getFullName().substring(0, 30) + "...");
        } else {
            groupName.setText(currentUser.getFullName());
        }

        // set the user location
        TextView groupDesc = (TextView) itemView.findViewById(R.id.item_secondary_information);
        groupDesc.setText(currentUser.getLocation());

        //create overriding onClick for ban buttons
        banButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                onBanClick(view);
            }
        });



        return itemView;
    }

    private void onBanClick(View view){
        View parentView = (View) view.getParent();
        ListView listView = (ListView) parentView.getParent().getParent();
        mApiCaller.requestData(new BanMemberEvent(listView.getPositionForView(parentView)));
    }

    /**
     * Changes the functionality of the list item's onClick to redirect a user to a page
     * displaying the user's information
     * @param id The id of the list view whose items' onClick property should be changed
     */
    public void setClickCallback(int id) {
        ListView listView = (ListView) mParentActivity.findViewById(id);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                User clickedUser = mUsers.get(position);
                Intent intent = new Intent(mParentActivity, ViewProfileActivity.class);

                // if the requested user is the user that is currently using the app, redirect them
                // to their own page

                if (clickedUser.getId().equals(mUser.getId())) {
                    intent.putExtra("isCurrentUser", true);
                } else {
                    intent.putExtra("userToShowId", clickedUser.getId());
                }
                mContext.startActivity(intent);
            }
        });
    }
}
