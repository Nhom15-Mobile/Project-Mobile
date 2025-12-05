package com.uithealthcare.domain.payment;

public class Payment {

    public String id;
    private String amount;
    private String orderInfo;
    private String payUrl;
    private String qrImage;
    private PaymentMeta meta;



    public String getId() { return id; }
    //public String getId() { return id; }
    //public String getAppointmentId() { return appointmentId; }
    //public String getProvider() { return provider; }

    //public String getCurrency() { return currency; }
    //public String getStatus() { return status; }
    public PaymentMeta getMeta() { return meta; }

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
