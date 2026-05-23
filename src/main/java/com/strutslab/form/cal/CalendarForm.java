package com.strutslab.form.cal;

import org.apache.struts.action.ActionForm;

public class CalendarForm extends ActionForm {
    private String year;

    public String getYear() { return year; }
    public void setYear(String v) { this.year = v; }
}
