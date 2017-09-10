package com.example.rj.cryptodisplay;

import com.example.rj.cryptodisplay.model.BidData;
import com.example.rj.cryptodisplay.model.CurrencyAPI;
import com.example.rj.cryptodisplay.model.Hourly;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by RJ on 9/8/17.
 */

public interface BitStampClient {

    @GET("/api/v2/order_book/btcusd/")
    Call<BidData> getBids();

    @GET("/api/v2/transactions/btcusd/")
    Call<List<CurrencyAPI>> getCurrent();

    @GET("/api/v2/ticker_hour/btcusd/")
    Call<Hourly> getHourly();

    //https://www.bitstamp.net/api/v2/order_book/btcusd/


}
