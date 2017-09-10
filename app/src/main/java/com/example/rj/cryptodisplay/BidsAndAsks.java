package com.example.rj.cryptodisplay;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.rj.cryptodisplay.model.BidData;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BidsAndAsks extends AppCompatActivity {
    private RecyclerView bidRecycle;
    private RecyclerView.Adapter bidAdapter;
    private List<BidItem> bidItems;
    public String API_BASE_URL = "https://www.bitstamp.net";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bids_and_asks);

        bidRecycle = (RecyclerView) findViewById(R.id.Bids);
        bidRecycle.setHasFixedSize(true);
        bidRecycle.setLayoutManager(new LinearLayoutManager(this));
        bidItems = new ArrayList<>();
        populateBidItems();

    }

    public void populateBidItems()
    {
        Call<BidData> call = fetchBids();
        // Execute call asynchronously.  Get a positive or negative callback

        call.enqueue(new Callback<BidData>() {
            @Override
            public void onResponse(Call<BidData> call, Response<BidData> response)
            {
                // The network call was a success and we got a response
                // TODO: use the repository list and display it

                BidData bids = response.body();
                List<List<String> > bidList = bids.getBids();

                for(int i = 0; i < 100; i++)
                {
                    BidItem bitem = new BidItem(bidList.get(i).get(0).toString(), bidList.get(i).get(1).toString(),
                            Double.toString((Double.parseDouble(bidList.get(i).get(0).toString()) * Double.parseDouble(bidList.get(i).get(1).toString()))));
                    bidItems.add(i, bitem);
                }

                bidAdapter = new BidAdapter(bidItems, BidsAndAsks.this);
                bidRecycle.setAdapter(bidAdapter);


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

    Call<BidData> fetchBids()
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
