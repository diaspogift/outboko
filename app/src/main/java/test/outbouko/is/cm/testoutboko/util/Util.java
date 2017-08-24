package test.outbouko.is.cm.testoutboko.util;

import org.json.JSONArray;

import java.util.List;

import test.outbouko.is.cm.testoutboko.model.Agence;

/**
 * Created by Ange_Douki on 05/08/2016.
 */
public class Util {
    public static String token ;
    public static final String TOKEN = "token";
    public static final String EMAIL = "email";
    public static final String TOKEN_TIME = "token_time";
    public static final String PreferenceTokenName = "shared_preference_token_FCM";
    public static final String DATA = "data";
    public static final String DELIVERY = "delivery";
    public static final String DELIVERIES = "deliveries";
    public static final String NOTIFICATION_TITLE = "notification_title";
    public static final String IS_NOTIFICATION = "is_notification";
    public static final String KEYS = "keys";
    public static final String TRACKING_CODE = "tracking_code";
    public static final String SIGNATURE = "signature";
    //public static Map<String, String> data;
    public static String WHO_IS_SHOING = "";
    public static final String URL_LOGIN = "";
    public static final String PASSWORD = "password";
    public static final int REQUEST_CODE_PHOTO = 0;
    public static final int REQUEST_CODE_CLIENT_SIGNATURE = 1;
    public static final int REQUEST_CODE_AGENT_SIGNATURE = 2;
    public static final int REQUEST_CODE_REMETTEUR_SIGNATURE = 3;
    public static final int REQUEST_CODE_RECEPTEUR_SIGNATURE = 4;
    public static final int REQUEST_CODE_RECEPTION = 5;
    public static int NOTIFICATION_ID = 0;
    public static List<Agence> agences;

    //public static final String BASE_URL = "http://softinovplus.tigrimigri.com/paquets/public/api/";
    public static final String BASE_URL = "http://outboko.cm/public/api/";
}
