package com.strutslab.form.cal;

import org.apache.struts.validator.ValidatorForm;

public class CalendarRegForm extends ValidatorForm {
    private String dateFrom;
    private String dateTo;
    private String holidayType;
    private String holidayName;
    private String transferFrom;
    private String transferTo;
    private String method;

    public String getDateFrom() { return dateFrom; }
    public void setDateFrom(String v) { this.dateFrom = v; }
    public String getDateTo() { return dateTo; }
    public void setDateTo(String v) { this.dateTo = v; }
    public String getHolidayType() { return holidayType; }
    public void setHolidayType(String v) { this.holidayType = v; }
    public String getHolidayName() { return holidayName; }
    public void setHolidayName(String v) { this.holidayName = v; }
    public String getTransferFrom() { return transferFrom; }
    public void setTransferFrom(String v) { this.transferFrom = v; }
    public String getTransferTo() { return transferTo; }
    public void setTransferTo(String v) { this.transferTo = v; }
    public String getMethod() { return method; }
    public void setMethod(String v) { this.method = v; }
}
