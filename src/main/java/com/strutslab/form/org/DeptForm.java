package com.strutslab.form.org;

import org.apache.struts.validator.ValidatorForm;

public class DeptForm extends ValidatorForm {
    private String deptCode;
    private String deptName;
    private String parentDeptCode;
    private String parentDeptName;
    private int deptLevel;
    private String deptType;
    private String startDate;
    private String endDate;
    private String address;
    private String tel;
    private String method;

    public String getDeptCode() { return deptCode; }
    public void setDeptCode(String v) { this.deptCode = v; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String v) { this.deptName = v; }
    public String getParentDeptCode() { return parentDeptCode; }
    public void setParentDeptCode(String v) { this.parentDeptCode = v; }
    public String getParentDeptName() { return parentDeptName; }
    public void setParentDeptName(String v) { this.parentDeptName = v; }
    public int getDeptLevel() { return deptLevel; }
    public void setDeptLevel(int v) { this.deptLevel = v; }
    public String getDeptType() { return deptType; }
    public void setDeptType(String v) { this.deptType = v; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String v) { this.startDate = v; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String v) { this.endDate = v; }
    public String getAddress() { return address; }
    public void setAddress(String v) { this.address = v; }
    public String getTel() { return tel; }
    public void setTel(String v) { this.tel = v; }
    public String getMethod() { return method; }
    public void setMethod(String v) { this.method = v; }
}
