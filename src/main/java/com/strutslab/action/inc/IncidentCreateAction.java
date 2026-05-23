package com.strutslab.action.inc;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;

import com.strutslab.dao.IncidentDao;
import com.strutslab.db.MyBatisUtil;
import com.strutslab.dto.IncidentDto;
import com.strutslab.dto.TimelineDto;
import com.strutslab.form.inc.IncidentForm;

public class IncidentCreateAction extends DispatchAction {

    /**
     * Default method when no method parameter is specified.
     * Determines the operation based on submit button names.
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        IncidentForm incForm = (IncidentForm) form;
        HttpSession session = request.getSession();

        // Check which button was clicked
        if (request.getParameter("searchSimilar") != null) {
            return searchSimilar(mapping, form, request, response);
        }
        if (request.getParameter("tempSave") != null) {
            return tempSave(mapping, form, request, response);
        }
        if (request.getParameter("save") != null) {
            return save(mapping, form, request, response);
        }

        // Default: show form
        return showForm(mapping, form, request, response);
    }

    /**
     * Display the create form. If fromInspection is set, load data from session.
     */
    public ActionForward showForm(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        IncidentForm incForm = (IncidentForm) form;
        HttpSession session = request.getSession();

        // If coming from inspection result
        String fromInspection = request.getParameter("fromInspection");
        if (fromInspection != null) {
            incForm.setFromInspection("true");
            // Load pre-populated data from session (set by ExecInputAction)
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

        // Load equipment info if planId is provided
        String planId = request.getParameter("planId");
        if (planId != null && !planId.isEmpty()) {
            loadEquipmentFromPlan(incForm, planId);
        }

        // Load saved temp data from session
        IncidentForm tempSaved = (IncidentForm) session.getAttribute("incidentTempSave");
        if (tempSaved != null && request.getParameter("loadTemp") != null) {
            copyForm(incForm, tempSaved);
            session.removeAttribute("incidentTempSave");
        }

        return mapping.findForward("input");
    }

    /**
     * Save the incident record.
     */
    public ActionForward save(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        IncidentForm incForm = (IncidentForm) form;

        // Manual validation
        ActionMessages errors = validateForm(incForm);
        if (!errors.isEmpty()) {
            saveErrors(request, errors);
            return mapping.findForward("input");
        }

        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            IncidentDao dao = sqlSession.getMapper(IncidentDao.class);

            // Generate incident number
            String incidentNo = dao.generateIncidentNo();
            incForm.setIncidentNo(incidentNo);

            // Build DTO
            IncidentDto dto = formToDto(incForm);

            // Set initial status
            dto.setStatus("未了");

            // Insert
            dao.insert(dto);

            // Add timeline entry
            TimelineDto timeline = new TimelineDto();
            timeline.setIncidentNo(incidentNo);
            timeline.setActionDatetime(new Timestamp(System.currentTimeMillis()));
            String user = (String) request.getSession().getAttribute("loginUser");
            timeline.setActionUser(user != null ? user : "system");
            timeline.setActionContent("異常報告を登録");
            timeline.setStatusFrom("");
            timeline.setStatusTo("未了");
            dao.insertTimeline(timeline);

            // Save uploaded files
            FormFile[] files = incForm.getFiles();
            if (files != null) {
                saveAttachments(files, incidentNo);
            }

            sqlSession.commit();
        }

        // Clear session temp data
        request.getSession().removeAttribute("incidentTempSave");

        return mapping.findForward("success");
    }

    /**
     * Search for similar incidents by type and part.
     */
    public ActionForward searchSimilar(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        IncidentForm incForm = (IncidentForm) form;

        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            IncidentDao dao = sqlSession.getMapper(IncidentDao.class);

            Map<String, Object> params = new HashMap<>();
            params.put("type", incForm.getIncidentType());
            params.put("part", incForm.getIncidentPart());

            List<IncidentDto> similarResults = dao.searchSimilar(params);
            request.setAttribute("similarResults", similarResults);
        }

        return mapping.findForward("similar");
    }

    /**
     * Temporarily save form data to session.
     */
    public ActionForward tempSave(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        IncidentForm incForm = (IncidentForm) form;

        // Save copy to session
        IncidentForm copy = new IncidentForm();
        copyForm(copy, incForm);
        request.getSession().setAttribute("incidentTempSave", copy);

        request.setAttribute("message", "一時保存しました。");

        return mapping.findForward("input");
    }

    // ---- Helper Methods ----

    private ActionMessages validateForm(IncidentForm form) {
        ActionMessages errors = new ActionMessages();
        if (form.getIncidentDateTime() == null || form.getIncidentDateTime().trim().isEmpty()) {
            errors.add("incidentDateTime", new ActionMessage("errors.required", "発生日時"));
        }
        if (form.getEquipmentCode() == null || form.getEquipmentCode().trim().isEmpty()) {
            errors.add("equipmentCode", new ActionMessage("errors.required", "設備コード"));
        }
        if (form.getIncidentType() == null || form.getIncidentType().trim().isEmpty()) {
            errors.add("incidentType", new ActionMessage("errors.required", "異常種別"));
        }
        if (form.getSeverity() == null || form.getSeverity().trim().isEmpty()) {
            errors.add("severity", new ActionMessage("errors.required", "重大度"));
        }
        if (form.getIncidentDetail() == null || form.getIncidentDetail().trim().isEmpty()) {
            errors.add("incidentDetail", new ActionMessage("errors.required", "異常内容詳細"));
        }
        return errors;
    }

    private IncidentDto formToDto(IncidentForm form) {
        IncidentDto dto = new IncidentDto();
        dto.setIncidentNo(form.getIncidentNo());
        dto.setResultId(form.getResultId());
        if (form.getIncidentDateTime() != null && !form.getIncidentDateTime().isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                dto.setIncidentDatetime(new Timestamp(sdf.parse(form.getIncidentDateTime()).getTime()));
            } catch (Exception e) {
                dto.setIncidentDatetime(new Timestamp(System.currentTimeMillis()));
            }
        }
        dto.setFinder(form.getFinder());
        dto.setEquipmentCode(form.getEquipmentCode());
        dto.setWeather(form.getWeather());
        dto.setTemperature(form.getTemperature());
        dto.setIncidentType(form.getIncidentType());
        dto.setSeverity(form.getSeverity());
        dto.setIncidentPart(form.getIncidentPart());
        dto.setIncidentDetail(form.getIncidentDetail());
        dto.setTmpAction(form.getTmpAction());
        dto.setTmpActionPerson(form.getTmpActionPerson());
        if (form.getTmpActionDate() != null && !form.getTmpActionDate().isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                dto.setTmpActionDate(new Timestamp(sdf.parse(form.getTmpActionDate()).getTime()));
            } catch (Exception e) {
                // ignore
            }
        }
        dto.setCause(form.getCause());
        dto.setCounterDetail(form.getCounterDetail());
        dto.setStatus(form.getStatus());
        return dto;
    }

    private void saveAttachments(FormFile[] files, String incidentNo) {
        String basePath = getServlet().getServletContext().getRealPath("/attachments/inc/" + incidentNo);
        File dir = new File(basePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        for (FormFile file : files) {
            if (file == null || file.getFileName() == null || file.getFileName().isEmpty()) continue;
            try {
                File dest = new File(dir, file.getFileName());
                java.io.FileOutputStream fos = new java.io.FileOutputStream(dest);
                fos.write(file.getFileData());
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadEquipmentFromPlan(IncidentForm incForm, String planId) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            com.strutslab.dao.PlanDao planDao = sqlSession.getMapper(com.strutslab.dao.PlanDao.class);
            com.strutslab.dto.PlanDto plan = planDao.findById(Integer.parseInt(planId));
            if (plan != null) {
                incForm.setEquipmentCode(plan.getEquipmentCode());
                incForm.setEquipmentName(plan.getEquipmentName());
            }
        } catch (Exception e) {
            // Ignore - equipment can be entered manually
        }
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
}
