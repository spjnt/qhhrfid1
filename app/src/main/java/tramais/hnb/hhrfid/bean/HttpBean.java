package tramais.hnb.hhrfid.bean;


/**
 *
 */

public class HttpBean {
    public int tag;
    public String response;
    public int code;

    public HttpBean(int tag, String response) {
        this.tag = tag;
        this.response = response;
        this.code = code;
    }

   /* public String getResponse(){
        String res = null;
        try {
             res = response.body().string();
        } catch (IOException e) {
            res = null;
            e.printStackTrace();
        }
        return res;
    }*/
}
