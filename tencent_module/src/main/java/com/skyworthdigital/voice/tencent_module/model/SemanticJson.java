package com.skyworthdigital.voice.tencent_module.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by SDT03046 on 2018/7/24.
 */

public class SemanticJson implements Serializable {

    public static final long serialVersionUID = 1L;

    @SerializedName("semantic")
    public Semantic mSemantic;

    @SerializedName("candidate_semantic")
    public List<Semantic> mCandidate_semantic;

    @SerializedName("status")
    public Status mStatus;

    public class Status implements Serializable {

        public static final long serialVersionUID = 1L;

        @SerializedName("code")
        public int mCode;


        @SerializedName("msg")
        public String msg;
    }
}
