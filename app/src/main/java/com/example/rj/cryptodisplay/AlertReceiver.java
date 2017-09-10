package com.example.rj.cryptodisplay;




import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.rj.cryptodisplay.model.Hourly;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by anon on 9/10/2017.
 */

public class AlertReceiver extends BroadcastReceiver {
    public String API_BASE_URL = "https://www.bitstamp.net";

    @Override
    public void onReceive(Context context, Intent intent) {
        final String p = intent.getStringExtra("PRICE");
        final Context c = context;

        //if the thing happens, create notification here
        Call<Hourly> call = fetchHourlyPrices();
        call.enqueue(new Callback<Hourly>() {
            @Override
            public void onResponse(Call<Hourly> call, Response<Hourly> response) {
                // The network call was a success and we got a response
                // TODO: use the repository list and display it

                Hourly hObject = response.body();

                if(Double.parseDouble(p) > Double.parseDouble(hObject.getVwap()))
                {
                    createNotification(c, hObject.getVwap(), "Price is below " + p, "Alert");
                }
            }

            @Override
            public void onFailure(Call<Hourly> call, Throwable t) {
                // the network call was a failure
                // TODO: handle error
                Log.d("HELP", "ONFAILURE");
            }
        });


    }

    public void createNotification(Context context, String msg, String msgText, String msgAlert)
    {
        Log.d("Help", "here");
        android.support.v7.app.NotificationCompat.Builder notification;
        final int uniqueID = 87424;

        notification = new android.support.v7.app.NotificationCompat.Builder(context);
        notification.setAutoCancel(true);

        //Build the notification
        notification.setSmallIcon(R.drawable.robot);
        notification.setTicker(msgAlert);
        notification.setWhen(System.currentTimeMillis());
        notification.setContentTitle(msg);
        notification.setContentText(msgText);
        notification.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND);


        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);

        //Builds notification and issues it
        NotificationManager nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        nm.notify(uniqueID, notification.build());
    }

    Call<Hourly> fetchHourlyPrices()
    {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        Retrofit.Builder builder =
                new Retrofit.Builder()
                        .baseUrl(API_BASE_URL)
                        .addConverterFactory(
                                GsonConverterFactory.create()
                        );

        Retrofit retrofit =
                builder
                        .client(
                                httpClient.build()
                        )
                        .build();

        BitStampClient client =  retrofit.create(BitStampClient.class);
        return client.getHourly();
    }
}
