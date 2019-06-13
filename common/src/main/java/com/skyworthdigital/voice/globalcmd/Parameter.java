package com.skyworthdigital.voice.globalcmd;


import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Parameter implements Parcelable, Serializable {

    private static final long serialVersionUID = 1L;
    protected String key;
    protected String value;

    public Parameter() {}

    public Parameter(String key, String value) {
        this.key = key;
        this.value = value;
    }

    protected Parameter(Parcel in) {
        key = in.readString();
        value = in.readString();
    }

    public static final Creator<Parameter> CREATOR = new Creator<Parameter>() {
        @Override
        public Parameter createFromParcel(Parcel in) {
            return new Parameter(in);
        }

        @Override
        public Parameter[] newArray(int size) {
            return new Parameter[size];
        }
    };

    /**
     * Gets the value of the key property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the value of the key property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setKey(String value) {
        this.key = value;
    }

    /**
     * Gets the value of the value property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(value);
    }
    @Override
    public String toString() {
        return "key:" + key + "|value:" + value;
    }
}

