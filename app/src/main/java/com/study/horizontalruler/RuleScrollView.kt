package com.study.horizontalruler

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.study.horizontalruler.RuleView.OnChangedListener

/**
 * 滑动尺子布局
 */
internal class RuleScrollView : RelativeLayout {

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs, 0) {
        init()
    }

    private lateinit var ruleView: RuleView

    private fun init() {
        val view = View.inflate(context, R.layout.layout_rule, null)
        addView(view)
        initView(view)
    }

    private fun initView(view: View) {
        ruleView = view.findViewById(R.id.rule_view) as RuleView
        val horizontalScrollView =
            findViewById(R.id.rule_scrollview) as RuleHorizontalScrollView
        // 去掉超出滑动后出现的阴影效果
        horizontalScrollView.overScrollMode = View.OVER_SCROLL_NEVER
        // 设置水平滑动
        ruleView.setHorizontalScrollView(horizontalScrollView)
        // 当滑动尺子的时候
        horizontalScrollView.setOnScrollListener(object :
            RuleHorizontalScrollView.OnScrollListener {
            override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
                ruleView.setScrollerChanged(l, t, oldl, oldt)
            }
        })
        ruleView.post { ruleView.setProcess(0) }
    }

    /**
     * 设置监听
     *
     * @param listener 监听
     */
    fun setOnChangedListener(listener: OnChangedListener?) {
        ruleView.setOnChangedListener(listener)
    }

    /**
     * 设置刻度最小值
     */
    fun setMinValue(minValue: Int) {
        ruleView.setMinValue(minValue)
    }

    /**
     * 设置刻度最大值
     */
    fun setMaxValue(maxValue: Int) {
        ruleView.maxValue = maxValue
    }

    /**
     * 设置默认刻度尺的刻度值,不会滚动到相应的位置
     *
     * @param process 默认值
     */
    fun setProcess(process: Int) {
        ruleView.post {
            ruleView.setProcess(process)
        }
    }

}