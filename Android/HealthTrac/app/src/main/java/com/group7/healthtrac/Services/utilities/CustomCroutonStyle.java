package com.group7.healthtrac.services.utilities;

import android.view.ViewGroup;

import com.group7.healthtrac.R;

import de.keyboardsurfer.android.widget.crouton.Style;

public class CustomCroutonStyle {

    public static final Style ALERT;
    public static final Style CONFIRM;

    public static final int ALERT_AMBER = R.color.alert_amber;
    public static final int CONFIRM_GREEN = R.color.confirm_green;


    static {
        ALERT   = new Style.Builder()
                .setBackgroundColor(ALERT_AMBER)
                .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .build();
        CONFIRM = new Style.Builder()
                .setBackgroundColor(CONFIRM_GREEN)
                .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .build();
    }
}
