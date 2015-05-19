package ar.com.ksys.ringo.service.util;

/**
 * This class is the same as Runnable but the calling object can pass
 * additional data as a parameter to the run() method
 * @param <DataType> Type of the object to be passed to the run method
 */
public abstract class DataRunnable<DataType> {
    public abstract void run(DataType data);
}
