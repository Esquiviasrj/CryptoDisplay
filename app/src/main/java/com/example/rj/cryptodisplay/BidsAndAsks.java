package com.example.rj.cryptodisplay;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.example.rj.cryptodisplay.model.BidData;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BidsAndAsks extends AppCompatActivity {
    private RecyclerView bidRecycle;
    private RecyclerView.Adapter bidAdapter;

    private RecyclerView askRecycle;
    private RecyclerView.Adapter askAdapter;

    private List<BidItem> bidItems;
    private List<AskItem> askItems;

    public String API_BASE_URL = "https://www.bitstamp.net";

    public static NotificationCompat.Builder notification;
    public static final int uniqueID = 87424;

    public Thread BidsThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bids_and_asks);

        bidRecycle = (RecyclerView) findViewById(R.id.Bids);
        bidRecycle.setHasFixedSize(true);
        bidRecycle.setLayoutManager(new LinearLayoutManager(this));

        askRecycle = (RecyclerView)findViewById(R.id.Asks);
        askRecycle.setHasFixedSize(true);
        askRecycle.setLayoutManager(new LinearLayoutManager(this));

        bidItems = new ArrayList<>();
        bidAdapter = new BidAdapter(bidItems, BidsAndAsks.this);
        bidRecycle.setAdapter(bidAdapter);

        askItems = new ArrayList<>();
        askAdapter = new AskAdapter(askItems, BidsAndAsks.this);
        askRecycle.setAdapter(askAdapter);

        askAdapter.notifyItemInserted(0);
        bidAdapter.notifyItemInserted(0);
        populateBidItems();

        //Create new thread of execution to handle updating the graph every 10 seconds
        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                updateBidsAndAsks();
                handler.postDelayed(this, 10000);
            }
        };
        //handler.postDelayed(r, 10000);
        BidsThread = new Thread(r);
        BidsThread.start();
    }

    public void addItem(View v){
        setNotificationParams();
    }

    public void setNotificationParams()
    {
        notification = new NotificationCompat.Builder(getApplicationContext());
        notification.setAutoCancel(true);

        //Build the notification
        notification.setSmallIcon(R.drawable.robot);
        notification.setTicker("This is the ticker");
        notification.setWhen(System.currentTimeMillis());
        notification.setContentTitle("Here is the title");
        notification.setContentText("I am the body text of your notification");

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);

        //Builds notification and issues it
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(uniqueID, notification.build());

    }

    public void updateBidsAndAsks()
    {
        Call<BidData> call = fetchBidsAndAsks();
        // Execute call asynchronously.  Get a positive or negative callback

        call.enqueue(new Callback<BidData>() {
            @Override
            public void onResponse(Call<BidData> call, Response<BidData> response)
            {
                // The network call was a success and we got a response
                // TODO: use the repository list and display it

                BidData bids = response.body();
                List<List<String> > bidList = bids.getBids();
                List<List<String> > askList = bids.getAsks();


                Stack<BidItem> bidStack = new Stack<BidItem>();

                //One loop for bids
                for(int i = 0; i < bidList.size(); i++)
                {
                    String Bid = bidList.get(i).get(0).toString();
                    String Price = bidList.get(i).get(1).toString();

                    if((i > bidItems.size() - 1) || (Bid.equals(bidItems.get(i).getBid()) && Price.equals(bidItems.get(i).getAmount())))
                    {
                        break;
                    }

                    BidItem bitem = new BidItem(Bid, Price,
                            Double.toString((Double.parseDouble(bidList.get(i).get(0).toString()) * Double.parseDouble(bidList.get(i).get(1).toString()))));
                    bidStack.push(bitem);
                }

                Stack<AskItem> askStack = new Stack<AskItem>();

                //One loop for asks
                for(int i = 0; i < askList.size(); i++)
                {
                    String Ask = askList.get(i).get(0).toString();
                    String Price = askList.get(i).get(1).toString();

                    if((i > askItems.size() - 1) || (Ask.equals(askItems.get(i).getBid()) && Price.equals(askItems.get(i).getAmount())))
                    {
                        break;
                    }

                    AskItem aitem = new AskItem(askList.get(i).get(0).toString(), askList.get(i).get(1).toString(),
                            Double.toString((Double.parseDouble(askList.get(i).get(0).toString()) * Double.parseDouble(askList.get(i).get(1).toString()))));
                    askStack.push(aitem);
                }

                //Add contents of both stacks into lists and notify adapters
                while(!bidStack.empty())
                {
                    bidItems.add(0, bidStack.pop());
                    //BidItem rItem = bidItems.get(bidItems.size() - 1);
                    //askItems.remove(rItem);
                    bidAdapter.notifyItemInserted(0);
                }
                while(!askStack.empty())
                {
                    askItems.add(0, askStack.pop());
                    //AskItem rItem = askItems.get(askItems.size() - 1);
                    //askItems.remove(rItem);
                    askAdapter.notifyItemInserted(0);
                }

                askRecycle.smoothScrollToPosition(0);
                bidRecycle.smoothScrollToPosition(0);
            }
            @Override
            public void onFailure(Call<BidData> call, Throwable t)
            {
                // the network call was a failure
            }
        });
    }

    public void populateBidItems()
    {
        Call<BidData> call = fetchBidsAndAsks();
        // Execute call asynchronously.  Get a positive or negative callback

        call.enqueue(new Callback<BidData>() {
            @Override
            public void onResponse(Call<BidData> call, Response<BidData> response)
            {
                // The network call was a success and we got a response
                // TODO: use the repository list and display it

                BidData bids = response.body();
                List<List<String> > bidList = bids.getBids();
                List<List<String> > askList = bids.getAsks();


                for(int i = 0; i < bidList.size(); i++)
                {
                    BidItem bitem = new BidItem(bidList.get(i).get(0).toString(), bidList.get(i).get(1).toString(),
                            Double.toString((Double.parseDouble(bidList.get(i).get(0).toString()) * Double.parseDouble(bidList.get(i).get(1).toString()))));
                    bidItems.add(i, bitem);

                    bidAdapter.notifyItemInserted(i);
                }

                for(int i = 0; i < askList.size(); i++)
                {
                    AskItem aitem = new AskItem(askList.get(i).get(0).toString(), askList.get(i).get(1).toString(),
                            Double.toString((Double.parseDouble(askList.get(i).get(0).toString()) * Double.parseDouble(askList.get(i).get(1).toString()))));
                    askItems.add(i, aitem);

                    askAdapter.notifyItemInserted(i);
                }
            }
            @Override
            public void onFailure(Call<BidData> call, Throwable t)
            {
                // the network call was a failure
            }
        });
    }

    Call<BidData> fetchBidsAndAsks()
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
        return client.getBids();
    }
}
