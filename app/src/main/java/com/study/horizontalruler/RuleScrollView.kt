package com.study.horizontalruler

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout

/**
 * 滑动尺子布局
 */
 class RuleScrollView : RelativeLayout {

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
     * 设置范围
     */
    fun setRange(minValue: Int,maxValue: Int){
        ruleView.minValue = (minValue)
        ruleView.maxValue = maxValue
        ruleView.requestLayout()
    }
    /**
     * 刻度进制,一个大区间代表的进度，默认10。
     */
    fun setUnit(unit:Int){
        ruleView.unit =unit
    }

    /**
     * 设置默认刻度值,并滚动到相应的位置
     *
     * @param process 默认值
     */
    fun setProcess(process: Int) {
        ruleView.post {
            ruleView.setProcess(process)
        }
    }

    interface OnChangedListener {
        /**
         * 滑动数值
         *@param isFromUser 是否来自用户
         * @param process 进度
         */
        fun onSlide(isFromUser:Boolean,process: Int)

        /**
         * 开始滑动
         */
        fun onStartTouch()
        /**
         * 停止滑动
         */
        fun onStopTouch()
    }


}