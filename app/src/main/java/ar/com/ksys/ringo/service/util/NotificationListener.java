package ar.com.ksys.ringo.service.util;

/**
 * This class is the same as Runnable but the calling object can pass
 * additional data as a parameter to the onNotificationReceived() method
 * @param <DataType> Type of the object to be passed to the onNotificationReceived method
 */
public abstract class NotificationListener<DataType> {
    public abstract void onNotificationReceived(DataType data);
}
