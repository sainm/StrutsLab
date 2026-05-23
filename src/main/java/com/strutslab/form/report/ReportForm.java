package com.strutslab.form.report;

import org.apache.struts.action.ActionForm;

public class ReportForm extends ActionForm {
    private String dateFrom;
    private String dateTo;
    private String equipmentType;
    private String team;

    public String getDateFrom() { return dateFrom; }
    public void setDateFrom(String v) { this.dateFrom = v; }
    public String getDateTo() { return dateTo; }
    public void setDateTo(String v) { this.dateTo = v; }
    public String getEquipmentType() { return equipmentType; }
    public void setEquipmentType(String v) { this.equipmentType = v; }
    public String getTeam() { return team; }
    public void setTeam(String v) { this.team = v; }
}
