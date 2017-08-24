package test.outbouko.is.cm.testoutboko.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import test.outbouko.is.cm.testoutboko.Deliveries;
import test.outbouko.is.cm.testoutboko.R;
import test.outbouko.is.cm.testoutboko.util.Util;


/**
 * Created by Ange_Douki on 29/09/2016.
 */
public class LogoutFragment extends Fragment {

    public LogoutFragment() {}
    private  View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logout, container, false);
        rootView = view;
        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Button button = (Button) rootView.findViewById(R.id.validate_logout);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Attention Confirmation")
                        .setMessage(getString(R.string.logout_message))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //Toast.makeText(Deliveries.this, "Yaay", Toast.LENGTH_SHORT).show();

                                dialog.dismiss();

                                FinishPickupDelivery finishPickupDelivery = new FinishPickupDelivery(getContext());
                                SharedPreferences prefs = getActivity().getSharedPreferences(Util.PreferenceTokenName,
                                        getActivity().MODE_PRIVATE);
                                String email = prefs.getString(Util.EMAIL, null);

                                String password = prefs.getString(Util.PASSWORD, null);

                                finishPickupDelivery.execute(
                                        Util.BASE_URL + "auth/finishPickup",
                                        "email=" + email + "&password=" + password);
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume(){
        super.onResume();
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
                if (returnedCode == 401) returnedMessage = "Non autorisé";
                if (returnedCode == 400) returnedMessage = "Mauvaise";
                if (returnedCode == 404) returnedMessage = "Ressource non trouvée";

            }

                    try {
                        JSONObject  jsonObject = new JSONObject(result);
                        boolean error = jsonObject.getBoolean("error");
                        String results = jsonObject.getString("results");
                        int status_code = jsonObject.getInt("status_code");
                        if (!error && results.equals("OK") && status_code == 200){
                            SharedPreferences.Editor editor = getActivity().getSharedPreferences(Util.PreferenceTokenName,
                                    getActivity().MODE_PRIVATE).edit();
                            //editor.putString(Util.EMAIL, email);
                            editor.remove(Util.PASSWORD);
                            editor.commit();
                            //returnedMessage += jsonObject.getString("results");
                            getActivity().finish();
                        }else {
                            AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
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
                        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
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

}
