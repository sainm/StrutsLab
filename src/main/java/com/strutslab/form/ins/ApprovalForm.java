package com.strutslab.form.ins;

import org.apache.struts.action.ActionForm;

public class ApprovalForm extends ActionForm {
    private String dateFrom;
    private String dateTo;
    private String team;
    private String status;
    private int[] selectedItems;
    private String rejectReason;

    public String getDateFrom() { return dateFrom; }
    public void setDateFrom(String v) { this.dateFrom = v; }
    public String getDateTo() { return dateTo; }
    public void setDateTo(String v) { this.dateTo = v; }
    public String getTeam() { return team; }
    public void setTeam(String v) { this.team = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }
    public int[] getSelectedItems() { return selectedItems; }
    public void setSelectedItems(int[] v) { this.selectedItems = v; }
    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String v) { this.rejectReason = v; }
}
