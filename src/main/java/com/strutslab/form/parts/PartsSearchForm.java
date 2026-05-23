package com.strutslab.form.parts;

import org.apache.struts.action.ActionForm;

public class PartsSearchForm extends ActionForm {
    private String equipmentType;
    private String partType;
    private String stockStatus;
    private String keyword;
    private int page = 1;

    public String getEquipmentType() { return equipmentType; }
    public void setEquipmentType(String v) { this.equipmentType = v; }
    public String getPartType() { return partType; }
    public void setPartType(String v) { this.partType = v; }
    public String getStockStatus() { return stockStatus; }
    public void setStockStatus(String v) { this.stockStatus = v; }
    public String getKeyword() { return keyword; }
    public void setKeyword(String v) { this.keyword = v; }
    public int getPage() { return page; }
    public void setPage(int v) { this.page = v; }
}
