package com.dhakad.personalexpensetracker.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dhakad.personalexpensetracker.R;
import com.dhakad.personalexpensetracker.model.Transaction;
import com.dhakad.personalexpensetracker.utils.Constants;

import java.util.ArrayList;

/**
 * RecyclerView Adapter for displaying transactions.
 */
public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private final ArrayList<Transaction> transactionList;
    private final TransactionClickListener listener;

    public TransactionAdapter(ArrayList<Transaction> transactionList,
                              TransactionClickListener listener) {

        this.transactionList = transactionList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                    int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction,
                        parent,
                        false);

        return new TransactionViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder,
                                 int position) {

        Transaction transaction = transactionList.get(position);

        holder.tvTitle.setText(transaction.getTitle());

        holder.tvCategory.setText(transaction.getCategory());

        holder.tvDate.setText(transaction.getDate());

        if (transaction.getType().equals(Constants.TYPE_INCOME)) {

            holder.tvAmount.setText("+ ₹" + transaction.getAmount());

            holder.tvAmount.setTextColor(Color.parseColor("#4CAF50"));

        } else {

            holder.tvAmount.setText("- ₹" + transaction.getAmount());

            holder.tvAmount.setTextColor(Color.parseColor("#F44336"));

        }

        holder.itemView.setOnClickListener(v ->
                listener.onTransactionClick(transaction));

        holder.itemView.setOnLongClickListener(v -> {

            listener.onTransactionLongClick(transaction);

            return true;

        });

    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    /**
     * Replace adapter data and refresh RecyclerView.
     */
    public void updateList(ArrayList<Transaction> newList) {
        transactionList.clear();
        transactionList.addAll(newList);
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class for transaction item.
     */
    static class TransactionViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        TextView tvCategory;
        TextView tvDate;
        TextView tvAmount;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvAmount = itemView.findViewById(R.id.tvAmount);
        }
    }

}