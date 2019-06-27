package com.skyworthdigital.voice.baidu_module;

import android.content.Context;

import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.baidu_module.voicemode.VoiceModeAdapter;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.guide.AbsGuideAgent;
import com.skyworthdigital.voice.guide.GuideTip;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ives 2019/6/14
 */
public class BdGuideAgent extends AbsGuideAgent {
    private String TAG = BdGuideAgent.class.getSimpleName();

    public static AbsGuideAgent getInstance(){
        if(mInstance[0]==null){
            synchronized (BdGuideAgent.class){
                if(mInstance[0]==null){
                    mInstance[0] = new BdGuideAgent();
                }
            }
        }
        return mInstance[0];
    }

    @Override
    public void resetSearchGuide(ArrayList<String> tips) {
        // do nothing
    }

    @Override
    public String getGuidetips(int type) {
        Context ctx = VoiceApp.getInstance();
        String TIPS_DEFAULT = ctx.getResources().getString(R.string.str_tip_default);
        try {
            StringBuilder sb = new StringBuilder();
            String[] welcomeArrays;
            switch (type) {
                case GuideTip.PAGE_SEARCH:
                    welcomeArrays = ctx.getResources().getStringArray(R.array.tips_search);
                    break;
                case GuideTip.PAGE_MEDIA:
                    welcomeArrays = ctx.getResources().getStringArray(R.array.tips_mediacontrol);
                    break;
                case GuideTip.PAGE_AUDIO:
                    welcomeArrays = ctx.getResources().getStringArray(R.array.tips_audio);
                    break;
                case GuideTip.PAGE_TVLIVE:
                    welcomeArrays = ctx.getResources().getStringArray(R.array.tips_tvlive);
                    break;
                case GuideTip.PAGE_MUSIC:
                    welcomeArrays = ctx.getResources().getStringArray(R.array.tips_music);
                    break;
                case GuideTip.PAGE_HOME:
                default:
                    if (VoiceModeAdapter.isAudioBox()) {
                        welcomeArrays = ctx.getResources().getStringArray(R.array.tips_home_audiobox);
                    } else {
                        welcomeArrays = ctx.getResources().getStringArray(R.array.tips_home_pai);
                    }
                    break;
            }

            List<Integer> queue = new ArrayList<>();

            for (int i = 0; i < welcomeArrays.length; i++) {
                queue.add(i, i);
            }
            int size = queue.size() - 1;
            SecureRandom secureRandom = new SecureRandom();

            int id = secureRandom.nextInt(size);//(int) (Math.random() * size);//随机产生一个index索引
            MLog.d(TAG, "id=" + id);
            queue.remove(id);
            sb.append(welcomeArrays[id]);
            sb.append(" | ");
            id = secureRandom.nextInt(size - 1);//(int) (Math.random() * (size - 1));//随机产生一个index索引
            MLog.d(TAG, "id2=" + queue.get(id));

            sb.append(welcomeArrays[queue.get(id)]);
            queue.remove(id);
            sb.append(" | ");
            id = secureRandom.nextInt(size - 2);//(int) (Math.random() * (size - 2));//随机产生一个index索引
            MLog.d(TAG, "id3=" + id);
            sb.append(welcomeArrays[queue.get(id)]);
            MLog.d(TAG, "tips=" + sb.toString());
            return sb.toString();
        } catch (Exception e) {
            return TIPS_DEFAULT;
        }
    }

    @Override
    public boolean isDialogShowing() {
        return BdController.getInstance().isDialogShow();
    }

    @Override
    public void dismissDialog() {
        BdController.getInstance().dismissDialog();
    }
}
