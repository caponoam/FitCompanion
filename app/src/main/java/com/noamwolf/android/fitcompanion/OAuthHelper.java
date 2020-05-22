package com.noamwolf.android.fitcompanion;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class OAuthHelper {

    // Scope
    private static final String AUTH_TOKEN_TYPE = "oauth2:https://www.googleapis.com/auth/fitness.activity.read";

    // Domain?
    private static final String ACCOUNT_TYPE = "com.google";

    public void connect(AccountManager am,
                        Activity context,
                        AccountManagerCallback<Bundle> callback) {
        Bundle options = new Bundle();

        am.getAuthTokenByFeatures(
                ACCOUNT_TYPE,
                AUTH_TOKEN_TYPE,
                null,
                context,
                null,
                null,
                callback,
                null);
    }
}
