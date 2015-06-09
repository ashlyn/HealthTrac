package com.group7.healthtrac.services.lists;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.group7.healthtrac.R;

public class ImageAdapter extends BaseAdapter {

    private final int MAX_DIMENSION;
    private int[] mImageIds = new int[] {R.drawable.group_image_1, R.drawable.group_image_2, R.drawable.group_image_3,
            R.drawable.group_image_4, R.drawable.group_image_5, R.drawable.group_image_6,
            R.drawable.group_image_7, R.drawable.group_image_8, R.drawable.group_image_9};
    private Context mContext;

    public ImageAdapter(Context context) {
        mContext = context;
        MAX_DIMENSION = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, mContext.getResources().getDisplayMetrics());
    }

    @Override
    public int getCount() {
        return mImageIds.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ImageView imageView;
        if (convertView == null) {

            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(MAX_DIMENSION, MAX_DIMENSION));
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setAdjustViewBounds(true);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mImageIds[position]);
        return imageView;
    }
}
