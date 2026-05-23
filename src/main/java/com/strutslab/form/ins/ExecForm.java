package com.strutslab.form.ins;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.upload.FormFile;
import org.apache.struts.validator.ValidatorForm;

public class ExecForm extends ValidatorForm {
    private int planId;
    private int resultId;
    private String executedDate;
    private String[] execJudge;
    private String[] execValue;
    private String[] execNote;
    private FormFile[][] execPhoto;
    private String summaryJudge;
    private String summaryNote;
    private String modifyReason;
    private int maxItems;

    public int getPlanId() { return planId; }
    public void setPlanId(int v) { this.planId = v; }
    public int getResultId() { return resultId; }
    public void setResultId(int v) { this.resultId = v; }
    public String getExecutedDate() { return executedDate; }
    public void setExecutedDate(String v) { this.executedDate = v; }
    public String[] getExecJudge() { return execJudge; }
    public void setExecJudge(String[] v) { this.execJudge = v; }
    public String[] getExecValue() { return execValue; }
    public void setExecValue(String[] v) { this.execValue = v; }
    public String[] getExecNote() { return execNote; }
    public void setExecNote(String[] v) { this.execNote = v; }
    public FormFile[][] getExecPhoto() { return execPhoto; }
    public void setExecPhoto(FormFile[][] v) { this.execPhoto = v; }
    public String getSummaryJudge() { return summaryJudge; }
    public void setSummaryJudge(String v) { this.summaryJudge = v; }
    public String getSummaryNote() { return summaryNote; }
    public void setSummaryNote(String v) { this.summaryNote = v; }
    public String getModifyReason() { return modifyReason; }
    public void setModifyReason(String v) { this.modifyReason = v; }
    public int getMaxItems() { return maxItems; }
    public void setMaxItems(int v) { this.maxItems = v; }

    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = super.validate(mapping, request);
        if (errors == null) errors = new ActionErrors();

        // Conditional validation for checklist items
        if (execJudge != null) {
            for (int i = 0; i < execJudge.length; i++) {
                String judge = execJudge[i];
                if ("×".equals(judge)) {
                    // value required for ×
                    if (execValue == null || execValue.length <= i
                            || execValue[i] == null || execValue[i].trim().isEmpty()) {
                        errors.add("execValue", new ActionMessage("errors.required", "点検値"));
                    }
                    // note required for ×
                    if (execNote == null || execNote.length <= i
                            || execNote[i] == null || execNote[i].trim().isEmpty()) {
                        errors.add("execNote", new ActionMessage("errors.required", "備考"));
                    }
                } else if ("△".equals(judge)) {
                    // note required for △
                    if (execNote == null || execNote.length <= i
                            || execNote[i] == null || execNote[i].trim().isEmpty()) {
                        errors.add("execNote", new ActionMessage("errors.required", "備考"));
                    }
                }
            }
        }

        return errors;
    }
}
