package com.group7.healthtrac.services;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ProfileViewPager extends ViewPager {

    private boolean mSlidingRight;
    private boolean mSlidingLeft;
    private double mLastX;

    public ProfileViewPager(Context context) {
        super(context);
    }

    public ProfileViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /*
    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        return (v != this && v instanceof ViewPager) || super.canScroll(v, checkV, dx, x, y);
    }*/

    @Override
    public boolean onTouchEvent(final MotionEvent ev) {
        final int action = ev.getAction();
        if (action == MotionEvent.ACTION_MOVE) {
                /*
                 * if this is the first item, scrolling from left to
                 * right should navigate in the surrounding ViewPager
                 */
                if (getCurrentItem() == 0) {
                    // swiping from left to right (->)?
                    if (mLastX <= ev.getX() && !mSlidingRight) {
                        // make the parent touch interception active -> parent pager can swipe
                        getParent().requestDisallowInterceptTouchEvent(false);
                    } else {
                        /*
                         * if the first swipe was from right to left, don't listen to swipes
                         * from left to right. this fixes glitches where the user first swipes
                         * right, then left and the scrolling state gets reset
                         */
                        mSlidingRight = true;

                        // save the current x position
                        mLastX = ev.getX();
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                } else
                /*
                 * if this is the last item, scrolling from right to
                 * left should navigate in the surrounding ViewPager
                 */
                    if (getCurrentItem() == getAdapter().getCount() - 1) {
                        // swiping from right to left (<-)?
                        if (mLastX >= ev.getX() && !mSlidingLeft) {
                            // make the parent touch interception active -> parent pager can swipe
                            getParent().requestDisallowInterceptTouchEvent(false);
                        } else {
                        /*
                         * if the first swipe was from left to right, don't listen to swipes
                         * from right to left. this fixes glitches where the user first swipes
                         * left, then right and the scrolling state gets reset
                         */
                            mSlidingLeft = true;

                            // save the current x position
                            mLastX = ev.getX();
                            getParent().requestDisallowInterceptTouchEvent(true);
                        }
                    }
        }

        super.onTouchEvent(ev);
        return true;
    }
}
