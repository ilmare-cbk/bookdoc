package com.kr.bookdoc.bookdoc.bookdocfcmpush;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.net.URLDecoder;
import java.util.Map;

import com.kr.bookdoc.bookdoc.BookdocMainActivity;
import com.kr.bookdoc.bookdoc.BookdocPropertyManager;
import com.kr.bookdoc.bookdoc.R;


public class BookdocFCMPushMessageService extends FirebaseMessagingService {

    private static final String TAG = "FCMPushMessageService";
    private static final int REQUEST_CODE = 0;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        Map<String, String> receiveData = remoteMessage.getData();
        Log.d(TAG, "id: "+receiveData.get("id"));
        Log.d(TAG, "name: "+receiveData.get("name"));
        Log.d(TAG, "imagePath: "+receiveData.get("imagePath"));
        Log.d(TAG, "prescriptionId: "+receiveData.get("prescriptionId"));
        Log.d(TAG, "questionId: "+receiveData.get("questionId"));
        Log.d(TAG, "body: "+remoteMessage.getNotification().getBody());
        Log.d(TAG, "title: "+remoteMessage.getNotification().getTitle());
        try {

            sendPushNotification(URLDecoder.decode(receiveData.get("name"), "UTF-8"),receiveData.get("prescriptionId"),receiveData.get("questionerId"));
        } catch (Exception e) {

        }
    }
    private void sendPushNotification(String pushMessage, String prescriptionId, String questionerId) {
        Intent intent = new Intent(this, BookdocMainActivity.class);
        intent.putExtra("prescriptionId", Integer.parseInt(prescriptionId));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, REQUEST_CODE, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder;
        if(Integer.parseInt(questionerId) == BookdocPropertyManager.getInstance().getUserId()){
            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("BOOKDOC 알림이 왔어요")
                    .setContentText(Html.fromHtml(String.format(getResources().getString(R.string.noti_prescription),pushMessage)))
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);
        }else{
            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("BOOKDOC 알림이 왔어요")
                    .setContentText(Html.fromHtml(String.format(getResources().getString(R.string.noti_wonder),pushMessage)))
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }
}