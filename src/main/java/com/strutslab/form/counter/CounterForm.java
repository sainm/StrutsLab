package com.strutslab.form.counter;

import org.apache.struts.validator.ValidatorForm;

public class CounterForm extends ValidatorForm {
    private String orderNo;
    private String incidentNo;
    private String orderDate;
    private String issuer;
    private String overallDeadline;
    private String overallPriority;
    private String method;

    // Indexed properties for details
    private String[] detailWorkContents;
    private String[] detailPersons;
    private String[] detailDeadlines;
    private String[] detailPriorities;

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
    public String getMethod() { return method; }
    public void setMethod(String v) { this.method = v; }

    public String[] getDetailWorkContents() { return detailWorkContents; }
    public void setDetailWorkContents(String[] v) { this.detailWorkContents = v; }
    public String[] getDetailPersons() { return detailPersons; }
    public void setDetailPersons(String[] v) { this.detailPersons = v; }
    public String[] getDetailDeadlines() { return detailDeadlines; }
    public void setDetailDeadlines(String[] v) { this.detailDeadlines = v; }
    public String[] getDetailPriorities() { return detailPriorities; }
    public void setDetailPriorities(String[] v) { this.detailPriorities = v; }

    // Indexed property accessors (Struts convention: details[N].workContent)
    public String getDetailWorkContent(int i) {
        if (detailWorkContents == null || i >= detailWorkContents.length) return "";
        return detailWorkContents[i];
    }
    public void setDetailWorkContent(int i, String v) {
        if (detailWorkContents == null) {
            detailWorkContents = new String[i + 1];
        } else if (i >= detailWorkContents.length) {
            String[] tmp = new String[i + 1];
            System.arraycopy(detailWorkContents, 0, tmp, 0, detailWorkContents.length);
            detailWorkContents = tmp;
        }
        detailWorkContents[i] = v;
    }
    public String getDetailPerson(int i) {
        if (detailPersons == null || i >= detailPersons.length) return "";
        return detailPersons[i];
    }
    public void setDetailPerson(int i, String v) {
        if (detailPersons == null) {
            detailPersons = new String[i + 1];
        } else if (i >= detailPersons.length) {
            String[] tmp = new String[i + 1];
            System.arraycopy(detailPersons, 0, tmp, 0, detailPersons.length);
            detailPersons = tmp;
        }
        detailPersons[i] = v;
    }
    public String getDetailDeadline(int i) {
        if (detailDeadlines == null || i >= detailDeadlines.length) return "";
        return detailDeadlines[i];
    }
    public void setDetailDeadline(int i, String v) {
        if (detailDeadlines == null) {
            detailDeadlines = new String[i + 1];
        } else if (i >= detailDeadlines.length) {
            String[] tmp = new String[i + 1];
            System.arraycopy(detailDeadlines, 0, tmp, 0, detailDeadlines.length);
            detailDeadlines = tmp;
        }
        detailDeadlines[i] = v;
    }
    public String getDetailPriority(int i) {
        if (detailPriorities == null || i >= detailPriorities.length) return "";
        return detailPriorities[i];
    }
    public void setDetailPriority(int i, String v) {
        if (detailPriorities == null) {
            detailPriorities = new String[i + 1];
        } else if (i >= detailPriorities.length) {
            String[] tmp = new String[i + 1];
            System.arraycopy(detailPriorities, 0, tmp, 0, detailPriorities.length);
            detailPriorities = tmp;
        }
        detailPriorities[i] = v;
    }

    public int getDetailCount() {
        if (detailWorkContents == null) return 0;
        return detailWorkContents.length;
    }
}
