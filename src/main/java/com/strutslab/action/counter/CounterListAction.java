package com.strutslab.action.counter;

import java.io.PrintWriter;
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

import com.strutslab.dao.CounterDao;
import com.strutslab.db.MyBatisUtil;
import com.strutslab.dto.CounterDetailDto;
import com.strutslab.dto.CounterDto;
import com.strutslab.form.counter.CounterSearchForm;

public class CounterListAction extends Action {

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        CounterSearchForm searchForm = (CounterSearchForm) form;

        // Check for CSV export
        if (request.getParameter("csv") != null) {
            return doCsvExport(mapping, searchForm, request, response);
        }

        // Check for bulk status update
        if (request.getParameter("bulkUpdateStatus") != null) {
            return doBulkUpdateStatus(mapping, searchForm, request);
        }

        // Default: perform search
        return doSearch(mapping, searchForm, request);
    }

    private ActionForward doSearch(ActionMapping mapping, CounterSearchForm form,
            HttpServletRequest request) throws Exception {

        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            CounterDao dao = sqlSession.getMapper(CounterDao.class);

            Map<String, Object> params = new HashMap<>();
            if (form.getDateFrom() != null && !form.getDateFrom().isEmpty()) {
                params.put("dateFrom", form.getDateFrom());
            }
            if (form.getDateTo() != null && !form.getDateTo().isEmpty()) {
                params.put("dateTo", form.getDateTo());
            }
            if (form.getPerson() != null && !form.getPerson().isEmpty()) {
                params.put("person", form.getPerson());
            }
            if (form.getStatus() != null && !form.getStatus().isEmpty()) {
                params.put("status", form.getStatus());
            }
            if (form.getPriority() != null && !form.getPriority().isEmpty()) {
                params.put("priority", form.getPriority());
            }

            List<CounterDto> orderList = dao.search(params);
            int totalCount = dao.count(params);

            // Load detail counts for each order
            Map<String, Integer> detailCounts = new HashMap<>();
            Map<String, Integer> completeCounts = new HashMap<>();
            for (CounterDto dto : orderList) {
                List<CounterDetailDto> details = dao.findDetailsByOrderNo(dto.getOrderNo());
                int total = details.size();
                int complete = 0;
                for (CounterDetailDto d : details) {
                    if ("完了".equals(d.getStatus())) complete++;
                }
                detailCounts.put(dto.getOrderNo(), total);
                completeCounts.put(dto.getOrderNo(), complete);
            }

            request.setAttribute("orderList", orderList);
            request.setAttribute("totalCount", totalCount);
            request.setAttribute("detailCounts", detailCounts);
            request.setAttribute("completeCounts", completeCounts);
        }

        return mapping.findForward("success");
    }

    private ActionForward doBulkUpdateStatus(ActionMapping mapping, CounterSearchForm form,
            HttpServletRequest request) throws Exception {

        int[] selected = form.getSelectedItems();
        if (selected == null || selected.length == 0) {
            ActionMessages errors = new ActionMessages();
            errors.add("selectedItems", new ActionMessage("errors.required", "更新対象"));
            saveErrors(request, errors);
            return doSearch(mapping, form, request);
        }

        String newStatus = request.getParameter("newStatus");
        if (newStatus == null || newStatus.isEmpty()) {
            ActionMessages errors = new ActionMessages();
            errors.add("newStatus", new ActionMessage("errors.required", "ステータス"));
            saveErrors(request, errors);
            return doSearch(mapping, form, request);
        }

        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            CounterDao dao = sqlSession.getMapper(CounterDao.class);
            for (int detailId : selected) {
                dao.updateDetailStatus(detailId, newStatus);
            }
            sqlSession.commit();
        }

        ActionMessages messages = new ActionMessages();
        messages.add("message", new ActionMessage("label.update", selected.length + "件更新しました"));
        saveMessages(request, messages);

        return doSearch(mapping, form, request);
    }

    private ActionForward doCsvExport(ActionMapping mapping, CounterSearchForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"counter_orders.csv\"");

        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            CounterDao dao = sqlSession.getMapper(CounterDao.class);

            Map<String, Object> params = new HashMap<>();
            if (form.getDateFrom() != null && !form.getDateFrom().isEmpty()) {
                params.put("dateFrom", form.getDateFrom());
            }
            if (form.getDateTo() != null && !form.getDateTo().isEmpty()) {
                params.put("dateTo", form.getDateTo());
            }
            if (form.getPerson() != null && !form.getPerson().isEmpty()) {
                params.put("person", form.getPerson());
            }
            if (form.getStatus() != null && !form.getStatus().isEmpty()) {
                params.put("status", form.getStatus());
            }
            if (form.getPriority() != null && !form.getPriority().isEmpty()) {
                params.put("priority", form.getPriority());
            }

            List<CounterDto> orderList = dao.search(params);

            PrintWriter pw = response.getWriter();
            pw.println("指示番号,指示日,関連異常報告,優先度,ステータス");
            for (CounterDto dto : orderList) {
                pw.print(escapeCsv(dto.getOrderNo())); pw.print(",");
                pw.print(escapeCsv(dto.getOrderDate())); pw.print(",");
                pw.print(escapeCsv(dto.getIncidentNo())); pw.print(",");
                pw.print(escapeCsv(dto.getOverallPriority())); pw.print(",");
                pw.println(escapeCsv(dto.getStatus()));
            }
            pw.flush();
        }

        return null;
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
