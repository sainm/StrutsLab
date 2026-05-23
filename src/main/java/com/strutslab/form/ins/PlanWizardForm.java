package com.strutslab.form.ins;

import org.apache.struts.action.ActionForm;

public class PlanWizardForm extends ActionForm {
    private int step;
    private String selectedEqpCode;
    private int selectedTmplId;
    private String planDate;
    private String teamCode;
    private String personCode;
    private String note;
    private String selectedEqpName;
    private String selectedTmplName;

    public int getStep() { return step; }
    public void setStep(int v) { this.step = v; }
    public String getSelectedEqpCode() { return selectedEqpCode; }
    public void setSelectedEqpCode(String v) { this.selectedEqpCode = v; }
    public int getSelectedTmplId() { return selectedTmplId; }
    public void setSelectedTmplId(int v) { this.selectedTmplId = v; }
    public String getPlanDate() { return planDate; }
    public void setPlanDate(String v) { this.planDate = v; }
    public String getTeamCode() { return teamCode; }
    public void setTeamCode(String v) { this.teamCode = v; }
    public String getPersonCode() { return personCode; }
    public void setPersonCode(String v) { this.personCode = v; }
    public String getNote() { return note; }
    public void setNote(String v) { this.note = v; }
    public String getSelectedEqpName() { return selectedEqpName; }
    public void setSelectedEqpName(String v) { this.selectedEqpName = v; }
    public String getSelectedTmplName() { return selectedTmplName; }
    public void setSelectedTmplName(String v) { this.selectedTmplName = v; }
}
