package com.example.rj.cryptodisplay.model;

import java.io.Serializable;
import java.util.List;

import static android.R.attr.name;

/**
 * Created by RJ on 9/9/17.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BidData {

    @SerializedName("timestamp")
    @Expose
    private String timestamp;
    @SerializedName("bids")
    @Expose
    private List<List<String>> bids = null;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public List<List<String>> getBids() {
        return bids;
    }

    public void setBids(List<List<String>> bids) {
        this.bids = bids;
    }

}