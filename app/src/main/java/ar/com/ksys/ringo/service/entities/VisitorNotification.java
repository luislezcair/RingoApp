package ar.com.ksys.ringo.service.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class VisitorNotification implements Parcelable {
    private List<Visitor> visitors;
    private URL pictureUrl;

    public VisitorNotification() { }

    private VisitorNotification(Parcel parcel) {
        visitors = new ArrayList<>();

        parcel.readTypedList(visitors, Visitor.CREATOR);
        String url = parcel.readString();

        try {
            pictureUrl = new URL(url);
        } catch (MalformedURLException e) {
            pictureUrl = null;
        }
    }

    public List<Visitor> getVisitors() {
        return visitors;
    }

    public void setVisitors(List<Visitor> visitors) {
        this.visitors = visitors;
    }

    public URL getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(URL pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(visitors);
        parcel.writeString(pictureUrl.toString());
    }

    public static final Creator<VisitorNotification> CREATOR = new Creator<VisitorNotification>() {
        @Override
        public VisitorNotification createFromParcel(Parcel parcel) {
            return new VisitorNotification(parcel);
        }

        @Override
        public VisitorNotification[] newArray(int size) {
            return new VisitorNotification[size];
        }
    };
}
