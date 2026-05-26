package com.strutslab.action.inc;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.actions.DispatchAction;

import com.strutslab.dto.IncidentDto;
import com.strutslab.dto.PlanDto;
import com.strutslab.form.inc.IncidentForm;
import com.strutslab.service.inc.IncidentCreateService;

public class IncidentCreateAction extends DispatchAction {

    private final IncidentCreateService service = new IncidentCreateService();

    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (request.getParameter("searchSimilar") != null) {
            return searchSimilar(mapping, form, request, response);
        }
        if (request.getParameter("tempSave") != null) {
            return tempSave(mapping, form, request, response);
        }
        if (request.getParameter("save") != null) {
            return save(mapping, form, request, response);
        }
        return showForm(mapping, form, request, response);
    }

    public ActionForward showForm(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        IncidentForm incForm = (IncidentForm) form;
        HttpSession session = request.getSession();

        String fromInspection = request.getParameter("fromInspection");
        if (fromInspection != null) {
            incForm.setFromInspection("true");
            IncidentForm tempData = (IncidentForm) session.getAttribute("incidentTempData");
            if (tempData != null) {
                incForm.setIncidentDateTime(tempData.getIncidentDateTime());
                incForm.setFinder(tempData.getFinder());
                incForm.setEquipmentCode(tempData.getEquipmentCode());
                incForm.setEquipmentName(tempData.getEquipmentName());
                incForm.setIncidentType(tempData.getIncidentType());
                incForm.setSeverity(tempData.getSeverity());
                incForm.setIncidentDetail(tempData.getIncidentDetail());
            }
        }

        String planId = request.getParameter("planId");
        if (planId != null && !planId.isEmpty()) {
            try {
                int pId = Integer.parseInt(planId);
                PlanDto plan = service.findPlanById(pId);
                if (plan != null) {
                    incForm.setEquipmentCode(plan.getEquipmentCode());
                    incForm.setEquipmentName(plan.getEquipmentName());
                }
            } catch (NumberFormatException e) {
                // ignore
            }
        }

        IncidentForm tempSaved = (IncidentForm) session.getAttribute("incidentTempSave");
        if (tempSaved != null && request.getParameter("loadTemp") != null) {
            copyForm(incForm, tempSaved);
            session.removeAttribute("incidentTempSave");
        }

        return mapping.findForward("input");
    }

    public ActionForward save(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        IncidentForm incForm = (IncidentForm) form;

        ActionMessages errors = validateForm(incForm);
        if (!errors.isEmpty()) {
            saveErrors(request, errors);
            return mapping.findForward("input");
        }

        String finder = (String) request.getSession().getAttribute("loginUser");
        if (finder == null) finder = "system";
        String uploadRoot = getServlet().getServletContext().getRealPath("/");

        service.save(incForm, finder, incForm.getFiles(), uploadRoot);

        request.getSession().removeAttribute("incidentTempSave");
        return mapping.findForward("success");
    }

    public ActionForward searchSimilar(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        IncidentForm incForm = (IncidentForm) form;
        List<IncidentDto> similarResults = service.searchSimilar(
                incForm.getIncidentType(), incForm.getIncidentPart());
        request.setAttribute("similarResults", similarResults);
        return mapping.findForward("similar");
    }

    public ActionForward tempSave(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        IncidentForm incForm = (IncidentForm) form;
        IncidentForm copy = new IncidentForm();
        copyForm(copy, incForm);
        request.getSession().setAttribute("incidentTempSave", copy);
        request.setAttribute("message", "一時保存しました。");
        return mapping.findForward("input");
    }

    private ActionMessages validateForm(IncidentForm form) {
        ActionMessages errors = new ActionMessages();
        if (isEmpty(form.getIncidentDateTime())) {
            errors.add("incidentDateTime", new ActionMessage("errors.required", "発生日時"));
        }
        if (isEmpty(form.getEquipmentCode())) {
            errors.add("equipmentCode", new ActionMessage("errors.required", "設備コード"));
        }
        if (isEmpty(form.getIncidentType())) {
            errors.add("incidentType", new ActionMessage("errors.required", "異常種別"));
        }
        if (isEmpty(form.getSeverity())) {
            errors.add("severity", new ActionMessage("errors.required", "重大度"));
        }
        if (isEmpty(form.getIncidentDetail())) {
            errors.add("incidentDetail", new ActionMessage("errors.required", "異常内容詳細"));
        }
        return errors;
    }

    private void copyForm(IncidentForm dest, IncidentForm src) {
        dest.setIncidentNo(src.getIncidentNo());
        dest.setResultId(src.getResultId());
        dest.setIncidentDateTime(src.getIncidentDateTime());
        dest.setFinder(src.getFinder());
        dest.setEquipmentCode(src.getEquipmentCode());
        dest.setEquipmentName(src.getEquipmentName());
        dest.setWeather(src.getWeather());
        dest.setTemperature(src.getTemperature());
        dest.setIncidentType(src.getIncidentType());
        dest.setSeverity(src.getSeverity());
        dest.setIncidentPart(src.getIncidentPart());
        dest.setIncidentDetail(src.getIncidentDetail());
        dest.setTmpAction(src.getTmpAction());
        dest.setTmpActionPerson(src.getTmpActionPerson());
        dest.setTmpActionDate(src.getTmpActionDate());
        dest.setCause(src.getCause());
        dest.setCounterDetail(src.getCounterDetail());
        dest.setStatus(src.getStatus());
        dest.setFromInspection(src.getFromInspection());
    }

    private boolean isEmpty(String s) { return s == null || s.trim().isEmpty(); }
}
