package com.strutslab.action.counter;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.strutslab.dao.CapaDao;
import com.strutslab.db.MyBatisUtil;
import com.strutslab.dto.CapaDto;
import com.strutslab.form.counter.CapaForm;

public class CapaAction extends Action {

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        CapaForm capaForm = (CapaForm) form;

        // Check for save/submit action
        if (request.getParameter("save") != null) {
            return doSave(mapping, capaForm, request);
        }

        // Default: show form (load incident info)
        return doShow(mapping, capaForm, request);
    }

    private ActionForward doShow(ActionMapping mapping, CapaForm form,
            HttpServletRequest request) throws Exception {

        String incidentNo = request.getParameter("incidentNo");
        if (incidentNo != null && !incidentNo.isEmpty()) {
            form.setIncidentNo(incidentNo);

            try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
                // Load incident info for display
                Map<String, Object> incidentInfo = sqlSession.selectOne(
                        "com.strutslab.dao.IncidentDao.findByIdSimple", incidentNo);
                if (incidentInfo != null) {
                    request.setAttribute("incidentInfo", incidentInfo);
                }
            } catch (Exception e) {
                // IncidentDao may not exist; just pass the incidentNo
                request.setAttribute("incidentNo", incidentNo);
            }
        }

        return mapping.findForward("input");
    }

    private ActionForward doSave(ActionMapping mapping, CapaForm form,
            HttpServletRequest request) throws Exception {

        // --- Validation ---
        ActionMessages errors = new ActionMessages();

        if (form.getIncidentNo() == null || form.getIncidentNo().trim().isEmpty()) {
            errors.add("incidentNo", new ActionMessage("errors.required", "異常報告番号"));
        }
        if (form.getWhy1() == null || form.getWhy1().trim().isEmpty()) {
            errors.add("why1", new ActionMessage("errors.required", "なぜ①"));
        }
        if (form.getWhy2() == null || form.getWhy2().trim().isEmpty()) {
            errors.add("why2", new ActionMessage("errors.required", "なぜ②"));
        }
        if (form.getWhy3() == null || form.getWhy3().trim().isEmpty()) {
            errors.add("why3", new ActionMessage("errors.required", "なぜ③"));
        }
        if (form.getWhy4() == null || form.getWhy4().trim().isEmpty()) {
            errors.add("why4", new ActionMessage("errors.required", "なぜ④"));
        }
        if (form.getWhy5() == null || form.getWhy5().trim().isEmpty()) {
            errors.add("why5", new ActionMessage("errors.required", "なぜ⑤"));
        }
        if (form.getCountermeasure() == null || form.getCountermeasure().trim().isEmpty()) {
            errors.add("countermeasure", new ActionMessage("errors.required", "是正処置"));
        }
        if (form.getVerifyMethod() == null || form.getVerifyMethod().trim().isEmpty()) {
            errors.add("verifyMethod", new ActionMessage("errors.required", "検証方法"));
        }
        if (form.getVerifyDate() == null || form.getVerifyDate().trim().isEmpty()) {
            errors.add("verifyDate", new ActionMessage("errors.required", "検証期限"));
        }

        if (!errors.isEmpty()) {
            saveErrors(request, errors);
            return mapping.getInputForward();
        }

        // --- Save ---
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            CapaDao dao = sqlSession.getMapper(CapaDao.class);

            CapaDto dto = new CapaDto();
            dto.setIncidentNo(form.getIncidentNo());
            dto.setWhy1(form.getWhy1());
            dto.setWhy2(form.getWhy2());
            dto.setWhy3(form.getWhy3());
            dto.setWhy4(form.getWhy4());
            dto.setWhy5(form.getWhy5());
            dto.setCountermeasure(form.getCountermeasure());
            dto.setVerifyMethod(form.getVerifyMethod());
            dto.setVerifyDate(form.getVerifyDate());
            dto.setStatus("申請中");

            dao.insert(dto);

            // Update incident status to 再発防止
            try {
                sqlSession.update("com.strutslab.dao.IncidentDao.updateStatus",
                        new java.util.HashMap<String, Object>() {{
                            put("incidentNo", form.getIncidentNo());
                            put("status", "再発防止");
                        }});
            } catch (Exception e) {
                // IncidentDao may not have this method yet
            }

            sqlSession.commit();

            ActionMessages messages = new ActionMessages();
            messages.add("message", new ActionMessage("label.update", "是正処置報告書を提出しました。"));
            saveMessages(request, messages);
        }

        return mapping.findForward("success");
    }
}
