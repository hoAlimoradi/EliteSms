package com.alimoradi.smsmms.com.android.mms.transaction;

/**
 * An interface for observing the state of a Transaction.
 */
public interface Observer {

    /**
     * Update the state of the observable.
     *
     * @param observable An observable object.
     */
    void update(Observable observable);

}
