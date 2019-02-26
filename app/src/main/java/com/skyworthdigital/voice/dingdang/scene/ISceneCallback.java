package com.skyworthdigital.voice.dingdang.scene;



public interface ISceneCallback {
    void onSceneCheckedOver(boolean matched);
    void onSceneEmpty();
    void onSceneRegisted(String scene);
    void onSearchPageRegisted();
}
