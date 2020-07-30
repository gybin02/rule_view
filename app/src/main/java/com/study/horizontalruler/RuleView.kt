package com.study.horizontalruler

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.text.TextPaint
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import java.text.DecimalFormat

internal class RuleView : View {
    private val tag = "RuleView"

    private lateinit var paint: Paint

    /**
     * 间隔总数
     */
    private var maxGap = 0f

    /**
     * 起点x的坐标
     */
    private var startX = 0f
    private var startY = 0f

    private lateinit var metrics: DisplayMetrics


    private lateinit var mScrollHandler: Handler
    private lateinit var horizontalScrollView: RuleHorizontalScrollView

    private var mCurrentX = -999999999

    /**
     * 刻度的间隙
     */
    private var gapWidth = 8f

    /**
     * 文本的字体大小
     */
    private var mFontSize = 0f

    /**
     * 刻度进制,一个大区间代表的进度，默认10。
     */
    var unit = 10

    // 画刻度线
    private val colorSmall = "#4DFFFFFF"
    private val colorLarge = "#B3FFFFFF"
    private val colorText = "#BDBEC5"

    //长竖线的高度
    private val largeHeight = 12f

    //短竖线的高度
    private val smallHeight = 8f

    /**
     * 设置刻度最大值
     */
    var maxValue = 20
    var minValue = -20

    /**
     * 是否来自用户触发
     */
    private var isFromUser: Boolean = false


    constructor(context: Context) : super(context) {
        init()
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setHorizontalScrollView(
        horizontalScrollView: RuleHorizontalScrollView
    ) {
        this.horizontalScrollView = horizontalScrollView
        this.horizontalScrollView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isFromUser = true
                    listener?.onStartTouch()
                }
                MotionEvent.ACTION_MOVE -> mScrollHandler.removeCallbacks(mScrollRunnable)
                MotionEvent.ACTION_UP -> {
                    mScrollHandler.post(mScrollRunnable)
                }
            }
            false
        }
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        init()
    }

    private fun init() {
        metrics = DisplayMetrics()
        val windowManager = context
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(metrics)
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.FILL
        paint.strokeWidth = Util.dip2px(context, 2f).toFloat()
        paint.color = Color.parseColor("#999999")
        mFontSize = Util.dip2px(context, 12f).toFloat()
        startY = Util.dip2px(context, 20f).toFloat()
        gapWidth = Util.dip2px(context, 8f).toFloat()
        startX = Util.getScreenWidth(context) / 2.0f
        mScrollHandler = Handler(context.mainLooper)
    }

    private fun getGapUnit(): Float {
        return unit / 10f
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.e(tag, "onMeasure: $maxValue: $minValue")
        maxGap = (maxValue - minValue) / getGapUnit()
        val width = maxGap * gapWidth + Util.getScreenWidth(context)
        setMeasuredDimension(width.toInt(), heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //刻度线的长度
        var yLength: Float
        for (i in 0..maxGap.toInt()) {
            val newX = i * gapWidth + startX
            val value = i * getGapUnit() + minValue
            if ((value) % unit == 0f) {
                paint.color = Color.parseColor(colorLarge)
                yLength = Util.dip2px(context, largeHeight).toFloat()

                // 画刻度文字
                paint.textSize = mFontSize
                val text = value.toInt().toString()
                // 获取文本的宽度
                val width = Util.px2dip(context, calculateTextWidth(text)) / 2f
                val y =
                    (startY + Util.dip2px(context, largeHeight)
                            + Util.dip2px(context, 28f))
                val x = newX - width
                canvas.drawText(text, x, y, paint)
            } else {
                paint.color = Color.parseColor(colorSmall)
                yLength =
                    Util.dip2px(context, smallHeight).toFloat()
            }
            canvas.drawLine(newX, startY, newX, yLength + startY, paint)
        }
    }

    /**
     * 获取TextView中文本的宽度
     */
    private fun calculateTextWidth(text: String): Float {
        val textPaint = TextPaint()
        textPaint.textSize = mFontSize * metrics.scaledDensity
        return textPaint.measureText(text)
    }

    var df = DecimalFormat("0.0")
    private var scrollWidth = 0

    /**
     * 当滑动尺子的时候
     */
    fun setScrollerChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        // 滑动的距离
        scrollWidth = l
        val number = scrollWidth / gapWidth
        val result = number * getGapUnit() + minValue
        listener?.onSlide(isFromUser, result.toInt())

    }

    private var listener: RuleScrollView.OnChangedListener? = null


    fun setOnChangedListener(listener: RuleScrollView.OnChangedListener?) {
        this.listener = listener
    }

    /**
     * 滚动监听线程
     */
    private val mScrollRunnable: Runnable = object : Runnable {
        override fun run() {
            // 滚动停止了
            if (mCurrentX == horizontalScrollView.scrollX) {
                mScrollHandler.removeCallbacks(this)
                isFromUser = false
                listener?.onStopTouch()
            } else {
                mCurrentX = horizontalScrollView.scrollX
                mScrollHandler.postDelayed(this, 50)
            }
        }
    }


    /**
     * 设置默认刻度尺的刻度值,不会滚动到相应的位置
     *
     * @param process 默认值
     */
    fun setProcess(process: Int) {
        val scrollX = ((process - minValue) * gapWidth).toInt()
        Handler().postDelayed({ horizontalScrollView.smoothScrollTo(scrollX, 0) }, 100)
    }

    /**
     * 设置当前刻度尺的刻度值,并滚动到相应的位置
     *
     * @param process 数值
     */
    fun setScaleScroll(process: Float) {
        val scrollX = ((process - minValue) * gapWidth).toInt()
        horizontalScrollView.smoothScrollTo(scrollX, 0)
    }

    internal object Util {
        fun dip2px(context: Context, dipValue: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (dipValue * scale + 0.5f).toInt()
        }

        fun px2dip(context: Context, pxValue: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (pxValue / scale + 0.5f).toInt()
        }

        fun getScreenWidth(context: Context): Int {
            val wm = context
                .getSystemService(Context.WINDOW_SERVICE) as WindowManager
            return wm.defaultDisplay.width
        }

        fun getScreenHeight(context: Context): Int {
            val wm = context
                .getSystemService(Context.WINDOW_SERVICE) as WindowManager
            return wm.defaultDisplay.height
        }
    }
}