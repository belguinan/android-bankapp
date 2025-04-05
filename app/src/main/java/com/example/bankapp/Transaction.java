package com.example.bankapp;

import android.os.Parcel;
import android.os.Parcelable;

public class Transaction implements Parcelable {
    Integer icon;
    String label;
    String price;
    String date;
    String numCompte;
    String numRef;
    double solde;

    public Transaction() {
    }

    public Transaction(Integer icon, String label, String price, String date, String numCompte, String numRef, double solde) {
        this.icon = icon;
        this.label = label;
        this.price = price;
        this.date = date;
        this.numCompte = numCompte;
        this.numRef = numRef;
        this.solde = solde;
    }

    protected Transaction(Parcel in) {
        if (in.readByte() == 0) {
            icon = null;
        } else {
            icon = in.readInt();
        }
        label = in.readString();
        price = in.readString();
        date = in.readString();
        numCompte = in.readString();
        numRef = in.readString();
        solde = in.readDouble();
    }

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };

    public Integer getIcon() {
        return icon;
    }

    public void setIcon(Integer icon) {
        this.icon = icon;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNumCompte() {
        return numCompte;
    }

    public void setNumCompte(String numCompte) {
        this.numCompte = numCompte;
    }

    public String getNumRef() {
        return numRef;
    }

    public void setNumRef(String numRef) {
        this.numRef = numRef;
    }

    public double getSolde() {
        return solde;
    }

    public void setSolde(double solde) {
        this.solde = solde;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (icon == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(icon);
        }
        dest.writeString(label);
        dest.writeString(price);
        dest.writeString(date);
        dest.writeString(numCompte);
        dest.writeString(numRef);
        dest.writeDouble(solde);
    }
}