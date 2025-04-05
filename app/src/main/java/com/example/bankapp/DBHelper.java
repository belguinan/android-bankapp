package com.example.bankapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String TAG = "DBHelper";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "bankAppDatabase";

    private static final String TABLE_TRANSACTIONS = "transactions";
    private static final String KEY_ID = "_id";
    private static final String ICON = "icon";
    private static final String LABEL = "label";
    private static final String PRICE = "price";
    private static final String DATE = "date";
    private static final String ACCOUNT_NUMBER = "accountnumber";
    private static final String REFERENCE = "reference";
    private static final String BALANCE = "balance";

    private static final String TABLE_AGENCIES = "agencies";
    private static final String AGENCY_ID = "_id";
    private static final String AGENCY_NAME = "name";
    private static final String AGENCY_ADDRESS = "address";
    private static final String AGENCY_PHONE = "phone";
    private static final String AGENCY_EMAIL = "email";
    private static final String AGENCY_LATITUDE = "latitude";
    private static final String AGENCY_LONGITUDE = "longitude";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String CREATE_TRANSACTIONS_TABLE = "CREATE TABLE " + TABLE_TRANSACTIONS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ICON + " INTEGER,"
                + LABEL + " TEXT,"
                + PRICE + " TEXT,"
                + DATE + " TEXT,"
                + ACCOUNT_NUMBER + " TEXT,"
                + REFERENCE + " TEXT,"
                + BALANCE + " REAL" + ")";
            db.execSQL(CREATE_TRANSACTIONS_TABLE);

            String CREATE_AGENCIES_TABLE = "CREATE TABLE " + TABLE_AGENCIES + "("
                + AGENCY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + AGENCY_NAME + " TEXT,"
                + AGENCY_ADDRESS + " TEXT,"
                + AGENCY_PHONE + " TEXT,"
                + AGENCY_EMAIL + " TEXT,"
                + AGENCY_LATITUDE + " REAL,"
                + AGENCY_LONGITUDE + " REAL" + ")";
            db.execSQL(CREATE_AGENCIES_TABLE);
        } catch (Exception e) {
            Log.e(TAG, "Error creating database: " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_AGENCIES);
            onCreate(db);
        } catch (Exception e) {
            Log.e(TAG, "Error upgrading database: " + e.getMessage());
        }
    }

    public long addTransaction(Transaction trans) {
        long id = -1;
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(ICON, trans.getIcon());
            values.put(LABEL, trans.getLabel());
            values.put(PRICE, trans.getPrice());
            values.put(DATE, trans.getDate());
            values.put(ACCOUNT_NUMBER, trans.getNumCompte());
            values.put(REFERENCE, trans.getNumRef());
            values.put(BALANCE, trans.getSolde());

            id = db.insert(TABLE_TRANSACTIONS, null, values);
        } catch (Exception e) {
            Log.e(TAG, "Error adding transaction: " + e.getMessage());
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return id;
    }

    public Transaction getTransaction(String ref) {
        Transaction transaction = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = this.getReadableDatabase();
            cursor = db.query(TABLE_TRANSACTIONS, null,
                REFERENCE + "=?", new String[] { ref },
                null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int iconIdx = cursor.getColumnIndex(ICON);
                int labelIdx = cursor.getColumnIndex(LABEL);
                int priceIdx = cursor.getColumnIndex(PRICE);
                int dateIdx = cursor.getColumnIndex(DATE);
                int accountIdx = cursor.getColumnIndex(ACCOUNT_NUMBER);
                int refIdx = cursor.getColumnIndex(REFERENCE);
                int balanceIdx = cursor.getColumnIndex(BALANCE);

                transaction = new Transaction(
                    cursor.getInt(iconIdx),
                    cursor.getString(labelIdx),
                    cursor.getString(priceIdx),
                    cursor.getString(dateIdx),
                    cursor.getString(accountIdx),
                    cursor.getString(refIdx),
                    cursor.getDouble(balanceIdx));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting transaction: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }

        return transaction;
    }

    public Cursor getAllTransactionsCursor() {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = this.getReadableDatabase();
            cursor = db.rawQuery("SELECT rowid as _id, * FROM " + TABLE_TRANSACTIONS, null);
        } catch (Exception e) {
            Log.e(TAG, "Error getting all transactions: " + e.getMessage());
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }

        return cursor;
    }

    public List<Transaction> getAllTransactions() {
        List<Transaction> transactionList = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = this.getReadableDatabase();
            cursor = db.query(TABLE_TRANSACTIONS, null, null, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int iconIdx = cursor.getColumnIndex(ICON);
                    int labelIdx = cursor.getColumnIndex(LABEL);
                    int priceIdx = cursor.getColumnIndex(PRICE);
                    int dateIdx = cursor.getColumnIndex(DATE);
                    int accountIdx = cursor.getColumnIndex(ACCOUNT_NUMBER);
                    int refIdx = cursor.getColumnIndex(REFERENCE);
                    int balanceIdx = cursor.getColumnIndex(BALANCE);

                    Transaction transaction = new Transaction(
                        cursor.getInt(iconIdx),
                        cursor.getString(labelIdx),
                        cursor.getString(priceIdx),
                        cursor.getString(dateIdx),
                        cursor.getString(accountIdx),
                        cursor.getString(refIdx),
                        cursor.getDouble(balanceIdx));

                    transactionList.add(transaction);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting all transactions: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }

        return transactionList;
    }

    public int getTransactionsCount() {
        int count = 0;
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = this.getReadableDatabase();
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_TRANSACTIONS, null);

            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error counting transactions: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }

        return count;
    }

    public void clearAllTransactions() {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            db.delete(TABLE_TRANSACTIONS, null, null);
        } catch (Exception e) {
            Log.e(TAG, "Error clearing transactions: " + e.getMessage());
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    public long addAgency(Agency agency) {
        long id = -1;
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(AGENCY_NAME, agency.getName());
            values.put(AGENCY_ADDRESS, agency.getAddress());
            values.put(AGENCY_PHONE, agency.getPhone());
            values.put(AGENCY_EMAIL, agency.getEmail());
            values.put(AGENCY_LATITUDE, agency.getLatitude());
            values.put(AGENCY_LONGITUDE, agency.getLongitude());

            id = db.insert(TABLE_AGENCIES, null, values);
        } catch (Exception e) {
            Log.e(TAG, "Error adding agency: " + e.getMessage());
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return id;
    }

    public List<Agency> getAllAgencies() {
        List<Agency> agencies = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = this.getReadableDatabase();
            cursor = db.query(TABLE_AGENCIES, null, null, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Agency agency = new Agency();
                    agency.setId(cursor.getLong(cursor.getColumnIndex(AGENCY_ID)));
                    agency.setName(cursor.getString(cursor.getColumnIndex(AGENCY_NAME)));
                    agency.setAddress(cursor.getString(cursor.getColumnIndex(AGENCY_ADDRESS)));
                    agency.setPhone(cursor.getString(cursor.getColumnIndex(AGENCY_PHONE)));
                    agency.setEmail(cursor.getString(cursor.getColumnIndex(AGENCY_EMAIL)));
                    agency.setLatitude(cursor.getDouble(cursor.getColumnIndex(AGENCY_LATITUDE)));
                    agency.setLongitude(cursor.getDouble(cursor.getColumnIndex(AGENCY_LONGITUDE)));
                    agencies.add(agency);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting agencies: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return agencies;
    }

    public int getAgenciesCount() {
        int count = 0;
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = this.getReadableDatabase();
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_AGENCIES, null);

            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error counting agencies: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }

        return count;
    }

    public void clearAllAgencies() {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            db.delete(TABLE_AGENCIES, null, null);
        } catch (Exception e) {
            Log.e(TAG, "Error clearing agencies: " + e.getMessage());
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }
}