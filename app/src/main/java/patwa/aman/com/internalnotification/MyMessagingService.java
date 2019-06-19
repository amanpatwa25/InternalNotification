package patwa.aman.com.internalnotification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;


import com.android.volley.toolbox.ImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static patwa.aman.com.internalnotification.StartActivity.*;

public class MyMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

//        notiData = (DatabaseReference) FirebaseDatabase.getInstance().getReference("notiData");
        if (remoteMessage.getData().isEmpty()) {
            showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(),remoteMessage.getData().get("image"));
//            System.out.println(remoteMessage.getNotification().getClickAction().toString());
        } else {
            showNotification(remoteMessage.getData());
//            System.out.println(remoteMessage.getNotification().getClickAction().toString());
        }


    }

    private void showNotification(String title, String body , String image)  {

        System.out.println("Title:" + title);
        Bitmap bmp = null;
        System.out.println("Body:" + body);

//        "http://image10.bizrate-images.com/resize?sq=60&uid=2216744464"

        
//        try {
//            URL url = new URL(image);
//            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


//        ImageLoader imgLoader = new ImageLoader((Context)this);
//
//        // whenever you want to load an image from url
//        // call DisplayImage function
//        // url - image url to load
//        // loader - loader image, will be displayed before getting image
//        // image - ImageView
//        imgLoader.DisplayImage();


        Uri uri = Uri.parse("https://wa.me/<" + title + ">/?text=" + image);
        Intent i = new Intent(Intent.ACTION_VIEW, uri);
        i.setPackage("com.whatsapp");

        PendingIntent pendingIntent = (PendingIntent) PendingIntent.getActivity(this, 1, i, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "patwa.aman.com.coutloot.test";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification", NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("Coutloot");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableVibration(true);
            notificationChannel.enableLights(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);


        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(pendingIntent)
                .setContentInfo("Info");

        notificationManager.notify(new Random().nextInt(), notificationBuilder.build());

//        startActivity(Intent.createChooser(i, "Share with"));

    }


    public void showNotification(Map<String, String> data) {
        String title = data.get("title").toString();
        String body = data.get("body").toString();
        String image = data.get("image").toString();
        Bitmap myBitmap = null;
        System.out.println("Title:" + title);
        System.out.println("Body:" + body);
        Log.v("Image",image+"");
//        try {
//            URL url = new URL(image);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setDoInput(true);
//            connection.connect();
//            InputStream input = connection.getInputStream();
//            myBitmap = BitmapFactory.decodeStream(input);
//            Log.v("Bitmap", String.valueOf(myBitmap));
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        Uri uri = Uri.parse("https://wa.me/<" + title + ">/?text=" +myBitmap);
        Intent i = new Intent(Intent.ACTION_VIEW, uri);
        i.setPackage("com.whatsapp");

        PendingIntent pendingIntent = (PendingIntent) PendingIntent.getActivity(this, 1, i, PendingIntent.FLAG_UPDATE_CURRENT);

        System.out.println("Title:" + title);
        System.out.println("Body:" + body);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "patwa.aman.com.coutloot.test";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Coutloot");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(title)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentText(body)
                .setContentIntent(pendingIntent)
                .setContentInfo("Info");

        notificationManager.notify(new Random().nextInt(), notificationBuilder.build());
//        startActivity(Intent.createChooser(i, "Share with"));

    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);


        Log.d("TOKENFIREBASE", s);
    }


}
//notificationChannel.setVibrationPattern(new long[]{0,1000,500,1000});