package com.strutslab.service.inc;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;

import com.strutslab.dao.IncidentDao;
import com.strutslab.db.MyBatisUtil;
import com.strutslab.dto.IncidentDto;

public class IncidentListService {

    private static final int PAGE_SIZE = 10;

    public static class SearchResult {
        public final List<IncidentDto> list;
        public final int currentPage;
        public final int totalPages;
        public final int totalCount;

        SearchResult(List<IncidentDto> list, int currentPage, int totalPages, int totalCount) {
            this.list = list;
            this.currentPage = currentPage;
            this.totalPages = totalPages;
            this.totalCount = totalCount;
        }
    }

    public SearchResult search(String incDateFrom, String incDateTo, String equipmentType,
            String incidentType, String status, String severity, String team, String keyword, int page) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            IncidentDao dao = sqlSession.getMapper(IncidentDao.class);
            Map<String, Object> params = buildParams(incDateFrom, incDateTo, equipmentType,
                    incidentType, status, severity, team, keyword);

            int totalCount = dao.count(params);
            int totalPages = (int) Math.ceil((double) totalCount / PAGE_SIZE);
            int currentPage = page;
            if (currentPage < 1) currentPage = 1;
            if (currentPage > totalPages && totalPages > 0) currentPage = totalPages;

            params.put("offset", (currentPage - 1) * PAGE_SIZE);
            params.put("limit", PAGE_SIZE);

            List<IncidentDto> list = dao.search(params);
            return new SearchResult(list, currentPage, totalPages, totalCount);
        }
    }

    public void bulkUpdateStatus(String[] incidentNos, String newStatus) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            IncidentDao dao = sqlSession.getMapper(IncidentDao.class);
            Map<String, Object> params = new HashMap<>();
            params.put("incidentNos", incidentNos);
            params.put("newStatus", newStatus);
            dao.bulkUpdateStatus(params);
            sqlSession.commit();
        }
    }

    public void exportCsv(HttpServletResponse response, String incDateFrom, String incDateTo,
            String equipmentType, String incidentType, String status, String severity,
            String team, String keyword) throws Exception {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            IncidentDao dao = sqlSession.getMapper(IncidentDao.class);
            Map<String, Object> params = buildParams(incDateFrom, incDateTo, equipmentType,
                    incidentType, status, severity, team, keyword);
            List<IncidentDto> list = dao.search(params);

            response.setContentType("text/csv; charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"incident_list.csv\"");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            try (PrintWriter pw = response.getWriter()) {
                pw.write('﻿');
                pw.println("報告番号,発生日時,設備名,異常種別,重大度,ステータス,発見者,担当班");
                for (IncidentDto dto : list) {
                    StringBuilder line = new StringBuilder();
                    appendCsvField(line, dto.getIncidentNo());
                    appendCsvField(line, dto.getIncidentDatetime() != null ? sdf.format(dto.getIncidentDatetime()) : "");
                    appendCsvField(line, dto.getEquipmentName());
                    appendCsvField(line, dto.getIncidentType());
                    appendCsvField(line, dto.getSeverity());
                    appendCsvField(line, dto.getStatus());
                    appendCsvField(line, dto.getFinder());
                    appendCsvField(line, dto.getTmpActionPerson());
                    pw.println(line.toString());
                }
            }
        }
    }

    public String buildPagingUrl(String contextPath, String incDateFrom, String incDateTo,
            String equipmentType, String incidentType, String status, String severity,
            String team, String keyword) {
        StringBuilder sb = new StringBuilder();
        sb.append(contextPath).append("/inc/list.do?");
        appendParam(sb, "incDateFrom", incDateFrom);
        appendParam(sb, "incDateTo", incDateTo);
        appendParam(sb, "equipmentType", equipmentType);
        appendParam(sb, "incidentType", incidentType);
        appendParam(sb, "status", status);
        appendParam(sb, "severity", severity);
        appendParam(sb, "team", team);
        appendParam(sb, "keyword", keyword);
        return sb.toString();
    }

    public String[] getEquipmentTypes() {
        return new String[]{"変圧器", "遮断器", "開閉器", "ケーブル", "母線", "保護継電器", "計器用変成器"};
    }

    private Map<String, Object> buildParams(String incDateFrom, String incDateTo,
            String equipmentType, String incidentType, String status, String severity,
            String team, String keyword) {
        Map<String, Object> params = new HashMap<>();
        if (incDateFrom != null && !incDateFrom.isEmpty()) params.put("incDateFrom", incDateFrom);
        if (incDateTo != null && !incDateTo.isEmpty()) params.put("incDateTo", incDateTo);
        if (equipmentType != null && !equipmentType.isEmpty()) params.put("equipmentType", equipmentType);
        if (incidentType != null && !incidentType.isEmpty()) params.put("incidentType", incidentType);
        if (status != null && !status.isEmpty()) params.put("status", status);
        if (severity != null && !severity.isEmpty()) params.put("severity", severity);
        if (team != null && !team.isEmpty()) params.put("team", team);
        if (keyword != null && !keyword.isEmpty()) params.put("keyword", keyword);
        return params;
    }

    private void appendCsvField(StringBuilder sb, String value) {
        if (sb.length() > 0) sb.append(',');
        if (value == null) return;
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            sb.append('"').append(value.replace("\"", "\"\"")).append('"');
        } else {
            sb.append(value);
        }
    }

    private void appendParam(StringBuilder sb, String name, String value) {
        if (value != null && !value.isEmpty()) {
            try {
                sb.append(name).append('=').append(java.net.URLEncoder.encode(value, "UTF-8")).append('&');
            } catch (java.io.UnsupportedEncodingException e) {
                throw new RuntimeException("UTF-8 not supported", e);
            }
        }
    }
}
