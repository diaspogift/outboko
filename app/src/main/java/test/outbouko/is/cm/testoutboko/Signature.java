package test.outbouko.is.cm.testoutboko;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;

import com.kyanogen.signatureview.SignatureView;

import java.io.ByteArrayOutputStream;

import test.outbouko.is.cm.testoutboko.util.Util;

public class Signature extends AppCompatActivity {

    private SignatureView signatureView;
    private Button  clear;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signature);
        signatureView = (SignatureView) findViewById(R.id.signature_paper);
        //save = (Button) findViewById(R.id.save);
        clear = (Button) findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signatureView.clearCanvas();
            }
        });


    }

    public void getSignature(View view){
        Bitmap bitmap = signatureView.getSignatureBitmap();
        Intent returnIntent = new Intent();


        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bao);

        String string_in_img = Base64.encodeToString(bao.toByteArray(), Base64.DEFAULT);

        returnIntent.putExtra(Util.SIGNATURE,string_in_img);
        setResult(Activity.RESULT_OK,returnIntent);
        //Toast.makeText(getApplicationContext(), bitmap.toString(), Toast.LENGTH_LONG).show();/**/
        finish();
    }


}
