package com.example.kotlincashloan.service.model.Loans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetNewsModel {
    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("result")
    @Expose
    private GetNewsResultModel result;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public GetNewsResultModel getResult() {
        return result;
    }

    public void setResult(GetNewsResultModel result) {
        this.result = result;
    }
}
