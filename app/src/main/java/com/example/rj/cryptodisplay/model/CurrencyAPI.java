package com.example.rj.cryptodisplay.model;

import java.io.Serializable;
import java.util.List;

import static android.R.attr.name;

/**
 * Created by RJ on 9/8/17.
 */
// date": "1504926797", "tid": "20682266", "price": "4273.14", "type": "1", "amount": "0.02000000"
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CurrencyAPI {

    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("tid")
    @Expose
    private String tid;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("amount")
    @Expose
    private String amount;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

}