package com.strutslab.form.parts;

import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.FormFile;

public class PartsForm extends ActionForm {
    private String partCode;
    private String partName;
    private String partType;
    private String unit;
    private Integer orderPoint;
    private Integer safetyStock;
    private Integer unitPrice;
    private String supplier;
    private String note;
    private String[] applicableEquipmentCodes;
    private FormFile attachFile;
    private String method;

    public String getPartCode() { return partCode; }
    public void setPartCode(String v) { this.partCode = v; }
    public String getPartName() { return partName; }
    public void setPartName(String v) { this.partName = v; }
    public String getPartType() { return partType; }
    public void setPartType(String v) { this.partType = v; }
    public String getUnit() { return unit; }
    public void setUnit(String v) { this.unit = v; }
    public Integer getOrderPoint() { return orderPoint; }
    public void setOrderPoint(Integer v) { this.orderPoint = v; }
    public Integer getSafetyStock() { return safetyStock; }
    public void setSafetyStock(Integer v) { this.safetyStock = v; }
    public Integer getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Integer v) { this.unitPrice = v; }
    public String getSupplier() { return supplier; }
    public void setSupplier(String v) { this.supplier = v; }
    public String getNote() { return note; }
    public void setNote(String v) { this.note = v; }
    public String[] getApplicableEquipmentCodes() { return applicableEquipmentCodes; }
    public void setApplicableEquipmentCodes(String[] v) { this.applicableEquipmentCodes = v; }
    public FormFile getAttachFile() { return attachFile; }
    public void setAttachFile(FormFile v) { this.attachFile = v; }
    public String getMethod() { return method; }
    public void setMethod(String v) { this.method = v; }
}
