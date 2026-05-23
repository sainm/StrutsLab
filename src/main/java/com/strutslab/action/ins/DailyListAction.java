package com.strutslab.action.ins;

import java.text.SimpleDateFormat;
import java.util.Date;
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

import com.strutslab.db.MyBatisUtil;
import com.strutslab.dto.ExecResultDto;
import com.strutslab.form.ins.DailyForm;

public class DailyListAction extends Action {

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        DailyForm dailyForm = (DailyForm) form;

        String targetDate = dailyForm.getTargetDate();
        if (targetDate == null || targetDate.isEmpty()) {
            targetDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
            dailyForm.setTargetDate(targetDate);
        }

        String statusFilter = dailyForm.getStatusFilter();
        if (statusFilter == null || statusFilter.isEmpty()) {
            statusFilter = "全部";
        }

        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            Map<String, Object> params = new HashMap<>();
            params.put("targetDate", targetDate);

            if (statusFilter != null && !"全部".equals(statusFilter)) {
                params.put("statusFilter", statusFilter);
            }

            String personCode = dailyForm.getPersonCode();
            if (personCode != null && !personCode.isEmpty()) {
                params.put("personCode", personCode);
            }

            List<ExecResultDto> dailyList = sqlSession.selectList(
                    "com.strutslab.dao.ExecDao.findByDate", params);

            request.setAttribute("dailyList", dailyList);
            request.setAttribute("targetDate", targetDate);

            // Load employee list for person filter
            List<Map<String, Object>> empList = sqlSession.selectList(
                    "com.strutslab.dao.EmpDao.findAll");
            request.setAttribute("empList", empList);
        }

        return mapping.findForward("success");
    }
}
