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
import com.strutslab.dto.ExecResultDto;
import com.strutslab.form.ins.ApprovalForm;

public class ApprovalListAction extends Action {

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        ApprovalForm approvalForm = (ApprovalForm) form;

        // Handle bulk operations
        if (request.getParameter("bulkApprove") != null) {
            return doBulkApprove(mapping, approvalForm, request);
        }
        if (request.getParameter("bulkReject") != null) {
            return doBulkReject(mapping, approvalForm, request);
        }

        // Load search results
        return doSearch(mapping, approvalForm, request);
    }

    private ActionForward doSearch(ActionMapping mapping, ApprovalForm form,
            HttpServletRequest request) throws Exception {

        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            Map<String, Object> params = new HashMap<>();

            if (form.getDateFrom() != null && !form.getDateFrom().isEmpty()) {
                params.put("dateFrom", form.getDateFrom());
            }
            if (form.getDateTo() != null && !form.getDateTo().isEmpty()) {
                params.put("dateTo", form.getDateTo());
            }
            if (form.getTeam() != null && !form.getTeam().isEmpty()) {
                params.put("team", form.getTeam());
            }
            if (form.getStatus() != null && !form.getStatus().isEmpty()) {
                params.put("status", form.getStatus());
            }

            List<ExecResultDto> approvalList = sqlSession.selectList(
                    "com.strutslab.dao.ExecDao.findPendingApprovals", params);

            int totalCount = 0;
            if (form.getStatus() != null && !form.getStatus().isEmpty()) {
                totalCount = sqlSession.selectOne(
                        "com.strutslab.dao.ExecDao.countPendingApprovals", params);
            } else {
                totalCount = approvalList.size();
            }

            request.setAttribute("approvalList", approvalList);
            request.setAttribute("totalCount", totalCount);

            // Load team list for filter
            List<Map<String, Object>> teamList = sqlSession.selectList(
                    "com.strutslab.dao.EmpDao.findAll");
            request.setAttribute("teamList", teamList);
        }

        return mapping.findForward("success");
    }

    private ActionForward doBulkApprove(ActionMapping mapping, ApprovalForm form,
            HttpServletRequest request) throws Exception {

        int[] selected = form.getSelectedItems();
        if (selected == null || selected.length == 0) {
            ActionMessages errors = new ActionMessages();
            errors.add("selectedItems", new ActionMessage("errors.required", "承認対象"));
            saveErrors(request, errors);
            return doSearch(mapping, form, request);
        }

        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            sqlSession.update(
                    "com.strutslab.dao.ExecDao.bulkApprove", selected);
            sqlSession.commit();
        }

        ActionMessages messages = new ActionMessages();
        messages.add("message", new ActionMessage("label.update", selected.length + "件承認しました"));
        saveMessages(request, messages);

        return doSearch(mapping, form, request);
    }

    private ActionForward doBulkReject(ActionMapping mapping, ApprovalForm form,
            HttpServletRequest request) throws Exception {

        int[] selected = form.getSelectedItems();
        if (selected == null || selected.length == 0) {
            ActionMessages errors = new ActionMessages();
            errors.add("selectedItems", new ActionMessage("errors.required", "差戻対象"));
            saveErrors(request, errors);
            return doSearch(mapping, form, request);
        }

        String rejectReason = form.getRejectReason();
        if (rejectReason == null || rejectReason.trim().isEmpty()) {
            ActionMessages errors = new ActionMessages();
            errors.add("rejectReason", new ActionMessage("errors.required", "差戻理由"));
            saveErrors(request, errors);
            return doSearch(mapping, form, request);
        }

        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            Map<String, Object> params = new HashMap<>();
            params.put("resultIds", selected);
            params.put("reason", rejectReason);
            sqlSession.update(
                    "com.strutslab.dao.ExecDao.bulkReject", params);
            sqlSession.commit();
        }

        ActionMessages messages = new ActionMessages();
        messages.add("message", new ActionMessage("label.update", selected.length + "件差戻しました"));
        saveMessages(request, messages);

        return doSearch(mapping, form, request);
    }
}
