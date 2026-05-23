package com.strutslab.form.counter;

import org.apache.struts.action.ActionForm;

public class CounterSearchForm extends ActionForm {
    private String dateFrom;
    private String dateTo;
    private String person;
    private String status;
    private String priority;
    private int page;
    private int[] selectedItems;

    public String getDateFrom() { return dateFrom; }
    public void setDateFrom(String v) { this.dateFrom = v; }
    public String getDateTo() { return dateTo; }
    public void setDateTo(String v) { this.dateTo = v; }
    public String getPerson() { return person; }
    public void setPerson(String v) { this.person = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }
    public String getPriority() { return priority; }
    public void setPriority(String v) { this.priority = v; }
    public int getPage() { return page; }
    public void setPage(int v) { this.page = v; }
    public int[] getSelectedItems() { return selectedItems; }
    public void setSelectedItems(int[] v) { this.selectedItems = v; }
}
