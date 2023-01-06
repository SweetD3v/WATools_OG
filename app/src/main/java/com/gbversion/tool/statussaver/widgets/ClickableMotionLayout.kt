package com.gbversion.tool.statussaver.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.children

class ClickableMotionLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : MotionLayout(context, attrs, defStyleAttr) {

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        // Take all child views that are clickable,
        // then see if any of those have just been clicked, and intercept the touch.
        // Otherwise, let the MotionLayout handle the touch event.
        if (children.filter { it.isClickable }.any {
                it.x < event.x && it.x + it.width > event.x &&
                        it.y < event.y && it.y + it.height > event.y
            }) {
            return false
        }
        return super.onInterceptTouchEvent(event)
    }
}