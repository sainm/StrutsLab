package com.strutslab.form.inc;

import org.apache.struts.action.ActionForm;

public class IncidentSearchForm extends ActionForm {
    private String incDateFrom;
    private String incDateTo;
    private String equipmentType;
    private String incidentType;
    private String status;
    private String severity;
    private String team;
    private String keyword;
    private int page = 1;
    private String[] selectedItems;
    private String bulkStatus;

    public String getIncDateFrom() { return incDateFrom; }
    public void setIncDateFrom(String v) { this.incDateFrom = v; }
    public String getIncDateTo() { return incDateTo; }
    public void setIncDateTo(String v) { this.incDateTo = v; }
    public String getEquipmentType() { return equipmentType; }
    public void setEquipmentType(String v) { this.equipmentType = v; }
    public String getIncidentType() { return incidentType; }
    public void setIncidentType(String v) { this.incidentType = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }
    public String getSeverity() { return severity; }
    public void setSeverity(String v) { this.severity = v; }
    public String getTeam() { return team; }
    public void setTeam(String v) { this.team = v; }
    public String getKeyword() { return keyword; }
    public void setKeyword(String v) { this.keyword = v; }
    public int getPage() { return page; }
    public void setPage(int v) { this.page = v; }
    public String[] getSelectedItems() { return selectedItems; }
    public void setSelectedItems(String[] v) { this.selectedItems = v; }
    public String getBulkStatus() { return bulkStatus; }
    public void setBulkStatus(String v) { this.bulkStatus = v; }
}
