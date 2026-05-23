package com.strutslab.form.parts;

import org.apache.struts.action.ActionForm;

public class PartsUsageSearchForm extends ActionForm {
    private String dateFrom;
    private String dateTo;
    private String equipmentType;
    private String partCode;

    public String getDateFrom() { return dateFrom; }
    public void setDateFrom(String v) { this.dateFrom = v; }
    public String getDateTo() { return dateTo; }
    public void setDateTo(String v) { this.dateTo = v; }
    public String getEquipmentType() { return equipmentType; }
    public void setEquipmentType(String v) { this.equipmentType = v; }
    public String getPartCode() { return partCode; }
    public void setPartCode(String v) { this.partCode = v; }
}
