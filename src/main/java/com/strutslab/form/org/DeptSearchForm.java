package com.strutslab.form.org;

import org.apache.struts.action.ActionForm;

public class DeptSearchForm extends ActionForm {
    private String deptCode;
    private String deptName;
    private int page = 1;

    public String getDeptCode() { return deptCode; }
    public void setDeptCode(String v) { this.deptCode = v; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String v) { this.deptName = v; }
    public int getPage() { return page; }
    public void setPage(int v) { this.page = v; }
}
