package com.strutslab.action.ins;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.strutslab.dto.ChkTmplDto;
import com.strutslab.dto.EqpDto;
import com.strutslab.form.ins.PlanWizardForm;
import com.strutslab.service.ins.PlanWizardService;

public class PlanWizardAction extends DispatchAction {

    private final PlanWizardService service = new PlanWizardService();

    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.getSession().removeAttribute("planWizardForm");
        return step1(mapping, form, request, response);
    }

    public ActionForward step1(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        PlanWizardForm wf = getWizardForm(request, form);
        request.setAttribute("eqpList", service.loadEquipmentList());
        wf.setStep(1);
        saveWizardForm(request, wf);
        return mapping.findForward("step1");
    }

    public ActionForward step2(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        PlanWizardForm wf = getWizardForm(request, form);

        if (wf.getSelectedEqpCode() == null || wf.getSelectedEqpCode().isEmpty()) {
            request.setAttribute("errorMessage",
                getResources(request).getMessage("errors.equipment.required"));
            request.setAttribute("eqpList", service.loadEquipmentList());
            wf.setStep(1);
            saveWizardForm(request, wf);
            return mapping.findForward("step1");
        }

        EqpDto eqp = service.loadEquipment(wf.getSelectedEqpCode());
        if (eqp != null) {
            wf.setSelectedEqpName(eqp.getEquipmentName());
            request.setAttribute("selectedEqpName", eqp.getEquipmentName());
            request.setAttribute("tmplList", service.loadTemplates(eqp.getEquipmentType()));
        }

        wf.setStep(2);
        saveWizardForm(request, wf);
        return mapping.findForward("step2");
    }

    public ActionForward step3(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        PlanWizardForm wf = getWizardForm(request, form);

        if (wf.getSelectedTmplId() <= 0) {
            request.setAttribute("errorMessage",
                getResources(request).getMessage("errors.template.required"));
            String eqpType = service.getEquipmentType(wf.getSelectedEqpCode());
            request.setAttribute("tmplList", service.loadTemplates(eqpType));
            wf.setStep(2);
            saveWizardForm(request, wf);
            return mapping.findForward("step2");
        }

        ChkTmplDto tmpl = service.loadTemplate(wf.getSelectedTmplId());
        if (tmpl == null) {
            request.setAttribute("errorMessage",
                getResources(request).getMessage("errors.template.required"));
            String eqpType = service.getEquipmentType(wf.getSelectedEqpCode());
            request.setAttribute("tmplList", service.loadTemplates(eqpType));
            wf.setStep(2);
            saveWizardForm(request, wf);
            return mapping.findForward("step2");
        }
        wf.setSelectedTmplName(tmpl.getTemplateName());
        wf.setStep(3);
        saveWizardForm(request, wf);
        request.setAttribute("empList", service.loadEmployees());
        return mapping.findForward("step3");
    }

    public ActionForward confirm(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        PlanWizardForm wf = getWizardForm(request, form);

        StringBuilder errors = new StringBuilder();
        if (wf.getPlanDate() == null || wf.getPlanDate().isEmpty()) {
            errors.append(getResources(request).getMessage("errors.required", "予定日"));
        }
        if (wf.getTeamCode() == null || wf.getTeamCode().isEmpty()) {
            if (errors.length() > 0) errors.append("<br>");
            errors.append(getResources(request).getMessage("errors.required", "担当班"));
        }

        if (errors.length() > 0) {
            request.setAttribute("errorMessage", errors.toString());
            wf.setStep(3);
            saveWizardForm(request, wf);
            return mapping.findForward("step3");
        }

        wf.setStep(4);
        saveWizardForm(request, wf);
        request.setAttribute("selEqpName", wf.getSelectedEqpName());
        request.setAttribute("selTmplName", wf.getSelectedTmplName());
        request.setAttribute("planDate", wf.getPlanDate());
        request.setAttribute("teamCode", wf.getTeamCode());
        request.setAttribute("personCode", wf.getPersonCode());
        request.setAttribute("note", wf.getNote());
        return mapping.findForward("confirm");
    }

    public ActionForward save(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        PlanWizardForm wf = getWizardForm(request, form);
        service.savePlan(wf, "予定");
        request.getSession().removeAttribute("planWizardForm");
        return mapping.findForward("success");
    }

    public ActionForward tempSave(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        PlanWizardForm wf = getWizardForm(request, form);
        service.savePlan(wf, "一時保存");
        request.getSession().removeAttribute("planWizardForm");
        return mapping.findForward("success");
    }

    private PlanWizardForm getWizardForm(HttpServletRequest request, ActionForm form) {
        HttpSession session = request.getSession();
        PlanWizardForm wf = (PlanWizardForm) session.getAttribute("planWizardForm");
        if (wf == null) {
            wf = (PlanWizardForm) form;
            session.setAttribute("planWizardForm", wf);
        } else {
            PlanWizardForm incoming = (PlanWizardForm) form;
            if (incoming.getSelectedEqpCode() != null && !incoming.getSelectedEqpCode().isEmpty())
                wf.setSelectedEqpCode(incoming.getSelectedEqpCode());
            if (incoming.getSelectedTmplId() > 0)
                wf.setSelectedTmplId(incoming.getSelectedTmplId());
            if (incoming.getPlanDate() != null && !incoming.getPlanDate().isEmpty())
                wf.setPlanDate(incoming.getPlanDate());
            if (incoming.getTeamCode() != null && !incoming.getTeamCode().isEmpty())
                wf.setTeamCode(incoming.getTeamCode());
            if (incoming.getPersonCode() != null && !incoming.getPersonCode().isEmpty())
                wf.setPersonCode(incoming.getPersonCode());
            if (incoming.getNote() != null && !incoming.getNote().isEmpty())
                wf.setNote(incoming.getNote());
        }
        return wf;
    }

    private void saveWizardForm(HttpServletRequest request, PlanWizardForm wf) {
        request.getSession().setAttribute("planWizardForm", wf);
    }
}
