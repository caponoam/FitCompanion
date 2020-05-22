package com.noamwolf.android.fitcompanion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.gson.Gson;
import com.noamwolf.android.fitcompanion.model.Activity;

/**
 * Activity to view a single Fit session.
 */
public class FitActivityDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fit_detail);

        Intent i = getIntent();
        String jsonFitActivity = i.getStringExtra("fitActivityJson");

        Gson gson = new Gson();
        dataBind(gson.fromJson(jsonFitActivity, Activity.class));

    }

    private void dataBind(Activity activity) {
        // return Objects.toStringHelper(this)
        //			.add("Date", getActivityTimeStamp())
        //			.add("id", this.getId())
        //			.add("Name", name)
        //			.add("Type", getTypeById(this.getActivityType()))
        //			.add("Description", this.getDescription())
        //			.add("Duration", getFormattedDuration(this.getDurationMillis()))
        //			.toString();

        TextView txtNameTitle = findViewById(R.id.txtNameTitle);
        TextView txtDate = findViewById(R.id.txtDate);
        TextView txtDescription = findViewById(R.id.txtDescription);

        txtNameTitle.setText(activity.getName());
        txtDate.setText(activity.getActivityTimeStamp());
        txtDescription.setText(activity.getDescription());

    }
}
