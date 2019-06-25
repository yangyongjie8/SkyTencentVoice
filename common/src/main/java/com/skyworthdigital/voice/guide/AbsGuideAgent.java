package com.skyworthdigital.voice.guide;

import com.skyworthdigital.voice.VoiceApp;

import java.util.ArrayList;

/**
 * Created by Ives 2019/6/14
 */
public abstract class AbsGuideAgent {
    protected volatile static AbsGuideAgent[] mInstance = new AbsGuideAgent[2];

    public static AbsGuideAgent getInstance(){
        if(VoiceApp.isDuer){
            return mInstance[0];
        }else {
            return mInstance[1];
        }
    }

    public abstract void resetSearchGuide(ArrayList<String> tips);

    public abstract String getGuidetips(int type);

    public abstract boolean isDialogShowing();// 这个本应该放在一个抽象的dialog里做，但dialog目前还不好抽象
    public abstract void dismissDialog();//
}
