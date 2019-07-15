package com.skyworthdigital.voice.videoplay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.forest.bigdatasdk.util.LogUtil;
import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.beesearch.BeeSearchParams;
import com.skyworthdigital.voice.beesearch.BeeSearchUtils;
import com.skyworthdigital.voice.beesearch.BeeSearchVideoResult;
import com.skyworthdigital.voice.common.AbsTTS;
import com.skyworthdigital.voice.common.R;
import com.skyworthdigital.voice.dingdang.utils.GlobalVariable;
import com.skyworthdigital.voice.dingdang.utils.GsonUtils;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.videoplay.utils.RequestUtil;
import com.skyworthdigital.voice.videosearch.OnGetBeeSearchResultListener;
import com.skyworthdigital.voice.videosearch.callback.OnGetSearchVideoResultListener;

import java.util.List;

import okhttp3.FormBody;


/**
 * 精准搜索
 * Created by SDT03046 on 2017/8/17.
 */

public class SkyVideoPlayUtils {
    private static final String TAG = "VideoPlay";
    private static final String VOICE_SEARCH_ACTION = "/search/publicInterface/voicesearch";//"/search/publicInterface/qmzVoiceSearch";//http://119.23.12.86/SmartPai/
    private static final String SEARCH_HOST_URL = "search.skyworthbox.com"; // search.skyworthbox.com
    private static final String SP_NAME = "launcher";
    private static final String IS_CDN = "isCDN";
    private static final String HTTP = "http://";
    private static final String CDN_UPDATE_URL = "meta.cdn.skyworthbox.com";// "192.168.52.15";
    private static final String SEARCH_CHANNEL_ID = "search";
    private static final String PLAY_ACTION = "com.skyworthdigital.skyallmedia.videoplay";
    private static final String VIDEOINFO = "videoInfo";
    private static final String ISNEEDPLAYHISTORY = "play_hisotry";
    private static final String PAGE_SIZE = "12";
    private static final String TX_VIDEO_PACKAGE = "com.ktcp.video";
    private static final String TX_VIDEO = "tenvideo2://?action=1&cover_id=";
    private static final int START_FROM_HISTORY = 0X1001;
    private static final int START_FROM_LIST = 0X1002;
    private static final String VIDEO_ID = "videoId";
    private static final String START_MODEL = "model";
    private static final String ORDER_INDEX = "orderIndex";
    public static final String SEARCH_WORD_BRAODCAST = "com.skyworthdigital.voice.dingdang.searchword";
    private static final String SOUHU_PLAYER_ACTION = "com.sohuott.tv.vod.action.PLAY";
    private static final String SEARCH_PARAM = "searchParam";
    //private static final String PACKAGE_NAME_SEARCH = "com.skyworthdigital.skyallmedia";
    private static final String ACTION_SEARCH = "com.skyworthdigital.voiceassistant.voicesearch";
    private static final String SOHU_PACKAGE = "com.sohuott.tv.vod";
    private static final String MGTV_PACKAGE = "com.hunantv.market";

    public static boolean videoPlay(final Context ctx, final String filmSlots, final String slotfilmname, final int whepisode) {
        String url = buildGetVoiceSearchVideoListUrl();
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("searchParam", filmSlots);
        builder.add("page", "1");//String.valueOf(page));
        builder.add("rows", PAGE_SIZE);//String.valueOf(rows));
        //VoiceManager.getInstance().postEvent(new EventMsg(EventMsg.MSG_DISMISS_DIALOG, 20000));
        RequestUtil.postFromNet(new SkyCommonCallback("startGetSearchVideoList") {
            @Override
            public void onSuccessed(String ret) {
                MLog.i(TAG, "**************startGetSearchVideoList onSuccess ret=\n" + ret);
                SearchVideoResult info = GsonUtils.parseResult(ret, SearchVideoResult.class);

                if (info != null && info.getRows() != null && info.getRows().size() > 0) {
                    SkyVideoInfo videoInfo = info.getRows().get(0);
                    if (isFilmnameMatched(slotfilmname, videoInfo.getName())) {
                        int firstVideoid = videoInfo.getVideoId();
                        getVideoDetailByID(videoInfo, firstVideoid, whepisode/*, listener*/);
                    } else {
                        jumpToSearch(ctx, filmSlots);
                    }
                } else {
                    Log.e(TAG, "error! no video found");
                    AbsTTS.getInstance(null).talk("", VoiceApp.getInstance().getString(R.string.no_content_tips));
                }
            }

            @Override
            public void onFail() {
                //listener.getSearchVideoResult(null, "");
                Log.e(TAG, "startGetSearchVideoList onFail");
                AbsTTS.getInstance(null).talk("", VoiceApp.getInstance().getString(R.string.searchfail_tips));
            }
        }, url, builder.build(), SEARCH_CHANNEL_ID);
        return true;
    }

    public static boolean videoPlayyy(final Context ctx, final String filmSlots, final String slotfilmname, final int whepisode) {
        String url = buildGetVoiceSearchVideoListUrl();
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("searchParam", filmSlots);
        builder.add("pn", "1");//String.valueOf(page));
        builder.add("ps", PAGE_SIZE);//String.valueOf(rows));

        RequestUtil.postFromNet(new SkyCommonCallback("startGetSearchVideoList") {
            @Override
            public void onSuccessed(String ret) {
                Log.e(TAG, "**************startGetSearchVideoList onSuccess ret=\n" + ret);
                SearchVideoResult info = GsonUtils.parseResult(ret, SearchVideoResult.class);

                if (info != null && info.getRows() != null && info.getRows().size() > 0) {
                    SkyVideoInfo videoInfo = info.getRows().get(0);
                    if (isFilmnameMatched(slotfilmname, videoInfo.getName())) {
                        int firstVideoid = videoInfo.getVideoId();
                        getVideoDetailByID(videoInfo, firstVideoid, whepisode/*, listener*/);
                    } else {
                        jumpToSearch(ctx, filmSlots);
                    }
                } else {
                    Log.e(TAG, "error! no video found");
                    AbsTTS.getInstance(null).talk("", VoiceApp.getInstance().getString(R.string.no_content_tips));
                }
            }

            @Override
            public void onFail() {
                //listener.getSearchVideoResult(null, "");
                Log.e(TAG, "startGetSearchVideoList onFail");
                AbsTTS.getInstance(null).talk("", VoiceApp.getInstance().getString(R.string.searchfail_tips));
            }
        }, url, builder.build(), SEARCH_CHANNEL_ID);
        return true;
    }

    public static void jumpToSearch(Context ctx, String searchparam) {
        Intent broadcast = new Intent(SkyVideoPlayUtils.SEARCH_WORD_BRAODCAST);
        Bundle bundle = new Bundle();
        bundle.putString(SEARCH_PARAM, searchparam);
        broadcast.putExtras(bundle);
        ctx.sendBroadcast(broadcast);
        try {
            Intent intent = new Intent(ACTION_SEARCH);
            intent.setPackage(GlobalVariable.VOICE_PACKAGE_NAME);
            intent.putExtras(bundle);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Log.i(TAG, searchparam);
            ctx.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    **filmSlots.getFilm()影片名可能同时有多个且用“,”隔开。用来判断搜索到的影片名是否完全匹配
     */
    private static boolean isFilmnameMatched(String slotfilmname, String name) {
        Log.e(TAG, "isFilmnameMatched " + slotfilmname + " vs " + name);
        if (slotfilmname != null && name != null) {
            String[] namesplits = slotfilmname.split(",");
            for (int i = 0; i < namesplits.length; i++) {
                //LogUtil.log(i + ":" + namesplits[i]);
                if (TextUtils.equals(name, namesplits[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    private static String getSearchHost() {
        String url = SEARCH_HOST_URL;
        try {
            Context launcher =
                    VoiceApp.getInstance().createPackageContext(
                            "com.skyworthdigital.sky2dlauncherv4",
                            Context.CONTEXT_IGNORE_SECURITY);
            if (launcher != null) {
                SharedPreferences sharedPreferences =
                        launcher.getSharedPreferences(SP_NAME, Context.MODE_WORLD_READABLE);
                if (sharedPreferences != null && sharedPreferences.getBoolean(IS_CDN, false)) {
                    url = CDN_UPDATE_URL;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return HTTP + url;
    }

    private static boolean isValueAvailable(int value) {
        return (value > 0);
    }

    private static String buildGetVideoDetailUrl(int videoId, int ps) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(RequestUtil.getVideoDetail2Url());
            sb.append("?videoId=");
            sb.append(videoId);
            if (isValueAvailable(ps)) {
                sb.append("&ps=");
                sb.append(ps);
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    static private String buildGetVoiceSearchVideoListUrl() {
        try {
            return getSearchHost() + VOICE_SEARCH_ACTION;
        } catch (Exception e) {
            return null;
        }
    }

    private static String buildGetCDNVideoDetailUrl(int videoId, int ps) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(RequestUtil.HTTP);
            sb.append(RequestUtil.CDN_UPDATE_URL);
            sb.append(RequestUtil.VIDEO_DETAIL_ACTION2);
            sb.append("?videoId=");
            sb.append(videoId);
            if (isValueAvailable(ps)) {
                sb.append("&ps=");
                sb.append(ps);
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static void getVideoDetailByID(final SkyVideoInfo videoinfo, final int videoId, final int whepisode) {
        BeeVideoPlayUtils.startToVideoDetail(VoiceApp.getInstance(), BeeVideoPlayUtils.SOURCE_IQIYI_ID, String.valueOf(videoId));
        return;
//        String url = buildGetVideoDetailUrl(videoId, -1);
//        if (url == null) {
//            //listener.onGetVideoDetail(null);
//            return;
//        }
//        RequestUtil.sendRequest(url, new SkyCommonCallback("startGetVideoDetail") {
//                    @Override
//                    public void onSuccessed(String ret) {
//                        //LogUtil.log("***************startGetVideoDetail onSuccess ret=\n" + result);
//                        //LogUtil.log("\n****************************");
//                        VideoDetailResult info = GsonUtils.parseResult(ret, VideoDetailResult.class);
//                        if (info != null) {
//                            startToPlay(videoinfo, videoId, info, whepisode);
//                        }
//                    }
//
//                    @Override
//                    public void onFail() {
//                        Log.e(TAG, "startGetVideoDetail onFail");
//                        //if (RequestUtil.ping()) {
//                        String cdnurl = buildGetCDNVideoDetailUrl(videoId, -1);
//                        RequestUtil.sendRequest(cdnurl, new SkyCommonCallback("startGetVideoDetail") {
//                            @Override
//                            public void onFail() {
//                                //listener.onGetVideoDetail(null);
//                            }
//
//                            @Override
//                            public void onSuccessed(String ret) {
//                                //LogUtil.log("***************startGetVideoDetail onSuccess ret=\n" + result);
//                                VideoDetailResult info = GsonUtils.parseResult(ret, VideoDetailResult.class);
//                                if (info != null) {
//                                    startToPlay(videoinfo, videoId, info, whepisode);
//                                }
//                            }
//                        });
//                        /*} /*else
//                        {
//                             listener.onGetVideoDetail(null);
//                        }*/
//                    }
//                }
//
//        );
    }

    private static void startToiQIYIPlay(Context context, SkyVideoSubInfo subInfo) {
        if (subInfo != null) {
            Log.e(TAG, "startToiQIYIPlay");
            Log.e(TAG, "info:" + subInfo.getName() + " videoid:" + subInfo.getVideoId() + " otherid:" + subInfo.getOtherId() + " " + subInfo.toString());

            //Robot.getInstance().setWords(context.getString(R.string.str_ok));//"\"" + subInfo.getName() + "\"" + context.getString(R.string.str_happy_watch));
            Intent intent = new Intent();
            intent.setAction(PLAY_ACTION);
            intent.putExtra(VIDEOINFO, subInfo);
            intent.putExtra(ISNEEDPLAYHISTORY, true);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            Log.e(TAG, "error! subInfo is null");
            /*Toast toast = Toast.makeText(MyApplication.getInstance(), "子集信息为空!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 50);
            toast.show();*/
        }
    }

    private static void startToPlay(SkyVideoInfo videoinfo, final int videoId, VideoDetailResult info, final int whepisode) {
        Context ctx = VoiceApp.getInstance();
        //VoiceModeAdapter wakeupmode = new VoiceModeAdapter();
        //wakeupmode.setRecognizeGoOn(false);
        try {
            SkyVideoDetailInfo detailInfo = info.getData();
            int sourceid = detailInfo.getSourceId();

            List<SkyVideoSubInfo> subInfo = detailInfo.getInfos();//.get(whepisode);
            if (subInfo != null) {
                Log.e(TAG, "infosTotal:" + detailInfo.getInfosTotal() + " episodeTotal:" + detailInfo.getEpisodeTotal() + " episodeLast:" + detailInfo.getEpisodeLast() + " subInfo.size():" + subInfo.size() + " whepisode:" + whepisode);
                Log.e(TAG, "sourceid:" + sourceid + " chid:" + detailInfo.getChnId() + " chname:" + detailInfo.getChnName());
                if (whepisode > subInfo.size()) {
                    Log.e(TAG, "error! whepisode is bigger than total size:" + subInfo.size());
                    //Robot.getInstance().setWords(ctx.getString(R.string.str_search_nomatch_whepisode));
                    return;
                }
                //ActivityManager.destroyActivities();
                switch (sourceid) {
                    case GlobalVariable.TENCENT_SOURCE:
                        //Robot.getInstance().setWords(ctx.getString(R.string.str_ok));//(ctx.getString(R.string.str_opening));
                        startToTxVideoDetail(ctx, videoinfo.getOtherId());
                        break;
                    case GlobalVariable.IQIYI_SOURCE:
                        if (subInfo.size() == 1) {
                            startToiQIYIPlay(ctx, subInfo.get(0));
                            break;
                        } else if (whepisode == -1 && subInfo.size() > 1) {
                            startToVideoDetail(VoiceApp.getInstance(), videoId);
                            if (detailInfo.getChnId() == 1) {
                                //wakeupmode.setRecognizeGoOn(true);

                                //MyTTS.getInstance(null).talkWithoutDisplay("",ctx.getString(R.string.str_search_whepisode_choose));
                            } else {
                                //wakeupmode.setRecognizeGoOn(false);
                                //Robot.getInstance().setWords(ctx.getString(R.string.str_ok));
                            }
                            break;
                        } else if (whepisode >= 1 && whepisode <= subInfo.size()) {
                            startToiQIYIPlay(VoiceApp.getInstance(), subInfo.get(whepisode - 1));
                            break;
                        }
                        break;
                    case GlobalVariable.SOUHU_SOURCE:
                        if (subInfo.size() == 1) {
                            startSouhuPlayer(
                                    ctx,
                                    Integer.valueOf(videoinfo.getOtherId()),
                                    Integer.valueOf(subInfo.get(0).getPid()));
                            break;
                        } else if (whepisode == -1 && subInfo.size() > 1) {
                            startToVideoDetail(VoiceApp.getInstance(), videoId);
                            if (detailInfo.getChnId() == 1) {
                                //wakeupmode.setRecognizeGoOn(true);
                                //Robot.getInstance().setWords(ctx.getString(R.string.str_search_whepisode_choose));
                            } else {
                                //wakeupmode.setRecognizeGoOn(false);
                                //Robot.getInstance().setWords(ctx.getString(R.string.str_ok));
                            }
                            break;
                        } else if (whepisode >= 1 && whepisode <= subInfo.size()) {
                            startSouhuPlayer(
                                    ctx,
                                    Integer.valueOf(videoinfo.getOtherId()),
                                    Integer.valueOf(subInfo.get(whepisode - 1).getPid()));
                            break;
                        }
                        break;
                    case GlobalVariable.MGTV_SOURCE:
                        startToMangguoVideoDetail(VoiceApp.getInstance(), videoinfo.getOtherId(), "");
                        break;
                    default:
                        int isHistoryPlay = videoinfo.getIsHistoryPlay();
                        if (isHistoryPlay == 0) {
                            int orderIndex = videoinfo.getInfoOrderIndex();
                            startToVideoDetail(
                                    ctx,
                                    videoinfo.getVideoId(),
                                    START_FROM_LIST,
                                    orderIndex);
                        } else {
                            startToVideoDetail(
                                    ctx,
                                    videoinfo.getVideoId(),
                                    START_FROM_HISTORY);
                        }
                        break;
                }
            }
            //listener.onGetVideoDetail(info);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startToTxVideoPlay(Context context, String videoId) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(TX_VIDEO_PACKAGE);
        if (intent == null) {
            intent = new Intent();
            intent.setPackage("com.mipt.store");
            intent.setAction("com.mipt.store.intent.APPID");
            intent.setData(Uri.parse("skystore://?packageName=com.ktcp.video"));
            AbsTTS.getInstance(null).talk(context.getString(R.string.str_install_tencenttv));
        } else {
            intent = new Intent();
            intent.setData(Uri.parse("tenvideo2://?action=7&video_id=" + videoId));
            Log.e(TAG, "tx intent data = " + TX_VIDEO + videoId);
            intent.setPackage(TX_VIDEO_PACKAGE);
            //Robot.getInstance().setWords(context.getString(R.string.str_ok));//(context.getString(R.string.str_opening));
        }
        if (context == VoiceApp.getInstance()) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        context.startActivity(intent);
    }

    public static void startToTxVideoDetail(Context context, String otherId) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(TX_VIDEO_PACKAGE);
        if (intent == null) {
            intent = new Intent();
            intent.setPackage("com.mipt.store");
            intent.setAction("com.mipt.store.intent.APPID");
            intent.setData(Uri.parse("skystore://?packageName=com.ktcp.video"));
            AbsTTS.getInstance(null).talk(context.getString(R.string.str_install_tencenttv));
        } else {
            intent = new Intent();
            intent.setData(Uri.parse(TX_VIDEO + otherId + "&stay_flag=0"));
            Log.e(TAG, "tx intent data = " + TX_VIDEO + otherId);
            intent.setPackage(TX_VIDEO_PACKAGE);
            //Robot.getInstance().setWords(context.getString(R.string.str_ok));//(context.getString(R.string.str_opening));
        }
        if (context == VoiceApp.getInstance()) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        context.startActivity(intent);
    }

    public static void startToVideoDetail(Context context, int videoId) {
        startToVideoDetail(context, videoId, START_FROM_HISTORY);
    }

    public static void startToMangguoVideoDetail(Context context, String otherId, String pId) {
        startToMangguoVideoDetail(context, otherId, pId, 0);
    }

    private static void startToMangguoVideoDetail(Context context, String otherId, String pId, int offset) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(MGTV_PACKAGE);
        if (intent == null) {
            intent = new Intent();
            intent.setPackage("com.mipt.store");
            intent.setAction("com.mipt.store.intent.APPID");
            intent.setData(Uri.parse("skystore://?packageName=" + MGTV_PACKAGE));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            AbsTTS.getInstance(null).talk(context.getString(R.string.str_install_mgtv));
        } else {
            Intent mgtvIntent = new Intent();
            mgtvIntent.setAction("com.hunantv.market.external");
            mgtvIntent.putExtra("cmd_ex", "mgtv_jump");
            mgtvIntent.putExtra("action_source_id", "1111");
            mgtvIntent.putExtra("jumpKind", "1");
            mgtvIntent.putExtra("jumpId", "" + otherId);
            if (!TextUtils.isEmpty(pId)) {
                mgtvIntent.putExtra("playpartId", "" + pId);
            }
            if (offset > 0) {
                mgtvIntent.putExtra("offset", offset);
            }
            //Robot.getInstance().setWords(context.getString(R.string.str_ok));//(context.getString(R.string.str_opening));
            mgtvIntent.addFlags(32); // mgtvIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            if (context == VoiceApp.getInstance()) {
                mgtvIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            }
            //LogUtil.log("start To Mangguo Video Detail");
            context.startActivity(mgtvIntent);
        }
    }

    private static void startToVideoDetail(Context context, int videoId, int... flags) {
        try {
            Intent intent = new Intent("com.skyworthdigital.skyallmedia.VideoDetail");//context, SkyMediaDetailActivity.class);
            intent.putExtra(VIDEO_ID, videoId);
            if (flags != null && flags.length == 1) {
                int model = flags[0];
                intent.putExtra(START_MODEL, model);
            } else if (flags != null && flags.length == 2) {
                int model = flags[0];
                int orderIndex = flags[1];
                intent.putExtra(START_MODEL, model);
                intent.putExtra(ORDER_INDEX, orderIndex);
            }
            if (context == VoiceApp.getInstance()) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            }
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void startSouhuPlayer(Context context, int aid, int vid) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(SOHU_PACKAGE);
        if (intent == null) {
            intent = new Intent();
            intent.setPackage("com.mipt.store");
            intent.setAction("com.mipt.store.intent.APPID");
            intent.setData(Uri.parse("skystore://?packageName=" + SOHU_PACKAGE));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            AbsTTS.getInstance(null).talk(context.getString(R.string.str_install_sohu));
            context.startActivity(intent);
        } else {
            try {
                //Robot.getInstance().setWords(context.getString(R.string.str_ok));
                Intent sohuIntent = new Intent();
                sohuIntent.setAction(SOUHU_PLAYER_ACTION);
                sohuIntent.putExtra("aid", aid);
                sohuIntent.putExtra("vid", vid);
                sohuIntent.putExtra("video_type", 0);
                sohuIntent.putExtra("source_id", 3);
                sohuIntent.setPackage("com.sohuott.tv.vod");
                sohuIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(sohuIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void startGetVoiceSearchVideoList(
            final String filmSlots,
            final int page,
            final int rows,
            @NonNull final OnGetSearchVideoResultListener listener) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = buildGetVoiceSearchVideoListUrl();
                FormBody.Builder builder = new FormBody.Builder();
                builder.add("searchParam", filmSlots);
                builder.add("pn", String.valueOf(page));
                builder.add("ps", String.valueOf(rows));
                RequestUtil.postFromNet(new SkyCommonCallback("startGetSearchVideoList") {
                    @Override
                    public void onSuccessed(String ret) {
                        Log.i(TAG, "startGetSearchVideoList onSuccess ret=" + ret);
                        SearchVideoResult info = GsonUtils.parseResult(ret, SearchVideoResult.class);
                        if (info != null) {
                            listener.getSearchVideoResult(info, "");
                        }
                    }

                    @Override
                    public void onFail() {
                        listener.getSearchFailed();
                        //listener.getSearchVideoResult(null, "");
                        //Robot.getInstance().setWords(MyApplication.getInstance().getString(R.string.searchfail_tips));
                        Log.e(TAG, "startGetSearchVideoList onFail");
                    }
                }, url, builder.build(), RequestUtil.SEARCH_CHANNEL_ID);
            }
        }).start();
    }

    public static void startGetBeeSearchVideoList(
            final String abnf,
            final int page,
            final int rows,
            @NonNull final OnGetBeeSearchResultListener listener) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = BeeSearchUtils.getVedioListPath()+"?"+BeeSearchParams.PARAM_INTERFACE_VER;
                FormBody.Builder builder = new FormBody.Builder();
                builder.add("abnf", abnf);
                builder.add("localcallid", BeeSearchParams.getInstance().getLocalcallid());
                builder.add("pageno", String.valueOf(page));
                builder.add("pagesize", String.valueOf(rows));
                builder.add("channel", "mifeng");
                RequestUtil.postFromNet(new SkyCommonCallback("DoSearchVideoList") {
                    @Override
                    public void onSuccessed(String ret) {
                        String result = ret;
                        //LogUtil.loglong("DoSearchVideoList onSuccess ret=" + result);
                        BeeSearchVideoResult info = GsonUtils.parseResult(result, BeeSearchVideoResult.class);
                        if (info != null) {
                            listener.getSearchVideoResult(info, "");
                        }
                    }

                    @Override
                    public void onFail() {
                        listener.getSearchFailed();
                        //listener.getSearchVideoResult(null, "");
                        //Robot.getInstance().setWords(MyApplication.getInstance().getString(R.string.searchfail_tips));
                        //LogUtil.log("startGetSearchVideoList onFail");
                    }
                }, url, builder.build(), RequestUtil.SEARCH_CHANNEL_ID);
            }
        }).start();
    }

    private static void startToPlay(final int videoId, VideoDetailResult info, int whepisode) {
        Context ctx = VoiceApp.getInstance();
        try {
            SkyVideoDetailInfo detailInfo = info.getData();
            int sourceid = detailInfo.getSourceId();

            List<SkyVideoSubInfo> subInfo = detailInfo.getInfos();
            if (subInfo != null) {
                Log.e(TAG, "infosTotal:" + detailInfo.getInfosTotal() + " episodeTotal:" + detailInfo.getEpisodeTotal() + " episodeLast:" + detailInfo.getEpisodeLast() + " subInfo.size():" + subInfo.size() + " whepisode:" + whepisode);
                Log.e(TAG, "sourceid:" + sourceid + " chid:" + detailInfo.getChnId() + " chname:" + detailInfo.getChnName());
                if (whepisode >= subInfo.size()) {
                    Log.e(TAG, "error! whepisode is bigger than total size:" + subInfo.size());
                    whepisode = 0;
                    AbsTTS.getInstance(null).talk(ctx.getString(R.string.str_search_nomatch_whepisode));
                    //return;
                }
                switch (sourceid) {
                    case GlobalVariable.TENCENT_SOURCE:
                        startToTxVideoDetail(ctx, detailInfo.getOtherId());
                        break;

                    case GlobalVariable.SOUHU_SOURCE:
                        if (subInfo.size() == 1) {
                            startSouhuPlayer(
                                    ctx,
                                    Integer.valueOf(detailInfo.getOtherId()),
                                    Integer.valueOf(subInfo.get(0).getPid()));
                            break;
                        } else if (whepisode == -1 && subInfo.size() > 1) {
                            startToVideoDetail(VoiceApp.getInstance(), videoId);
                            break;
                        } else if (whepisode >= 1 && whepisode <= subInfo.size()) {
                            startSouhuPlayer(
                                    ctx,
                                    Integer.valueOf(detailInfo.getOtherId()),
                                    Integer.valueOf(subInfo.get(whepisode - 1).getPid()));
                            break;
                        }
                        break;
                    case GlobalVariable.MGTV_SOURCE:
                        startToMangguoVideoDetail(VoiceApp.getInstance(), detailInfo.getOtherId(), "");
                        break;
                    case GlobalVariable.IQIYI_SOURCE:
                        if (subInfo.size() == 1) {
                            startToiQIYIPlay(ctx, subInfo.get(0));
                            break;
                        } else if (whepisode < 0 && subInfo.size() > 1) {
                            startToVideoDetail(VoiceApp.getInstance(), videoId);
                            break;
                        } else if (whepisode >= 0 && whepisode <= subInfo.size()) {
                            startToiQIYIPlay(VoiceApp.getInstance(), subInfo.get(whepisode));
                            break;
                        }
                    default:
                        break;
                }
            }
            //listener.onGetVideoDetail(info);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startToPlayByID(final int videoId, final int whepisode) {
        String url = buildGetVideoDetailUrl(videoId, -1);
        if (url == null) {
            return;
        }
        RequestUtil.sendRequest(url, new SkyCommonCallback("startGetVideoDetail") {
                    @Override
                    public void onSuccessed(String ret) {
                        LogUtil.log("***************startGetVideoDetail onSuccess ret=\n" + videoId);
                        //LogUtil.log("\n****************************");
                        VideoDetailResult info = GsonUtils.parseResult(ret, VideoDetailResult.class);
                        if (info != null) {
                            startToPlay(videoId, info, whepisode);
                        }
                    }

                    @Override
                    public void onFail() {
                        Log.e(TAG, "startGetVideoDetail onFail");
                        //if (RequestUtil.ping()) {
                        String cdnurl = buildGetCDNVideoDetailUrl(videoId, -1);
                        RequestUtil.sendRequest(cdnurl, new SkyCommonCallback("startGetVideoDetail") {
                            @Override
                            public void onFail() {
                                //listener.onGetVideoDetail(null);
                            }

                            @Override
                            public void onSuccessed(String ret) {
                                //LogUtil.log("***************startGetVideoDetail onSuccess ret=\n" + result);
                                VideoDetailResult info = GsonUtils.parseResult(ret, VideoDetailResult.class);
                                if (info != null) {
                                    startToPlay(videoId, info, whepisode);
                                }
                            }
                        });
                    }
                }
        );
    }
}
