package com.skyworthdigital.voice.videosearch.callback;


import com.skyworthdigital.voice.videoplay.SearchVideoResult;

public interface OnGetSearchVideoResultListener {

    void getSearchVideoResult(SearchVideoResult result, String keyword);
    void getSearchFailed();
}
