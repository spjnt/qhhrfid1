package tramais.hnb.hhrfid.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

class xx {


    /**
     * getBankResulData
     */
    private List<GetBankResulDataDTO> getBankResulData;
    /**
     * code
     */
    @JSONField(name = "Code")
    private int code;
    /**
     * msg
     */
    @JSONField(name = "Msg")
    private String msg;

    public List<GetBankResulDataDTO> getGetBankResulData() {
        return getBankResulData;
    }

    public void setGetBankResulData(List<GetBankResulDataDTO> getBankResulData) {
        this.getBankResulData = getBankResulData;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class GetBankResulDataDTO {
        /**
         * getBankResultDetaiData
         */
        private List<GetBankResultDetaiDataDTO> getBankResultDetaiData;
        /**
         * fBackName
         */
        @JSONField(name = "FBackName")
        private String fBackName;

        public List<GetBankResultDetaiDataDTO> getGetBankResultDetaiData() {
            return getBankResultDetaiData;
        }

        public void setGetBankResultDetaiData(List<GetBankResultDetaiDataDTO> getBankResultDetaiData) {
            this.getBankResultDetaiData = getBankResultDetaiData;
        }

        public String getFBackName() {
            return fBackName;
        }

        public void setFBackName(String fBackName) {
            this.fBackName = fBackName;
        }

        public static class GetBankResultDetaiDataDTO {
            /**
             * fBankCode
             */
            @JSONField(name = "FBankCode")
            private String fBankCode;
            /**
             * fBankDetailName
             */
            @JSONField(name = "FBankDetailName")
            private String fBankDetailName;
            /**
             * fDetailCode
             */
            @JSONField(name = "FDetailCode")
            private String fDetailCode;

            public String getFBankCode() {
                return fBankCode;
            }

            public void setFBankCode(String fBankCode) {
                this.fBankCode = fBankCode;
            }

            public String getFBankDetailName() {
                return fBankDetailName;
            }

            public void setFBankDetailName(String fBankDetailName) {
                this.fBankDetailName = fBankDetailName;
            }

            public String getFDetailCode() {
                return fDetailCode;
            }

            public void setFDetailCode(String fDetailCode) {
                this.fDetailCode = fDetailCode;
            }
        }
    }
}
