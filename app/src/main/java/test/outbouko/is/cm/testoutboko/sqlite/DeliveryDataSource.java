package test.outbouko.is.cm.testoutboko.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import test.outbouko.is.cm.testoutboko.model.Delivery;

/**
 * Created by Ange_Douki on 17/08/2016.
 */
public class DeliveryDataSource {

    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private Context mContext;
    private String[] allColumns = { MySQLiteHelper._ID,
            MySQLiteHelper.COUNCIL_ID,   MySQLiteHelper.DIVISION_ID,
            MySQLiteHelper.REGION_ID, MySQLiteHelper.COUNTRY_ID,
            MySQLiteHelper.TRACKING_CODE,
            MySQLiteHelper.QUARTER,
            MySQLiteHelper.CITY,
            MySQLiteHelper.NAME_SURNAME,
            MySQLiteHelper.EMAIL,
            MySQLiteHelper.PHONENUMBER,
            MySQLiteHelper.DESCRIPTION
    };

    public DeliveryDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
        mContext = context;
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void createDelivery(int council_id, int division_id, int region_id, int country_id,
                              String tracking_code, String quarter, String city, String name_surname,
                              String email, String phonenumber, String description) {
        ContentValues values = new ContentValues();
        //values.put(MySQLiteHelper._ID, id);
        values.put(MySQLiteHelper.COUNCIL_ID, council_id);
        values.put(MySQLiteHelper.DIVISION_ID, division_id);
        values.put(MySQLiteHelper.REGION_ID, region_id);
        values.put(MySQLiteHelper.COUNTRY_ID, country_id);
        values.put(MySQLiteHelper.TRACKING_CODE, tracking_code);
        values.put(MySQLiteHelper.TRACKING_CODE, tracking_code);
        values.put(MySQLiteHelper.QUARTER, quarter);
        values.put(MySQLiteHelper.CITY, city);
        values.put(MySQLiteHelper.NAME_SURNAME, name_surname);
        values.put(MySQLiteHelper.EMAIL, email);
        values.put(MySQLiteHelper.PHONENUMBER, phonenumber);
        values.put(MySQLiteHelper.DESCRIPTION, description);

        database.insert(MySQLiteHelper.TABLE_DELIVERIES, null,values);
        /*Cursor cursor = database.query(MySQLiteHelper.TABLE_DELIVERIES,
                allColumns, MySQLiteHelper._ID + " = " + id, null,
                null, null, null);
        cursor.moveToFirst();
        Delivery newDelivery = cursorToDelivery(cursor);
        cursor.close();
        return newDelivery;*/
    }

    private Delivery cursorToDelivery(Cursor cursor) {
        Delivery delivery = new Delivery();
        delivery.setId(cursor.getInt(0));
        delivery.setCouncil_id(cursor.getInt(1));
        delivery.setDivision_id(cursor.getInt(2));
        delivery.setRegion_id(cursor.getInt(3));
        delivery.setCity(cursor.getString(4));
        delivery.setTracking_code(cursor.getString(5));
        delivery.setQuarter(cursor.getString(6));
        delivery.setCity(cursor.getString(7));
        delivery.setName_surname(cursor.getString(8));
        delivery.setEmail(cursor.getString(9));
        delivery.setPhonenumber(cursor.getString(10));
        delivery.setDescription(cursor.getString(11));
        return delivery;
    }

    public void deleteDelivery(Delivery delivery) {
        if(delivery != null){
            int id = delivery.getId();
            //System.out.println("Comment deleted with id: " + id);
            int res = database.delete(MySQLiteHelper.TABLE_DELIVERIES, MySQLiteHelper._ID
                    + " = " + id , null);
            //Toast.makeText(mContext, "" + res, Toast.LENGTH_LONG).show();
            if(res == 1)
                Toast.makeText(mContext, "delivery supprimé avec succès", Toast.LENGTH_LONG).show();
            else Toast.makeText(mContext, "Ne peut etre supprimé", Toast.LENGTH_LONG).show();
        }else{
            int rest = database.delete(MySQLiteHelper.TABLE_DELIVERIES, MySQLiteHelper._ID + ">=0", null);
            Toast.makeText(mContext, "" + rest + " delivery supprimé avec succès", Toast.LENGTH_LONG).show();
        }
    }
    public void deleteDeliveryByTrackingCode(String trackingcode) {
        int rest = database.delete(MySQLiteHelper.TABLE_DELIVERIES, MySQLiteHelper.TRACKING_CODE + "=" + trackingcode, null);
        Toast.makeText(mContext, "" + rest + " delivery supprimé avec succès", Toast.LENGTH_LONG).show();
    }

    public List<Delivery> getAllDelivery() {
        List<Delivery> deliveries = new ArrayList<Delivery>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_DELIVERIES,
                allColumns, "1" , null, null, null, MySQLiteHelper._ID + " ");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Delivery delivery = cursorToDelivery(cursor);
            deliveries.add(delivery);
            cursor.moveToNext();
        }
        return deliveries;
    }

    public boolean updateDelivery(int id, ContentValues values){
        String [] params = {"" + id};
        int a  = database.update(MySQLiteHelper.TABLE_DELIVERIES, values, "" +MySQLiteHelper._ID
                + "=?"  , params);
        return (a==1);
    }

    public Delivery getDeliveryByTrackingCode(String trackingcode){

        Log.e("TRACKING CODE", trackingcode);

        List<Delivery> deliveries = new ArrayList<Delivery>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_DELIVERIES,
                allColumns, MySQLiteHelper.TRACKING_CODE + "='" + trackingcode + "'"   , null, null, null, MySQLiteHelper._ID + " ");
        /*String[] params = new String[]{trackingcode};
        Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_DELIVERIES +
                " WHERE " + MySQLiteHelper.TRACKING_CODE + "='?'" , params);*/
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Delivery delivery = cursorToDelivery(cursor);
            deliveries.add(delivery);
            cursor.moveToNext();
        }
        if (deliveries.size() > 0) return deliveries.get(0);
        return null;
    }
}
