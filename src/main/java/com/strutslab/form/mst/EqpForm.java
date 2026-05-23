package com.strutslab.form.mst;

import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.FormFile;

public class EqpForm extends ActionForm {
    private String equipmentCode;
    private String equipmentName;
    private String equipmentType;
    private String voltageLevel;
    private Integer ratedCapacity;
    private Integer ratedCurrent;
    private String frequency;
    private String parentEquipmentCode;
    private String installDate;
    private String locationAddress;
    private String coordinates;
    private String maintenanceRank;
    private Integer inspectionInterval;
    private String lastInspectionDate;
    private String nextInspectionDate;
    private String status;
    private String note;
    private FormFile attachFile;
    private String method;

    // getters and setters for all fields
    public String getEquipmentCode() { return equipmentCode; }
    public void setEquipmentCode(String v) { this.equipmentCode = v; }
    public String getEquipmentName() { return equipmentName; }
    public void setEquipmentName(String v) { this.equipmentName = v; }
    public String getEquipmentType() { return equipmentType; }
    public void setEquipmentType(String v) { this.equipmentType = v; }
    public String getVoltageLevel() { return voltageLevel; }
    public void setVoltageLevel(String v) { this.voltageLevel = v; }
    public Integer getRatedCapacity() { return ratedCapacity; }
    public void setRatedCapacity(Integer v) { this.ratedCapacity = v; }
    public Integer getRatedCurrent() { return ratedCurrent; }
    public void setRatedCurrent(Integer v) { this.ratedCurrent = v; }
    public String getFrequency() { return frequency; }
    public void setFrequency(String v) { this.frequency = v; }
    public String getParentEquipmentCode() { return parentEquipmentCode; }
    public void setParentEquipmentCode(String v) { this.parentEquipmentCode = v; }
    public String getInstallDate() { return installDate; }
    public void setInstallDate(String v) { this.installDate = v; }
    public String getLocationAddress() { return locationAddress; }
    public void setLocationAddress(String v) { this.locationAddress = v; }
    public String getCoordinates() { return coordinates; }
    public void setCoordinates(String v) { this.coordinates = v; }
    public String getMaintenanceRank() { return maintenanceRank; }
    public void setMaintenanceRank(String v) { this.maintenanceRank = v; }
    public Integer getInspectionInterval() { return inspectionInterval; }
    public void setInspectionInterval(Integer v) { this.inspectionInterval = v; }
    public String getLastInspectionDate() { return lastInspectionDate; }
    public void setLastInspectionDate(String v) { this.lastInspectionDate = v; }
    public String getNextInspectionDate() { return nextInspectionDate; }
    public void setNextInspectionDate(String v) { this.nextInspectionDate = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }
    public String getNote() { return note; }
    public void setNote(String v) { this.note = v; }
    public FormFile getAttachFile() { return attachFile; }
    public void setAttachFile(FormFile v) { this.attachFile = v; }
    public String getMethod() { return method; }
    public void setMethod(String v) { this.method = v; }
}
