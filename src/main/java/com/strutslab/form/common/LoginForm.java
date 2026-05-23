package com.strutslab.form.common;

import org.apache.struts.action.ActionForm;

public class LoginForm extends ActionForm {
    private String loginId;
    private String password;

    public String getLoginId() { return loginId; }
    public void setLoginId(String v) { this.loginId = v; }
    public String getPassword() { return password; }
    public void setPassword(String v) { this.password = v; }
}
