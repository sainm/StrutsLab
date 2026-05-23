package com.strutslab.dto;

import java.sql.Timestamp;

public class TimelineDto {
    private int timelineId;
    private String incidentNo;
    private Timestamp actionDatetime;
    private String actionUser;
    private String actionContent;
    private String statusFrom;
    private String statusTo;

    public int getTimelineId() { return timelineId; }
    public void setTimelineId(int v) { this.timelineId = v; }
    public String getIncidentNo() { return incidentNo; }
    public void setIncidentNo(String v) { this.incidentNo = v; }
    public Timestamp getActionDatetime() { return actionDatetime; }
    public void setActionDatetime(Timestamp v) { this.actionDatetime = v; }
    public String getActionUser() { return actionUser; }
    public void setActionUser(String v) { this.actionUser = v; }
    public String getActionContent() { return actionContent; }
    public void setActionContent(String v) { this.actionContent = v; }
    public String getStatusFrom() { return statusFrom; }
    public void setStatusFrom(String v) { this.statusFrom = v; }
    public String getStatusTo() { return statusTo; }
    public void setStatusTo(String v) { this.statusTo = v; }
}
