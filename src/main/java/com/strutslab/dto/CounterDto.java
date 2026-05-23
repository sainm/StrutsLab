package com.strutslab.dto;

public class CounterDto {
    private String orderNo;
    private String incidentNo;
    private String orderDate;
    private String issuer;
    private String overallDeadline;
    private String overallPriority;
    private String status;

    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String v) { this.orderNo = v; }
    public String getIncidentNo() { return incidentNo; }
    public void setIncidentNo(String v) { this.incidentNo = v; }
    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String v) { this.orderDate = v; }
    public String getIssuer() { return issuer; }
    public void setIssuer(String v) { this.issuer = v; }
    public String getOverallDeadline() { return overallDeadline; }
    public void setOverallDeadline(String v) { this.overallDeadline = v; }
    public String getOverallPriority() { return overallPriority; }
    public void setOverallPriority(String v) { this.overallPriority = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }
}
