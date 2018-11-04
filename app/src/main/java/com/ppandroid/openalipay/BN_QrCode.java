package com.ppandroid.openalipay;

import java.util.List;

public class BN_QrCode {


    /**
     * title : 要创建二维码的订单列表接口
     * isSuccess : true
     * message : [{"id":3,"deleteStatus":false,"version":0,"createTime":"2018-11-01 16:48:08","createBy":null,"createById":null,"lastModifyTime":"2018-11-01 16:48:08","lastModifyBy":null,"lastModifyById":null,"inAlipayAccount":"1366","orderNo":"112","inAmount":1,"outAlipayAccount":null,"outAlipayPwd":null,"outAmount":0,"status":0,"qrcodeUrl":null,"createQrcodeTime":null,"payTime":null,"statusName":"待生成二维码"}]
     * errorCode :
     */

    private String title;
    private boolean isSuccess;
    private String errorCode;
    private List<MessageBean> message;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public List<MessageBean> getMessage() {
        return message;
    }

    public void setMessage(List<MessageBean> message) {
        this.message = message;
    }

    public static class MessageBean {
        /**
         * id : 3
         * deleteStatus : false
         * version : 0
         * createTime : 2018-11-01 16:48:08
         * createBy : null
         * createById : null
         * lastModifyTime : 2018-11-01 16:48:08
         * lastModifyBy : null
         * lastModifyById : null
         * inAlipayAccount : 1366
         * orderNo : 112
         * inAmount : 1
         * outAlipayAccount : null
         * outAlipayPwd : null
         * outAmount : 0
         * status : 0
         * qrcodeUrl : null
         * createQrcodeTime : null
         * payTime : null
         * statusName : 待生成二维码
         */

        private int id;
        private boolean deleteStatus;
        private int version;
        private String createTime;
        private Object createBy;
        private Object createById;
        private String lastModifyTime;
        private Object lastModifyBy;
        private Object lastModifyById;
        private String inAlipayAccount;
        private String orderNo;
        private String inAmount;
        private Object outAlipayAccount;
        private Object outAlipayPwd;
        private int outAmount;
        private int status;
        private Object qrcodeUrl;
        private Object createQrcodeTime;
        private Object payTime;
        private String statusName;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public boolean isDeleteStatus() {
            return deleteStatus;
        }

        public void setDeleteStatus(boolean deleteStatus) {
            this.deleteStatus = deleteStatus;
        }

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public Object getCreateBy() {
            return createBy;
        }

        public void setCreateBy(Object createBy) {
            this.createBy = createBy;
        }

        public Object getCreateById() {
            return createById;
        }

        public void setCreateById(Object createById) {
            this.createById = createById;
        }

        public String getLastModifyTime() {
            return lastModifyTime;
        }

        public void setLastModifyTime(String lastModifyTime) {
            this.lastModifyTime = lastModifyTime;
        }

        public Object getLastModifyBy() {
            return lastModifyBy;
        }

        public void setLastModifyBy(Object lastModifyBy) {
            this.lastModifyBy = lastModifyBy;
        }

        public Object getLastModifyById() {
            return lastModifyById;
        }

        public void setLastModifyById(Object lastModifyById) {
            this.lastModifyById = lastModifyById;
        }

        public String getInAlipayAccount() {
            return inAlipayAccount;
        }

        public void setInAlipayAccount(String inAlipayAccount) {
            this.inAlipayAccount = inAlipayAccount;
        }

        public String getOrderNo() {
            return orderNo;
        }

        public void setOrderNo(String orderNo) {
            this.orderNo = orderNo;
        }

        public String getInAmount() {
            return inAmount;
        }

        public void setInAmount(String inAmount) {
            this.inAmount = inAmount;
        }

        public Object getOutAlipayAccount() {
            return outAlipayAccount;
        }

        public void setOutAlipayAccount(Object outAlipayAccount) {
            this.outAlipayAccount = outAlipayAccount;
        }

        public Object getOutAlipayPwd() {
            return outAlipayPwd;
        }

        public void setOutAlipayPwd(Object outAlipayPwd) {
            this.outAlipayPwd = outAlipayPwd;
        }

        public int getOutAmount() {
            return outAmount;
        }

        public void setOutAmount(int outAmount) {
            this.outAmount = outAmount;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public Object getQrcodeUrl() {
            return qrcodeUrl;
        }

        public void setQrcodeUrl(Object qrcodeUrl) {
            this.qrcodeUrl = qrcodeUrl;
        }

        public Object getCreateQrcodeTime() {
            return createQrcodeTime;
        }

        public void setCreateQrcodeTime(Object createQrcodeTime) {
            this.createQrcodeTime = createQrcodeTime;
        }

        public Object getPayTime() {
            return payTime;
        }

        public void setPayTime(Object payTime) {
            this.payTime = payTime;
        }

        public String getStatusName() {
            return statusName;
        }

        public void setStatusName(String statusName) {
            this.statusName = statusName;
        }
    }
}
