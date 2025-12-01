package com.uithealthcare.domain.payment;

public class Payment {
    private String amount;
    private String orderInfo;
    private String payUrl;
    private String qrImage;

    public Payment() {
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(String orderInfo) {
        this.orderInfo = orderInfo;
    }

    public String getPayUrl() {
        return payUrl;
    }

    public void setPayUrl(String payUrl) {
        this.payUrl = payUrl;
    }

    public String getQrImage() {
        return qrImage;
    }

    public void setQrImage(String qrImage) {
        this.qrImage = qrImage;
    }
}
