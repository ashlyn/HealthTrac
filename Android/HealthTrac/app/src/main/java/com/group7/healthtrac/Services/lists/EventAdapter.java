package com.group7.healthtrac.services.lists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.group7.healthtrac.R;
import com.group7.healthtrac.models.FeedEvent;
import com.group7.healthtrac.services.utilities.Utility;
import com.squareup.picasso.Picasso;

import java.util.List;

public class EventAdapter extends ArrayAdapter<FeedEvent> {

    private final static String TAG = "EventAdapter";
    private List<FeedEvent> mFeedEvents;
    private Context mContext;

    public EventAdapter(Context context, List<FeedEvent> feedEvents) {
        super(context, R.layout.feed_item, feedEvents);

        mFeedEvents = feedEvents;
        mContext = context;
        // todo mMoodImages = moodImages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        //boolean isMoodItem = false;

        // Ensure that the view is not null
        if(itemView == null) {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.feed_item, parent, false);
        }

        // Find the event to use
        FeedEvent currentFeedEvent = mFeedEvents.get(position);
        /* todo add later
        if (currentFeedEvent.getType() == 7) {
            isMoodItem = true;
        }*/

        // Fill view
        ImageView imageView = (ImageView) itemView.findViewById(R.id.feed_item_image);
        Picasso.with(getContext())
                .load(currentFeedEvent.getUser().getImageUrl())
                .error(R.drawable.account_box)
                .noFade()
                .into(imageView);

        // set the user name
        TextView userName = (TextView) itemView.findViewById(R.id.feed_item_user_name);
        userName.setText(currentFeedEvent.getUser().getFullName());

        // set the event type
        TextView eventType = (TextView) itemView.findViewById(R.id.feed_item_primary_information);
        eventType.setText(Utility.displayDate(currentFeedEvent.getDate()));

        TextView eventTime = (TextView) itemView.findViewById(R.id.feed_item_time);
        eventTime.setText(Utility.displayTime(currentFeedEvent.getDate()));

        // set the event desc
        TextView eventDesc = (TextView) itemView.findViewById(R.id.feed_item_secondary_information);
        String description = currentFeedEvent.getDescription();
        //if (description.length() > 27) {
           // description = description.substring(0, 26) + "\r\n" + description.substring(26);
        //}
        eventDesc.setText(description);

        /* todo potentially add an image to the feed event if it is a mood update
        if (isMoodItem) {
            ImageView moodImage = (ImageView) itemView.findViewById(R.id.feed_item_mood_image);
            Bitmap bitmap = BitmapFactory.decodeByteArray(, 0, );
            moodImage.setImageBitmap(bitmap);
        }*/

        return itemView;
    }
}
