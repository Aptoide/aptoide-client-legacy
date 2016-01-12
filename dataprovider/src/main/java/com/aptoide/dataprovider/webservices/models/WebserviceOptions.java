package com.aptoide.dataprovider.webservices.models;

/**
 * Created by rmateus on 08-07-2014.
 */
public class WebserviceOptions {
    public final static String WebServicesLink = "/webservices.aptoide.com/webservices/";

    String key;
    String value;

    public WebserviceOptions(String key, String value) {
        this.value = value;
        this.key = key;
    }

    /**
     * Returns a string containing a concise, human-readable description of this
     * object. Subclasses are encouraged to override this method and provide an
     * implementation that takes into account the object's type and data. The
     * default implementation is equivalent to the following expression:
     * <pre>
     *   getClass().getName() + '@' + Integer.toHexString(hashCode())</pre>
     * <p>See <a href="{@docRoot}reference/java/lang/Object.html#writing_toString">Writing a useful
     * {@code toString} method</a>
     * if you intend implementing your own {@code toString} method.
     *
     * @return a printable representation of this object.
     */

    @Override
    public String toString() {
        return key + "=" + value;    //To change body of overridden methods use File | Settings | File Templates.
    }
}