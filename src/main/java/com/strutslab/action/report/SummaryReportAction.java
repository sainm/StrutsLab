package com.strutslab.action.report;

import java.io.PrintWriter;
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
import com.strutslab.form.report.ReportForm;

public class SummaryReportAction extends Action {

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        ReportForm reportForm = (ReportForm) form;

        // Default to current fiscal year if not specified
        if (reportForm.getDateFrom() == null || reportForm.getDateFrom().isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
            reportForm.setDateFrom(sdf.format(new Date()).substring(0, 4) + "01");
        }
        if (reportForm.getDateTo() == null || reportForm.getDateTo().isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
            reportForm.setDateTo(sdf.format(new Date()));
        }

        String paramCsv = request.getParameter("csv");
        if ("true".equals(paramCsv)) {
            exportCsv(response, reportForm);
            return null;
        }

        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            // 1. Inspection completion rate per equipment type per month
            List<Map<String, Object>> completionMatrix = computeCompletionRate(sqlSession, reportForm);
            request.setAttribute("completionMatrix", completionMatrix);

            // 2. Incident cross-tabulation (month x type)
            List<Map<String, Object>> crossTab = computeIncidentCrossTab(sqlSession, reportForm);
            request.setAttribute("crossTab", crossTab);

            // 3. Equipment ranking (top 10 by incidents)
            List<Map<String, Object>> ranking = computeEquipmentRanking(sqlSession, reportForm);
            request.setAttribute("ranking", ranking);
        }

        request.setAttribute("reportForm", reportForm);
        return mapping.findForward("success");
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> computeCompletionRate(SqlSession sqlSession, ReportForm form) {
        // Query: count total plans vs executed plans per equipment type per month
        Map<String, Object> params = new HashMap<>();
        params.put("dateFrom", form.getDateFrom());
        params.put("dateTo", form.getDateTo());
        if (form.getEquipmentType() != null && !form.getEquipmentType().isEmpty()) {
            params.put("equipmentType", form.getEquipmentType());
        }
        return sqlSession.selectList(
            "com.strutslab.dao.ReportDao.computeCompletionRate", params);
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> computeIncidentCrossTab(SqlSession sqlSession, ReportForm form) {
        Map<String, Object> params = new HashMap<>();
        params.put("dateFrom", form.getDateFrom());
        params.put("dateTo", form.getDateTo());
        if (form.getEquipmentType() != null && !form.getEquipmentType().isEmpty()) {
            params.put("equipmentType", form.getEquipmentType());
        }
        return sqlSession.selectList(
            "com.strutslab.dao.ReportDao.computeIncidentCrossTab", params);
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> computeEquipmentRanking(SqlSession sqlSession, ReportForm form) {
        Map<String, Object> params = new HashMap<>();
        params.put("dateFrom", form.getDateFrom());
        params.put("dateTo", form.getDateTo());
        if (form.getEquipmentType() != null && !form.getEquipmentType().isEmpty()) {
            params.put("equipmentType", form.getEquipmentType());
        }
        return sqlSession.selectList(
            "com.strutslab.dao.ReportDao.computeEquipmentRanking", params);
    }

    private void exportCsv(HttpServletResponse response, ReportForm form) throws Exception {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            List<Map<String, Object>> ranking = computeEquipmentRanking(sqlSession, form);

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
