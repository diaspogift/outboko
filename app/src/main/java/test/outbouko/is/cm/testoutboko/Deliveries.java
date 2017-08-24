package test.outbouko.is.cm.testoutboko;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
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

import test.outbouko.is.cm.testoutboko.adapter.ViewPagerAdapter;
import test.outbouko.is.cm.testoutboko.fragment.DechargeFragment;
import test.outbouko.is.cm.testoutboko.fragment.DeliveriesFragment;
import test.outbouko.is.cm.testoutboko.fragment.LogoutFragment;
import test.outbouko.is.cm.testoutboko.fragment.TransfereFragment;
import test.outbouko.is.cm.testoutboko.model.Delivery;
import test.outbouko.is.cm.testoutboko.util.Util;


public class Deliveries extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public static AppCompatActivity thisActivity;

    private DeliveriesFragment deliveriesFragment = new DeliveriesFragment();
    private DechargeFragment dechargeFragment = new DechargeFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliveries);
        thisActivity = this;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.home);
        tabLayout.getTabAt(1).setIcon(R.drawable.decharge);
        tabLayout.getTabAt(2).setIcon(R.drawable.transfere);
        tabLayout.getTabAt(3).setIcon(R.drawable.logout);
        tabLayout.setSelectedTabIndicatorHeight(10);
    }

    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(deliveriesFragment, "Paquets");
        adapter.addFragment(dechargeFragment, "Decharge");
        adapter.addFragment(new TransfereFragment(), "Transfert");
        adapter.addFragment(new LogoutFragment(), "Logout");
        viewPager.setAdapter(adapter);
    }


    @Override
    public void onBackPressed()
    {


        new AlertDialog.Builder(Deliveries.this)
                .setTitle("Attention Confirmation")
                .setMessage(getString(R.string.logout_message))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //Toast.makeText(Deliveries.this, "Yaay", Toast.LENGTH_SHORT).show();

                        dialog.dismiss();

                        FinishPickupDelivery finishPickupDelivery = new FinishPickupDelivery(Deliveries.thisActivity);
                        SharedPreferences prefs = getSharedPreferences(Util.PreferenceTokenName, MODE_PRIVATE);
                        String email = prefs.getString(Util.EMAIL, null);

                        String password = prefs.getString(Util.PASSWORD, null);

                        finishPickupDelivery.execute(
                                Util.BASE_URL + "auth/finishPickup",
                                "email=" + email + "&password=" + password);
                    }})
                .setNegativeButton(android.R.string.no, null).show();

        //super.onBackPressed();  // optional depending on your needs
    }



    public class FinishPickupDelivery extends AsyncTask<String,Integer, String> {
        private ProgressDialog dialog;
        private int returnedCode;
        private Context context;
        public FinishPickupDelivery(Context context) {
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
                    returnedCode = urlConnection.getResponseCode();

                    if(returnedCode == 200){
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
                                    return "Failed";
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
            Log.e("Result finish Pickup" , result + " | Errorcode: " + returnedCode);

            //TextView tv = (TextView) findViewById(R.id.resultat2);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            String returnedMessage = "Déconnecté avec succès";
            String titre = "Attention";
            if (result.equals("Failed")){
                returnedMessage = "Echec d'enregistrement:";
                if (returnedCode == 401) returnedMessage += "Non autorisé";
                if (returnedCode == 400) returnedMessage += "Mauvaise";
                if (returnedCode == 404) returnedMessage += "Ressource non trouvée";

            }

            try {
                JSONObject  jsonObject = new JSONObject(result);
                boolean error = jsonObject.getBoolean("error");
                String results = jsonObject.getString("results");
                int status_code = jsonObject.getInt("status_code");
                if (!error && results.equals("OK") && status_code == 200){
                    SharedPreferences.Editor editor = getSharedPreferences(Util.PreferenceTokenName,
                            MODE_PRIVATE).edit();
                    //editor.putString(Util.EMAIL, email);
                    editor.remove(Util.PASSWORD);
                    editor.commit();
                    //returnedMessage += jsonObject.getString("results");
                    finish();
                }else {
                    AlertDialog alertDialog = new AlertDialog.Builder(getApplicationContext()).create();
                    alertDialog.setTitle(titre);
                    alertDialog.setMessage("Une erreur s'est produite");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            } catch (JSONException e) {
                AlertDialog alertDialog = new AlertDialog.Builder(getApplicationContext()).create();
                alertDialog.setTitle(titre);
                alertDialog.setMessage("Une erreur s'est produite");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
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

    public void checkOrOnCheckAll(View view){
        CheckBox checkBox = (CheckBox) view;
        for (Delivery d : dechargeFragment.getAll_deliveries()){
            d.setChecked(checkBox.isChecked());
        }
        dechargeFragment.getAll_adapter().notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode , int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == Util.REQUEST_CODE_RECEPTION && resultCode == Activity.RESULT_OK){
            dechargeFragment.getDeliveriesByUser();
        }
    }


}
