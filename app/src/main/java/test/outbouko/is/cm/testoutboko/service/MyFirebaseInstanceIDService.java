package test.outbouko.is.cm.testoutboko.service;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import test.outbouko.is.cm.testoutboko.util.Util;

/**
 * Created by Ange_Douki on 05/08/2016.
 */


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Util.token = refreshedToken;
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.

        //sendRegistrationToServer(refreshedToken);

        SharedPreferences.Editor editor = getSharedPreferences(Util.PreferenceTokenName, MODE_PRIVATE).edit();
        editor.putString(Util.TOKEN, refreshedToken);
        //editor.putString(Util.TOKEN_TIME);
       // editor.putInt("idName", 12);

        editor.commit();
    }
}
