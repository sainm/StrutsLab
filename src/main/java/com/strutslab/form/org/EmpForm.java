package com.strutslab.form.org;

import org.apache.struts.validator.ValidatorForm;

public class EmpForm extends ValidatorForm {
    // Section 1: 基本情報
    private String empNo;
    private String name;
    private String nameKana;
    private String birthDate;
    private String joinDate;

    // Section 2: 所属・職位
    private String deptCode;
    private String deptName;
    private String position;
    private String assignDate;

    // Section 3: 保有資格・技能
    private String[] qualifications;
    private String[] qualCertDates;
    private String[] qualExpireDates;

    // Section 4: 点検員認定
    private String inspectionRank;
    private String inspectionCertDate;
    private String inspectionCertExpire;

    // Section 5: アカウント情報
    private String loginId;
    private String password;
    private String passwordConfirm;

    private String method;

    // Getters/Setters
    public String getEmpNo() { return empNo; }
    public void setEmpNo(String v) { this.empNo = v; }
    public String getName() { return name; }
    public void setName(String v) { this.name = v; }
    public String getNameKana() { return nameKana; }
    public void setNameKana(String v) { this.nameKana = v; }
    public String getBirthDate() { return birthDate; }
    public void setBirthDate(String v) { this.birthDate = v; }
    public String getJoinDate() { return joinDate; }
    public void setJoinDate(String v) { this.joinDate = v; }
    public String getDeptCode() { return deptCode; }
    public void setDeptCode(String v) { this.deptCode = v; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String v) { this.deptName = v; }
    public String getPosition() { return position; }
    public void setPosition(String v) { this.position = v; }
    public String getAssignDate() { return assignDate; }
    public void setAssignDate(String v) { this.assignDate = v; }
    public String[] getQualifications() { return qualifications; }
    public void setQualifications(String[] v) { this.qualifications = v; }
    public String[] getQualCertDates() { return qualCertDates; }
    public void setQualCertDates(String[] v) { this.qualCertDates = v; }
    public String[] getQualExpireDates() { return qualExpireDates; }
    public void setQualExpireDates(String[] v) { this.qualExpireDates = v; }
    public String getInspectionRank() { return inspectionRank; }
    public void setInspectionRank(String v) { this.inspectionRank = v; }
    public String getInspectionCertDate() { return inspectionCertDate; }
    public void setInspectionCertDate(String v) { this.inspectionCertDate = v; }
    public String getInspectionCertExpire() { return inspectionCertExpire; }
    public void setInspectionCertExpire(String v) { this.inspectionCertExpire = v; }
    public String getLoginId() { return loginId; }
    public void setLoginId(String v) { this.loginId = v; }
    public String getPassword() { return password; }
    public void setPassword(String v) { this.password = v; }
    public String getPasswordConfirm() { return passwordConfirm; }
    public void setPasswordConfirm(String v) { this.passwordConfirm = v; }
    public String getMethod() { return method; }
    public void setMethod(String v) { this.method = v; }
}
