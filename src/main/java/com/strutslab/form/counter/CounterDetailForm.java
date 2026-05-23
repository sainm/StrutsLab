package com.strutslab.form.counter;

import org.apache.struts.action.ActionForm;

public class CounterDetailForm extends ActionForm {
    private String orderNo;
    private String method;
    private Integer detailIndex;

    // Completion fields (one per detail)
    private Double[] actualHours;
    private String[] usedPartCodes;
    private Integer[] usedQuantities;
    private String[] notes;

    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String v) { this.orderNo = v; }
    public String getMethod() { return method; }
    public void setMethod(String v) { this.method = v; }
    public Integer getDetailIndex() { return detailIndex; }
    public void setDetailIndex(Integer v) { this.detailIndex = v; }

    public Double[] getActualHours() { return actualHours; }
    public void setActualHours(Double[] v) { this.actualHours = v; }
    public String[] getUsedPartCodes() { return usedPartCodes; }
    public void setUsedPartCodes(String[] v) { this.usedPartCodes = v; }
    public Integer[] getUsedQuantities() { return usedQuantities; }
    public void setUsedQuantities(Integer[] v) { this.usedQuantities = v; }
    public String[] getNotes() { return notes; }
    public void setNotes(String[] v) { this.notes = v; }

    // Indexed accessors
    public Double getActualHour(int i) {
        if (actualHours == null || i >= actualHours.length) return null;
        return actualHours[i];
    }
    public void setActualHour(int i, Double v) {
        if (actualHours == null) {
            actualHours = new Double[i + 1];
        } else if (i >= actualHours.length) {
            Double[] tmp = new Double[i + 1];
            System.arraycopy(actualHours, 0, tmp, 0, actualHours.length);
            actualHours = tmp;
        }
        actualHours[i] = v;
    }
    public String getUsedPartCode(int i) {
        if (usedPartCodes == null || i >= usedPartCodes.length) return "";
        return usedPartCodes[i];
    }
    public void setUsedPartCode(int i, String v) {
        if (usedPartCodes == null) {
            usedPartCodes = new String[i + 1];
        } else if (i >= usedPartCodes.length) {
            String[] tmp = new String[i + 1];
            System.arraycopy(usedPartCodes, 0, tmp, 0, usedPartCodes.length);
            usedPartCodes = tmp;
        }
        usedPartCodes[i] = v;
    }
    public Integer getUsedQuantity(int i) {
        if (usedQuantities == null || i >= usedQuantities.length) return null;
        return usedQuantities[i];
    }
    public void setUsedQuantity(int i, Integer v) {
        if (usedQuantities == null) {
            usedQuantities = new Integer[i + 1];
        } else if (i >= usedQuantities.length) {
            Integer[] tmp = new Integer[i + 1];
            System.arraycopy(usedQuantities, 0, tmp, 0, usedQuantities.length);
            usedQuantities = tmp;
        }
        usedQuantities[i] = v;
    }
    public String getNote(int i) {
        if (notes == null || i >= notes.length) return "";
        return notes[i];
    }
    public void setNote(int i, String v) {
        if (notes == null) {
            notes = new String[i + 1];
        } else if (i >= notes.length) {
            String[] tmp = new String[i + 1];
            System.arraycopy(notes, 0, tmp, 0, notes.length);
            notes = tmp;
        }
        notes[i] = v;
    }
}
