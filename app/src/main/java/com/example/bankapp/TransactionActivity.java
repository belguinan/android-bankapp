package com.example.bankapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TransactionActivity extends AppCompatActivity {
    private static final String TAG = "TransactionActivity";

    ListView trans;
    DBHelper dbHelper;
    TransactionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_transaction);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DBHelper(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        trans = findViewById(R.id.transactions);
        dbHelper = new DBHelper(this);

        // Check if database already has transactions
        int count = dbHelper.getTransactionsCount();
        if (count == 0) {
            Transaction tr1 = new Transaction(R.drawable.money_out, "ATM Withdrawal", "295", "12/12/21", "97876534", "R1234567", 10000);
            Transaction tr2 = new Transaction(R.drawable.money_out, "Utility Bill Payment", "455", "13/12/21", "97876534", "R1234568", 14000);
            Transaction tr3 = new Transaction(R.drawable.money_out, "Rent Transfer", "2450", "22/12/21", "97876534", "R1234569", 20000);
            Transaction tr4 = new Transaction(R.drawable.money_in, "Salary Deposit", "2150", "29/12/21", "97876534", "R1234560", 17000);
            Transaction tr5 = new Transaction(R.drawable.money_out, "Grocery Shopping", "695", "01/12/21", "97876534", "R1234564", 10900);
            Transaction tr6 = new Transaction(R.drawable.money_out, "Internet Service", "500", "02/12/21", "97876534", "R1234564", 15900);
            Transaction tr7 = new Transaction(R.drawable.money_out, "Bank Transfer", "1250", "05/01/22", "97876534", "R1234570", 17150);
            Transaction tr8 = new Transaction(R.drawable.money_in, "Salary Deposit", "8500", "15/01/22", "97876534", "R1234571", 25650);
            Transaction tr9 = new Transaction(R.drawable.money_out, "Utility Payment", "350", "18/01/22", "97876534", "R1234572", 25300);
            Transaction tr10 = new Transaction(R.drawable.money_out, "ATM Withdrawal", "1000", "20/01/22", "97876534", "R1234573", 24300);
            Transaction tr11 = new Transaction(R.drawable.money_out, "Credit Card Payment", "2700", "25/01/22", "97876534", "R1234574", 21600);
            Transaction tr12 = new Transaction(R.drawable.money_out, "Grocery Purchase", "450", "28/01/22", "97876534", "R1234575", 21150);
            Transaction tr13 = new Transaction(R.drawable.money_out, "Insurance Premium", "1200", "01/02/22", "97876534", "R1234576", 19950);
            Transaction tr14 = new Transaction(R.drawable.money_out, "Restaurant Payment", "320", "05/02/22", "97876534", "R1234577", 19630);
            Transaction tr15 = new Transaction(R.drawable.money_out, "Online Shopping", "850", "10/02/22", "97876534", "R1234578", 18780);
            Transaction tr16 = new Transaction(R.drawable.money_out, "IAM Payment", "500", "11/02/22", "97876534", "R1234579", 18280);  // Changed reference
            Transaction tr17 = new Transaction(R.drawable.money_out, "* ONLINE TRANSACTION", "200", "13/02/22", "97876534", "R1234580", 18080);  // Changed reference

            dbHelper.addTransaction(tr1);
            dbHelper.addTransaction(tr2);
            dbHelper.addTransaction(tr3);
            dbHelper.addTransaction(tr4);
            dbHelper.addTransaction(tr5);
            dbHelper.addTransaction(tr6);
            dbHelper.addTransaction(tr7);
            dbHelper.addTransaction(tr8);
            dbHelper.addTransaction(tr9);
            dbHelper.addTransaction(tr10);
            dbHelper.addTransaction(tr11);
            dbHelper.addTransaction(tr12);
            dbHelper.addTransaction(tr13);
            dbHelper.addTransaction(tr14);
            dbHelper.addTransaction(tr15);
            dbHelper.addTransaction(tr16);
            dbHelper.addTransaction(tr17);
        }

        Cursor cursor = dbHelper.getAllTransactionsCursor();
        adapter = new TransactionAdapter(this, cursor, 0);

        trans.setFocusable(true);
        trans.setAdapter(adapter);
        trans.setEnabled(true);
        trans.setItemsCanFocus(false);

        trans.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) adapter.getItem(position);
                int refIndex = cursor.getColumnIndex("reference");

                if (refIndex != -1) {
                    String reference = cursor.getString(refIndex);
                    Transaction selectedTransaction = dbHelper.getTransaction(reference);

                    if (selectedTransaction != null) {
                        Intent intent = new Intent(getApplicationContext(), TransactionDetailsActivity.class);
                        intent.putExtra("transactionObject", selectedTransaction);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (adapter != null && adapter.getCursor() != null) {
                adapter.getCursor().close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error closing cursor: " + e.getMessage());
        }
    }
}