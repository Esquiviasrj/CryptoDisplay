package com.example.rj.cryptodisplay;

import com.example.rj.cryptodisplay.model.CurrencyAPI;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by RJ on 9/8/17.
 */

public interface BitStampClient {

    @GET("/api/v2/transactions/btcusd/")
    Call<List<CurrencyAPI>> getCurrent();
}
