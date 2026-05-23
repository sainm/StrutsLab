package com.strutslab.form.org;

import org.apache.struts.action.ActionForm;

public class EmpSearchForm extends ActionForm {
    private String deptCode;
    private String position;
    private String yearFrom;
    private String yearTo;
    private int page = 1;
    private String[] qualifications;

    public String getDeptCode() { return deptCode; }
    public void setDeptCode(String v) { this.deptCode = v; }
    public String getPosition() { return position; }
    public void setPosition(String v) { this.position = v; }
    public String getYearFrom() { return yearFrom; }
    public void setYearFrom(String v) { this.yearFrom = v; }
    public String getYearTo() { return yearTo; }
    public void setYearTo(String v) { this.yearTo = v; }
    public int getPage() { return page; }
    public void setPage(int v) { this.page = v; }
    public String[] getQualifications() { return qualifications; }
    public void setQualifications(String[] v) { this.qualifications = v; }
}
