package com.meythomaapp;

/**
 * Created by Murali on 12-May-18.
 */

public class CartBean {
    private String cmpnyName;
    private String gstYesNo;
    private String produType;
    private String qualityKg;
    private String priceprKg;
    private String amounttot;
    private String gstamt;
    private String totalamt;



    private String billno;
    private String ordby;

    public String getGstamt() {
        return gstamt;
    }

    public void setGstamt(String gstamt) {
        this.gstamt = gstamt;
    }

    public String getTotalamt() {
        return totalamt;
    }

    public void setTotalamt(String totalamt) {
        this.totalamt = totalamt;
    }

    public String getCmpnyName() {
        return cmpnyName;
    }

    public void setCmpnyName(String cmpnyName) {
        this.cmpnyName = cmpnyName;
    }

    public String getGstYesNo() {
        return gstYesNo;
    }

    public void setGstYesNo(String gstYesNo) {
        this.gstYesNo = gstYesNo;
    }

    public String getProduType() {
        return produType;
    }

    public void setProduType(String produType) {
        this.produType = produType;
    }

    public String getQualityKg() {
        return qualityKg;
    }

    public void setQualityKg(String qualityKg) {
        this.qualityKg = qualityKg;
    }

    public String getPriceprKg() {
        return priceprKg;
    }

    public void setPriceprKg(String priceprKg) {
        this.priceprKg = priceprKg;
    }

    public String getAmounttot() {
        return amounttot;
    }

    public void setAmounttot(String amounttot) {
        this.amounttot = amounttot;
    }
    public String getBillno() {
        return billno;
    }

    public void setBillno(String billno) {
        this.billno = billno;
    }

    public String getOrdby() {
        return ordby;
    }

    public void setOrdby(String ordby) {
        this.ordby = ordby;
    }
}
