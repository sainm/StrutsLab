package com.strutslab.service.counter;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;

import com.strutslab.dao.CounterDao;
import com.strutslab.db.MyBatisUtil;
import com.strutslab.dto.CounterDetailDto;
import com.strutslab.dto.CounterDto;

public class CounterListService {

    public List<CounterDto> search(String dateFrom, String dateTo, String person,
            String status, String priority) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            CounterDao dao = sqlSession.getMapper(CounterDao.class);
            return dao.search(buildParams(dateFrom, dateTo, person, status, priority));
        }
    }

    public int count(String dateFrom, String dateTo, String person, String status, String priority) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            CounterDao dao = sqlSession.getMapper(CounterDao.class);
            return dao.count(buildParams(dateFrom, dateTo, person, status, priority));
        }
    }

    public void computeCompletionStats(List<CounterDto> orders,
            Map<String, Integer> detailCounts, Map<String, Integer> completeCounts) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            CounterDao dao = sqlSession.getMapper(CounterDao.class);
            for (CounterDto dto : orders) {
                List<CounterDetailDto> details = dao.findDetailsByOrderNo(dto.getOrderNo());
                int total = details.size();
                int complete = 0;
                for (CounterDetailDto d : details) {
                    if ("完了".equals(d.getStatus())) complete++;
                }
                detailCounts.put(dto.getOrderNo(), total);
                completeCounts.put(dto.getOrderNo(), complete);
            }
        }
    }

    public void bulkUpdateDetailStatus(int[] detailIds, String newStatus) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            CounterDao dao = sqlSession.getMapper(CounterDao.class);
            for (int detailId : detailIds) {
                dao.updateDetailStatus(detailId, newStatus);
            }
            sqlSession.commit();
        }
    }

    public void exportCsv(HttpServletResponse response, String dateFrom, String dateTo,
            String person, String status, String priority) throws Exception {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            CounterDao dao = sqlSession.getMapper(CounterDao.class);
            List<CounterDto> list = dao.search(buildParams(dateFrom, dateTo, person, status, priority));

            response.setContentType("text/csv; charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"counter_orders.csv\"");

            try (PrintWriter pw = response.getWriter()) {
                pw.println("指示番号,指示日,関連異常報告,優先度,ステータス");
                for (CounterDto dto : list) {
                    pw.print(escapeCsv(dto.getOrderNo())); pw.print(",");
                    pw.print(escapeCsv(dto.getOrderDate())); pw.print(",");
                    pw.print(escapeCsv(dto.getIncidentNo())); pw.print(",");
                    pw.print(escapeCsv(dto.getOverallPriority())); pw.print(",");
                    pw.println(escapeCsv(dto.getStatus()));
                }
            }
        }
    }

    private Map<String, Object> buildParams(String dateFrom, String dateTo, String person,
            String status, String priority) {
        Map<String, Object> params = new HashMap<>();
        if (dateFrom != null && !dateFrom.isEmpty()) params.put("dateFrom", dateFrom);
        if (dateTo != null && !dateTo.isEmpty()) params.put("dateTo", dateTo);
        if (person != null && !person.isEmpty()) params.put("person", person);
        if (status != null && !status.isEmpty()) params.put("status", status);
        if (priority != null && !priority.isEmpty()) params.put("priority", priority);
        return params;
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
