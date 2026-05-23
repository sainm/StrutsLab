package com.strutslab.dto;

public class TimelineDto {
    private String actionDatetime;
    private String actionUser;
    private String actionContent;
    private String statusFrom;
    private String statusTo;

    public String getActionDatetime() { return actionDatetime; }
    public void setActionDatetime(String v) { this.actionDatetime = v; }
    public String getActionUser() { return actionUser; }
    public void setActionUser(String v) { this.actionUser = v; }
    public String getActionContent() { return actionContent; }
    public void setActionContent(String v) { this.actionContent = v; }
    public String getStatusFrom() { return statusFrom; }
    public void setStatusFrom(String v) { this.statusFrom = v; }
    public String getStatusTo() { return statusTo; }
    public void setStatusTo(String v) { this.statusTo = v; }
}
