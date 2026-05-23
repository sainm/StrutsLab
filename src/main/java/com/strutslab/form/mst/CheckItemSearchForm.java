package com.strutslab.form.mst;

import org.apache.struts.action.ActionForm;

public class CheckItemSearchForm extends ActionForm {
    private String equipmentType;
    private String inspectionKind;
    private int page = 1;

    public String getEquipmentType() { return equipmentType; }
    public void setEquipmentType(String v) { this.equipmentType = v; }
    public String getInspectionKind() { return inspectionKind; }
    public void setInspectionKind(String v) { this.inspectionKind = v; }
    public int getPage() { return page; }
    public void setPage(int v) { this.page = v; }
}
