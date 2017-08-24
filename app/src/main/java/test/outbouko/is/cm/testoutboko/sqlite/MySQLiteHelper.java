package test.outbouko.is.cm.testoutboko.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Ange_Douki on 17/08/2016.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_DELIVERIES = "deliveries";
    public static final String _ID = "_id";
    public static final String COUNCIL_ID = "council_id";
    public static final String DIVISION_ID = "division_id";
    public static final String REGION_ID = "region_id";
    public static final String COUNTRY_ID = "country_id";
    public static final String TRACKING_CODE = "tracking_code";
    public static final String QUARTER = "quarter";
    public static final String CITY = "city";
    public static final String NAME_SURNAME = "name_surname";
    public static final String EMAIL = "email";
    public static final String PHONENUMBER = "phonenumber";
    public static final String DESCRIPTION = "description";

    private static final String DATABASE_NAME = "deliveries.db";
    private static final int DATABASE_VERSION = 2;

    private static final String DATABASE_CREATE = "create table "
            + TABLE_DELIVERIES + "(" + _ID
            + " integer primary key, " + COUNCIL_ID
            + " integer not null, " + DIVISION_ID
            + " integer not null, " + REGION_ID
            + " integer not null," + COUNTRY_ID
            + " integer not null, " + TRACKING_CODE
            + " text not null, " + QUARTER
            + " text not null, " + CITY
            + " text not null, " + NAME_SURNAME
            + " text not null, " + EMAIL
            + " text not null, " + PHONENUMBER
            + " text not null, " + DESCRIPTION
            + " text not null "
            +");";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + i + " to "
                        + i1 + ", which will destroy all old data");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_DELIVERIES);
        onCreate(sqLiteDatabase);
    }
}
