package com.strutslab.dto;

public class ChkItemDto {
    private int itemId;
    private int templateId;
    private Integer parentItemId;
    private int itemLevel;
    private String itemName;
    private String judgeCriteria;
    private String normalRange;
    private String unit;
    private int sortOrder;

    public int getItemId() { return itemId; }
    public void setItemId(int v) { this.itemId = v; }
    public int getTemplateId() { return templateId; }
    public void setTemplateId(int v) { this.templateId = v; }
    public Integer getParentItemId() { return parentItemId; }
    public void setParentItemId(Integer v) { this.parentItemId = v; }
    public int getItemLevel() { return itemLevel; }
    public void setItemLevel(int v) { this.itemLevel = v; }
    public String getItemName() { return itemName; }
    public void setItemName(String v) { this.itemName = v; }
    public String getJudgeCriteria() { return judgeCriteria; }
    public void setJudgeCriteria(String v) { this.judgeCriteria = v; }
    public String getNormalRange() { return normalRange; }
    public void setNormalRange(String v) { this.normalRange = v; }
    public String getUnit() { return unit; }
    public void setUnit(String v) { this.unit = v; }
    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int v) { this.sortOrder = v; }
}
