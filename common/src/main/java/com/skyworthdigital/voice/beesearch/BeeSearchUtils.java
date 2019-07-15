package com.skyworthdigital.voice.beesearch;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import com.skyworthdigital.voice.DefaultCmds;
import com.skyworthdigital.voice.guide.GuideTip;
import com.skyworthdigital.voice.UserGuideStrings;
import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.common.AbsTTS;
import com.skyworthdigital.voice.common.IStatus;
import com.skyworthdigital.voice.common.R;
import com.skyworthdigital.voice.common.utils.StringUtils;
import com.skyworthdigital.voice.common.utils.Utils;
import com.skyworthdigital.voice.dingdang.utils.AppUtil;
import com.skyworthdigital.voice.dingdang.utils.GlobalVariable;
import com.skyworthdigital.voice.dingdang.utils.GsonUtils;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.dingdang.utils.VolumeUtils;
import com.skyworthdigital.voice.videoplay.BeeVideoPlayUtils;
import com.skyworthdigital.voice.videoplay.SkyCommonCallback;
import com.skyworthdigital.voice.videoplay.SkyVideoInfo;
import com.skyworthdigital.voice.videoplay.SkyVideoPlayUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Request;

import static com.skyworthdigital.voice.scene.SkySceneService.INTENT_TOPACTIVITY_CALL;


public class BeeSearchUtils {
    private static final String TAG = "BeeSearchUtils";
    private static final String ABNF_PARAM = "abnf";
    private static ArrayList<String> mSpeakTexts = new ArrayList<>();   // 保存用户说的话，最多存SPEAK_TEXTS_MAX_SIZE条
    private static final int SPEAK_TEXTS_MAX_SIZE = 4;
    public static SpeakSameInfo mSpeakSameInfo; // 同音字纠错

    private static String getNluPath(@NonNull String txt, String localcallid, String usrid, String token) {
        StringBuilder sb = new StringBuilder();
        sb.append(BeeSearchParams.ASR_PATH);
        if (VoiceApp.getVoiceApp().mAiType == GlobalVariable.AI_VOICE) {
            sb.append(BeeSearchParams.VOICE_MODEL);
        } else {
            sb.append(BeeSearchParams.IR_MODEL);
        }
        sb.append(BeeSearchParams.GETNLU_PATH);
        sb.append("?txt=");
        sb.append(txt);
        if (!TextUtils.isEmpty(localcallid)) {
            sb.append("&localcallid=");
            sb.append(localcallid);
        }
        if (!TextUtils.isEmpty(usrid)) {
            sb.append("&usrid=");
            sb.append(usrid);
        }
        if (!TextUtils.isEmpty(token)) {
            sb.append("&token=");
            sb.append(token);
        }

        sb.append("&channel=mifeng");
        sb.append("&").append(BeeSearchParams.PARAM_INTERFACE_VER);
        //MLog.d(TAG, "skynlupath:" + sb.toString());
        return sb.toString();
    }

    private static String getSearchPath() {
        StringBuilder sb = new StringBuilder();
        sb.append(BeeSearchParams.ASR_PATH);
        if (VoiceApp.getVoiceApp().mAiType == GlobalVariable.AI_VOICE) {
            sb.append(BeeSearchParams.VOICE_MODEL);
        } else {
            sb.append(BeeSearchParams.IR_MODEL);
        }
        sb.append(BeeSearchParams.DOSEARCH_PATH);
        return sb.toString();
    }

    private static void sendNluRequest(@NonNull String txt, /*String localcallid, String usrid, String token,*/ SkyCommonCallback callback) {
        String token = "voice";
        UUID uuid = UUID.randomUUID();

        BeeSearchParams.getInstance().setLocalcallid(uuid.toString());
        Request request = new Request.Builder()
                .get()
                .url(getNluPath(txt, BeeSearchParams.getInstance().getLocalcallid(), BeeSearchParams.getInstance().getUserid(), token))
                .build();
        VoiceApp.getOkHttpClient().newCall(request).enqueue(callback);
    }

    private static void sendSearchRequest(@NonNull String abnf, String lastreply,/*String localcallid, String usrid, String token,*/ SkyCommonCallback callback) {
        String newUrl = getSearchPath();
        //MLog.d(TAG,"skydosearchpath:" + newUrl);
        Request.Builder builder = new Request.Builder().url(newUrl+"?"+BeeSearchParams.PARAM_INTERFACE_VER);
        FormBody.Builder bodybuilder = new FormBody.Builder();

        bodybuilder.add("abnf", abnf);
        if (lastreply != null) {
            bodybuilder.add("lastreply", lastreply);
        }
        bodybuilder.add("usrid", BeeSearchParams.getInstance().getUserid());
        bodybuilder.add("localcallid", BeeSearchParams.getInstance().getLocalcallid());
        bodybuilder.add("token", "voice");
        bodybuilder.add("channel", "mifeng");

        final Request request = builder.post(bodybuilder.build()).build();
        Call call = VoiceApp.getVoiceApp().getOkHttpClient().newCall(request);
        call.enqueue(callback);
    }


    public static String getVedioListPath() {
        StringBuilder sb = new StringBuilder();
        sb.append(BeeSearchParams.ASR_PATH);
        if (VoiceApp.getVoiceApp().mAiType == GlobalVariable.AI_VOICE) {
            sb.append(BeeSearchParams.VOICE_MODEL);
        } else {
            sb.append(BeeSearchParams.IR_MODEL);
        }
        sb.append(BeeSearchParams.GETMOVIEMETA_PATH);
        return sb.toString();
    }

    public static void doBeeSearch(final Context ctx, final String originSpeech, final String domain) {
        sendNluRequest(originSpeech, new SkyCommonCallback("skynlucallback") {
            @Override
            public void onFail() {
                AbsTTS.getInstance(null).talk(ctx.getResources().getString(R.string.try_again), ctx.getResources().getString(R.string.requestfail_tips));
            }

            @Override
            public void onSuccessed(String ret) {
                successProcess(ctx, ret, domain);
            }
        });
    }

    private static void successProcess(final Context ctx, String ret, String domain) {
        String result = ret;
        final BeeObject skynlu = GsonUtils.parseResult(result, BeeObject.class);
        MLog.d(TAG, "ret:" + result);
        if (skynlu.items.size() <= 0) {
            //MLog.d(TAG, "size <=0");
            return;
        }
        BeeItemObject skyItemObj = skynlu.items.get(0);
        if (skyItemObj == null) {
            return;
        }
        String operationcode = skyItemObj.getOperationCode();
        if (!operationcode.equals(BeeSearchParams.RESULT_CORRECT) && !operationcode.equals(BeeSearchParams.RESULT_CMD)) {
            // 有用户说话
            if (mSpeakTexts.size() >= SPEAK_TEXTS_MAX_SIZE) {
                mSpeakTexts.remove(0);
            }
            mSpeakTexts.add(skyItemObj.getWord());
        }
        switch (operationcode) {
            case BeeSearchParams.RESULT_SEARCH: {
                IStatus.mAsrErrorCnt = 0;
                Log.i(TAG, "code:" + operationcode + " " + skyItemObj.getWord());
                sendSearchRequest(skynlu.abnf, BeeSearchParams.getInstance().getLastReply(), new SkyCommonCallback("SkydoSearch") {
                    @Override
                    public void onFail() {
                        AbsTTS.getInstance(null).talk(ctx.getResources().getString(R.string.try_again), ctx.getResources().getString(R.string.requestfail_tips));
                    }

                    @Override
                    public void onSuccessed(String ret) {
                        beeSearchJump(ctx, ret);
                    }
                });
            }
            mSpeakSameInfo = null;
            IStatus.resetDismissTime();
            break;
            case BeeSearchParams.RESULT_CORRECT: {//纠正逻辑
                IStatus.mAsrErrorCnt = 0;
                { // 纠正逻辑
                    // 判断上几次的说话中是否有这些词
                    boolean contained = false;
                    if (skyItemObj.getSpeaksame() != null && skyItemObj.getSpeaksame().size() > 0) {
                        String text = "";
                        String keyword = "";
                        for (String word : skyItemObj.getSpeaksame()) {
                            MLog.i(TAG, "可以纠正的关键词选项：" + word);
                            //for(int i=speakTexts.size()-1; i>=0; i--){
                            for (int i = 0; i < mSpeakTexts.size(); i++) {
                                text = mSpeakTexts.get(i);
                                MLog.i(TAG, "之前说过的话: " + text);
                                contained = text.contains(word);
                                if (contained) {
                                    break;
                                }
                            }
                            if (contained) {
                                keyword = word;
                                break;
                            }
                        }
                        if (contained) {
                            AbsTTS.getInstance(null).talk(skyItemObj.getTts(), skyItemObj.getOutput() + "\n" + skyItemObj.speaksameToString());

                            mSpeakSameInfo = new SpeakSameInfo(skyItemObj.getSpeaksame(), text, keyword);

                        } else {  // 没有说过这个词
                            AbsTTS.getInstance(null).talk("没有找到同音字", "您没有说过这个关键词哦");
                            mSpeakSameInfo = null;
                        }
                    } else {
                        AbsTTS.getInstance(null).talk(skyItemObj.getTts(), skyItemObj.getOutput());
                        //keyItemObject.replyInfo = null;
                    }
                }

            }
            break;
            case BeeSearchParams.RESULT_CMD: {
                IStatus.mAsrErrorCnt = 0;
                if (skyItemObj.getCmd() != null) {
                    IStatus.resetDismissTime();
                    Log.i(TAG, skyItemObj.getCmd().toString());
                    if (skyItemObj.cmd.cmdid == BeeCommand.OPT_CODE_PLAY_N) {
                        if (convertToNewSearch(ctx, skyItemObj)) {
                            return;
                        }
                    } else if (skyItemObj.cmd.cmdid == BeeCommand.OPT_CODE_VOLUME_100) {
                        VolumeUtils.getInstance(ctx).setVolume(100);
                        return;
                    } else if (skyItemObj.cmd.cmdid == BeeCommand.OPT_CODE_PLAY_LAST) {
                        Intent intent = new Intent(INTENT_TOPACTIVITY_CALL);
                        intent.setPackage(GlobalVariable.VOICE_PACKAGE_NAME);
                        intent.putExtra(DefaultCmds.PREDEFINE, DefaultCmds.PLAY_CMD);
                        String intentName;
                        if (skyItemObj.cmd.args.value > 0) {
                            intentName = DefaultCmds.PLAYER_CMD_NEXT;
                            intent.putExtra(DefaultCmds.VALUE, 1);
                        } else {
                            intentName = DefaultCmds.PLAYER_CMD_PREVIOUS;
                            intent.putExtra(DefaultCmds.VALUE, 1);
                        }
                        intent.putExtra(DefaultCmds.INTENT, intentName);
                        ctx.startService(intent);
                        return;
                    }
                    // 快速指令
                    if (!TextUtils.isEmpty(skyItemObj.cmd.action)) {
                        if (skyItemObj.cmd != null) {
                            if (commandExection(ctx, skyItemObj.getCmd())) {
                                return;
                            }
                        }
                    }

                    if (!playByName(ctx, skyItemObj.getCmd())) {
                        AbsTTS.getInstance(null).talk("", ctx.getResources().getString(R.string.try_note));
                    }
                }
            }
            break;

            case BeeSearchParams.RESULT_UNCERTAIN:
                Log.i(TAG, "RESULT_UNCERTAIN");
                if (skyItemObj.getCmd() != null && TextUtils.equals(skyItemObj.getCmd().getAction(), "playbyname")) {
                    if (!BeeSearchParams.getInstance().isInSearchPage()) {
                        doBeeSearch(ctx, skyItemObj.getCmd().args.name, "");
                        return;
                    }
                    if (playByName(ctx, skyItemObj.getCmd())) {
                        return;
                    }
                }
                if (VoiceApp.getVoiceApp().mAiType == GlobalVariable.AI_VOICE) {
                    IStatus.mAsrErrorCnt += 1;
                    IStatus.mRecognizeStatus = IStatus.STATUS_ERROR;
                    if (!IStatus.isInScene() && IStatus.mAsrErrorCnt >= IStatus.getMaxAsrErrorCount()) {
                        Intent intent = new Intent(IStatus.ACTION_FORCE_QUIT_ASR);
                        ctx.sendBroadcast(intent);
                    } else if (IStatus.mSceneType != IStatus.SCENE_GIVEN && IStatus.mSceneType != IStatus.SCENE_SEARCHPAGE) {
                        Intent tmp = new Intent(IStatus.ACTION_RESTART_ASR);
                        ctx.sendBroadcast(tmp);
                    }
                }
                MLog.d(TAG, "RESTART_ASR 444");
                AbsTTS.getInstance(null).talk("", ctx.getResources().getString(R.string.try_note));// "你试试说\"第*个\"或者\"退出\"");

                break;

            case BeeSearchParams.RESULT_UNKNOWN:
                mSpeakSameInfo = null;
                if (DefaultCmds.SystemCmdPatchProcess(ctx, skyItemObj.getWord())) {
                    break;
                }
                MLog.d(TAG, "mRecognizeStatus" + IStatus.mRecognizeStatus);
                if (IStatus.mRecognizeStatus == IStatus.STATUS_FINISHED) {
                    AbsTTS.getInstance(null).talkDelay(""/*skyItemObj.getTts()*/, skyItemObj.getOutput(), 1000);
                }
                if (VoiceApp.getVoiceApp().mAiType == GlobalVariable.AI_VOICE && BeeSearchParams.getInstance().isInSearchPage()) {
                    IStatus.mAsrErrorCnt += 1;
                    IStatus.mRecognizeStatus = IStatus.STATUS_ERROR;
                    Log.i(TAG, "err cnt:" + IStatus.mAsrErrorCnt + " type:" + IStatus.mSceneType);
                    if (!IStatus.isInScene() && IStatus.mAsrErrorCnt >= IStatus.getMaxAsrErrorCount()) {
                        Intent tmp = new Intent(IStatus.ACTION_FORCE_QUIT_ASR);
                        ctx.sendBroadcast(tmp);
                        return;
                    } else if (IStatus.mSceneType != IStatus.SCENE_GIVEN && IStatus.mSceneType != IStatus.SCENE_SEARCHPAGE) {
                        Intent tmp = new Intent(IStatus.ACTION_RESTART_ASR);
                        ctx.sendBroadcast(tmp);
                    }
                }
                break;
            case BeeSearchParams.RESULT_PLAY_DIRECT:
                AbsTTS.getInstance(null).talk(ctx.getString(R.string.playing_note));
//                SkyVideoPlayUtils.startToPlayByID(skyItemObj.videoId, skyItemObj.infoindex);
                BeeVideoPlayUtils.startToVideoDetail(ctx,BeeVideoPlayUtils.SOURCE_IQIYI_ID, String.valueOf(skyItemObj.videoId));
                break;
            default:
                Log.i(TAG, "code:" + operationcode + " " + skyItemObj.getWord());
                if (!BeeSearchParams.getInstance().isInSearchPage()) {
                    if (!playByName(ctx, skyItemObj.getCmd())) {
                        AbsTTS.getInstance(null).talk("", ctx.getResources().getString(R.string.try_note));
                    }
                }
                break;
        }

    }

    private static void beeSearchJump(Context ctx, String ret) {
        BeeSearchParams.getInstance().setLastReply(ret);
        BeeSearchBean skydosearch = GsonUtils.parseResult(ret, BeeSearchBean.class);
        MLog.d(TAG, "===dosearch result" + ret);
        AbsTTS.getInstance(null).talk(skydosearch.getTts(), skydosearch.getOutput());
        //MLog.d(TAG, skydosearch.getUserguaid().toString() + " size:" + skydosearch.getUserguaid().size());

        ArrayList<String> userGuide = new ArrayList<>();
        if (skydosearch.getUserguaid() != null && skydosearch.getUserguaid().size() > 0) {
            for (int i = 0; i < skydosearch.getUserguaid().size() && i < 5; i++) {
                userGuide.add(skydosearch.getUserguaid().get(i));
            }
        }
        ArrayList<String> userGuideNew = UserGuideStrings.getUserGuide(UserGuideStrings.getTagsFrom(UserGuideStrings.TYPE_SEARCH_CONTROL), 3, true);
        userGuide.addAll(userGuideNew);

        GuideTip.getInstance().resetSearchGuide(userGuide);

        if (TextUtils.equals(skydosearch.getLocalcallid(), BeeSearchParams.getInstance().getLocalcallid())) {
            jumpToNewSearch(ctx, skydosearch.getAbnf(), skydosearch.getTotal(), skydosearch.getResultstr());
        }

    }

    public static void jumpToNewSearch(Context ctx, String abnf, int total, String resultstr) {
        Intent broadcast = new Intent(SkyVideoPlayUtils.SEARCH_WORD_BRAODCAST);
        Bundle bundle = new Bundle();
        bundle.putString(ABNF_PARAM, abnf);
        bundle.putInt("total", total);
        bundle.putString("resultstr", resultstr);
        broadcast.putExtras(bundle);
        ctx.sendBroadcast(broadcast);
        try {
            Intent intent = new Intent("com.skyworthdigital.voice.dingdang.beesearch");
            intent.setPackage(GlobalVariable.VOICE_PACKAGE_NAME);
            intent.putExtras(bundle);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            //LogUtil.log(searchparam);
            ctx.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean convertToNewSearch(Context ctx, BeeItemObject skyItemObj) {
        // 同音字纠错
        if (mSpeakSameInfo != null && mSpeakSameInfo.getSpeaksame() != null
                && mSpeakSameInfo.getSpeaksame().size() > 0 && mSpeakSameInfo.getLastText() != null
                && mSpeakSameInfo.getLastText().length() > 0 && skyItemObj.cmd.args != null) {
            int position = skyItemObj.cmd.args.value - 1;
            if (position >= 0 && position < mSpeakSameInfo.getSpeaksame().size()) {
                // 替换新的词
                String text = mSpeakSameInfo.getLastText().replace(mSpeakSameInfo.getKeyword(), mSpeakSameInfo.getSpeaksame().get(position));
                // 执行新的搜索
                //mSpeakSameText = text;
                // 取消第n个命令
                skyItemObj.cmd = null;
                mSpeakSameInfo = null;
                Log.i(TAG, "new:" + text);
                AbsTTS.getInstance(null).talk(null, text);
                doBeeSearch(ctx, text, "");
            } else {
                AbsTTS.getInstance(null).talk(ctx.getString(R.string.str_searchfilm_numerr));
            }
            return true;
        }
        return false;
    }

    private static boolean playByName(Context ctx, BeeCommand cmd) {
        if (cmd != null && TextUtils.equals(cmd.getAction(), "playbyname")) {
            BeeSearchVideoResult videoresult = BeeSearchParams.getInstance().getMetasInfo();
            //LogUtil.log(" type:" + WelcomeTip.getInstance().getType());
            if (videoresult != null) {
                try {
                    ArrayList<String> commons = new ArrayList<>();
                    int index = 0;
                    for (SkyVideoInfo videoitem : videoresult.getVideolist()) {
                        String videoname = videoitem.getName();
                        videoname = StringUtils.format(videoname);
                        int common = (int) (Utils.levenshtein(videoname, cmd.args.name) * 100);
                        if (common > 0) {
                            commons.add("{\"index\":" + index + ",\"common\":" + common + "}");
                            Log.i(TAG, "{\"index\":" + index + ",\"common\":" + common + "}");
                        }
                        index++;
                    }
                    if (!commons.isEmpty()) {
                        int maxComman = 0;
                        int maxIndex = 0;
                        for (String string : commons) {
                            try {
                                JSONObject jsonObject = new JSONObject(string);
                                int common = jsonObject.getInt("common");
                                index = jsonObject.getInt("index");
                                if (common > maxComman) {
                                    maxComman = common;
                                    maxIndex = index;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        if (maxComman >= 50) {
                            AbsTTS.getInstance(null).talk("播放" + videoresult.getVideolist().get(maxIndex).getName());
                            String strPackage = GlobalVariable.VOICE_PACKAGE_NAME;
                            Intent intent = new Intent(INTENT_TOPACTIVITY_CALL);
                            intent.setPackage(strPackage);
                            intent.putExtra(DefaultCmds.SEQUERY, DefaultCmds.PLAY_CMD);
                            intent.putExtra(DefaultCmds.INTENT, DefaultCmds.COMMAND_LOCATION);
                            intent.putExtra(DefaultCmds.VALUE, maxIndex + 1);
                            ctx.startService(intent);
                            return true;
                        }
                    }

                    AbsTTS.getInstance(null).talk("搜索影片", "这一批没有名字匹配的影片，重新搜索");
                    doBeeSearch(ctx, cmd.args.name, "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    private static boolean commandExection(Context ctx, BeeCommand cmd) {
        switch (cmd.getCmdid()) {
            case BeeCommand.OPT_CODE_SUMMARY:
                if (BeeSearchParams.getInstance().isInSearchPage()) {
                    String strPackage = GlobalVariable.VOICE_PACKAGE_NAME;
                    Intent intent = new Intent(INTENT_TOPACTIVITY_CALL);
                    intent.setPackage(strPackage);
                    intent.putExtra(DefaultCmds.SEQUERY, "jianjie");
                    Log.i(TAG, "value:" + cmd.getArgs().value);
                    intent.putExtra(DefaultCmds.VALUE, cmd.getArgs().value);
                    ctx.startService(intent);
                    AbsTTS.getInstance(null).talk(ctx.getString(R.string.str_open_introduce));
                }
                return true;
            case BeeCommand.OPT_CODE_VOLUME_UNMUTE:
                AbsTTS.getInstance(null).talk(ctx.getString(R.string.str_volume_unmute));
                VolumeUtils.getInstance(ctx).cancelMute();
                return true;
            case BeeCommand.OPT_CODE_QUIT:
                AbsTTS.getInstance(null).talk(ctx.getString(R.string.str_exit));
                AppUtil.killTopApp();
                return true;
            case BeeCommand.OPT_CODE_RETURN:
                AbsTTS.getInstance(null).talk(ctx.getString(R.string.str_back));
                Utils.simulateKeystroke(KeyEvent.KEYCODE_BACK);
                return true;
            default:
                break;
        }
        return false;
    }
}
