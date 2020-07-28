package com.study.horizontalruler;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private TextView tv;

    private RuleHorizontalScrollView horizontalScrollView;

    private RuleView ruleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.tv);
        RuleScrollView ruleView = (RuleScrollView) findViewById(R.id.rule_scroll_view);
        ruleView.setMinValue(-45);
        ruleView.setMaxValue(45);
        ruleView.setProcess(10);
        ruleView.setOnChangedListener(new RuleView.OnChangedListener() {
            @Override
            public void onSlide(int number) {
                tv.setText(number + "");
            }
        });

    }
}
