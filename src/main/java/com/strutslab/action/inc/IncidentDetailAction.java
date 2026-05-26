package com.strutslab.action.inc;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.actions.DispatchAction;

import com.strutslab.dto.IncidentDto;
import com.strutslab.dto.TimelineDto;
import com.strutslab.form.inc.IncidentForm;
import com.strutslab.service.inc.IncidentDetailService;

public class IncidentDetailAction extends DispatchAction {

    private final IncidentDetailService service = new IncidentDetailService();

    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadDetail(mapping, form, request, response);
    }

    public ActionForward investigate(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        return transition(mapping, form, request, response, "調査中",
                "調査を開始", null, null);
    }

    public ActionForward counter(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        IncidentForm incForm = (IncidentForm) form;
        if (incForm.getCause() == null || incForm.getCause().trim().isEmpty()) {
            ActionMessages errors = new ActionMessages();
            errors.add("cause", new ActionMessage("errors.required", "原因"));
            saveErrors(request, errors);
            return loadDetail(mapping, form, request, response);
        }
        return transition(mapping, form, request, response, "対応中",
                "対応を開始", "cause", incForm.getCause());
    }

    public ActionForward complete(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        IncidentForm incForm = (IncidentForm) form;
        if (incForm.getCounterDetail() == null || incForm.getCounterDetail().trim().isEmpty()) {
            ActionMessages errors = new ActionMessages();
            errors.add("counterDetail", new ActionMessage("errors.required", "対応内容"));
            saveErrors(request, errors);
            return loadDetail(mapping, form, request, response);
        }
        return transition(mapping, form, request, response, "完了",
                "対応完了", "counterDetail", incForm.getCounterDetail());
    }

    public ActionForward closeIncident(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        return transition(mapping, form, request, response, "クローズ",
                "クローズ", null, null);
    }

    public ActionForward capa(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        IncidentForm incForm = (IncidentForm) form;
        request.getSession().setAttribute("capaIncidentNo", incForm.getIncidentNo());
        return mapping.findForward("capa");
    }

    private ActionForward loadDetail(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        IncidentForm incForm = (IncidentForm) form;
        String incidentNo = request.getParameter("incidentNo");
        if (incidentNo == null || incidentNo.isEmpty()) {
            incidentNo = incForm.getIncidentNo();
        }
        if (incidentNo == null || incidentNo.isEmpty()) {
            ActionMessages errors = new ActionMessages();
            errors.add("incidentNo", new ActionMessage("errors.required", "報告番号"));
            saveErrors(request, errors);
            return mapping.findForward("success");
        }

        IncidentDto dto = service.findById(incidentNo);
        if (dto == null) {
            ActionMessages errors = new ActionMessages();
            errors.add("incidentNo", new ActionMessage("errors.record", incidentNo));
            saveErrors(request, errors);
            return mapping.findForward("success");
        }

        dtoToForm(dto, incForm);

        List<TimelineDto> timeline = service.getTimeline(incidentNo);
        request.setAttribute("timeline", timeline);
        request.setAttribute("incident", dto);

        return mapping.findForward("success");
    }

    private ActionForward transition(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response,
            String newStatus, String actionContent,
            String fieldToUpdate, String fieldValue) throws Exception {
        IncidentForm incForm = (IncidentForm) form;
        String incidentNo = incForm.getIncidentNo();
        if (incidentNo == null || incidentNo.isEmpty()) {
            incidentNo = request.getParameter("incidentNo");
        }
        if (incidentNo == null || incidentNo.isEmpty()) {
            ActionMessages errors = new ActionMessages();
            errors.add("incidentNo", new ActionMessage("errors.required", "報告番号"));
            saveErrors(request, errors);
            return mapping.findForward("success");
        }

        String user = (String) request.getSession().getAttribute("loginUser");
        if (user == null) user = "system";

        service.transition(incidentNo, newStatus, fieldToUpdate, fieldValue, user, actionContent);

        IncidentDto dto = service.findById(incidentNo);
        dtoToForm(dto, incForm);

        List<TimelineDto> timelineList = service.getTimeline(incidentNo);
        request.setAttribute("timeline", timelineList);
        request.setAttribute("incident", dto);

        String methodName = request.getParameter("method");
        if (methodName != null && !methodName.isEmpty()) {
            return mapping.findForward(methodName);
        }
        return mapping.findForward("success");
    }

    private void dtoToForm(IncidentDto dto, IncidentForm form) {
        form.setIncidentNo(dto.getIncidentNo());
        form.setResultId(dto.getResultId());
        if (dto.getIncidentDatetime() != null) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm");
            form.setIncidentDateTime(sdf.format(dto.getIncidentDatetime()));
        }
        form.setFinder(dto.getFinder());
        form.setEquipmentCode(dto.getEquipmentCode());
        form.setEquipmentName(dto.getEquipmentName());
        form.setWeather(dto.getWeather());
        form.setTemperature(dto.getTemperature());
        form.setIncidentType(dto.getIncidentType());
        form.setSeverity(dto.getSeverity());
        form.setIncidentPart(dto.getIncidentPart());
        form.setIncidentDetail(dto.getIncidentDetail());
        form.setTmpAction(dto.getTmpAction());
        form.setTmpActionPerson(dto.getTmpActionPerson());
        if (dto.getTmpActionDate() != null) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy/MM/dd");
            form.setTmpActionDate(sdf.format(dto.getTmpActionDate()));
        }
        form.setCause(dto.getCause());
        form.setCounterDetail(dto.getCounterDetail());
        form.setStatus(dto.getStatus());
    }
}
