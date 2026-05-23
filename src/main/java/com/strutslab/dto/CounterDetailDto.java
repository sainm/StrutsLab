package com.strutslab.dto;

public class CounterDetailDto {
    private Integer seqNo;
    private String workContent;
    private String person;
    private String deadline;
    private String priority;

    public Integer getSeqNo() { return seqNo; }
    public void setSeqNo(Integer v) { this.seqNo = v; }
    public String getWorkContent() { return workContent; }
    public void setWorkContent(String v) { this.workContent = v; }
    public String getPerson() { return person; }
    public void setPerson(String v) { this.person = v; }
    public String getDeadline() { return deadline; }
    public void setDeadline(String v) { this.deadline = v; }
    public String getPriority() { return priority; }
    public void setPriority(String v) { this.priority = v; }
}
