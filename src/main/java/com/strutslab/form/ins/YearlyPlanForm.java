package com.strutslab.form.ins;

import org.apache.struts.action.ActionForm;

public class YearlyPlanForm extends ActionForm {
    private String fiscalYear;
    private String equipmentType;
    private String team;

    public String getFiscalYear() { return fiscalYear; }
    public void setFiscalYear(String v) { this.fiscalYear = v; }
    public String getEquipmentType() { return equipmentType; }
    public void setEquipmentType(String v) { this.equipmentType = v; }
    public String getTeam() { return team; }
    public void setTeam(String v) { this.team = v; }
}
