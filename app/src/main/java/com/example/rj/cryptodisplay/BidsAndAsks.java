package com.example.rj.cryptodisplay;

import android.os.Handler;
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
        askItems = new ArrayList<>();
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
        Thread BidsThread = new Thread(r);
        BidsThread.start();
    }

    public void addItem(View v){
        askItems.add(0, new AskItem("1", "2", "3"));
        askAdapter.notifyItemInserted(0);
        //mRecyclerView.smoothScrollToPosition(0);
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
                for(int i = 0; i < 100; i++)
                {
                    String Bid = bidList.get(i).get(0).toString();
                    String Price = bidList.get(i).get(1).toString();

                    if(Bid.equals(bidItems.get(i).getBid()) && Price.equals(bidItems.get(i).getAmount()))
                    {
                        break;
                    }



                    BidItem bitem = new BidItem(Bid, Price,
                            Double.toString((Double.parseDouble(bidList.get(i).get(0).toString()) * Double.parseDouble(bidList.get(i).get(1).toString()))));
                    bidStack.push(bitem);
                }

                Stack<AskItem> askStack = new Stack<AskItem>();

                //One loop for asks
                for(int i = 0; i < 100; i++)
                {
                    String Ask = askList.get(i).get(0).toString();
                    String Price = askList.get(i).get(1).toString();

                    if(Ask.equals(askItems.get(i).getBid()) && Price.equals(askItems.get(i).getAmount()))
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
                    bidAdapter.notifyItemInserted(0);
                }
                while(!askStack.empty())
                {
                    askItems.add(0, askStack.pop());
                    askAdapter.notifyItemInserted(0);
                }

                askRecycle.smoothScrollToPosition(0);
                bidRecycle.smoothScrollToPosition(0);
            }
            @Override
            public void onFailure(Call<BidData> call, Throwable t)
            {
                // the network call was a failure
                // TODO: handle error
                Toast.makeText(BidsAndAsks.this, "Error :(", Toast.LENGTH_SHORT).show();
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

                for(int i = 0; i < 100; i++)
                {
                    BidItem bitem = new BidItem(bidList.get(i).get(0).toString(), bidList.get(i).get(1).toString(),
                            Double.toString((Double.parseDouble(bidList.get(i).get(0).toString()) * Double.parseDouble(bidList.get(i).get(1).toString()))));
                    bidItems.add(i, bitem);

                    AskItem aitem = new AskItem(askList.get(i).get(0).toString(), askList.get(i).get(1).toString(),
                            Double.toString((Double.parseDouble(askList.get(i).get(0).toString()) * Double.parseDouble(askList.get(i).get(1).toString()))));
                    askItems.add(i, aitem);
                }

                bidAdapter = new BidAdapter(bidItems, BidsAndAsks.this);
                bidRecycle.setAdapter(bidAdapter);

                askAdapter = new AskAdapter(askItems, BidsAndAsks.this);
                askRecycle.setAdapter(askAdapter);

            }
            @Override
            public void onFailure(Call<BidData> call, Throwable t)
            {
                // the network call was a failure
                // TODO: handle error
                Toast.makeText(BidsAndAsks.this, "Error :(", Toast.LENGTH_SHORT).show();
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
