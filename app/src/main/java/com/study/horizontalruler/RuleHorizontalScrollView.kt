package com.study.horizontalruler

import android.content.Context
import android.util.AttributeSet
import android.widget.HorizontalScrollView

/**
 * 尺子滚动
 */
class RuleHorizontalScrollView @JvmOverloads constructor(
    context: Context?, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : HorizontalScrollView(context, attrs, defStyleAttr) {
    private var onScrollListener: OnScrollListener? =
        null

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        if (onScrollListener != null) {
            onScrollListener!!.onScrollChanged(l, t, oldl, oldt)
        }
    }

    interface OnScrollListener {
        fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int)
    }

    fun setOnScrollListener(onScrollListener: OnScrollListener?) {
        this.onScrollListener = onScrollListener
    }
}