package com.example.rj.cryptodisplay;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.rj.cryptodisplay.model.BidData;
import com.example.rj.cryptodisplay.model.CurrencyAPI;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

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
    private String lastTid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        linechart = (LineChart)findViewById(R.id.crypto_line_chart);
        populateGraph();

        // enable touch gestures
        linechart.setTouchEnabled(true);

        // enable scaling and dragging
        linechart.setDragEnabled(true);
        linechart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        linechart.setPinchZoom(true);

        // set an alternative background color
        linechart.setBackgroundColor(Color.LTGRAY);


        final Handler handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                updateGraph();
                handler.postDelayed(this, 10000);
            }
        };

        handler.postDelayed(r, 10000);
        linechart.invalidate();
    }

    public void populateGraph()
    {
        Call<List<CurrencyAPI>> call = fetchCurrencyList();

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
                    entries.add(new Entry(i, Float.parseFloat(currencyList.get(currencyList.size() - 1 - i).getPrice())));
                    labels.add(currencyList.get(i).getDate());
                }
                lastTid = currencyList.get(0).getTid();

                LineDataSet lineSet1 = new LineDataSet(entries, "Prices");
                lineSet1.setDrawCircles(false);
                lineSet1.setColor(Color.BLUE);

                ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();
                lineDataSets.add(lineSet1);

                LineData data = new LineData(lineDataSets);
                data.setValueTextColor(Color.WHITE);
                linechart.setData(data);

                // get the legend (only possible after setting data)
                Legend l = linechart.getLegend();

                // modify the legend ...
                l.setForm(Legend.LegendForm.LINE);
                l.setTextColor(Color.WHITE);

                XAxis xl = linechart.getXAxis();

                xl.setTextColor(Color.WHITE);
                xl.setDrawGridLines(false);
                xl.setAvoidFirstLastClipping(true);
                xl.setEnabled(true);

                YAxis leftAxis = linechart.getAxisLeft();

                leftAxis.setTextColor(Color.WHITE);
                //leftAxis.setAxisMaximum(4400f);
                //leftAxis.setAxisMinimum(4300f);
                leftAxis.setDrawGridLines(true);

                YAxis rightAxis = linechart.getAxisRight();
                rightAxis.setEnabled(false);

                linechart.getAxisRight().setAxisMinValue(lineSet1.getYMin());
                linechart.getAxisRight().setAxisMaxValue(lineSet1.getYMax());
                linechart.getAxisRight().setStartAtZero(false);

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

    public void updateGraph()
    {
        Call<List<CurrencyAPI>> call = fetchCurrencyList();

        // Execute call asynchronously.  Get a positive or negative callback
        call.enqueue(new Callback<List<CurrencyAPI>>() {
            @Override
            public void onResponse(Call<List<CurrencyAPI>> call, Response<List<CurrencyAPI>> response) {
                // The network call was a success and we got a response
                // TODO: use the repository list and display it

                List<CurrencyAPI> currencyList = response.body();
                currencyList.get(0);


                LineData data = linechart.getData();

                if(data != null)
                {
                    ILineDataSet set = data.getDataSetByIndex(0);
                    if(set == null)
                    {
                        set = createSet();
                        data.addDataSet(set);
                    }

                    if(!currencyList.get(0).getTid().equals(lastTid))
                    {
                        int crawler = 0;
                        for(crawler = 0; crawler < 100; crawler++)
                        {
                            //Log.d("TAG", "Last Tid: " + lastTid);
                            //Log.d("TAG", currencyList.get(crawler).getTid());
                            if(currencyList.get(crawler).getTid().equals(lastTid)) {
                                break;
                            }

                            data.addEntry(new Entry(set.getEntryCount(), Float.parseFloat(currencyList.get(crawler).getPrice())), 0);
                        }


                        data.notifyDataChanged();
                        linechart.notifyDataSetChanged();


                         // let the chart know it's data changed
                        linechart.invalidate(); // refresh

                        // limit the number of visible entries
                        //linechart.setVisibleXRangeMaximum(100);
                        // mChart.setVisibleYRange(30, AxisDependency.LEFT);

                        // move to the latest entry, this will also call invalidate
                        //linechart.moveViewToX(data.getEntryCount());

                        //set new lastTid
                        lastTid = currencyList.get(0).getTid();

                        Toast.makeText(MainActivity.this, "Crawler: " + Integer.toString(crawler) + " New Price: " + currencyList.get(0).getPrice(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<CurrencyAPI>> call, Throwable t) {
                // the network call was a failure
                // TODO: handle error
                Toast.makeText(MainActivity.this, "Error :(", Toast.LENGTH_SHORT).show();
            }
        });
    }

    Call<List<CurrencyAPI>> fetchCurrencyList()
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
        return client.getCurrent();
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(Color.WHITE);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }

    public void moveToBids(View v)
    {
        Intent intent = new Intent(this, BidsAndAsks.class);
        startActivity(intent);
    }
}