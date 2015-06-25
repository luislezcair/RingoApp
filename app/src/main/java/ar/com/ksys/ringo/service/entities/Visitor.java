package ar.com.ksys.ringo.service.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Visitor implements Parcelable {
    private String name;

    public Visitor() { }

    private Visitor(Parcel source) {
        name = source.readString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
    }

    public static final Creator<Visitor> CREATOR = new Creator<Visitor>() {
        @Override
        public Visitor createFromParcel(Parcel parcel) {
            return new Visitor(parcel);
        }

        @Override
        public Visitor[] newArray(int i) {
            return new Visitor[i];
        }
    };
}
