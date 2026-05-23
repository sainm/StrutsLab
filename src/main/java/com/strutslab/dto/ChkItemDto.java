package com.strutslab.dto;

public class ChkItemDto {
    private String itemId;
    private Integer itemLevel;
    private String itemName;
    private String judgeCriteria;
    private String normalRange;
    private String unit;
    private String parentItemId;

    public String getItemId() { return itemId; }
    public void setItemId(String v) { this.itemId = v; }
    public Integer getItemLevel() { return itemLevel; }
    public void setItemLevel(Integer v) { this.itemLevel = v; }
    public String getItemName() { return itemName; }
    public void setItemName(String v) { this.itemName = v; }
    public String getJudgeCriteria() { return judgeCriteria; }
    public void setJudgeCriteria(String v) { this.judgeCriteria = v; }
    public String getNormalRange() { return normalRange; }
    public void setNormalRange(String v) { this.normalRange = v; }
    public String getUnit() { return unit; }
    public void setUnit(String v) { this.unit = v; }
    public String getParentItemId() { return parentItemId; }
    public void setParentItemId(String v) { this.parentItemId = v; }
}
