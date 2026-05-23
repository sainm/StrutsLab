package com.strutslab.dto;

public class ChkTmplDto {
    private int templateId;
    private String templateName;
    private String equipmentType;
    private String inspectionKind;
    private int itemCount;
    private String lastUpdateDate;
    private int sortOrder;

    public int getTemplateId() { return templateId; }
    public void setTemplateId(int v) { this.templateId = v; }
    public String getTemplateName() { return templateName; }
    public void setTemplateName(String v) { this.templateName = v; }
    public String getEquipmentType() { return equipmentType; }
    public void setEquipmentType(String v) { this.equipmentType = v; }
    public String getInspectionKind() { return inspectionKind; }
    public void setInspectionKind(String v) { this.inspectionKind = v; }
    public int getItemCount() { return itemCount; }
    public void setItemCount(int v) { this.itemCount = v; }
    public String getLastUpdateDate() { return lastUpdateDate; }
    public void setLastUpdateDate(String v) { this.lastUpdateDate = v; }
    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int v) { this.sortOrder = v; }
}
