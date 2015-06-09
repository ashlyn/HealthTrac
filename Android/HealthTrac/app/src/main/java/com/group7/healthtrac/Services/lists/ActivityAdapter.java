package com.group7.healthtrac.services.lists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.group7.healthtrac.R;
import com.group7.healthtrac.models.Activity;
import com.group7.healthtrac.services.utilities.Utility;

import java.util.List;

public class ActivityAdapter extends ArrayAdapter<Activity> {

    private List<Activity> mActivities;
    private Context mContext;

    public ActivityAdapter(Context context, List<Activity> activities) {
        super(context, R.layout.activity_view, activities);

        mContext = context;
        mActivities = activities;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;

        // Ensure that the view is not null
        if(itemView == null) {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.activity_view, parent, false);
        }

        // Find the user to use
        final Activity currentActivity = mActivities.get(position);

        // set the activity's info
        TextView activityInfo = (TextView) itemView.findViewById(R.id.activity_item_info);
        String info = "";

        switch (currentActivity.getType()) {
            case 0:
                info = "Ran ";
                break;
            case 1:
                info = "Biked ";
                break;
            case 2:
                info = "Jogged ";
                break;
            case 3:
                info = "Walked ";
                break;
            case 4:
                info = "Activity: " + currentActivity.getName() + " Distance: ";
                break;
        }

        info += (Math.round(currentActivity.getDistance() * 1000) / 1000.0) + " miles";
        activityInfo.setText(info);

        // set the activity's date
        TextView activityDate = (TextView) itemView.findViewById(R.id.activity_item_date);
        activityDate.setText(Utility.displayDateAndTime(currentActivity.getStartTime()));

        return itemView;
    }
}
