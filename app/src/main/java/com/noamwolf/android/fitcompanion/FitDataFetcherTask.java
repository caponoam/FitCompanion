package com.noamwolf.android.fitcompanion;

import android.os.AsyncTask;
import android.security.keystore.UserNotAuthenticatedException;
import android.util.Log;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class FitDataFetcherTask extends AsyncTask<String, Void, String> {

    private static final String GET_SESSIONS_URL =
            "https://www.googleapis.com/fitness/v1/users/me/sessions";

    private static final String START_TIME_KEY = "startTime";
    private static final String END_TIME_KEY = "endTime";

    private static final String BEARER = "Bearer";

    private static final String CONTENT_TYPE = "application/json";

    private final String startTimeValue;
    private final String endTimeValue;

    FitDataFetcherTask(String startTimeValue, String endTimeValue) {
        this.startTimeValue = startTimeValue;
        this.endTimeValue = endTimeValue;
    }

    @Override
    protected String doInBackground(String... tokens) {
        Log.println(Log.INFO,"fetch", "fetching data with token " + tokens[0]);
        String json = null;

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Content-Type", CONTENT_TYPE);
        headers.set("Content-Type", CONTENT_TYPE);

//            -H "Content-Type: application/json" \
//            -H "Authorization: Bearer $ACCESS_TOKEN"

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = GET_SESSIONS_URL + "?" +
                    START_TIME_KEY + "=" + startTimeValue + "&" +
                    END_TIME_KEY + "=" + endTimeValue +
                    "&access_token={token}" +
                    "&access_token_type=bearer";

        Log.println(Log.INFO,"url", url);
        ResponseEntity<String> v = null;

        // TODO(nwolf): Handler 401 with a reauth
        boolean retry = false;
        do try {
            if (retry) {
                Log.println(Log.INFO, "retry", "Retrying get for " + url);
            }
            v = restTemplate.exchange(url, HttpMethod.GET, entity, String.class, tokens[0]);
            retry = true;
        } catch (HttpClientErrorException e) {
            // TODO(nwolf): If 401 - reauthenticate
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                retry = false;
            }
            Log.println(Log.ERROR, "HttpException for GET", e.getMessage());
        } while (!retry);

        return v.getBody();
    }

   // @Override
//    protected void onPostExecute(String results) {
//        if (results != null) {
//
//            Gson gson = new GsonBuilder().create();
//            Session session = gson.fromJson(results, Session.class);
//
//            int count = 0;
//            long duration = 0l;
//            for (com.noamwolf.android.fitcompanion.model.Activity activity : session.getSession()) {
//
//                if (activity.getActivityType() == 44) {
//                    count++;
//                    duration += activity.getDurationMillis();
//                }
//            }
//            Log.println(Log.INFO, "count", ""+ count);
//            Log.println(Log.INFO, "duration", ""+ com.noamwolf.android.fitcompanion.model.Activity.getFormattedDuration(duration));
//        } else {
//            Log.println(Log.ERROR,"error", "uh oh results was empty bro.");
//        }
//    }
}
