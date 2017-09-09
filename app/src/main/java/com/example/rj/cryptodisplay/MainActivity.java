package com.example.rj.cryptodisplay;

import android.graphics.Color;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.rj.cryptodisplay.model.CurrencyAPI;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.R.attr.entries;

public class MainActivity extends AppCompatActivity {

    public String API_BASE_URL = "https://www.bitstamp.net";
    public LineChart linechart;
    public LineData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linechart = (LineChart)findViewById(R.id.crypto_line_chart);
        populateGraph();

        final Handler handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                updateGraph();
                handler.postDelayed(this, 1000);
            }
        };

        handler.postDelayed(r, 1000);
    }

    public void populateGraph()
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

        // Fetch a list
        Call<List<CurrencyAPI>> call = client.getCurrent();

        // Execute call asynchronously.  Get a positive or negative callback
        call.enqueue(new Callback<List<CurrencyAPI>>(){
            @Override
            public void onResponse(Call<List<CurrencyAPI>> call, Response<List<CurrencyAPI>> response) {
                // The network call was a success and we got a response
                // TODO: use the repository list and display it

                List<CurrencyAPI> currencyList = response.body();

                //Y value
                ArrayList<Entry> entries = new ArrayList<Entry>();

                //X value
                ArrayList<String> labels = new ArrayList<String>();

                for(int i = 0; i < currencyList.size(); i++)
                {
                    entries.add(new Entry(i, Float.parseFloat(currencyList.get(i).getPrice())));
                    labels.add(currencyList.get(i).getDate());
                    //Log.d("HELP", currencyList.get(i).getPrice());
                }

                LineDataSet lineSet1 = new LineDataSet(entries, "Prices");
                lineSet1.setDrawCircles(false);
                lineSet1.setColor(Color.BLUE);

                ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();
                lineDataSets.add(lineSet1);

                data = new LineData(lineDataSets);
                linechart.setData(new LineData(lineDataSets));
            }

            @Override
            public void onFailure(Call<List<CurrencyAPI>> call, Throwable t) {
                // the network call was a failure
                // TODO: handle error
                Toast.makeText(MainActivity.this, "Error :(", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateGraph()
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

        // Fetch a list
        Call<List<CurrencyAPI>> call = client.getCurrent();

        // Execute call asynchronously.  Get a positive or negative callback
        call.enqueue(new Callback<List<CurrencyAPI>>() {
            @Override
            public void onResponse(Call<List<CurrencyAPI>> call, Response<List<CurrencyAPI>> response) {
                // The network call was a success and we got a response
                // TODO: use the repository list and display it

                List<CurrencyAPI> currencyList = response.body();
                currencyList.get(0);
                Entry recentPrice = new Entry(0, Float.parseFloat(currencyList.get(0).getPrice()));
                data.addEntry(recentPrice, 0);
                Toast.makeText(MainActivity.this, currencyList.get(0).getPrice(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<CurrencyAPI>> call, Throwable t) {
                // the network call was a failure
                // TODO: handle error
                Toast.makeText(MainActivity.this, "Error :(", Toast.LENGTH_SHORT).show();
            }
        });

        linechart.invalidate();
    }

}
