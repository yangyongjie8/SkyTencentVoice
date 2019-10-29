package com.skyworthdigital.voice.wemust;

import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.skyworthdigital.skysmartsdk.common.util.SPUtil;
import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.common.AbsTTS;
import com.skyworthdigital.voice.common.utils.DateUtil;
import com.skyworthdigital.voice.common.utils.DeviceUtil;
import com.skyworthdigital.voice.common.utils.EncryptUtil;
import com.skyworthdigital.voice.common.utils.RsaUtil;

import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.asn1.pkcs.RSAPublicKey;

import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WemustApi {

    private static final String URL_GET_PUBLIC_KEY = "https://www.wmstlink.com/lot/xd0002/ex_sys_key";
    private static final String URL_DEVICE_REGISTER = "https://www.wmstlink.com/lot/xd0002/sign_in";
    private static final String URL_DEVICE_CONTROL = "https://www.wmstlink.com/lot/xd0002/talk";

    private static final String SP_KEY_SERVER_PUBKEY = "sp_key_server_pubkey";
    private static final String SP_KEY_CLIENT_PRIKEY = "sp_key_client_prikey";
    private static final String SP_KEY_CLIENT_PUBKEY = "sp_key_client_pubkey";
    private static final String SP_KEY_CLIENT_KEY_EXPIRED = "sp_key_client_key_expired";
    private static final String SP_KEY_SERVER_KEY_EXPIRED = "sp_key_server_key_expired";

    private static int info_i = 0;//对话序列，0 正常对话； 大于0，多轮对话的序号

    private static void clearKeys(Context context){
        SPUtil.putString(context, SP_KEY_SERVER_PUBKEY, null);
        SPUtil.putString(context, SP_KEY_CLIENT_PRIKEY, null);
        SPUtil.putString(context, SP_KEY_CLIENT_PUBKEY, null);
    }

    public static synchronized void initKeys(Context context) throws Exception {
        if(Thread.currentThread().getId()== Looper.getMainLooper().getThread().getId()) throw new Exception("cannot invoke from main thread.");

        // 服务端公钥
        String svrPubKey = SPUtil.getString(context, SP_KEY_SERVER_PUBKEY);
        String svrExpired = SPUtil.getString(context, SP_KEY_SERVER_KEY_EXPIRED);
        Log.i("WemustApi", "服务端公钥过期时间："+svrExpired);
        Date svrExpiredDate = null;
        if(!TextUtils.isEmpty(svrExpired)){
            try {
                svrExpiredDate = DateUtil.getDate(svrExpired);
            } catch (ParseException e) {
                Log.w("", "server expired time:"+svrExpired+". ignore if svrExpired is empty");
                e.printStackTrace();
            }
        }
        // 客户端秘钥
        String cliPriKey = SPUtil.getString(context, SP_KEY_CLIENT_PRIKEY);
        String cliPubKey = SPUtil.getString(context, SP_KEY_CLIENT_PUBKEY);
        String cliExpired = SPUtil.getString(context, SP_KEY_CLIENT_KEY_EXPIRED);
        Log.i("WemustApi", "客户端秘钥过期时间："+cliExpired);
        Date cliExpiredDate = null;
        if(!TextUtils.isEmpty(cliExpired)){
            try {
                cliExpiredDate = DateUtil.getDate(cliExpired);
            } catch (ParseException e) {
                Log.w("", "client expired time:"+cliExpired+". ignore if cliExpired is empty");
                e.printStackTrace();
            }
        }
        boolean isServerKeyExpired = TextUtils.isEmpty(svrPubKey) || svrExpiredDate==null || svrExpiredDate.before(DateUtil.getDateOneWeekLater());
        boolean isClientKeyExpired = TextUtils.isEmpty(cliPubKey)||TextUtils.isEmpty(cliPriKey)||cliExpiredDate==null||cliExpiredDate.before(DateUtil.getDateOneWeekLater());
        Log.d("WemustApi", "isServerKeyExpired:"+isServerKeyExpired+" isClientKeyExpired:"+isClientKeyExpired);
        if(isServerKeyExpired || isClientKeyExpired){
            Log.i("WemustApi", "秘钥过期或不存在，重新请求远程公钥并创建本地秘钥");
            requestPublicKey(context);

            // 新创建秘钥对
            KeyPair pair = RsaUtil.generateRSAKeyPair();
            String privatePem = RsaUtil.privatePkcs1ToPem(RsaUtil.privatePkcs8ToPkcs1(pair.getPrivate()));
            String publicPem = RsaUtil.publicPkcs1ToPem(RsaUtil.publicX509ToPkcs1(pair.getPublic()));
            SPUtil.putString(context, SP_KEY_CLIENT_PRIKEY, privatePem);
            SPUtil.putString(context, SP_KEY_CLIENT_PUBKEY, publicPem);
            // 向服务端注册本地公钥，失败则清空本地私钥
            signin(context);
        }else {
//            Log.i("########privatePem", cliPriKey);
//            Log.i("########cli publicPem", cliPubKey);
        }
    }

    public static synchronized void requestPublicKey(Context context) throws Exception {
        if(Thread.currentThread().getId()== Looper.getMainLooper().getThread().getId()) throw new Exception("cannot invoke from main thread.");
        Request.Builder builder = new Request.Builder().url(URL_GET_PUBLIC_KEY);

        try {

            Map<String, String> bodyMap = new HashMap<>();
            bodyMap.put("cli_sys_id", "xd0002");
            bodyMap.put("cli_term_id", DeviceUtil.getMacAddress(context).replaceAll(":",""));
            bodyMap.put("en_data", "");
            bodyMap.put("sign", "");
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), new Gson().toJson(bodyMap));

            Request request = builder.post(body).build();
            Response response = VoiceApp.getOkHttpClient().newCall(request).execute();
            if(response!=null && response.code()==200){
                String bodyStr = response.body().string();
                WrapResponse commonResponse = new Gson().fromJson(bodyStr, WrapResponse.class);
                EnData enData = new Gson().fromJson(commonResponse.getEn_data(), EnData.class);
                // 记录公钥
                SPUtil.putString(context, SP_KEY_SERVER_PUBKEY, enData.getSys_pub_key());
                Calendar c = Calendar.getInstance();
                c.add(Calendar.SECOND, Integer.valueOf(enData.getSys_pk_exp()));
                // 记录过期时间
                SPUtil.putString(context, SP_KEY_SERVER_KEY_EXPIRED, DateUtil.getTimestamp(c.getTime()));

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static synchronized void signin(Context context) throws Exception {
        if(Thread.currentThread().getId()== Looper.getMainLooper().getThread().getId()) throw new Exception("cannot invoke from main thread.");

        Request.Builder builder = new Request.Builder().url(URL_DEVICE_REGISTER);

        try {
            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("cli_pub_key", SPUtil.getString(context, SP_KEY_CLIENT_PUBKEY));// 客户端公钥

            int expiredSeconds = 24*3600*365;
            Calendar c = Calendar.getInstance();
            c.add(Calendar.SECOND, expiredSeconds);
            // 记录过期时间
            SPUtil.putString(context, SP_KEY_CLIENT_KEY_EXPIRED, DateUtil.getTimestamp(c.getTime()));
            dataMap.put("cli_pk_exp", String.valueOf(expiredSeconds));// 客户端公钥有效期，单位秒
            dataMap.put("cli_time", DateUtil.getCurrentTimestamp());// 客户端当前时间
            String enData = encrypt(context, new Gson().toJson(dataMap));

            Map<String, String> bodyMap = new HashMap<>();
            bodyMap.put("cli_sys_id", "xd0002");
            bodyMap.put("cli_term_id", DeviceUtil.getMacAddress(context).replaceAll(":",""));
            bodyMap.put("en_data", enData);
            bodyMap.put("sign", sign(context, enData));
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), new Gson().toJson(bodyMap));

            Request request = builder.post(body).build();
            Response response = VoiceApp.getOkHttpClient().newCall(request).execute();
            if(response!=null && response.code()==200){
                String bodyStr = response.body().string();
                WrapResponse commonResponse = new Gson().fromJson(bodyStr, WrapResponse.class);
                String deData = decrypt(context, commonResponse.getEn_data());
                EnData enDataObj = new Gson().fromJson(deData, EnData.class);
                // 业务正确
                if(enDataObj!=null && "000000".equals(enDataObj.getRet_code())){
                    Log.i("WemustApi", "公钥注册成功");
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 注册失败，清空本地秘钥信息，以便下次再触发注册
        SPUtil.putString(context, SP_KEY_CLIENT_PRIKEY, "");
        SPUtil.putString(context, SP_KEY_CLIENT_PUBKEY, "");
    }

    /**
     *
     * @param context
     * @param speech
     * @return false 未被消费
     */
    public static synchronized boolean talk(Context context, String speech) throws Exception {
        if(Thread.currentThread().getId()== Looper.getMainLooper().getThread().getId()) throw new Exception("cannot invoke from main thread.");
        try {
            initKeys(context);
        } catch (Exception e) {
            Log.e("WemustApi", "秘钥初始化失败，跳过后续业务动作");
            e.printStackTrace();
            SPUtil.putString(context, SP_KEY_CLIENT_PRIKEY, "");
            SPUtil.putString(context, SP_KEY_CLIENT_PUBKEY, "");
            return false;
        }

        Request.Builder builder = new Request.Builder().url(URL_DEVICE_CONTROL);

        try {
            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("info_i", String.valueOf(info_i));
            dataMap.put("info", speech);// 语音内容
            dataMap.put("cli_time", DateUtil.getCurrentTimestamp());// 客户端当前时间
            String enData = encrypt(context, new Gson().toJson(dataMap));

            Map<String, String> bodyMap = new HashMap<>();
            bodyMap.put("cli_sys_id", "xd0002");
            bodyMap.put("cli_term_id", DeviceUtil.getMacAddress(context).replaceAll(":",""));
            bodyMap.put("en_data", enData);
            bodyMap.put("sign", sign(context, enData));
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), new Gson().toJson(bodyMap));

            Request request = builder.post(body).build();
            Response response = VoiceApp.getOkHttpClient().newCall(request).execute();
            if(response!=null && response.code()==200){
                String bodyStr = response.body().string();
                WrapResponse commonResponse = new Gson().fromJson(bodyStr, WrapResponse.class);
                // 为空则后台秘钥可能已丢失，需要尝试重新更新秘钥
                if(TextUtils.isEmpty(commonResponse.getEn_data())){
                    clearKeys(context);
                    return false;
                }
                EnData enDataObj = new Gson().fromJson(decrypt(context,commonResponse.getEn_data()), EnData.class);
                // 被威玛斯特消费
                if(enDataObj!=null && "000000".equals(enDataObj.getRet_code())){
                    if("1".equals(enDataObj.getR_info_type())){
                        info_i = Integer.valueOf(enDataObj.getR_info_i());
                    }else if("0".equals(enDataObj.getR_info_type())){
                        info_i = 0;
                    }
                    if(!TextUtils.isEmpty(enDataObj.getR_info())) {
                        AbsTTS.getInstance(null).talk(enDataObj.getR_info());
                    }
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void test(Context context) throws Exception {
        String pripem = SPUtil.getString(context, SP_KEY_CLIENT_PRIKEY);
        RSAPrivateKey pri = RSAPrivateKey.getInstance(RsaUtil.getPrivateKeyFromPem2(SPUtil.getString(context, SP_KEY_CLIENT_PRIKEY)));
        PrivateKey priKey = RsaUtil.getPrivateKeyFromPem(SPUtil.getString(context, SP_KEY_CLIENT_PRIKEY));
        Log.e("#####privatePem", pripem);
        String test = "1111111111111111";
        Log.i("#########sha256", ""+ EncryptUtil.byte2Hex(EncryptUtil.sha256(test)).toUpperCase());
        try {
            String result = EncryptUtil.bin2hex(RsaUtil.encrypt(EncryptUtil.byte2Hex(EncryptUtil.sha256(test)).toUpperCase(), pri));
            Log.i("####private encrypt", ""+result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String sign(Context context, String enData) throws Exception {
        RSAPrivateKey pri = RSAPrivateKey.getInstance(RsaUtil.getPrivateKeyFromPem2(SPUtil.getString(context, SP_KEY_CLIENT_PRIKEY)));
        return EncryptUtil.bin2hex(RsaUtil.encrypt(EncryptUtil.byte2Hex(EncryptUtil.sha256(enData)).toUpperCase(), pri));
    }
    private static String encrypt(Context context, String data) throws Exception {
        String enData = EncryptUtil.byte2Hex(RsaUtil.encrypt(data, RSAPublicKey.getInstance(RsaUtil.getPublicKeyFromPem2(SPUtil.getString(context, SP_KEY_SERVER_PUBKEY)))));
        return enData;
    }
    private static String decrypt(Context context, String enData) throws Exception {
        String deData = new String(RsaUtil.decrypt(EncryptUtil.hexToBytes(enData), RSAPrivateKey.getInstance(RsaUtil.getPrivateKeyFromPem2(SPUtil.getString(context, SP_KEY_CLIENT_PRIKEY)))));
        Log.i("WemustApi", "解密数据："+deData);
        return deData;
    }
}
