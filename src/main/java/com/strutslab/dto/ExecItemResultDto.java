package com.strutslab.dto;

public class ExecItemResultDto {
    private int resultItemId;
    private int resultId;
    private int itemId;
    private String itemName;
    private String judge;
    private String measuredValue;
    private String note;
    private int itemLevel;
    private Integer parentItemId;
    private int sortOrder;

    public int getResultItemId() { return resultItemId; }
    public void setResultItemId(int v) { this.resultItemId = v; }
    public int getResultId() { return resultId; }
    public void setResultId(int v) { this.resultId = v; }
    public int getItemId() { return itemId; }
    public void setItemId(int v) { this.itemId = v; }
    public String getItemName() { return itemName; }
    public void setItemName(String v) { this.itemName = v; }
    public String getJudge() { return judge; }
    public void setJudge(String v) { this.judge = v; }
    public String getMeasuredValue() { return measuredValue; }
    public void setMeasuredValue(String v) { this.measuredValue = v; }
    public String getNote() { return note; }
    public void setNote(String v) { this.note = v; }
    public int getItemLevel() { return itemLevel; }
    public void setItemLevel(int v) { this.itemLevel = v; }
    public Integer getParentItemId() { return parentItemId; }
    public void setParentItemId(Integer v) { this.parentItemId = v; }
    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int v) { this.sortOrder = v; }
}
