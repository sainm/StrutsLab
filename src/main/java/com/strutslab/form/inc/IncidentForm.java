package com.strutslab.form.inc;

import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.FormFile;

public class IncidentForm extends ActionForm {
    private String incidentNo;
    private Integer resultId;
    private String incidentDateTime;
    private String finder;
    private String equipmentCode;
    private String equipmentName;
    private String weather;
    private Integer temperature;
    private String incidentType;
    private String severity;
    private String incidentPart;
    private String incidentDetail;
    private String tmpAction;
    private String tmpActionPerson;
    private String tmpActionDate;
    private String cause;
    private String counterDetail;
    private String status;
    private String fromInspection;
    private FormFile[] files;

    public String getIncidentNo() { return incidentNo; }
    public void setIncidentNo(String v) { this.incidentNo = v; }
    public Integer getResultId() { return resultId; }
    public void setResultId(Integer v) { this.resultId = v; }
    public String getIncidentDateTime() { return incidentDateTime; }
    public void setIncidentDateTime(String v) { this.incidentDateTime = v; }
    public String getFinder() { return finder; }
    public void setFinder(String v) { this.finder = v; }
    public String getEquipmentCode() { return equipmentCode; }
    public void setEquipmentCode(String v) { this.equipmentCode = v; }
    public String getEquipmentName() { return equipmentName; }
    public void setEquipmentName(String v) { this.equipmentName = v; }
    public String getWeather() { return weather; }
    public void setWeather(String v) { this.weather = v; }
    public Integer getTemperature() { return temperature; }
    public void setTemperature(Integer v) { this.temperature = v; }
    public String getIncidentType() { return incidentType; }
    public void setIncidentType(String v) { this.incidentType = v; }
    public String getSeverity() { return severity; }
    public void setSeverity(String v) { this.severity = v; }
    public String getIncidentPart() { return incidentPart; }
    public void setIncidentPart(String v) { this.incidentPart = v; }
    public String getIncidentDetail() { return incidentDetail; }
    public void setIncidentDetail(String v) { this.incidentDetail = v; }
    public String getTmpAction() { return tmpAction; }
    public void setTmpAction(String v) { this.tmpAction = v; }
    public String getTmpActionPerson() { return tmpActionPerson; }
    public void setTmpActionPerson(String v) { this.tmpActionPerson = v; }
    public String getTmpActionDate() { return tmpActionDate; }
    public void setTmpActionDate(String v) { this.tmpActionDate = v; }
    public String getCause() { return cause; }
    public void setCause(String v) { this.cause = v; }
    public String getCounterDetail() { return counterDetail; }
    public void setCounterDetail(String v) { this.counterDetail = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }
    public String getFromInspection() { return fromInspection; }
    public void setFromInspection(String v) { this.fromInspection = v; }
    public FormFile[] getFiles() { return files; }
    public void setFiles(FormFile[] v) { this.files = v; }
}
