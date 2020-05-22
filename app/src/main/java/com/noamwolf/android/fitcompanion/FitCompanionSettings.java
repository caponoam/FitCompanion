package com.noamwolf.android.fitcompanion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class FitCompanionSettings extends AppCompatActivity {

    public static String COUNT_GOAL_KEY = "count_goal_key";
    public static String ROLLS_GOAL_KEY = "rolls_goal_key";
    public static String HOURS_GOAL_KEY = "hours_goal_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fit_companion_settings);

        final SharedPreferences sharedPreferences =
                getSharedPreferences(getString(R.string.goals_key), MODE_PRIVATE);

        if (sharedPreferences == null) {
            // log error?
        }

        final EditText txtCountGoal = findViewById(R.id.editTxtCountYearlyGoal);
        final EditText txtRollsGoal = findViewById(R.id.editTxtRollsYearlyGoal);
        final EditText txtHoursGoal = findViewById(R.id.editTxtHoursYearlyGoal);

        int countGoal = sharedPreferences.getInt(COUNT_GOAL_KEY, 150);
        int rollsGoal = sharedPreferences.getInt(ROLLS_GOAL_KEY, 1000);
        int hoursGoal = sharedPreferences.getInt(HOURS_GOAL_KEY, 150);

        // DataBind
        txtCountGoal.setText("" + countGoal);
        txtRollsGoal.setText("" + rollsGoal);
        txtHoursGoal.setText("" + hoursGoal);

        Button btnSaveGoals = findViewById(R.id.btnSaveGoals);
        btnSaveGoals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Collect
                String countGoal = txtCountGoal.getText().toString();
                String rollsGoal = txtRollsGoal.getText().toString();
                String hoursGoal = txtHoursGoal.getText().toString();

                // Validate


                // Save
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(COUNT_GOAL_KEY, Integer.parseInt(countGoal));
                editor.putInt(ROLLS_GOAL_KEY, Integer.parseInt(rollsGoal));
                editor.putInt(HOURS_GOAL_KEY, Integer.parseInt(hoursGoal));
                editor.commit();

                // Toast
                Snackbar.make(v, "Preferences successfully saved!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}
