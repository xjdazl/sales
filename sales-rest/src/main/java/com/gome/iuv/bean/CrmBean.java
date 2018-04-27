package com.gome.iuv.bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by xiajiadong on 2018/1/6.
 */
@Component
public class CrmBean {

    @Value("${access_token_url}")
    private  String access_token_url;

    @Value("${grant_type}")
    private String grant_type;

    @Value("${client_id}")
    private String client_id;

    @Value("${client_secret}")
    private String client_secret;

    @Value("${username}")
    private String username;

    @Value("${password}")
    private String password;

    @Value("${out_warehouse_url}")
    private String out_warehouse_url;

    @Value("${out_warehouse_back_url}")
    private String out_warehouse_back_url;

    @Value("${request_id}")
    private String request_id;

    @Value("${imei_commit_crm_url}")
    private String imei_commit_crm_url;

    public String getAccess_token_url() {
        return access_token_url;
    }

    public void setAccess_token_url(String access_token_url) {
        this.access_token_url = access_token_url;
    }

    public String getGrant_type() {
        return grant_type;
    }

    public void setGrant_type(String grant_type) {
        this.grant_type = grant_type;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getClient_secret() {
        return client_secret;
    }

    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOut_warehouse_url() {
        return out_warehouse_url;
    }

    public void setOut_warehouse_url(String out_warehouse_url) {
        this.out_warehouse_url = out_warehouse_url;
    }

    public String getOut_warehouse_back_url() {
        return out_warehouse_back_url;
    }

    public void setOut_warehouse_back_url(String out_warehouse_back_url) {
        this.out_warehouse_back_url = out_warehouse_back_url;
    }

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public String getImei_commit_crm_url() {
        return imei_commit_crm_url;
    }

    public void setImei_commit_crm_url(String imei_commit_crm_url) {
        this.imei_commit_crm_url = imei_commit_crm_url;
    }
}
