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

import com.group7.healthtrac.ViewGroupActivity;
import com.group7.healthtrac.models.Group;
import com.group7.healthtrac.R;
import com.group7.healthtrac.models.User;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GroupAdapter extends ArrayAdapter<Group> {

    private List<Group> mGroups;
    private Context mContext;
    private Activity mParentActivity;
    private User mCurrentUser;

    public GroupAdapter(Context context, List<Group> groups, Activity activity, int groupListId, User currentUser) {
        super(context, R.layout.item_view, groups);

        mContext = context;
        mGroups = groups;
        mParentActivity = activity;
        mCurrentUser = currentUser;

        setClickCallback(groupListId);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;

        // Ensure that the view is not null
        if(itemView == null) {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.item_view, parent, false);
        }

        // Find the group to use
        Group currentGroup = mGroups.get(position);

        // Fill view
        ImageView imageView = (ImageView) itemView.findViewById(R.id.item_image);
        imageView.setVisibility(View.VISIBLE);
        Picasso.with(getContext())
                .load(currentGroup.getImageUrl())
                .error(R.drawable.group)
                .noFade()
                .into(imageView);

        // set the group Name
        TextView groupName = (TextView) itemView.findViewById(R.id.item_primary_information);
        if (currentGroup.getGroupName().length() > 30) {
            groupName.setText(currentGroup.getGroupName().substring(0, 30) + "...");
        } else {
            groupName.setText(currentGroup.getGroupName());
        }

        // set the group desc
        TextView groupDesc = (TextView) itemView.findViewById(R.id.item_secondary_information);
        groupDesc.setText(currentGroup.getDescription());

        return itemView;
    }

    /**
     * Changes the functionality of the list item's onClick to redirect a user to a page
     * displaying the group's information
     * @param id The id of the list view whose items' onClick property should be changed
     */
    public void setClickCallback(int id) {
        ListView listView = (ListView) mParentActivity.findViewById(id);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Group clickedGroup = mGroups.get(position);
                Intent intent = new Intent(mContext, ViewGroupActivity.class);
                intent.putExtra("groupId", clickedGroup.getId());
                intent.putExtra("userStatus", mGroups.get(position).getStatusInGroup(mCurrentUser.getId()));
                mParentActivity.startActivity(intent);
            }
        });
    }
}
