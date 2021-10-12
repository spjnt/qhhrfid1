package tramais.hnb.hhrfid.net;

import com.alibaba.fastjson.JSONArray;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Params {

    private final HashMap<String, Object> params;

    private Params() {
        params = new HashMap<String, Object>();
    }

    /**
     * 创建参数列表
     *
     * @return
     */
    public static Params createParams() {
        return new Params();
    }

    /**
     * 添加参数并返回参数列表
     *
     * @param key
     * @param value
     * @return
     */
    public String addParam(String key, String value) {
        params.put(key, value);
        return getParams();
    }

    /**
     * 添加参数
     *
     * @param key
     * @param value
     * @return
     */
    public Params add(String key, String value) {
        params.put(key, value);
        return this;
    }
    public Params add(String key, com.alibaba.fastjson.JSONObject value) {
        params.put(key, value);
        return this;
    }
    public Params add(String key, JSONArray value) {
        params.put(key, value);
        return this;
    }

    public Params add(String key, double value) {
        params.put(key, value);
        return this;
    }

    /**
     * 添加参数
     *
     * @param key
     * @param value
     * @return
     */
    public Params add(String key, String[] value) {
        params.put(key, value);
        return this;
    }

    /**
     * 添加参数
     *
     * @param key
     * @param value
     * @return
     */
    public Params add(String key, byte[] value) {
        params.put(key, value);
        return this;
    }

    /**
     * 添加参数
     *
     * @param key
     * @param value
     * @return
     */
    public Params add(String key, Date value) {
        params.put(key, value);
        return this;
    }

    /**
     * 添加参数
     *
     * @param key
     * @param value
     * @return
     */
    public Params add(String key, List<String> value) {
        params.put(key, value);
        return this;
    }

    /**
     * 添加参数
     *
     * @param key
     * @param value
     * @return
     */
    public Params add(String key, Byte value) {
        params.put(key, value);
        return this;
    }

    /**
     * 添加参数
     *
     * @param key
     * @param value
     * @return
     */
    public Params add(String key, boolean value) {
        params.put(key, value);
        return this;
    }

    /**
     * 添加参数
     *
     * @param key
     * @param value
     * @return
     */
    public Params add(String key, int value) {
        params.put(key, value);
        return this;
    }


    /**
     * 添加参数
     *
     * @param key
     * @param value
     * @return
     */
    public Params add(String key, float value) {
        params.put(key, value);
        return this;
    }


    public Params add(String key, JSONObject value) {
        params.put(key, value);
        return this;
    }

    public int getLength() {
        return params.size();
    }

    /**
     * 返回参数列表，key为"params"
     *
     * @return
     */
    public String getParams() {
       // for (Map.Entry<String, Object> entry : params.entrySet()) {
      //      System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
       // }

      //  Map<String, Object> result = new HashMap<String, Object>();
        //result.put("params", new JSONObject(params));
        return new JSONObject(params).toString();
    }

    /**
     * 直接将参数变为json字符串返回
     *
     * @return
     */
    public String getParamsMapString() {
        return new JSONObject(params).toString();
    }

    /**
     * 获取参数hashmap
     *
     * @return
     */
    public HashMap<String, Object> getHashMp() {
        return params;
    }
}
