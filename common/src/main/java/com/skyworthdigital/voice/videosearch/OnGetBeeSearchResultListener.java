package com.skyworthdigital.voice.videosearch;


import com.skyworthdigital.voice.beesearch.BeeSearchVideoResult;

/**
 * Created by SDT03046 on 2018/6/6.
 */

public interface OnGetBeeSearchResultListener {
    void getSearchVideoResult(BeeSearchVideoResult result, String keyword);

    void getSearchFailed();
}
