package com.example.rj.cryptodisplay;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by anon on 9/9/2017.
 */

public class BidAdapter extends RecyclerView.Adapter<BidAdapter.ViewHolder> {

    private List<BidItem> listItems;
    private Context context;

    public BidAdapter(List<BidItem> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.biditem, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BidItem biditem = listItems.get(position);

        holder.Bid.setText(biditem.getBid());
        holder.Amount.setText(biditem.getAmount());
        holder.Value.setText(biditem.getValue());
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView Bid;
        public TextView Amount;
        public TextView Value;

        public ViewHolder(View itemView) {
            super(itemView);

            Bid = (TextView) itemView.findViewById(R.id.Bid);
            Amount = (TextView) itemView.findViewById(R.id.Amount);
            Value = (TextView) itemView.findViewById(R.id.Value);
        }
    }
}
