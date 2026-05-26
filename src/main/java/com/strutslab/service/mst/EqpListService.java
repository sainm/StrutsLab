package com.strutslab.service.mst;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;

import com.strutslab.dao.EqpDao;
import com.strutslab.db.MyBatisUtil;
import com.strutslab.dto.EqpDto;
import com.strutslab.form.mst.EqpSearchForm;

public class EqpListService {

    private static final int PAGE_SIZE = 10;

    public static class SearchResult {
        public final List<EqpDto> list;
        public final int currentPage;
        public final int totalPages;

        SearchResult(List<EqpDto> list, int currentPage, int totalPages) {
            this.list = list;
            this.currentPage = currentPage;
            this.totalPages = totalPages;
        }
    }

    public SearchResult search(EqpSearchForm form) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            EqpDao dao = sqlSession.getMapper(EqpDao.class);

            Map<String, Object> params = buildSearchParams(form);

            int totalCount = dao.count(params);
            int totalPages = (int) Math.ceil((double) totalCount / PAGE_SIZE);
            int currentPage = form.getPage();
            if (currentPage < 1) currentPage = 1;
            if (currentPage > totalPages && totalPages > 0) currentPage = totalPages;

            int offset = (currentPage - 1) * PAGE_SIZE;
            params.put("offset", offset);
            params.put("limit", PAGE_SIZE);

            List<EqpDto> list = dao.search(params);
            return new SearchResult(list, currentPage, totalPages);
        }
    }

    public void exportCsv(HttpServletResponse response, EqpSearchForm form) throws Exception {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            EqpDao dao = sqlSession.getMapper(EqpDao.class);
            List<EqpDto> list = dao.search(buildSearchParams(form));

            response.setContentType("text/csv; charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"equipment_list.csv\"");

            try (PrintWriter pw = response.getWriter()) {
                pw.write('﻿');
                pw.println("設備コード,設備名,設備種別,電圧階級,設置年月,保全ランク,担当部署");
                for (EqpDto eqp : list) {
                    StringBuilder line = new StringBuilder();
                    appendCsvField(line, eqp.getEquipmentCode());
                    appendCsvField(line, eqp.getEquipmentName());
                    appendCsvField(line, eqp.getEquipmentType());
                    appendCsvField(line, eqp.getVoltageLevel());
                    appendCsvField(line, eqp.getInstallDate());
                    appendCsvField(line, eqp.getMaintenanceRank());
                    appendCsvField(line, eqp.getLocationAddress());
                    pw.println(line.toString());
                }
            }
        }
    }

    public void deleteSelected(String[] selected) {
        if (selected == null || selected.length == 0) return;

        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            EqpDao dao = sqlSession.getMapper(EqpDao.class);
            for (String code : selected) {
                if (code != null && !code.trim().isEmpty()) {
                    dao.delete(code.trim());
                }
            }
            sqlSession.commit();
        }
    }

    public String buildPagingUrl(String contextPath, EqpSearchForm form) {
        StringBuilder pagingUrl = new StringBuilder();
        pagingUrl.append(contextPath).append("/mst/eqp/list.do?");
        appendParam(pagingUrl, "equipmentType", form.getEquipmentType());
        appendParam(pagingUrl, "voltageLevel", form.getVoltageLevel());
        appendParam(pagingUrl, "yearFrom", form.getYearFrom());
        appendParam(pagingUrl, "yearTo", form.getYearTo());
        appendParam(pagingUrl, "maintenanceRank", form.getMaintenanceRank());
        appendParam(pagingUrl, "deptName", form.getDeptName());
        return pagingUrl.toString();
    }

    private Map<String, Object> buildSearchParams(EqpSearchForm form) {
        Map<String, Object> params = new HashMap<>();
        if (form.getEquipmentType() != null && !form.getEquipmentType().isEmpty())
            params.put("equipmentType", form.getEquipmentType());
        if (form.getVoltageLevel() != null && !form.getVoltageLevel().isEmpty())
            params.put("voltageLevel", form.getVoltageLevel());
        if (form.getYearFrom() != null && !form.getYearFrom().isEmpty())
            params.put("yearFrom", form.getYearFrom());
        if (form.getYearTo() != null && !form.getYearTo().isEmpty())
            params.put("yearTo", form.getYearTo());
        if (form.getMaintenanceRank() != null && !form.getMaintenanceRank().isEmpty())
            params.put("maintenanceRank", form.getMaintenanceRank());
        if (form.getDeptName() != null && !form.getDeptName().isEmpty())
            params.put("deptName", form.getDeptName());
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
