package com.strutslab.dto;

public class PlanDto {
    private int planId;
    private String fiscalYear;
    private String equipmentCode;
    private String equipmentName;
    private Integer templateId;
    private String plannedDate;
    private String teamCode;
    private String personCode;
    private String status;
    private boolean isLocked;
    private String note;

    public int getPlanId() { return planId; }
    public void setPlanId(int v) { this.planId = v; }
    public String getFiscalYear() { return fiscalYear; }
    public void setFiscalYear(String v) { this.fiscalYear = v; }
    public String getEquipmentCode() { return equipmentCode; }
    public void setEquipmentCode(String v) { this.equipmentCode = v; }
    public String getEquipmentName() { return equipmentName; }
    public void setEquipmentName(String v) { this.equipmentName = v; }
    public Integer getTemplateId() { return templateId; }
    public void setTemplateId(Integer v) { this.templateId = v; }
    public String getPlannedDate() { return plannedDate; }
    public void setPlannedDate(String v) { this.plannedDate = v; }
    public String getTeamCode() { return teamCode; }
    public void setTeamCode(String v) { this.teamCode = v; }
    public String getPersonCode() { return personCode; }
    public void setPersonCode(String v) { this.personCode = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }
    public boolean getIsLocked() { return isLocked; }
    public void setIsLocked(boolean v) { this.isLocked = v; }
    public String getNote() { return note; }
    public void setNote(String v) { this.note = v; }
}
