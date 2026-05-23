package com.strutslab.dto;

import java.util.List;

public class ExecResultDto {
    private int resultId;
    private int planId;
    private String executedDate;
    private String executedBy;
    private String summaryJudge;
    private String summaryNote;
    private String nextRecommendedDate;
    private String approvalStatus;
    private String equipmentName;
    private String equipmentCode;
    private String inspectionKind;
    private String plannedTime;
    private String plannedDate;
    private String personCode;
    private String templateName;
    private int templateId;
    private String rejectReason;
    private List<ExecItemResultDto> items;

    public int getResultId() { return resultId; }
    public void setResultId(int v) { this.resultId = v; }
    public int getPlanId() { return planId; }
    public void setPlanId(int v) { this.planId = v; }
    public String getExecutedDate() { return executedDate; }
    public void setExecutedDate(String v) { this.executedDate = v; }
    public String getExecutedBy() { return executedBy; }
    public void setExecutedBy(String v) { this.executedBy = v; }
    public String getSummaryJudge() { return summaryJudge; }
    public void setSummaryJudge(String v) { this.summaryJudge = v; }
    public String getSummaryNote() { return summaryNote; }
    public void setSummaryNote(String v) { this.summaryNote = v; }
    public String getNextRecommendedDate() { return nextRecommendedDate; }
    public void setNextRecommendedDate(String v) { this.nextRecommendedDate = v; }
    public String getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(String v) { this.approvalStatus = v; }
    public String getEquipmentName() { return equipmentName; }
    public void setEquipmentName(String v) { this.equipmentName = v; }
    public String getEquipmentCode() { return equipmentCode; }
    public void setEquipmentCode(String v) { this.equipmentCode = v; }
    public String getInspectionKind() { return inspectionKind; }
    public void setInspectionKind(String v) { this.inspectionKind = v; }
    public String getPlannedTime() { return plannedTime; }
    public void setPlannedTime(String v) { this.plannedTime = v; }
    public String getPlannedDate() { return plannedDate; }
    public void setPlannedDate(String v) { this.plannedDate = v; }
    public String getPersonCode() { return personCode; }
    public void setPersonCode(String v) { this.personCode = v; }
    public String getTemplateName() { return templateName; }
    public void setTemplateName(String v) { this.templateName = v; }
    public int getTemplateId() { return templateId; }
    public void setTemplateId(int v) { this.templateId = v; }
    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String v) { this.rejectReason = v; }
    public List<ExecItemResultDto> getItems() { return items; }
    public void setItems(List<ExecItemResultDto> v) { this.items = v; }
}
