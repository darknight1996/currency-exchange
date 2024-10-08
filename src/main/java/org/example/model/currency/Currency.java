package org.example.model.currency;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Currency {

    private int id;
    private String code;
    @JsonProperty("name")
    private String fullName;
    private String sign;

    public Currency() {
    }

    public Currency(final int id, final String code, final String fullName, final String sign) {
        this.id = id;
        this.code = code;
        this.fullName = fullName;
        this.sign = sign;
    }

    public Currency(final String code, final String fullName, final String sign) {
        this.code = code;
        this.fullName = fullName;
        this.sign = sign;
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(final String fullName) {
        this.fullName = fullName;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(final String sign) {
        this.sign = sign;
    }

}
