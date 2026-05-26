package com.strutslab.service.report;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;

import com.strutslab.dao.ReportDao;
import com.strutslab.db.MyBatisUtil;
import com.strutslab.form.report.ReportForm;

public class ReportService {

    public List<Map<String, Object>> computeCompletionRate(ReportForm form) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            ReportDao dao = sqlSession.getMapper(ReportDao.class);
            return dao.computeCompletionRate(buildParams(form));
        }
    }

    public List<Map<String, Object>> computeIncidentCrossTab(ReportForm form) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            ReportDao dao = sqlSession.getMapper(ReportDao.class);
            return dao.computeIncidentCrossTab(buildParams(form));
        }
    }

    public List<Map<String, Object>> computeEquipmentRanking(ReportForm form) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            ReportDao dao = sqlSession.getMapper(ReportDao.class);
            return dao.computeEquipmentRanking(buildParams(form));
        }
    }

    public void exportCsv(HttpServletResponse response, ReportForm form) throws Exception {
        List<Map<String, Object>> ranking = computeEquipmentRanking(form);

        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"summary_report.csv\"");

        try (PrintWriter pw = response.getWriter()) {
            pw.write('﻿');
            pw.println("順位,設備コード,設備名,設備種別,異常件数");
            int rank = 1;
            for (Map<String, Object> row : ranking) {
                StringBuilder line = new StringBuilder();
                appendCsvField(line, String.valueOf(rank++));
                appendCsvField(line, (String) row.get("equipmentCode"));
                appendCsvField(line, (String) row.get("equipmentName"));
                appendCsvField(line, (String) row.get("equipmentType"));
                appendCsvField(line, String.valueOf(row.get("incidentCount")));
                pw.println(line.toString());
            }
        }
    }

    private Map<String, Object> buildParams(ReportForm form) {
        Map<String, Object> params = new HashMap<>();
        params.put("dateFrom", form.getDateFrom());
        params.put("dateTo", form.getDateTo());
        if (form.getEquipmentType() != null && !form.getEquipmentType().isEmpty()) {
            params.put("equipmentType", form.getEquipmentType());
        }
        return params;
    }

    private void appendCsvField(StringBuilder sb, String value) {
        if (sb.length() > 0) sb.append(',');
        if (value == null) { sb.append(""); return; }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            sb.append('"').append(value.replace("\"", "\"\"")).append('"');
        } else {
            sb.append(value);
        }
    }
}
