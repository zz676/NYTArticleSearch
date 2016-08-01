package com.codepath.nytarticlesearch;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sam on 7/31/16.
 */
public class Filters implements Parcelable {
    private String beginDate;
    private String sorter;

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getSorter() {
        return sorter;
    }

    public void setSorter(String sorter) {
        this.sorter = sorter;
    }

    public String getDesks() {
        return desks;
    }

    public void setDesks(String desks) {
        this.desks = desks;
    }

    private String desks;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.beginDate);
        dest.writeString(this.sorter);
        dest.writeString(this.desks);
    }

    public Filters() {
    }

    protected Filters(Parcel in) {
        this.beginDate = in.readString();
        this.sorter = in.readString();
        this.desks = in.readString();
    }

    public static final Parcelable.Creator<Filters> CREATOR = new Parcelable.Creator<Filters>() {
        @Override
        public Filters createFromParcel(Parcel source) {
            return new Filters(source);
        }

        @Override
        public Filters[] newArray(int size) {
            return new Filters[size];
        }
    };
}
