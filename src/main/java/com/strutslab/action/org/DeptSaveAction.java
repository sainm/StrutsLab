package com.strutslab.action.org;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.strutslab.dto.DeptDto;
import com.strutslab.form.org.DeptForm;
import com.strutslab.service.org.OrgService;

public class DeptSaveAction extends DispatchAction {

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
        DeptForm deptForm = (DeptForm) form;
        String deptCode = request.getParameter("deptCode");

        if (deptCode != null && !deptCode.isEmpty()) {
            DeptDto dto = service.findDeptById(deptCode);
            if (dto != null) {
                deptForm.setDeptCode(dto.getDeptCode());
                deptForm.setDeptName(dto.getDeptName());
                deptForm.setParentDeptCode(dto.getParentDeptCode());
                deptForm.setParentDeptName(dto.getParentDeptName());
                deptForm.setDeptLevel(dto.getDeptLevel());
                deptForm.setDeptType(dto.getDeptType());
                deptForm.setStartDate(dto.getStartDate());
                deptForm.setEndDate(dto.getEndDate());
                deptForm.setAddress(dto.getAddress());
                deptForm.setTel(dto.getTel());
            }
        }
        return mapping.findForward("success");
    }

    public ActionForward newMethod(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        DeptForm deptForm = (DeptForm) form;
        deptForm.setDeptCode(null);
        deptForm.setDeptName(null);
        deptForm.setParentDeptCode(null);
        deptForm.setParentDeptName(null);
        deptForm.setDeptLevel(1);
        deptForm.setDeptType(null);
        deptForm.setStartDate(null);
        deptForm.setEndDate(null);
        deptForm.setAddress(null);
        deptForm.setTel(null);
        return mapping.findForward("success");
    }

    public ActionForward save(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        DeptForm deptForm = (DeptForm) form;

        String errorMsg = validate(deptForm, request);
        if (errorMsg != null) {
            request.setAttribute("errorMessage", errorMsg);
            return mapping.getInputForward();
        }

        if (deptForm.getEndDate() != null && !deptForm.getEndDate().isEmpty()) {
            String today = new java.text.SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
            if (deptForm.getEndDate().compareTo(today) < 0) {
                request.setAttribute("warningMessage", "終了日が過去日付です。異動・廃止履歴として記録されます。");
            }
        }

        DeptDto dto = new DeptDto();
        dto.setDeptCode(deptForm.getDeptCode());
        dto.setDeptName(deptForm.getDeptName());
        dto.setParentDeptCode(deptForm.getParentDeptCode());
        dto.setDeptLevel(deptForm.getDeptLevel());
        dto.setDeptType(deptForm.getDeptType());
        dto.setStartDate(deptForm.getStartDate());
        dto.setEndDate(deptForm.getEndDate());
        dto.setAddress(deptForm.getAddress());
        dto.setTel(deptForm.getTel());

        service.saveDept(dto);
        return mapping.findForward("success");
    }

    private String validate(DeptForm form, HttpServletRequest request) {
        if (form.getDeptName() == null || form.getDeptName().trim().isEmpty()) {
            return getResources(request).getMessage("errors.dept.name.required");
        }
        if (form.getDeptName().length() > 100) {
            return getResources(request).getMessage("errors.maxlength",
                new Object[]{getResources(request).getMessage("label.deptName"), "100"});
        }
        if (form.getStartDate() == null || form.getStartDate().isEmpty()) {
            return getResources(request).getMessage("errors.required",
                getResources(request).getMessage("label.startDate"));
        }
        if (form.getDeptType() == null || form.getDeptType().isEmpty()) {
            return getResources(request).getMessage("errors.dept.type.required");
        }
        return null;
    }
}
