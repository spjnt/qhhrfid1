package tramais.hnb.hhrfid.net;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.apkfuns.logutils.LogUtils;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import tramais.hnb.hhrfid.bean.FarmBackBean;
import tramais.hnb.hhrfid.bean.HttpBean;
import tramais.hnb.hhrfid.constant.Config;
import tramais.hnb.hhrfid.interfaces.GetOneString;
import tramais.hnb.hhrfid.interfaces.OkResponseInterface;


public class OkhttpUtil {

    private static OkhttpUtil mInstance;
    private final Gson mGson;
    private Context mContext;

    /*
     * 单例模式
     * */
    private OkhttpUtil(Context mContext) {
        mGson = new Gson();
        if (mContext != null)
            this.mContext = mContext.getApplicationContext();
    }

    public static OkhttpUtil getInstance(Context context) {
        if (mInstance == null) {
            synchronized (OkhttpUtil.class) {
                if (mInstance == null && context != null) {
                    mInstance = new OkhttpUtil(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * @param context  当前上下文
     * @param url      请求URL
     * @param request  请求参数
     * @param listener 回调接口
     * @param tag      区分不同的请求的标识
     */


    /**
     * @param url      请求URL
     * @param listener 回调接口
     *                 <p>
     *                 MediaType mediaType = MediaType.parse("application/json;charset=utf-8"); // [A]
     *                 RequestBody body = RequestBody.create(mediaType, "media");
     *                 String[] aclHeader = "x-goog-acl:public-read".split(":");   builder.addHeader("Content-Type", "text/plain");
     *                 builder.addHeader(aclHeader[0], aclHeader[1]);
     */

    public void doPosts(final String url, final Params params, final OkResponseInterface listener) {

        PostFormBuilder builder = OkHttpUtils
                .post()
                .url(url);

//        builder.addHeader("Content-Type", "application/json;charset=utf-8");
        Iterator iter = params.getHashMp().entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            if (val != null) {
                builder.addParams(key.toString(), val.toString());
            } else {
                builder.addParams(key.toString(), "");
            }
        }

        builder.build()
                .execute(new StringCallback() {
                    @Override
                    public void onBefore(Request request, int id) {
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (!url.contains("UploadPicture"))
                            LogUtils.e("onResponse :--->" + url + "  " + params.getParams() + "    " + response);
                        else   LogUtils.e("onResponse :--->" + url + "  "  + response);
//                        FileUtil.writeTxtToFile(params.getParams(), FileUtil.getSDPath() + Constants.sdk_first_path, "parmars" + ".txt");
                        listener.onSuccess(new HttpBean(id, response), id);
                    }

                    @Override
                    public void onAfter(int id) {
                    }

                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        Log.e("onError :--->", params.getParams() + ":" + url + "          :" + e.toString());
                    }
                });
    }

    /**
     * get请求
     *
     * @param url
     * @param
     * @param responseInterface
     */
    public void doPost(String url, final Params params, final OkResponseInterface responseInterface) {


        Map<String, String> headers = new HashMap<>();
        headers.put("Signature", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJSZXNvdXJjZXMgQ2VydGlmaWNhdGlvbiIsImNvbnRleHRLZXkiOiI0ZDNhNjllNjg3OWE0MzdhYmRiZmY5ZDQ2ZThkYWZhOCIsImNvbnRleHRTZWNyZXQiOiJhZGIzOWJkM2VmM2M0Njc3OTM2YWE3MzkyMjlkYzY5ZiIsImV4cCI6MTYwNjIwMTIzN30.0Qno8TfOhkyfLAfieV_mNa7Her5A8MpHDq1SQHWlJhg"
        );
        headers.put("Content-Type", "application/json");
        PostFormBuilder getBuilder = OkHttpUtils.post().headers(headers).url(url);
        GetBuilder url1 = OkHttpUtils.get().headers(headers).url(url);

        Iterator iter = params.getHashMp().entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            if (val != null) {
                getBuilder.addHeader(key.toString(), val.toString());
            } else {
                getBuilder.addHeader(key.toString(), "");
            }
        }
        getBuilder.build().execute(new StringCallback() {
            @Override
            public void onError(okhttp3.Call call, Exception e, int id) {
                Log.e("onError :--->", params.getParams() + "   " + url + "   " + e.toString());
                responseInterface.onError(e);
            }

            @Override
            public void onResponse(String response, int id) {
                responseInterface.onSuccess(new HttpBean(id, response), id);
                LogUtils.e("response" + response);
            }
        });


    }

    public void doPost1(String url, String json, final GetOneString getOneString) {
//        eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJSZXNvdXJjZXMgQ2VydGlmaWNhdGlvbiIsImNvbnRleHRLZXkiOiI1ZGE5NThhNzUxNzI0Zjk1YTg1ZDQxNjI5YjIxZTZjZiIsImNvbnRleHRTZWNyZXQiOiJkM2ZjMGEyZDBlNzg0ZmRiODk4MGMwYTg2MmE0YTVhNiIsImV4cCI6MTYwNjE4Nzg0NH0.2SaZ-HVtSl0zwBVr9Z6WP3AV2hebaplPpXRZGh0nTB4
        if (TextUtils.isEmpty(json) || json == null) return;
        MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
        FarmBackBean farmBackBean = JSON.parseObject(json, FarmBackBean.class);

        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, json);
        Request requestPost = new Request.Builder().url(url).header("Signature",
                farmBackBean.getToken())
                .post(requestBody).build();


        OkHttpClient client = new OkHttpClient();
        client.newCall(requestPost).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                LogUtils.e("guoyuan" + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                getOneString.getString(response.body().string());
            }
        });


    }

    /**
     * get请求
     *
     * @param url
     * @param params
     * @param responseInterface
     */
    public void doGet(String url, final Params params, final OkResponseInterface responseInterface) {

        GetBuilder getBuilder = OkHttpUtils.get().url(url);

        Iterator iter = params.getHashMp().entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            if (val != null) {
                getBuilder.addParams(key.toString(), val.toString());
            } else {
                getBuilder.addParams(key.toString(), "");
            }
        }

        getBuilder.build().execute(new StringCallback() {


            @Override
            public void onError(okhttp3.Call call, Exception e, int id) {
                responseInterface.onError(e);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtils.e("get::" + params.getParams() + response);
                responseInterface.onSuccess(new HttpBean(id, response), id);
            }


        });


    }

}
