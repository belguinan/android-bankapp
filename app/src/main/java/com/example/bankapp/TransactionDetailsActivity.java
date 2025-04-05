package com.example.bankapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TransactionDetailsActivity extends AppCompatActivity {

    TextView labelText, priceText, dateText, accountText, referenceText, balanceText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_transaction_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        labelText = findViewById(R.id.detail_label);
        priceText = findViewById(R.id.detail_price);
        dateText = findViewById(R.id.detail_date);
        accountText = findViewById(R.id.detail_account);
        referenceText = findViewById(R.id.detail_reference);
        balanceText = findViewById(R.id.detail_balance);

        Transaction transaction = getIntent().getParcelableExtra("transactionObject");

        if (transaction != null) {
            labelText.setText(transaction.getLabel());
            priceText.setText(transaction.getPrice() + " DH");
            dateText.setText(transaction.getDate());
            accountText.setText(transaction.getNumCompte());
            referenceText.setText(transaction.getNumRef());
            balanceText.setText(String.valueOf(transaction.getSolde()) + " DH");
        }
    }
}