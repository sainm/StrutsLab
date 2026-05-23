package com.strutslab.action.inc;

import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.actions.DispatchAction;

import com.strutslab.dao.IncidentDao;
import com.strutslab.db.MyBatisUtil;
import com.strutslab.dto.IncidentDto;
import com.strutslab.dto.TimelineDto;
import com.strutslab.form.inc.IncidentForm;

public class IncidentDetailAction extends DispatchAction {

    /**
     * Default: load incident detail and timeline, display the page.
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        return loadDetail(mapping, form, request, response);
    }

    /**
     * Transition: 未了 → 調査中
     */
    public ActionForward investigate(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        return transition(mapping, form, request, response, "調査中",
                "調査を開始", null, null);
    }

    /**
     * Transition: 調査中 → 対応中 (cause required)
     */
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

    /**
     * Transition: 対応中 → 完了 (counterDetail required)
     */
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

    /**
     * Transition: 完了 → クローズ
     */
    public ActionForward closeIncident(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        return transition(mapping, form, request, response, "クローズ",
                "クローズ", null, null);
    }

    /**
     * Forward to CAPA creation with incident data.
     */
    public ActionForward capa(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        IncidentForm incForm = (IncidentForm) form;
        // Set incident data in session for CAPA action to read
        request.getSession().setAttribute("capaIncidentNo", incForm.getIncidentNo());
        return mapping.findForward("capa");
    }

    // ---- Private helpers ----

    private ActionForward loadDetail(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        IncidentForm incForm = (IncidentForm) form;
        String incidentNo = request.getParameter("incidentNo");

        if (incidentNo == null || incidentNo.isEmpty()) {
            // Try from form
            incidentNo = incForm.getIncidentNo();
        }

        if (incidentNo == null || incidentNo.isEmpty()) {
            ActionMessages errors = new ActionMessages();
            errors.add("incidentNo", new ActionMessage("errors.required", "報告番号"));
            saveErrors(request, errors);
            return mapping.findForward("success");
        }

        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            IncidentDao dao = sqlSession.getMapper(IncidentDao.class);

            IncidentDto dto = dao.findById(incidentNo);
            if (dto == null) {
                ActionMessages errors = new ActionMessages();
                errors.add("incidentNo", new ActionMessage("errors.record", incidentNo));
                saveErrors(request, errors);
                return mapping.findForward("success");
            }

            // Populate form from DTO
            dtoToForm(dto, incForm);

            // Load timeline
            java.util.List<TimelineDto> timeline = dao.getTimeline(incidentNo);
            request.setAttribute("timeline", timeline);
            request.setAttribute("incident", dto);
        }

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

        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            IncidentDao dao = sqlSession.getMapper(IncidentDao.class);

            IncidentDto dto = dao.findById(incidentNo);
            if (dto == null) {
                ActionMessages errors = new ActionMessages();
                errors.add("incidentNo", new ActionMessage("errors.record", incidentNo));
                saveErrors(request, errors);
                return mapping.findForward("success");
            }

            String oldStatus = dto.getStatus();

            // Update status
            dto.setStatus(newStatus);

            // Update optional field
            if ("cause".equals(fieldToUpdate)) {
                dto.setCause(fieldValue);
            } else if ("counterDetail".equals(fieldToUpdate)) {
                dto.setCounterDetail(fieldValue);
            }

            dao.update(dto);

            // Add timeline entry
            TimelineDto timeline = new TimelineDto();
            timeline.setIncidentNo(incidentNo);
            timeline.setActionDatetime(new Timestamp(System.currentTimeMillis()));
            String user = (String) request.getSession().getAttribute("loginUser");
            timeline.setActionUser(user != null ? user : "system");
            timeline.setActionContent(actionContent);
            timeline.setStatusFrom(oldStatus);
            timeline.setStatusTo(newStatus);
            dao.insertTimeline(timeline);

            sqlSession.commit();

            // Refresh form from updated DTO
            dtoToForm(dto, incForm);

            // Reload timeline
            java.util.List<TimelineDto> timelineList = dao.getTimeline(incidentNo);
            request.setAttribute("timeline", timelineList);
            request.setAttribute("incident", dto);
        }

        // Determine forward name based on method
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
