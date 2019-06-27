package com.skyworthdigital.voice.tencent_module;

import com.skyworthdigital.voice.UserGuideStrings;
import com.skyworthdigital.voice.beesearch.BeeSearchParams;
import com.skyworthdigital.voice.guide.AbsGuideAgent;
import com.skyworthdigital.voice.guide.GuideTip;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ives 2019/6/14
 */
public class TxGuideAgent extends AbsGuideAgent {

    private ArrayList<String> mSearchGuide = null;

    public static AbsGuideAgent getInstance(){
        if(mInstance[1]==null){
            synchronized (TxGuideAgent.class){
                if(mInstance[1]==null){
                    mInstance[1] = new TxGuideAgent();
                }
            }
        }
        return mInstance[1];
    }

    @Override
    public void resetSearchGuide(ArrayList<String> tips) {
        mSearchGuide = tips;
    }

    @Override
    public String getGuidetips(int type) {
        ArrayList<String> userGuide = new ArrayList<>();
        try {
            if (BeeSearchParams.getInstance().isInSearchPage() && mSearchGuide != null) {
                userGuide.addAll(mSearchGuide);
                return flatTipsArray(userGuide);
            }
            switch (type) {
                case GuideTip.PAGE_SEARCH:
                    if (mSearchGuide != null) {
                        userGuide.addAll(mSearchGuide);
                    } else {
                        userGuide.addAll(UserGuideStrings.generateUserGuide(UserGuideStrings.TYPE_SEARCH));
                    }
                    break;
                case GuideTip.PAGE_MEDIA:
                    //mSearchGuide = null;
                    userGuide.addAll(UserGuideStrings.generateUserGuide(UserGuideStrings.TYPE_MEDIA_CONTROL));
                    break;
                case GuideTip.PAGE_AUDIO:
                    mSearchGuide = null;
                    userGuide.addAll(UserGuideStrings.generateUserGuide(UserGuideStrings.TYPE_AUDIO));
                    break;
                case GuideTip.PAGE_TVLIVE:
                    mSearchGuide = null;
                    userGuide.addAll(UserGuideStrings.generateUserGuide(UserGuideStrings.TYPE_LIVE));
                    break;
                case GuideTip.PAGE_MUSIC:
                    mSearchGuide = null;
                    userGuide.addAll(UserGuideStrings.generateUserGuide(UserGuideStrings.TYPE_MUSIC));
                    break;
                case GuideTip.PAGE_HOME:
                default:
                    userGuide.addAll(UserGuideStrings.generateUserGuide(UserGuideStrings.TYPE_HOME));
                    break;
            }
        } catch (Exception e) {
            userGuide.addAll(UserGuideStrings.generateUserGuide(UserGuideStrings.TYPE_HOME));
            return flatTipsArray(userGuide);
        }
        return flatTipsArray(userGuide);
    }

    @Override
    public boolean isDialogShowing() {
        return TxController.getInstance().isAsrDialogShowing();
    }

    @Override
    public void dismissDialog() {
        TxController.getInstance().getAsrDialogControler().dialogDismiss(0);
    }

    private String flatTipsArray(List<String> guide){
        StringBuilder sb = new StringBuilder();
        sb.append("你可以说：");
        for (String item : guide) {
            sb.append(item);
            sb.append(" ");
        }
        return sb.toString();
    }
}
