package tramais.hnb.hhrfid.interfaces;


import com.alibaba.fastjson.JSONArray;

public interface GetResultJsonArarry {
    void getResult(int rtnCode, String message, int totalNums, JSONArray datas);
}
