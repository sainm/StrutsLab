package com.strutslab.dto;

public class PartsUsageDto {
    private Integer usageId;
    private String partCode;
    private String partName;
    private String equipmentCode;
    private String equipmentName;
    private String usageDate;
    private Integer quantity;
    private Integer stockBefore;
    private Integer stockAfter;
    private String purpose;
    private String usedBy;
    private String orderNo;
    private String note;

    public Integer getUsageId() { return usageId; }
    public void setUsageId(Integer v) { this.usageId = v; }
    public String getPartCode() { return partCode; }
    public void setPartCode(String v) { this.partCode = v; }
    public String getPartName() { return partName; }
    public void setPartName(String v) { this.partName = v; }
    public String getEquipmentCode() { return equipmentCode; }
    public void setEquipmentCode(String v) { this.equipmentCode = v; }
    public String getEquipmentName() { return equipmentName; }
    public void setEquipmentName(String v) { this.equipmentName = v; }
    public String getUsageDate() { return usageDate; }
    public void setUsageDate(String v) { this.usageDate = v; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer v) { this.quantity = v; }
    public Integer getStockBefore() { return stockBefore; }
    public void setStockBefore(Integer v) { this.stockBefore = v; }
    public Integer getStockAfter() { return stockAfter; }
    public void setStockAfter(Integer v) { this.stockAfter = v; }
    public String getPurpose() { return purpose; }
    public void setPurpose(String v) { this.purpose = v; }
    public String getUsedBy() { return usedBy; }
    public void setUsedBy(String v) { this.usedBy = v; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String v) { this.orderNo = v; }
    public String getNote() { return note; }
    public void setNote(String v) { this.note = v; }
}
