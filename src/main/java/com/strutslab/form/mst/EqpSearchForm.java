package com.strutslab.form.mst;

import org.apache.struts.action.ActionForm;

public class EqpSearchForm extends ActionForm {
    private String equipmentType;
    private String voltageLevel;
    private String yearFrom;
    private String yearTo;
    private String maintenanceRank;
    private String deptName;
    private int page = 1;
    private String[] selectedItems;

    public String getEquipmentType() { return equipmentType; }
    public void setEquipmentType(String v) { this.equipmentType = v; }
    public String getVoltageLevel() { return voltageLevel; }
    public void setVoltageLevel(String v) { this.voltageLevel = v; }
    public String getYearFrom() { return yearFrom; }
    public void setYearFrom(String v) { this.yearFrom = v; }
    public String getYearTo() { return yearTo; }
    public void setYearTo(String v) { this.yearTo = v; }
    public String getMaintenanceRank() { return maintenanceRank; }
    public void setMaintenanceRank(String v) { this.maintenanceRank = v; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String v) { this.deptName = v; }
    public int getPage() { return page; }
    public void setPage(int v) { this.page = v; }
    public String[] getSelectedItems() { return selectedItems; }
    public void setSelectedItems(String[] v) { this.selectedItems = v; }
}
