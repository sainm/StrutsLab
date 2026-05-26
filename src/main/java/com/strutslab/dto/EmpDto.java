package com.strutslab.dto;

public class EmpDto {
    private String empNo;
    private String name;
    private String nameKana;
    private String birthDate;
    private String joinDate;
    private String deptCode;
    private String position;
    private String assignDate;
    private String inspectionRank;
    private String inspectionCertDate;
    private String inspectionCertExpire;
    private String loginId;
    private String passwordHash;
    private String passwordSalt;
    private Boolean isLocked;

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
    public String getPosition() { return position; }
    public void setPosition(String v) { this.position = v; }
    public String getAssignDate() { return assignDate; }
    public void setAssignDate(String v) { this.assignDate = v; }
    public String getInspectionRank() { return inspectionRank; }
    public void setInspectionRank(String v) { this.inspectionRank = v; }
    public String getInspectionCertDate() { return inspectionCertDate; }
    public void setInspectionCertDate(String v) { this.inspectionCertDate = v; }
    public String getInspectionCertExpire() { return inspectionCertExpire; }
    public void setInspectionCertExpire(String v) { this.inspectionCertExpire = v; }
    public String getLoginId() { return loginId; }
    public void setLoginId(String v) { this.loginId = v; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String v) { this.passwordHash = v; }
    public String getPasswordSalt() { return passwordSalt; }
    public void setPasswordSalt(String v) { this.passwordSalt = v; }
    public Boolean getIsLocked() { return isLocked; }
    public void setIsLocked(Boolean v) { this.isLocked = v; }
}
