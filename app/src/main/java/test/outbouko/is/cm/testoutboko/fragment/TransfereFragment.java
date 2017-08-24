package test.outbouko.is.cm.testoutboko.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import test.outbouko.is.cm.testoutboko.R;
import test.outbouko.is.cm.testoutboko.ReceptionDecharge;
import test.outbouko.is.cm.testoutboko.ReceptionTransfert;
import test.outbouko.is.cm.testoutboko.adapter.DeliveryPerUserAdapter;
import test.outbouko.is.cm.testoutboko.model.Delivery;
import test.outbouko.is.cm.testoutboko.util.Util;


/**
 * Created by Ange_Douki on 29/09/2016.
 */
public class TransfereFragment extends Fragment {

    private EditText delivery_per_user_email_transfere;

    private ListView deliveries_by_agent_transfere;
    private List<Delivery> all_deliveries, selectedItems;

    private DeliveryPerUserAdapter all_adapter;
    private View rootView ;
    private CheckBox checkbox_all_transfere;
    private Button validate_get_deliveries_by_email_transfere;
    private String globalEmail;
    //private Button validate_reception;
    FloatingActionButton floatingActionButton;

    public TransfereFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transfere, container, false);

        rootView = view;
        validate_get_deliveries_by_email_transfere = (Button) view.findViewById(R.id.validate_get_deliveries_by_email_transfere);
        deliveries_by_agent_transfere = (ListView) view.findViewById(R.id.deliveries_by_agent_transfere);
        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab_transfere);

        return view;
    }

    public void getSelectedItems() throws JSONException {
        CheckBox checkbox_all = (CheckBox) rootView.findViewById(R.id.checkbox_all_transfere);
        if (checkbox_all.isChecked()){
            selectedItems = all_deliveries;
        }else{
            selectedItems = new ArrayList<Delivery>();
            for (Delivery d : all_deliveries){
                if (d.isChecked()){
                    selectedItems.add(d);
                }
            }
        }
        JSONArray trackingsCodes = new JSONArray();
        for (Delivery d : selectedItems){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", d.getTracking_code());
            trackingsCodes.put(jsonObject);
        }

        if (trackingsCodes.length() > 0){
            Intent intention = new Intent(getActivity().getApplicationContext(), ReceptionTransfert.class);
            Toast.makeText(getActivity().getApplicationContext(), trackingsCodes.toString(), Toast.LENGTH_LONG).show();
            intention.putExtra(Util.DELIVERIES, trackingsCodes.toString());
            SharedPreferences prefs = getActivity().getSharedPreferences(Util.PreferenceTokenName, getActivity().MODE_PRIVATE);
            String email = prefs.getString(Util.EMAIL, null);
            intention.putExtra(Util.EMAIL, email);
            startActivityForResult(intention, Util.REQUEST_CODE_RECEPTION);
        }else {
            Toast.makeText(getActivity().getApplicationContext(), "Pas de paquet selectionné", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {


        //deliveries_by_agent.addFooterView(deliveries_footer);
        validate_get_deliveries_by_email_transfere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeliveriesByUser();
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    getSelectedItems();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
       /* LayoutInflater inflater = (LayoutInflater)getActivity().getApplicationContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);*/
        //View header = inflater.inflate(R.layout.deliveries_by_agent_header, null);
        //deliveries_by_agent.addHeaderView(header);
        checkbox_all_transfere = (CheckBox) rootView.findViewById(R.id.checkbox_all_transfere);
        checkbox_all_transfere.setChecked(false);

        checkbox_all_transfere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkOrOnCheckAll();
            }
        });

        delivery_per_user_email_transfere = (EditText) rootView.findViewById(R.id.delivery_per_user_email_transfere);

        all_deliveries = new ArrayList<Delivery>();
        selectedItems = new ArrayList<Delivery>();

        all_adapter = new DeliveryPerUserAdapter(getContext(),all_deliveries);
        deliveries_by_agent_transfere.setAdapter(all_adapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume(){
        super.onResume();
        validate_get_deliveries_by_email_transfere.performClick();
    }

    public boolean isInternetOn()
    {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public void getDeliveriesByUser(){

        EditText email = (EditText) rootView.findViewById(R.id.delivery_per_user_email_transfere);

        String email_str = email.getText().toString();
        globalEmail = email_str;

        if (!email_str.equals("")) {
            if (isInternetOn()){
                GetDeliveryByEmail getDeliveryByEmail = new GetDeliveryByEmail(getContext());
                getDeliveryByEmail.execute(
                        Util.BASE_URL + "delivery/getDeliveriesForTransportationByEmail",
                        "email=" + email_str);
            }else {
                //ActionBarActivity actionBarActivity = (ActionBarActivity) getActivity();
                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.no_internet), Toast.LENGTH_LONG).show();
            }

        } else {

            //ActionBarActivity activity = (ActionBarActivity) getActivity();
            //ErrorDialog errorDialog = ErrorDialog.newInstance(++mStackLevel, "NUI ou CNE invalide");
            //errorDialog.show(activity.getSupportFragmentManager(), "dialog");
            // popAlertError("NUI ou CNE  invalide ");
            Toast.makeText(getActivity().getApplicationContext(), "Email invalide", Toast.LENGTH_LONG).show();
        }
    }

    public void checkOrOnCheckAll(){
        for (Delivery d : all_deliveries){
            d.setChecked(checkbox_all_transfere.isChecked());
        }
        all_adapter.notifyDataSetChanged();
    }

    private class GetDeliveryByEmail extends AsyncTask<String,Integer, String> {
        private ProgressDialog dialog;
        private int returnedCode;
        private Context context;
        public GetDeliveryByEmail(Context context) {
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
            //TextView tv = (TextView) findViewById(R.id.resultat2);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            Log.e("Result get deliveries" , result + " | Errorcode: " + returnedCode);

            if (returnedCode == 200){
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    boolean error = jsonObject.getBoolean("error");
                    String res = jsonObject.getString("results");
                    int status_code = jsonObject.getInt("status_code");
                    JSONArray deliveries = jsonObject.getJSONArray("deliveries");
                    if (!error && res.equals("OK") && status_code == 200 && deliveries.length() > 0){
                       /* Intent intention = new Intent(Deliveries.thisActivity, DeliveryPerUser.class);
                        intention.putExtra(Util.DELIVERIES, deliveries.toString());
                        intention.putExtra(Util.EMAIL, globalEmail);
                        startActivity(intention);*/
                        all_deliveries = new ArrayList<Delivery>();

                        for (int i=0; i<deliveries.length(); i++){
                            JSONObject job = deliveries.getJSONObject(i);
                            Delivery delivery = new Delivery(
                                    job.getInt("id"),
                                    job.getInt("council_id"),
                                    job.getInt("division_id"),
                                    job.getInt("region_id"),
                                    job.getInt("country_id"),
                                    job.getString("tracking_code"),
                                    job.getString("quarter"),
                                    job.getString("city"),
                                    job.getString("name_surname"),
                                    job.getString("email"),
                                    job.getString("phonenumber"),
                                    job.getString("description")
                            );
                            all_deliveries.add(delivery);
                        }
                        all_adapter = new DeliveryPerUserAdapter(this.context, all_deliveries);
                        deliveries_by_agent_transfere.setAdapter(all_adapter);

                        all_adapter.notifyDataSetChanged();
                    }else {
                        all_deliveries = new ArrayList<Delivery>();
                        all_adapter = new DeliveryPerUserAdapter(this.context, all_deliveries);
                        deliveries_by_agent_transfere.setAdapter(all_adapter);
                        all_adapter.notifyDataSetChanged();
                        if (!error && res.equals("OK") && status_code == 200 )
                            Toast.makeText(getContext(), " Aucun paquet trouvé sous la responsabilité de  " +  globalEmail,
                                    Toast.LENGTH_LONG).show();
                        else{
                            Toast.makeText(getContext(), "Erreur:  Essayez de nouveau ", Toast.LENGTH_LONG).show();
                        }
                    }
                    if (all_adapter != null)
                        all_adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Toast.makeText(getContext(), "Erreur:  Essayez de nouveau", Toast.LENGTH_LONG).show();
                }
            }else {
                Toast.makeText(getContext(), "Erreur:  Essayez de nouveau", Toast.LENGTH_LONG).show();
            }


        }
    }

}
