package test.outbouko.is.cm.testoutboko.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Set;

import test.outbouko.is.cm.testoutboko.Deliveries;
import test.outbouko.is.cm.testoutboko.MainActivity;
import test.outbouko.is.cm.testoutboko.R;
import test.outbouko.is.cm.testoutboko.sqlite.DeliveryDataSource;
import test.outbouko.is.cm.testoutboko.util.Util;

/**
 * Created by Ange_Douki on 05/08/2016.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private Map<String, String> data;
    private RemoteMessage.Notification notification;
    private DeliveryDataSource deliveryDataSource;
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ


        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData() != null && remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            data = remoteMessage.getData();
            Log.e("notification data 0", data.toString());
            //Util.data = data;
            //Toast.makeText(getApplicationContext(), "Message data payload: " + remoteMessage.getData(), Toast.LENGTH_LONG).show();
        }else {
            //Toast.makeText(getApplicationContext(), "No data sent back", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Message data payload: " + "No data sent back");
        }
        // Check if message contains a notification payload.
        /*if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            notification = remoteMessage.getNotification();
            //Toast.makeText(getApplicationContext(), "Message Notification Body: " +
                   // remoteMessage.getNotification().getBody(), Toast.LENGTH_LONG).show();
        }else {
            Log.d(TAG, "Message Notification Body: " + " Null notification");
        }*/

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        try {
            createNotification();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    // [END receive_message]

    private void createNotification( ) throws JSONException {

        long when = System.currentTimeMillis();
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        int icon = R.drawable.logo;
        //CharSequence title = context.getResources().getString(R.string.titleService);
        String title = "Assignation d'un nouveau paquet";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext());
        notificationBuilder = notificationBuilder.setSmallIcon(icon);
        notificationBuilder = notificationBuilder.setContentTitle(title);
        notificationBuilder = notificationBuilder.setContentText(title);
        notificationBuilder = notificationBuilder.setTicker(title);
        notificationBuilder = notificationBuilder.setWhen(when);
        notificationBuilder = notificationBuilder.setSound(soundUri);
        long[] pattern =  {500,500,500,500,500,500,500,500,500};
        notificationBuilder = notificationBuilder.setVibrate(pattern);
        notificationBuilder = notificationBuilder.setAutoCancel(true);
        notificationBuilder = notificationBuilder.setLights(Color.BLUE, 500, 500);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(title);
        bigText.setBigContentTitle(title);
        bigText.setSummaryText(title);
        notificationBuilder.setStyle(bigText);
        notificationBuilder.setPriority(NotificationCompat.PRIORITY_MAX);//ceci fait apparaitre la notification en grand format

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(getApplicationContext());

        SharedPreferences prefs = getSharedPreferences(Util.PreferenceTokenName, MODE_PRIVATE);
        String password = prefs.getString(Util.PASSWORD, null);
        /*if (password == null) {
            regId="";
        }*/

        Intent intent = null;
        if (password == null){
            intent = new Intent(this , MainActivity.class);
        }else {
            intent = new Intent(this , Deliveries.class);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        taskStackBuilder.addParentStack(Deliveries.class);
        taskStackBuilder.addNextIntent(intent);

       // Bundle extras = new Bundle();
        //HashMap<String, String> hdata = new HashMap<String, String>(data);
        //extras.putSerializable(Util.DATA,hdata);
        //intent.putExtras(extras);
        //intent.putExtra("data", (new JSONObject(data)).toString());
        //Log.e("Nkalla key", Util.data.toString());
        //String notification_title = hdata.get(Util.NOTIFICATION_TITLE);
        Set<String> keySet = data.keySet();
        //Log.e("keySet", keySet.toString());
        //Log.e("keySet", hdata.get(Util.DATA));

        String donnee = data.get(Util.DATA);

        JSONObject jsonObject = new JSONObject(donnee);

        JSONObject delivery = jsonObject.getJSONObject(Util.DELIVERY);

        String notification_title = jsonObject.getString(Util.NOTIFICATION_TITLE);

        //intent.putExtra(Util.IS_NOTIFICATION, Util.IS_NOTIFICATION);


        deliveryDataSource = new DeliveryDataSource(getApplicationContext());
        deliveryDataSource.open();

        deliveryDataSource.createDelivery(
            delivery.getInt("council_id"),
            delivery.getInt("division_id"),
            delivery.getInt("region_id"),
            delivery.getInt("country_id"),
            delivery.getString("tracking_code"),
            delivery.getString("quarter"),
            delivery.getString("city"),
            delivery.getString("name_surname"),
            delivery.getString("email"),
            delivery.getString("phonenumber"),
            ""
        );
        deliveryDataSource.close();
        /*



        Iterator<String> iter = delivery.keys();
        ArrayList<String> arrayKeys = new ArrayList<String>();
        int i = 0;
        while (iter.hasNext()) {
            String key = iter.next();
            try {
                Object value = delivery.get(key);
                if (value instanceof String ){
                    intent.putExtra(key, (String) value);
                }else {
                    intent.putExtra(key, (int) value);
                }
                arrayKeys.add(key);
            } catch (JSONException e) {
                // Something went wrong!
            }
        }

        intent.putStringArrayListExtra(Util.KEYS, arrayKeys);*/



       /* Log.e("Extra delivery", delivery.toString());
        PendingIntent resultIntent = PendingIntent.getActivity(this , 0, intent, PendingIntent.FLAG_ONE_SHOT);


        Uri notificationSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.fax)
                .setContentTitle(notification_title)
                .setContentText(notification_title)
                .setAutoCancel( true )
                .setSound(notificationSoundURI)
                .setContentIntent(resultIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);



        notificationManager.notify(++Util.NOTIFICATION_ID, mNotificationBuilder.build());*/


        //intent.putExtra("bundle", bundle);
        PendingIntent notificationPendingIntent =
                taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder = notificationBuilder.setContentIntent(notificationPendingIntent);
        Notification notification = notificationBuilder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        NotificationManager notificationManager =
                (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Util.NOTIFICATION_ID ++, notification);
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.fax)
                .setContentTitle("FCM Message")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
