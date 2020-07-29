package com.study.horizontalruler;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";


    private TextView tv;

    private RuleHorizontalScrollView horizontalScrollView;

    private RuleScrollView ruleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.tv);
        ruleView = (RuleScrollView) findViewById(R.id.rule_scroll_view);
        ruleView.setRange(-45,45);
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
            public void onSlide(int number) {
//                Log.e(TAG, "onSlide: " + number);
                tv.setText(number + "");
            }
        });

    }

    public void clickBtnA(View view) {
        ruleView.setRange(-30, 30);
        ruleView.setProcess(0);
    }

    public void clickBtnB(View view) {
        ruleView.setRange(-55, 55);
        ruleView.setProcess(30);
    }

    public void clickBtnC(View view) {
        ruleView.setRange(-10, 80);
        ruleView.setProcess(40);
    }
}
