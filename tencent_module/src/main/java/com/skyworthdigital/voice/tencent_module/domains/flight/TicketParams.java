package com.skyworthdigital.voice.tencent_module.domains.flight;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by SDT03046 on 2018/8/15.
 */

public class TicketParams implements Serializable {
    public static final long serialVersionUID = 1L;

    @SerializedName("strDate")
    public String mDate;

    @SerializedName("strFrom")
    public String mFrom;

    @SerializedName("strTo")
    public String mTo;
}
