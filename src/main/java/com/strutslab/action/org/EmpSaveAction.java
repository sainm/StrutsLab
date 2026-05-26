package com.strutslab.action.org;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.strutslab.dto.EmpDto;
import com.strutslab.form.org.EmpForm;
import com.strutslab.service.org.OrgService;

public class EmpSaveAction extends DispatchAction {

    private final OrgService service = new OrgService();

    @Override
    protected ActionForward dispatchMethod(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response, String name)
            throws Exception {
        if ("new".equals(name)) {
            return newMethod(mapping, form, request, response);
        }
        return super.dispatchMethod(mapping, form, request, response, name);
    }

    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        EmpForm empForm = (EmpForm) form;
        String empNo = request.getParameter("empNo");

        if (empNo != null && !empNo.isEmpty()) {
            EmpDto dto = service.findEmpById(empNo);
            if (dto != null) {
                empForm.setEmpNo(dto.getEmpNo());
                empForm.setName(dto.getName());
                empForm.setNameKana(dto.getNameKana());
                empForm.setBirthDate(dto.getBirthDate());
                empForm.setJoinDate(dto.getJoinDate());
                empForm.setDeptCode(dto.getDeptCode());
                empForm.setPosition(dto.getPosition());
                empForm.setAssignDate(dto.getAssignDate());
                empForm.setInspectionRank(dto.getInspectionRank());
                empForm.setInspectionCertDate(dto.getInspectionCertDate());
                empForm.setInspectionCertExpire(dto.getInspectionCertExpire());
                empForm.setLoginId(dto.getLoginId());
            }
        }
        return mapping.findForward("success");
    }

    public ActionForward newMethod(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        EmpForm empForm = (EmpForm) form;
        empForm.setEmpNo(null);
        empForm.setName(null);
        empForm.setNameKana(null);
        empForm.setBirthDate(null);
        empForm.setJoinDate(null);
        empForm.setDeptCode(null);
        empForm.setPosition(null);
        empForm.setAssignDate(null);
        empForm.setQualifications(null);
        empForm.setInspectionRank(null);
        empForm.setInspectionCertDate(null);
        empForm.setInspectionCertExpire(null);
        empForm.setLoginId(null);
        empForm.setPassword(null);
        empForm.setPasswordConfirm(null);
        return mapping.findForward("success");
    }

    public ActionForward save(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        EmpForm empForm = (EmpForm) form;

        String errorMsg = validate(empForm, request);
        if (errorMsg != null) {
            request.setAttribute("errorMessage", errorMsg);
            return mapping.getInputForward();
        }

        if (empForm.getInspectionCertExpire() != null && !empForm.getInspectionCertExpire().isEmpty()) {
            String today = new java.text.SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
            if (empForm.getInspectionCertExpire().compareTo(today) < 0) {
                request.setAttribute("errorMessage",
                    getResources(request).getMessage("errors.future"));
                return mapping.getInputForward();
            }
        }

        if (empForm.getLoginId() != null && !empForm.getLoginId().isEmpty()) {
            EmpDto existing = service.findEmpByLoginId(empForm.getLoginId());
            if (existing != null && !existing.getEmpNo().equals(empForm.getEmpNo())) {
                request.setAttribute("errorMessage",
                    getResources(request).getMessage("errors.loginid.duplicate", empForm.getLoginId()));
                return mapping.getInputForward();
            }
        }

        EmpDto dto = new EmpDto();
        dto.setEmpNo(empForm.getEmpNo());
        dto.setName(empForm.getName());
        dto.setNameKana(empForm.getNameKana());
        dto.setBirthDate(empForm.getBirthDate());
        dto.setJoinDate(empForm.getJoinDate());
        dto.setDeptCode(empForm.getDeptCode());
        dto.setPosition(empForm.getPosition());
        dto.setAssignDate(empForm.getAssignDate());
        dto.setInspectionRank(empForm.getInspectionRank());
        dto.setInspectionCertDate(empForm.getInspectionCertDate());
        dto.setInspectionCertExpire(empForm.getInspectionCertExpire());
        dto.setLoginId(empForm.getLoginId());

        service.saveEmp(dto, empForm.getPassword());

        return mapping.findForward("success");
    }

    private String validate(EmpForm form, HttpServletRequest request) {
        if (form.getName() == null || form.getName().trim().isEmpty()) {
            return getResources(request).getMessage("errors.name.required");
        }

        if (form.getNameKana() != null && !form.getNameKana().isEmpty()) {
            if (!form.getNameKana().matches("[\\u30A0-\\u30FF\\u3000\\s]+")) {
                return getResources(request).getMessage("errors.kana");
            }
        }

        if (form.getLoginId() == null || form.getLoginId().isEmpty()) {
            return getResources(request).getMessage("label.loginId");
        }

        boolean isNew = form.getEmpNo() == null || form.getEmpNo().isEmpty();
        if (isNew && (form.getPassword() == null || form.getPassword().isEmpty())) {
            return getResources(request).getMessage("errors.password.required");
        }

        if (form.getPassword() != null && !form.getPassword().isEmpty()) {
            if (form.getPassword().length() < 6) {
                return getResources(request).getMessage("errors.password.minlength", "6");
            }
            if (!form.getPassword().equals(form.getPasswordConfirm())) {
                return getResources(request).getMessage("errors.password.mismatch");
            }
        }

        if (form.getInspectionCertExpire() != null && !form.getInspectionCertExpire().isEmpty()) {
            if (form.getInspectionCertDate() == null || form.getInspectionCertDate().isEmpty()) {
                return getResources(request).getMessage("errors.required", "認定日");
            }
        }

        return null;
    }
}
