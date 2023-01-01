package com.gbversion.tool.statussaver.speedtest.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

/**
 * The type Custom text view.
 */
public class CustomTextView extends TextView {
    /**
     * Instantiates a new Custom text view.
     *
     * @param context  the context
     * @param attrs    the attrs
     * @param defStyle the def style
     */
    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    /**
     * Instantiates a new Custom text view.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    /**
     * Instantiates a new Custom text view.
     *
     * @param context the context
     */
    public CustomTextView(Context context) {
        super(context);
        init(null);
    }

    private void init(AttributeSet attrs) {
        Log.e("TAG", "attrs" + attrs);
    }
}
