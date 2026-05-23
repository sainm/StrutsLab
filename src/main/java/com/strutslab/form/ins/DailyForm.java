package com.strutslab.form.ins;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.struts.action.ActionForm;

public class DailyForm extends ActionForm {
    private String targetDate;
    private String personCode;
    private String statusFilter;

    public DailyForm() {
        // Default targetDate to today
        this.targetDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        this.statusFilter = "全部";
    }

    public String getTargetDate() { return targetDate; }
    public void setTargetDate(String v) { this.targetDate = v; }

    public String getPersonCode() { return personCode; }
    public void setPersonCode(String v) { this.personCode = v; }

    public String getStatusFilter() { return statusFilter; }
    public void setStatusFilter(String v) { this.statusFilter = v; }
}
