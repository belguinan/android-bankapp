package com.example.bankapp;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TransactionAdapter extends CursorAdapter {

    public TransactionAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.view_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        try {
            ImageView iconView = view.findViewById(R.id.icon);
            TextView labelView = view.findViewById(R.id.label);
            TextView priceView = view.findViewById(R.id.price);
            TextView dateView = view.findViewById(R.id.date);

            // Get column indices
            int iconIndex = cursor.getColumnIndex("icon");
            int labelIndex = cursor.getColumnIndex("label");
            int priceIndex = cursor.getColumnIndex("price");
            int dateIndex = cursor.getColumnIndex("date");

            if (iconIndex != -1 && labelIndex != -1 && priceIndex != -1 && dateIndex != -1) {
                int icon = cursor.getInt(iconIndex);
                String label = cursor.getString(labelIndex);
                String price = cursor.getString(priceIndex);
                String date = cursor.getString(dateIndex);

                iconView.setImageResource(icon);
                labelView.setText(label != null ? label : "");
                priceView.setText((price != null ? price : "") + " DH");
                dateView.setText(date != null ? date : "");
            } else {
                Log.e("TransactionAdapter", "Column indices not found in cursor");
            }
        } catch (Exception e) {
            Log.e("TransactionAdapter", "Error binding view: " + e.getMessage());
            e.printStackTrace();
        }
    }
}