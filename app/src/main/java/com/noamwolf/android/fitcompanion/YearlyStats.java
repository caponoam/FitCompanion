package com.noamwolf.android.fitcompanion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.noamwolf.android.fitcompanion.model.Session;
import com.noamwolf.android.fitcompanion.stats.ActivityStatsHelper;
import com.noamwolf.android.fitcompanion.stats.MonthlyStats;
import com.noamwolf.android.fitcompanion.view.GoalProgressView;

import org.apache.commons.lang3.time.DurationFormatUtils;

import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class YearlyStats extends AppCompatActivity {

    private YearlyStats context;
    private AccountManager am;
    private int currentYear;
    private String authToken = "failure";
    private int countGoal, rollsGoal, hoursGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yearly_stats);

        context = this;
        am = AccountManager.get(context);
        getPrefrences();

        // Initial load - TODO(nwolf): Go to cache/storage first.
        try {
            connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ImageButton btnPrevious = findViewById(R.id.btnPrevious);
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO(check edge cases).
                int newYear = currentYear - 1;
                currentYear = newYear;
                fetchDataForDate(currentYear, authToken);
            }
        });

        ImageButton btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO(check edge cases).
                int newYear = currentYear + 1;
                currentYear = newYear;
                fetchDataForDate(currentYear, authToken);
            }
        });
    }

    private void connect() {
        OAuthHelper helper = new OAuthHelper();
        helper.connect(am, context, new AccountManagerCallback<Bundle>() {
            @Override
            public void run(AccountManagerFuture<Bundle> future) {
                try {
                    Bundle bundle = future.getResult();
                    authToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                } catch (Exception e) {
                    System.out.println("getAuthTokenByFeatures() cancelled or failed:");
                    e.printStackTrace();
                    authToken = "failure";
                }

                if(!authToken.equals("failure")) {
                    Calendar date = Calendar.getInstance();
                    int year = date.get(Calendar.YEAR);
                    fetchDataForDate(year, authToken);
                }
            }
        });
    }

    private void fetchDataForDate(int year, String authToken) {
        if (authToken.equals("failure")) { connect(); return; }
        currentYear = year;

        TextView txtTitle = findViewById(R.id.txtYearTitle);
        txtTitle.setText(" " + year);

        String startTimeValue = "" + year + "-01-01T00:00:00.000Z" ; //2020-01-01
        String endTimeValue = "" + year + "-12-31T23:59:59.999Z";

        FitDataFetcherTask fitDataFetcherTask = new FitDataFetcherTask(startTimeValue, endTimeValue);
        AsyncTask<String, Void, String> asyncTask = fitDataFetcherTask.execute(authToken);
        try {
            dataBind(asyncTask.get());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void dataBind(String results) {
        GoalProgressView sessionsProgressView = findViewById(R.id.session_count_progress_view);
        GoalProgressView durationProgressView = findViewById(R.id.duration_progress_view);
        GoalProgressView rollsProgressView = findViewById(R.id.rolls_progress_view);

        ActivityStatsHelper statsHelper = new ActivityStatsHelper();
        if (results != null) {

            Gson gson = new GsonBuilder().create();
            Session session = gson.fromJson(results, Session.class);

            // Filter out "Martial Arts" for now.
            Iterable<com.noamwolf.android.fitcompanion.model.Activity> activities =
                    Iterables.<com.noamwolf.android.fitcompanion.model.Activity>filter(session.getSession(),
                            com.noamwolf.android.fitcompanion.model.Activity.BJJ_PREDICATE);

            MonthlyStats stats =
                    statsHelper.calculateYearlyStats(activities);

            sessionsProgressView.dataBind(stats.getTotalCount(), countGoal, "sessions");
            durationProgressView.dataBind(DurationFormatUtils.formatDuration(
                    stats.getDuration(), "H.mm", false),
                    "" + hoursGoal, "hours");
            rollsProgressView.dataBind(stats.getRolls(), rollsGoal, "rolls");

            TextView txtGi = findViewById(R.id.txtGi);
            TextView txtNoGi = findViewById(R.id.txtNoGi);
            TextView txtOpenMat = findViewById(R.id.txtOpenMat);
            TextView txtKidsParents = findViewById(R.id.txtKidParents);
            TextView txtCoach = findViewById(R.id.txtCountCoach);
            txtGi.setText("Gi: " + stats.getCountGi());
            txtNoGi.setText("No-gi: " + stats.getCountNoGi());
            txtOpenMat.setText("Open mat: " + stats.getCountOpenMat());
            txtKidsParents.setText("Kids & Parents: " + stats.getCountKidsParents());
            txtCoach.setText("Coach: " + stats.getCountCoach());


//            stats.getDayHistogram()

        } else {
            Log.println(Log.ERROR,"error", "uh oh results was empty bro.");
        }
    }

    private void getPrefrences() {
        // Get Goals
        SharedPreferences sharedPreferences =
                getSharedPreferences(getString(R.string.goals_key), MODE_PRIVATE);

        // Yearly goals
        countGoal = sharedPreferences.getInt(FitCompanionSettings.COUNT_GOAL_KEY, 150);
        rollsGoal = sharedPreferences.getInt(FitCompanionSettings.ROLLS_GOAL_KEY, 1000);
        hoursGoal = sharedPreferences.getInt(FitCompanionSettings.HOURS_GOAL_KEY, 150);
    }
}
