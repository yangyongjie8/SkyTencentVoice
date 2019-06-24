package com.skyworthdigital.voice.guide;

import java.util.ArrayList;

/**
 * Created by Ives 2019/6/14
 */
public abstract class AbsGuideAgent {
    public abstract void resetSearchGuide(ArrayList<String> tips);

    public abstract String getGuidetips(int type);

    public abstract boolean isDialogShowing();// 这个本应该放在一个抽象的dialog里做，但dialog目前还不好抽象
    public abstract void dismissDialog();//
}
