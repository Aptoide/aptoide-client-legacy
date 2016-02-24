package com.aptoide.amethyst.openiab.webservices.json;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by rmateus on 21-05-2014.
 */
public class Metadata implements Parcelable {


    public int id;
     public String icon;
     public double price;
     public String currency;
     public double tax_rate;



    public Metadata(){

    }

    public int getId() {
        return id;
    }

    public String getIcon() {
        return icon;
    }

    public double getPrice() {
        return price;
    }

    public String getCurrency() {
        return currency;
    }

    public double getTax_rate() {
        return tax_rate;
    }


    protected Metadata(Parcel in) {
        id = in.readInt();
        icon = in.readString();
        price = in.readDouble();
        currency = in.readString();
        tax_rate = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(icon);
        dest.writeDouble(price);
        dest.writeString(currency);
        dest.writeDouble(tax_rate);
    }

    @SuppressWarnings("unused")
    public static final Creator<Metadata> CREATOR = new Creator<Metadata>() {
        @Override
        public Metadata createFromParcel(Parcel in) {
            return new Metadata(in);
        }

        @Override
        public Metadata[] newArray(int size) {
            return new Metadata[size];
        }
    };
}
