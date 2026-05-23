package com.strutslab.dto;

public class CounterDetailDto {
    private int detailId;
    private String orderNo;
    private int seqNo;
    private String workContent;
    private String personCode;
    private String personName;
    private String deadline;
    private String priority;
    private String status;
    private Double actualHours;
    private String usedPartCode;
    private Integer usedQuantity;
    private String note;

    public int getDetailId() { return detailId; }
    public void setDetailId(int v) { this.detailId = v; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String v) { this.orderNo = v; }
    public int getSeqNo() { return seqNo; }
    public void setSeqNo(int v) { this.seqNo = v; }
    public String getWorkContent() { return workContent; }
    public void setWorkContent(String v) { this.workContent = v; }
    public String getPersonCode() { return personCode; }
    public void setPersonCode(String v) { this.personCode = v; }
    public String getPersonName() { return personName; }
    public void setPersonName(String v) { this.personName = v; }
    public String getDeadline() { return deadline; }
    public void setDeadline(String v) { this.deadline = v; }
    public String getPriority() { return priority; }
    public void setPriority(String v) { this.priority = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }
    public Double getActualHours() { return actualHours; }
    public void setActualHours(Double v) { this.actualHours = v; }
    public String getUsedPartCode() { return usedPartCode; }
    public void setUsedPartCode(String v) { this.usedPartCode = v; }
    public Integer getUsedQuantity() { return usedQuantity; }
    public void setUsedQuantity(Integer v) { this.usedQuantity = v; }
    public String getNote() { return note; }
    public void setNote(String v) { this.note = v; }
}
