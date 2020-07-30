# HorizontalRuler
水平滑动的尺子

支持设置最大值和最小值，灵活多变

![image](https://raw.githubusercontent.com/QQabby/HorizontalRuler/master/app/src/main/screenshot/ruler.gif)
## 使用：

    <com.study.horizontalruler.RuleScrollView
        android:id="@+id/rule_scroll_view"
        android:layout_width="fill_parent"
        android:layout_height="wrap_content" />
       
```kotlin
        ruleView = (RuleScrollView) findViewById(R.id.rule_scroll_view);
        ruleView.setRange(-45, 45);
        ruleView.setUnit(15);
        ruleView.setProcess(10);
        ruleView.setOnChangedListener(new RuleScrollView.OnChangedListener() {
            @Override
            public void onStartTouch() {
                Log.e(TAG, "onStartTouch");
            }

            @Override
            public void onStopTouch() {
                Log.e(TAG, "onStopTouch");
            }

            @Override
            public void onSlide(boolean isFromUser, int number) {
                Log.e(TAG, "onSlide: " + number);
                tv.setText("全部：" + number);
                if (isFromUser) {
                    tv_user.setText("用户触发： " + number);
                }
            }
        });
```

## API：

```kotlin
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
     * 设置默认刻度值,滚动到相应的位置
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

```

