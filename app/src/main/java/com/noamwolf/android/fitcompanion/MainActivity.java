package com.noamwolf.android.fitcompanion;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.noamwolf.android.fitcompanion.model.Session;
import com.noamwolf.android.fitcompanion.stats.ActivityStatsHelper;
import com.noamwolf.android.fitcompanion.stats.MonthlyStats;
import com.noamwolf.android.fitcompanion.view.GoalProgressView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.apache.commons.lang3.time.DurationFormatUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {
    private AccountManager am;
    private Activity context;
    private String authToken;
    private FitDataFetcherTask fitDataFetcherTask;
    private int currentMonth;
    private int currentYear;

    private String clientID = "218512693039-0ojgfmutrlmrm8grft0rm9gjamnvvi1s.apps.googleusercontent.com";
    private static final String START_TIME_VALUE = "2020-01-01T00:00:00.000Z";
    private static final String END_DATE_VALUE = "2020-12-31T23:59:59.999Z";

    private int countGoal;
    private int rollsGoal;
    private int hoursGoal;

    static final Map<Integer, String> MONTH_NAME_MAPPER = ImmutableMap.<Integer, String>builder()
            .put(1, "January")
            .put(2, "February")
            .put(3, "March")
            .put(4, "April")
            .put(5, "May")
            .put(6, "June")
            .put(7, "July")
            .put(8, "August")
            .put(9, "September")
            .put(10, "October")
            .put(11, "November")
            .put(12, "December")
            .build();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;
        am = AccountManager.get(context);

        // Initial load - TODO(nwolf): Go to cache/storage first.
        try {
            connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Refreshing", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                try {
                    connect();
                } catch (Exception e) {
                    Log.e("error", e.getMessage());
                }
            }
        });

        ImageButton btnPrevious = findViewById(R.id.btnPrevious);
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO(check edge cases).
                int tmpMonth = currentMonth - 1;
                int newYear = currentYear;
                int newMonth;
                if (tmpMonth <= 0) {
                    newYear--;
                    newMonth = 12;
                } else {
                    newMonth = tmpMonth;
                }

                fetchDataForDate(newYear,newMonth);
            }
        });

        ImageButton btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO(check edge cases).
                int tmpMonth = currentMonth + 1;
                int newYear = currentYear;
                int newMonth;
                if (tmpMonth > 12) {
                    newYear++;
                    newMonth = 1;
                } else {
                    newMonth = tmpMonth;
                }
                fetchDataForDate(newYear, newMonth);
            }
        });

        // Get preferences.
        getPrefrences();

        //        final CalendarView calendarView = findViewById(R.id.calendarView);
//        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
//            @Override
//            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
//                fetchDataForDate(year, month);
//            }
//        });
//
//        Button btnCalendar = findViewById(R.id.btnToggleCalendar);
//        btnCalendar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (calendarView.getVisibility() == View.VISIBLE) {
//                    calendarView.setVisibility(View.INVISIBLE);
//                } else {
//                    calendarView.setVisibility(View.VISIBLE);
//                }
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPrefrences();
    }

    private void getPrefrences() {
        // Get Goals
        SharedPreferences sharedPreferences =
                getSharedPreferences(getString(R.string.goals_key), MODE_PRIVATE);

        // Yearly goals
        countGoal = sharedPreferences.getInt(FitCompanionSettings.COUNT_GOAL_KEY, 150);
        rollsGoal = sharedPreferences.getInt(FitCompanionSettings.ROLLS_GOAL_KEY, 1000);
        hoursGoal = sharedPreferences.getInt(FitCompanionSettings.HOURS_GOAL_KEY, 150);

        // Divide by 12
        countGoal = countGoal / 12;
        rollsGoal = rollsGoal / 12;
        hoursGoal = hoursGoal / 12;
    }

    private void connect() throws Exception {
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
                    int month = date.get(Calendar.MONTH) + 1;
                    int year = date.get(Calendar.YEAR);
                    fetchDataForDate(year, month);
                }
            }
        });
    }

    private void fetchDataForDate(int year, int month) {
        currentMonth = month;
        currentYear = year;

        // TODO: do something better.
        Calendar date = Calendar.getInstance();
        date.set(year, month+1, 1);
        int lastDay = date.getActualMaximum(Calendar.DAY_OF_MONTH);

        TextView txtTitle = findViewById(R.id.txtMonthOfValue);
        txtTitle.setText(MONTH_NAME_MAPPER.get(month) + " " + year);
        String startTimeValue = "" + year + "-" + month + "-01T00:00:00.000Z" ; //2020-01-01
        String endTimeValue = "" + year + "-" + month + "-" + lastDay + "T23:59:59.999Z";

        fitDataFetcherTask = new FitDataFetcherTask(startTimeValue, endTimeValue);
        AsyncTask<String, Void, String> asyncTask = fitDataFetcherTask.execute(authToken);
        try {
            setMonthlyStats(asyncTask.get());
        } catch (ExecutionException e) {
            Log.println(Log.ERROR, "DataFetcher failed", e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            Log.println(Log.ERROR, "DataFetcher failed", e.getMessage());
            e.printStackTrace();
        }
    }

    private void setMonthlyStats(String results) {
        GoalProgressView sessionsProgressView = findViewById(R.id.session_count_progress_view);
        GoalProgressView durationProgressView = findViewById(R.id.duration_progress_view);
        GoalProgressView rollsProgressView = findViewById(R.id.rolls_progress_view);

//        Log.println(Log.INFO, "monthly stats", results);
        ActivityStatsHelper statsHelper = new ActivityStatsHelper();
        if (results != null) {

            Gson gson = new GsonBuilder().create();
            Session session = gson.fromJson(results, Session.class);

            // Filter out "Martial Arts" for now.
            Iterable<com.noamwolf.android.fitcompanion.model.Activity> activities =
                    Iterables.<com.noamwolf.android.fitcompanion.model.Activity>filter(session.getSession(),
                            com.noamwolf.android.fitcompanion.model.Activity.BJJ_PREDICATE);

            MonthlyStats stats =
                    statsHelper.calculateMonthlyStats(44, currentMonth, activities);

            sessionsProgressView.dataBind(stats.getTotalCount(), countGoal, "sessions");
            durationProgressView.dataBind(DurationFormatUtils.formatDuration(
                    stats.getDuration(), "H.mm", false),
                    ""+ hoursGoal, "hours");
            rollsProgressView.dataBind(stats.getRolls(), rollsGoal, "rolls");

            // Medium
            TextView txtCountGi = findViewById(R.id.txtCountGi);
            TextView txtCountNoGi = findViewById(R.id.txtCountNoGi);
            TextView txtCountOpenMat = findViewById(R.id.txtCountOpenMat);

            txtCountGi.setText("Gi: " + stats.getCountGi());
            txtCountNoGi.setText("No-gi: " + stats.getCountNoGi());
            txtCountOpenMat.setText("Open mat: " + stats.getCountOpenMat());

            // List of activities
            RecyclerView recyclerViewDetails = findViewById(R.id.recyclerViewDetails);
            recyclerViewDetails.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerViewDetails.setLayoutManager(layoutManager);

            RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder> adapter =
                    new ActivityAdapter(activities, context);
            recyclerViewDetails.setAdapter(adapter);
        } else {
            Log.println(Log.ERROR,"error", "uh oh results was empty bro.");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(context, FitCompanionSettings.class);

            context.startActivity(intent);
            return true;
        }
        if (id == R.id.action_yearly) {
            Intent intent = new Intent(context, YearlyStats.class);

            context.startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder> {

    private final List<com.noamwolf.android.fitcompanion.model.Activity> activities;
    private final Activity context;
    public static String dateFormat = "h:mm a - MMM d -";
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);

    public static class ActivityViewHolder extends RecyclerView.ViewHolder {

        public TextView txtDate, txtName, txtDuration, txtPosition, txtRolls;
        public ActivityViewHolder(@NonNull final View itemView, final Activity context,
                                  final List<com.noamwolf.android.fitcompanion.model.Activity> activities) {
            super(itemView);
            this.txtDate = itemView.findViewById(R.id.txtDate);
            this.txtName = itemView.findViewById(R.id.txtName);
            this.txtDuration = itemView.findViewById(R.id.txtDuration);
            this.txtPosition = itemView.findViewById(R.id.txtPosition);
            this.txtRolls = itemView.findViewById(R.id.txtRolls);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView clickedPosition = v.findViewById(R.id.txtPosition);
                    int position = Integer.parseInt(clickedPosition.getText().toString());
                    Intent intent = new Intent(context, FitActivityDetail.class);

                    intent.putExtra("fitActivityJson", activities.get(position).asJson());
                    context.startActivity(intent);
                }
            });
        }

    }

    public ActivityAdapter(Iterable<com.noamwolf.android.fitcompanion.model.Activity> activities, Activity context) {
        this.activities = Lists.newArrayList(activities);
        this.context = context;
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fit_item_view, parent, false);

        return new ActivityViewHolder(v, context, activities);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        com.noamwolf.android.fitcompanion.model.Activity item = activities.get(position);
        holder.txtPosition.setText(position + "");
        holder.txtDate.setText(simpleDateFormat.format(item.getStartTimeMillis()));
        holder.txtName.setText(item.getName());
        holder.txtDuration.setText(DurationFormatUtils.formatDuration(
                item.getDurationMillis(), "H hr mm 'min -'", false));
        int rolls = ActivityStatsHelper.getRollsFromDescription(item.getDescription());
        holder.txtRolls.setText(rolls + " rolls");
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }
}

