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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import test.outbouko.is.cm.testoutboko.sqlite.DeliveryDataSource;
import test.outbouko.is.cm.testoutboko.util.Util;

public class Pickup extends AppCompatActivity {

    private String u_signature, a_signature, d_photo;
    private ImageView mImageView;
    private TextView dataFromprevious;
    public static AppCompatActivity thisActivity;
    private DeliveryDataSource deliveryDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pickup_form);

        thisActivity = this;
        dataFromprevious = (TextView) findViewById(R.id.dataFromprevious);
        Bundle bundle = getIntent().getExtras();
        dataFromprevious.setText(bundle.getString(Util.EMAIL) + " | " + bundle.getString(Util.PASSWORD) + " | " +
                                bundle.getString(Util.TRACKING_CODE));
        //deletePhoto = (ImageButton) findViewById(R.id.delete_photo);
        mImageView = (ImageView)findViewById(R.id.picture);
        /*deletePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mImageView.setImageResource(-1);
                deletePhoto.setVisibility(View.GONE);
            }
        });*/



    }

    public void getPicture(View view){
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(intent, Util.REQUEST_CODE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Util.REQUEST_CODE_PHOTO && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageView = (ImageView) findViewById(R.id.picture);
            mImageView.setImageBitmap(imageBitmap);
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, bao);

            d_photo = Base64.encodeToString(bao.toByteArray(), Base64.DEFAULT);
        }

        if (requestCode == Util.REQUEST_CODE_CLIENT_SIGNATURE && resultCode == Activity.RESULT_OK){
            Bundle extras = data.getExtras();
            String signature = (String) extras.get(Util.SIGNATURE);
            u_signature = signature;
            //Toast.makeText(getApplicationContext(),signature, Toast.LENGTH_LONG).show();
            byte[] b  = Base64.decode(signature, Base64.DEFAULT);
            ImageView client_signature = (ImageView) findViewById(R.id.client_signature);
            Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);
            client_signature.setImageBitmap(bmp);
            client_signature.getLayoutParams().height = 100;
            client_signature.getLayoutParams().width = 150;
        }

        if (requestCode == Util.REQUEST_CODE_AGENT_SIGNATURE && resultCode == Activity.RESULT_OK){
            Bundle extras = data.getExtras();
            String signature = (String) extras.get(Util.SIGNATURE);
            a_signature = signature;
            //Toast.makeText(getApplicationContext(),signature, Toast.LENGTH_LONG).show();
            byte[] b  = Base64.decode(signature, Base64.DEFAULT);
            ImageView agent_signature = (ImageView) findViewById(R.id.agent_signature);
            Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);
            agent_signature.setImageBitmap(bmp);
            agent_signature.getLayoutParams().height = 150;
            agent_signature.getLayoutParams().width = 100;
        }
    }


    public void getClientSignature(View view){
        Intent intent = new Intent(this, Signature.class);
        startActivityForResult(intent, Util.REQUEST_CODE_CLIENT_SIGNATURE);
    }

    public void getAgentSignature(View view){
        Intent intent = new Intent(this, Signature.class);
        startActivityForResult(intent, Util.REQUEST_CODE_AGENT_SIGNATURE);
    }

    public void makePickup(View view){

        if (isInternetOn()){
            String url = Util.BASE_URL +  "delivery/pickup";
            Bundle bundle = getIntent().getExtras();
            String description = ((EditText)findViewById(R.id.edit_description)).getText().toString();
            String place = ((EditText)findViewById(R.id.edit_place)).getText().toString();
            String quarter = ((EditText)findViewById(R.id.edit_quarter)).getText().toString();
            String town = ((EditText)findViewById(R.id.edit_town)).getText().toString();

            String query = "email=" + bundle.getString(Util.EMAIL) +
                            "&password="+bundle.getString(Util.PASSWORD)+
                            "&trackingcode="+bundle.getString(Util.TRACKING_CODE)+
                            "&description="+description+
                            "&photo=" + d_photo+
                            "&place=" + place+
                            "&quarter="+ quarter+
                            "&town=" + town+
                            "&agentsignature=" + a_signature+
                            "&usersignature=" + u_signature
                            ;

            PickupDelivery pickupDelivery = new PickupDelivery(Pickup.thisActivity);
            pickupDelivery.execute(url,query);
        }else {
            Toast.makeText(getApplicationContext(), "Not connected", Toast.LENGTH_LONG).show();
        }
    }

    private class PickupDelivery extends AsyncTask<String,Integer, String> {
        private ProgressDialog dialog;
        private int returnedCode;
        private Context context;
        public PickupDelivery(Context context) {
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

            Log.e("Result Pickup" , result);

            //TextView tv = (TextView) findViewById(R.id.resultat2);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

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
                int status_code = jsonObject.getInt("status_code");
                //returnedMessage += "\nStatus_code: " + status_code;
                if(status_code != 200) returnedMessage += jsonObject.getString("results");
                else {
                    deliveryDataSource = new DeliveryDataSource(getApplicationContext());
                    deliveryDataSource.open();
                    Bundle bundle = Pickup.this.getIntent().getExtras();
                    deliveryDataSource.deleteDeliveryByTrackingCode( bundle.getString(Util.TRACKING_CODE));
                    deliveryDataSource.close();
                }
            } catch (JSONException e) {
                //e.printStackTrace();
                returnedMessage = "Erreur: " + e.getMessage();
            }

            AlertDialog alertDialog = new AlertDialog.Builder(Pickup.this).create();
            alertDialog.setTitle(titre);
            alertDialog.setMessage(returnedMessage);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
            alertDialog.show();
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
}
