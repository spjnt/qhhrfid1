package tramais.hnb.hhrfid.interfaces;


import tramais.hnb.hhrfid.bean.HttpBean;

public interface OkResponseInterface {
    /**
     * 成功回调
     *
     * @param bean 返回由tag(用来区分不同的请求)和响应的字符串封装好的bean，id可以忽略
     * @param id
     */
    void onSuccess(HttpBean bean, int id);


    /**
     * 失败回调
     *
     * @param e
     */
    void onError(Exception e);
}

