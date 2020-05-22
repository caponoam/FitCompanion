package com.noamwolf.android.fitcompanion.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.noamwolf.android.fitcompanion.R;

public class GoalProgressView extends RelativeLayout {

//    private final ImageView iconView;
    private TextView progressView, goalView, metricView;

    public GoalProgressView(Context context) {
        super(context);
        init(context);
    }

    public GoalProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GoalProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View v = LayoutInflater.from(context).inflate(R.layout.goal_progress_view, this);

        progressView = v.findViewById(R.id.progress_text);
        goalView = v.findViewById(R.id.goal_text);
        metricView = v.findViewById(R.id.metric_text);
    }

    public void dataBind(String progress, String goal, String metric) {
        progressView.setText(progress);
        goalView.setText("/" + goal);
        metricView.setText(metric);
    }

    public void dataBind(int progress, int goal, String metric) {
        if (progress >= goal) {
            // set style to green
            progressView.setTextAppearance(R.style.AppTheme_GoalMet);
        } else {
            // set style to red
            progressView.setTextAppearance(R.style.AppTheme_GoalUnMet);
        }

        dataBind(progress + "", goal + "", metric);
    }
}
