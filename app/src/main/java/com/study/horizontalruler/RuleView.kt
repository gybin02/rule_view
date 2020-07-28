package com.study.horizontalruler

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import java.text.DecimalFormat

class RuleView : View {
    var paint: Paint? = null
    /**
     * 间隔总数
     */
    private var maxGap = 320

    /**
     * 起点x的坐标
     */
    private var startX = 0f
    private var startY = 0f

    /**
     * 刻度线的长度
     */
    private var yLenght = 0f

    /**
     * 刻度的间隙
     */
    private var gap = 8f
    private var metrics: DisplayMetrics? = null

    /**
     * 文本的字体大小
     */
    private var mFontSize = 0f
    private var mScrollHandler: Handler? = null
    private var horizontalScrollView: RuleHorizontalScrollView? = null
    private var mCurrentX = -999999999

    /**
     * 刻度进制
     */
    private val unit = 10f
    /**
     * 获取刻度最大值
     */
    /**
     * 设置刻度最大值
     */
    var maxValue = 20
    private var minValue = -20

    constructor(context: Context) : super(context) {
        init()
    }

    fun setHorizontalScrollView(
        horizontalScrollView: RuleHorizontalScrollView?
    ) {
        this.horizontalScrollView = horizontalScrollView
        this.horizontalScrollView!!.setOnTouchListener { v, event ->
            val action = event.action
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                }
                MotionEvent.ACTION_MOVE -> mScrollHandler!!.removeCallbacks(mScrollRunnable)
                MotionEvent.ACTION_UP -> mScrollHandler!!.post(mScrollRunnable)
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

    fun init() {
        metrics = DisplayMetrics()
        val wmg = context
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wmg.defaultDisplay.getMetrics(metrics)
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint!!.style = Paint.Style.FILL
        paint!!.strokeWidth = resources.getDimension(R.dimen.text_h2)
        paint!!.color = Color.parseColor("#999999")
        mFontSize = Util.dip2px(context, 12f).toFloat()
        startY = Util.dip2px(context, 20f).toFloat()
        yLenght = Util.dip2px(context, 10f).toFloat()
        gap = Util.dip2px(context, 8f).toFloat()
        startX =
            Util.getScreenWidth(context) / 2.0f - Util.dip2px(
                context,
                12f
            )
        mScrollHandler = Handler(context.mainLooper)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        maxGap = maxValue - minValue
        val width =
            maxGap * gap + Util.getScreenWidth(context) - Util.dip2px(
                context,
                12f
            ) * 2.0f
        setMeasuredDimension(width.toInt(), heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 画刻度线
        val colorSmall = "#4DFFFFFF"
        val colorLarge = "#B3FFFFFF"
        val colorText = "#BDBEC5"
        //长竖线的高度
        val largeHeight = 12f
        //短竖线的高度
        val smallHeight = 8f
        for (i in 0..maxGap) {
            val newX = i * gap + startX
            if ((i + minValue) % 10 == 0) {
                paint!!.color = Color.parseColor(colorLarge)
                yLenght =
                    Util.dip2px(context, largeHeight).toFloat()

                // 画刻度文字
                paint!!.textSize = mFontSize
                val text = (minValue + i).toString() + ""
                // 获取文本的宽度
                val width = Util.px2dip(
                    context,
                    calculateTextWidth(text)
                ) / 2f
                val y =
                    (startY + Util.dip2px(context, largeHeight)
                            + Util.dip2px(context, 28f))
                val x = newX - width
                canvas.drawText(text, x, y, paint)
            } else {
                paint!!.color = Color.parseColor(colorSmall)
                yLenght =
                    Util.dip2px(context, smallHeight).toFloat()
            }
            canvas.drawLine(newX, startY, newX, yLenght + startY, paint)
        }
        paint!!.color = Color.parseColor(colorText)
        var i = 0
        while (i <= maxGap / unit) {
            i++
        }
    }

    /**
     * 获取TextView中文本的宽度
     */
    private fun calculateTextWidth(text: String): Float {
        val textPaint = TextPaint()
        textPaint.textSize = mFontSize * metrics!!.scaledDensity
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
        val number = scrollWidth / gap
        val result = number.toInt() + minValue
        if (listener != null) {
            listener!!.onSlide(result)
        }
    }

    private var listener: OnChangedListener? = null

    interface OnChangedListener {
        /**
         * 滑动数值
         *
         * @param process 进度
         */
        fun onSlide(process: Int)
    }

    fun setOnChangedListener(listener: OnChangedListener?) {
        this.listener = listener
    }

    /**
     * 滚动监听线程
     */
    private val mScrollRunnable: Runnable = object : Runnable {
        override fun run() {
            // 滚动停止了
            if (mCurrentX == horizontalScrollView!!.scrollX) {
                try {
                    val x = horizontalScrollView!!.scrollX.toFloat()
                    // 当前的值
                    val value = x / (gap * unit)
                    val s = df.format(value.toDouble())

                    // 滑动到11.0 ok
                    val scrollX = (s.toDouble() * gap * unit).toInt()
                    horizontalScrollView!!.smoothScrollTo(scrollX, 0)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                mScrollHandler!!.removeCallbacks(this)
            } else {
                mCurrentX = horizontalScrollView!!.scrollX
                mScrollHandler!!.postDelayed(this, 50)
            }
        }
    }

    /**
     * 设置刻度最小值
     */
    fun setMinValue(minValue: Int) {
        this.minValue = minValue
    }

    /**
     * 设置默认刻度尺的刻度值,不会滚动到相应的位置
     *
     * @param process 默认值
     */
    fun setProcess(process: Int) {
        val scrollX = ((process - minValue) * gap).toInt()
        Handler().postDelayed({ horizontalScrollView!!.smoothScrollTo(scrollX, 0) }, 100)
    }

    /**
     * 设置当前刻度尺的刻度值,并滚动到相应的位置
     *
     * @param process 数值
     */
    fun setScaleScroll(process: Float) {
        val scrollX = ((process - minValue) * gap).toInt()
        horizontalScrollView!!.smoothScrollTo(scrollX, 0)
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