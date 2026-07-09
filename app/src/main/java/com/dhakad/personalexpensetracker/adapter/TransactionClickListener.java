package com.dhakad.personalexpensetracker.adapter;

import com.dhakad.personalexpensetracker.model.Transaction;

/**
 * Listener interface for transaction item click events.
 */
public interface TransactionClickListener {

    /**
     * Called when user taps a transaction.
     */
    void onTransactionClick(Transaction transaction);

    /**
     * Called when user long presses a transaction.
     */
    void onTransactionLongClick(Transaction transaction);

}