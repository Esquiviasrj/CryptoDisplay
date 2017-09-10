package com.example.rj.cryptodisplay;

/**
 * Created by anon on 9/9/2017.
 */

public class AskItem {

    private String Bid, Amount, Value;

    public AskItem(String bid, String amount, String value) {
        Bid = bid;
        Amount = amount;
        Value = value;
    }

    public String getBid() {
        return Bid;
    }

    public String getAmount() {
        return Amount;
    }

    public String getValue() {
        return Value;
    }

    public void setBid(String bid) {
        Bid = bid;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public void setValue(String value) {
        Value = value;
    }
}
