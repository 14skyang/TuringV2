package com.ysk.turingv2.bean;

/**
 * 描述: 发送数据实体类
 */

public class Ask {
    /**
     * reqType : 0
     * perception : {"inputText":{"text":"你叫什么"}}
     * userInfo : {"apiKey":"c00282de107144fb940adab994d9ff98","userId":"225167"}
     */

    private int reqType;
    private PerceptionBean perception;
    private UserInfoBean userInfo;

    public Ask() {

    }

    public Ask(PerceptionBean perception) {
        this.perception = perception;
    }

    public int getReqType() {
        return reqType;
    }

    public void setReqType(int reqType) {
        this.reqType = reqType;
    }

    public PerceptionBean getPerception() {
        return perception;
    }

    public void setPerception(PerceptionBean perception) {
        this.perception = perception;
    }

    public UserInfoBean getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfoBean userInfo) {
        this.userInfo = userInfo;
    }

    public static class PerceptionBean {
        /**
         * inputText : {"text":"你叫什么"}
         */

        private InputTextBean inputText;


        public PerceptionBean(InputTextBean inputText) {
            this.inputText = inputText;
        }

        public InputTextBean getInputText() {
            return inputText;
        }

        public void setInputText(InputTextBean inputText) {
            this.inputText = inputText;
        }

        public static class InputTextBean {

            /**
             * text : 你叫什么
             */

            public InputTextBean(String text) {
                this.text = text;
            }

            private String text;

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }
        }
    }

    public static class UserInfoBean {
        /**
         * apiKey :e54abcf09bb44bbd87966e9cc5367424    //apiKey : c00282de107144fb940adab994d9ff98
         *     userId : 319103                 // userId : 225167
         */

        private String apiKey;
        private String userId;




        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
}
