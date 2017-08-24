package test.outbouko.is.cm.testoutboko;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import test.outbouko.is.cm.testoutboko.util.Util;

public class MainActivity extends AppCompatActivity {

    public static AppCompatActivity thisActivity;

    private EditText edit_email, edit_password;
    private String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        thisActivity = this;

        Util.WHO_IS_SHOING = getClass().getSimpleName();

        SharedPreferences prefs_deliveries = getSharedPreferences(Util.DELIVERIES, MODE_PRIVATE);

        edit_email = (EditText) findViewById(R.id.edit_email);

        SharedPreferences prefs = getSharedPreferences(Util.PreferenceTokenName, MODE_PRIVATE);
        String email = prefs.getString(Util.EMAIL, null);
        if (email != null)
            edit_email.setText(email);

        edit_password = (EditText)findViewById(R.id.edit_password);
    }

    public void authentifieMoi(View view){
        if (isInternetOn()){
            if (edit_email.getText().toString().equals("") || edit_password.getText().toString().equals("")){
                Toast.makeText(getApplicationContext(), "Données incomplètes", Toast.LENGTH_LONG).show();
            }else {
                email = edit_email.getText().toString();
                password = edit_password.getText().toString();
                SignIn signIn = new SignIn(MainActivity.thisActivity);
                String url  = Util.BASE_URL +  "auth/login";

                SharedPreferences prefs = getSharedPreferences(Util.PreferenceTokenName, MODE_PRIVATE);
                String regId = prefs.getString(Util.TOKEN, null);
                if (regId == null) {
                    regId="";
                }
                WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                WifiInfo wInfo = wifiManager.getConnectionInfo();
                String macAddress = wInfo.getMacAddress();
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

                String queryString ="email="+email+"&password="+password+"&registration_id="+
                        regId+"&mac_address="+macAddress + "&provider=Android&imei=" + "xxxxxxxxxx" /*telephonyManager.getDeviceId()*/;
                Log.e("queryString", queryString);
                signIn.execute(url, queryString);
            }
        }else {
            Toast.makeText(getApplicationContext(), getString(R.string.no_internet), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // String s = "";
        TextView networkstate_mainActivity = (TextView) findViewById(R.id.networkstate_mainActivity);
        if (isInternetOn()){
            networkstate_mainActivity.setBackgroundColor(Color.rgb(0,200, 0));
            networkstate_mainActivity.setTextColor(Color.WHITE);
            networkstate_mainActivity.setText(MainActivity.thisActivity.getString(R.string.yes_internet));
            //s = MainActivity.thisActivity.getString(R.string.yes_internet);
        }else {
            networkstate_mainActivity.setBackgroundColor(Color.rgb(200, 0 ,0));
            networkstate_mainActivity.setTextColor(Color.WHITE);
            networkstate_mainActivity.setText(MainActivity.thisActivity.getString(R.string.no_internet));
            //s = MainActivity.thisActivity.getString(R.string.no_internet);
        }
        // Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    private class SignIn extends AsyncTask<String,Integer, String> {
        private ProgressDialog dialog;
        private Context context;
        private int responseCode;

        public SignIn(Context context) {
            this.context = context;
            dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog.setMessage("Veuillez patienter");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String resultat = "";
            String urlString = strings[0];
            String queryString = strings[1];
            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection;
                try {
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    OutputStream os = urlConnection.getOutputStream();
                    OutputStreamWriter out = new OutputStreamWriter(os);

                    out.write(queryString);
                    out.close();
                    responseCode = urlConnection.getResponseCode();
                    if(responseCode == 200){
                        InputStream in = urlConnection.getInputStream();
                        BufferedReader br = null;
                        StringBuilder sb = new StringBuilder();
                        String line;
                        try {
                            br = new BufferedReader(new InputStreamReader(in));
                            while ((line = br.readLine()) != null) {
                                sb.append(line);
                            }

                        } catch (IOException e) {
                            return e.getMessage();
                        } finally {
                            if (br != null) {
                                try {
                                    br.close();
                                } catch (IOException e) {
                                    return "Failed 1";
                                }
                            }
                        }
                        in.close();
                        //os.close();
                        resultat = sb.toString();
                    }else {
                        return "Failed";
                    }
                } catch (IOException e) {
                    return "Failed";
                }
            } catch (MalformedURLException e) {
                return "Failed" ;
            }

            return resultat;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute( result);

            Log.e("result" , result);

            //TextView tv = (TextView) findViewById(R.id.resultat2);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if (responseCode == 200){

                try {
                    JSONObject  jsonObject = new JSONObject(result);
                    boolean error = jsonObject.getBoolean("error");
                    String results = jsonObject.getString("results");
                    int status_code = jsonObject.getInt("status_code");
                    if (!error && results.equals("OK") && status_code == 200){
                        Intent intention = new Intent(getApplicationContext(), Deliveries.class);
                        intention.putExtra(Util.EMAIL, email);
                        intention.putExtra(Util.PASSWORD, password);

                        SharedPreferences.Editor editor = getSharedPreferences(Util.PreferenceTokenName, MODE_PRIVATE).edit();
                        editor.putString(Util.EMAIL, email);
                        editor.putString(Util.PASSWORD, password);
                        editor.commit();
                        edit_password.setText("");
                        startActivity(intention);
                        finish();
                    }else {
                        Toast.makeText(getApplicationContext(), "Erreur: email et ou mot de passe incorrect" ,
                                Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }else{
                Toast.makeText(getApplicationContext(), "responseCode: " + responseCode , Toast.LENGTH_LONG).show();
            }
            //Toast.makeText(getApplicationContext(), "Result: " + result , Toast.LENGTH_LONG).show();
            //Toast.makeText(getApplicationContext(), "password: " + password, Toast.LENGTH_LONG).show();
        }
    }

    public boolean isInternetOn()
    {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public static class NetworkListener extends BroadcastReceiver {
        //private static final String TAG = "NetworkConnectivityListener";
        private NetworkInfo.State mState;
        private NetworkInfo mNetworkInfo;
        private NetworkInfo mOtherNetworkInfo;
        private String mReason;
        private boolean mIsFailover;
        private static final boolean DBG = true;
        @Override
        public void onReceive(Context context, final Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)){
                boolean noConnectivity =
                        intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

                if (noConnectivity) {
                    mState = NetworkInfo.State.DISCONNECTED;
                } else {
                    mState = NetworkInfo.State.CONNECTED;
                }

                mNetworkInfo = (NetworkInfo)
                        intent.getParcelableExtra(ConnectivityManager.EXTRA_EXTRA_INFO);
                mOtherNetworkInfo = (NetworkInfo)
                        intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

                mReason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
                mIsFailover =
                        intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);
                //context.getApplicationContext().getCon

                //RelativeLayout activity_main = (RelativeLayout)Logger.thisActivity.findViewById(R.id.activity_main);
                //context.getResources().getLayout(R.layout.activity_main).get;
                if (Util.WHO_IS_SHOING.equals("MainActivity")){
                    TextView networkstate_mainActivity = (TextView) MainActivity.thisActivity.findViewById(R.id.networkstate_mainActivity);

                    // Button button = (Button) activity_main.findViewById(R.id.imageButton);

                    if (mState.toString().equals("CONNECTED")){
                        networkstate_mainActivity.setBackgroundColor(Color.rgb(0,200, 0));
                        networkstate_mainActivity.setTextColor(Color.WHITE);
                        networkstate_mainActivity.setText(MainActivity.thisActivity.getString(R.string.yes_internet));

                    }else{
                        networkstate_mainActivity.setBackgroundColor(Color.rgb(200, 0 ,0));
                        networkstate_mainActivity.setTextColor(Color.WHITE);
                        networkstate_mainActivity.setText(MainActivity.thisActivity.getString(R.string.no_internet));
                    }

                    //View view = context.getResources().getLayout(R.layout.);
                    if (DBG) {
                        Log.e("ListenConnection", "Logger: onReceive(): mNetworkInfo=" + mNetworkInfo +  " mOtherNetworkInfo = "
                                + (mOtherNetworkInfo == null ? "[none]" : mOtherNetworkInfo +
                                " noConn=" + noConnectivity) + " mState=" + mState.toString());
                    }
                }


                //if (context.getClass() instanceof  )
            }

        }
    }

    public void getPicture(View view){
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(intent, Util.REQUEST_CODE_PHOTO);
    }

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Util.REQUEST_CODE_PHOTO && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ImageView mImageView = (ImageView) findViewById(R.id.picture);
            mImageView.setImageBitmap(imageBitmap);
        }
    }*/

}
