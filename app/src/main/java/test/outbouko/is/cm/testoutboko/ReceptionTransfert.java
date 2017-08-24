package test.outbouko.is.cm.testoutboko;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
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
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import test.outbouko.is.cm.testoutboko.model.Agence;
import test.outbouko.is.cm.testoutboko.util.Util;

public class ReceptionTransfert extends AppCompatActivity {

    private String trackingCodes, email, remetteur_signature, recepteur_signature;
    public static AppCompatActivity thisActivity;
   // private Spinner spinner_tr;
    private List<Agence> agences;
    private Agence selectedAgence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reception_transfert);
        thisActivity = this;
        Bundle b = getIntent().getExtras();
        trackingCodes = b.getString(Util.DELIVERIES);
        email = b.getString(Util.EMAIL);

        if (isInternetOn()){
            //// if (Util.agences != null){
            ////    agences = Util.agences;
            ////    ArrayList<String> strings = new ArrayList<String>();

            ////    for (Agence agence:agences){
            ////          strings.add(agence.getName());
            ////    }

               //// spinner_tr = (Spinner) findViewById(R.id.agences_tr);

                //// ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ReceptionTransfert.thisActivity,
                ////         android.R.layout.simple_spinner_item, strings);
                ////  dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                ////  spinner_tr.setAdapter(dataAdapter);

                // Create an ArrayAdapter using the string array and a default spinner layout
                //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                // R.array.type_de_document, android.R.layout.simple_spinner_item);
                           /* MySpinnerAdapter mySpinnerAdapter = new MySpinnerAdapter(
                                    ReceptionDecharge.thisActivity, agences);

                            spinner.setAdapter(mySpinnerAdapter);*/


                ////  Log.e("spinner data: " , agences.toString());
                //// selectedAgence = agences.get(0);
                //// spinner_tr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                ////   @Override
                ////   public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ////       selectedAgence = agences.get(position);
                ////    }
                ////    @Override
                ////     public void onNothingSelected(AdapterView<?> parent) {
                ////         selectedAgence = null;
                ////    }
                ////  });
                //// }else{
                ////  GetAgences getAgences = new GetAgences(ReceptionTransfert.thisActivity);
                ////  getAgences.execute(Util.BASE_URL + "delivery/agences", "");
                //// }

        }else {
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_LONG).show();
        }

    }


    public void getPartiSignature(View view){
        int id = view.getId();
        int requestCode = 0;

        if (id == R.id.btn_signature_remetteur_tr){
            requestCode = Util.REQUEST_CODE_REMETTEUR_SIGNATURE;
        }else if (id == R.id.btn_signature_recepteur_tr){
            requestCode = Util.REQUEST_CODE_RECEPTEUR_SIGNATURE;
        }

        Intent intent = new Intent(this, Signature.class);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Util.REQUEST_CODE_REMETTEUR_SIGNATURE && resultCode == Activity.RESULT_OK){
            Bundle extras = data.getExtras();
            String signature = (String) extras.get(Util.SIGNATURE);
            remetteur_signature = signature;
            //Toast.makeText(getApplicationContext(),signature, Toast.LENGTH_LONG).show();
            byte[] b  = Base64.decode(signature, Base64.DEFAULT);
            ImageView client_signature = (ImageView) findViewById(R.id.signature_remetteur_tr);
            Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);
            client_signature.setImageBitmap(bmp);
            client_signature.getLayoutParams().height = 200;
            client_signature.getLayoutParams().width = 200;
        }

        if (requestCode == Util.REQUEST_CODE_RECEPTEUR_SIGNATURE && resultCode == Activity.RESULT_OK){
            Bundle extras = data.getExtras();
            String signature = (String) extras.get(Util.SIGNATURE);
            recepteur_signature = signature;
            //Toast.makeText(getApplicationContext(),signature, Toast.LENGTH_LONG).show();
            byte[] b  = Base64.decode(signature, Base64.DEFAULT);
            ImageView agent_signature = (ImageView) findViewById(R.id.signature_recepteur_tr);
            Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);
            agent_signature.setImageBitmap(bmp);
            agent_signature.getLayoutParams().height = 200;
            agent_signature.getLayoutParams().width = 200;
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

    public void receiptDeliveries_(View view){
        String url = Util.BASE_URL + "delivery/dechargeTransport";
        String query = "email=" + email +
                "&trackingcodes="+trackingCodes+
               // "&agence_id=" + selectedAgence.getId()+
                "&transport_signature_agent=" + remetteur_signature+
                "&transport_signature_chauffeur="+recepteur_signature;

        //Toast.makeText(ReceptionDecharge.thisActivity, query, Toast.LENGTH_LONG).show();
        Log.e("query receipt", query);
        ReceiptDelivery receiptDelivery = new ReceiptDelivery(ReceptionTransfert.thisActivity);
        receiptDelivery.execute(url, query);
    }

    private class ReceiptDelivery extends AsyncTask<String,Integer, String> {
        private ProgressDialog dialog;
        private int returnedCode;
        private Context context;
        public ReceiptDelivery(Context context) {
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

            Log.e("ResultTransportation" , result);

            String returnedMessage = "Enregistré avec succès";
            String titre = "Attention";
            if (result.equals("Failed")){
                returnedMessage = "Echec d'enregistrement:";
                if (returnedCode == 401) returnedMessage = "Non autorisé";
                if (returnedCode == 400) returnedMessage = "Mauvaise";
                if (returnedCode == 404) returnedMessage = "Ressource non trouvée";

            }

            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("result");

                //int status_code = jsonObject.getInt("status_code");
                //returnedMessage += "\nStatus_code: " + status_code;
                if(jsonArray.length() > 0){
                    returnedMessage +=
                            "\nLes paquets de code de suivi suivants ont été receptionnés avec succès.\n Il s'agit de:\n";
                    for (int i= 0; i<jsonArray.length(); i++){
                        JSONObject job = jsonArray.getJSONObject(i);
                        returnedMessage += (i+1) + "- " + job.getString("code") + "\n";
                    }
                }else {
                    returnedMessage += "Aucun paquet mis à jour";
                }
            } catch (JSONException e) {
                //e.printStackTrace();
                returnedMessage = "Erreur: " + e.getMessage();
            }

            AlertDialog alertDialog = new AlertDialog.Builder(ReceptionTransfert.this).create();
            alertDialog.setTitle(titre);
            alertDialog.setMessage(returnedMessage);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent returnIntent = new Intent();
                            //returnIntent.putExtra(Util.SIGNATURE,string_in_img);
                            setResult(Activity.RESULT_OK,returnIntent);
                            finish();
                        }
                    });
            alertDialog.show();
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }


    private class GetAgences extends AsyncTask<String,Integer, String> {
        private ProgressDialog dialog;
        private int returnedCode;
        private Context context;
        public GetAgences(Context context) {
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
            }  catch (IOException e) {
                e.printStackTrace();
            }

            return resultat;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute( result);

            Log.e("Result get agences" , result);

            //TextView tv = (TextView) findViewById(R.id.resultat2);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if (!result.equals("Failed")){
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    boolean error = jsonObject.getBoolean("error");
                    int status_code = jsonObject.getInt("status_code");
                    String results = jsonObject.getString("results");
                    if (!error && status_code == 200 && results.equals("OK")){
                        JSONArray jsonArray = jsonObject.getJSONArray("agences");
                        ArrayList<String> strings = new ArrayList<String>();
                        if (jsonArray != null && jsonArray.length() > 0){
                            agences = new ArrayList<Agence>();
                            for (int i = 0; i<jsonArray.length(); i++){
                                JSONObject job = jsonArray.getJSONObject(i);
                                agences.add(new Agence(
                                        job.getInt("id"),
                                        job.getString("name")
                                ));
                                strings.add(job.getString("name"));
                            }

                           /* Util.agences = agences;

                            spinner_tr = (Spinner) findViewById(R.id.agences_tr);
                            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ReceptionTransfert.thisActivity,
                                    android.R.layout.simple_spinner_item, strings);
                            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner_tr.setAdapter(dataAdapter);*/

                            // Create an ArrayAdapter using the string array and a default spinner layout
                            //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                            // R.array.type_de_document, android.R.layout.simple_spinner_item);
                           /* MySpinnerAdapter mySpinnerAdapter = new MySpinnerAdapter(
                                    ReceptionDecharge.thisActivity, agences);

                            spinner.setAdapter(mySpinnerAdapter);*/


                           /* Log.e("spinner data: " , agences.toString());
                            selectedAgence = agences.get(0);
                            spinner_tr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    selectedAgence = agences.get(position);
                                }
                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                    selectedAgence = null;
                                }
                            });*/

                        }else {
                            Toast.makeText(getApplicationContext(), "Failed to get agences", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(getApplicationContext(), "Failed to get agences", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Failed to get agences", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(getApplicationContext(), "Failed to get agences", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
