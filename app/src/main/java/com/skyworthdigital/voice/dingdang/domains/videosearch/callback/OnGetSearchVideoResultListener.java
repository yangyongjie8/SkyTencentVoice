package com.skyworthdigital.voice.dingdang.domains.videosearch.callback;


import com.skyworthdigital.voice.dingdang.domains.videoplay.model.SearchVideoResult;

public interface OnGetSearchVideoResultListener {

    void getSearchVideoResult(SearchVideoResult result, String keyword);
    void getSearchFailed();
}
