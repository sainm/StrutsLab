package com.strutslab.action.ins;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.strutslab.db.MyBatisUtil;
import com.strutslab.dto.ExecItemResultDto;
import com.strutslab.dto.ExecResultDto;
import com.strutslab.form.ins.ExecForm;

public class ExecDetailAction extends Action {

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        ExecForm execForm = (ExecForm) form;

        if (request.getParameter("modify") != null) {
            return doModify(mapping, execForm, request);
        } else {
            return doView(mapping, execForm, request);
        }
    }

    private ActionForward doView(ActionMapping mapping, ExecForm form,
            HttpServletRequest request) throws Exception {

        String resultIdStr = request.getParameter("resultId");
        if (resultIdStr == null || resultIdStr.isEmpty()) {
            ActionMessages errors = new ActionMessages();
            errors.add("resultId", new ActionMessage("errors.required", "結果ID"));
            saveErrors(request, errors);
            return mapping.findForward("success");
        }

        int resultId = Integer.parseInt(resultIdStr);

        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            ExecResultDto result = sqlSession.selectOne(
                    "com.strutslab.dao.ExecDao.findById", resultId);
            if (result == null) {
                ActionMessages errors = new ActionMessages();
                errors.add("resultId", new ActionMessage("errors.required", "結果が見つかりません"));
                saveErrors(request, errors);
                return mapping.findForward("success");
            }

            List<ExecItemResultDto> items = sqlSession.selectList(
                    "com.strutslab.dao.ExecDao.findItemsByResultId", resultId);

            request.setAttribute("execResult", result);
            request.setAttribute("execItems", items);

            // Populate form for display
            form.setResultId(resultId);
            form.setSummaryJudge(result.getSummaryJudge());
            form.setSummaryNote(result.getSummaryNote());
        }

        return mapping.findForward("success");
    }

    private ActionForward doModify(ActionMapping mapping, ExecForm form,
            HttpServletRequest request) throws Exception {

        ActionMessages errors = new ActionMessages();

        String modifyReason = form.getModifyReason();
        if (modifyReason == null || modifyReason.trim().isEmpty()) {
            errors.add("modifyReason", new ActionMessage("errors.required", "修正理由"));
            saveErrors(request, errors);
            // Re-load the view with the form data
            return doView(mapping, form, request);
        }

        if (form.getResultId() <= 0) {
            errors.add("resultId", new ActionMessage("errors.required", "結果ID"));
            saveErrors(request, errors);
            return mapping.findForward("success");
        }

        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            // Update approval status to 申請中 and store reject_reason as modifyReason
            // (reusing the reject_reason column for modify request justification)
            Map<String, Object> updateParams = new HashMap<>();
            updateParams.put("param1", form.getResultId());
            updateParams.put("param2", "申請中");
            updateParams.put("param3", modifyReason);
            sqlSession.update(
                    "com.strutslab.dao.ExecDao.updateApprovalStatus",
                    updateParams);
            sqlSession.commit();
        }

        ActionMessages messages = new ActionMessages();
        messages.add("message", new ActionMessage("label.update", "修正申請"));
        saveMessages(request, messages);

        return mapping.findForward("success");
    }
}
