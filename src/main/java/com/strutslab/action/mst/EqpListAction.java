package com.strutslab.action.mst;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.strutslab.dao.EqpDao;
import com.strutslab.db.MyBatisUtil;
import com.strutslab.dto.EqpDto;
import com.strutslab.form.mst.EqpSearchForm;

public class EqpListAction extends Action {

    private static final int PAGE_SIZE = 10;

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        EqpSearchForm searchForm = (EqpSearchForm) form;
        HttpSession session = request.getSession();

        // Persist search conditions in session
        session.setAttribute("eqpSearchForm", searchForm);

        String paramCsv = request.getParameter("csv");
        String paramDelete = request.getParameter("delete");

        if ("true".equals(paramCsv)) {
            exportCsv(response, searchForm);
            return null;
        }

        if ("true".equals(paramDelete)) {
            deleteSelected(searchForm);
            // After deletion, reload with page 1
            searchForm.setPage(1);
        }

        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            EqpDao dao = sqlSession.getMapper(EqpDao.class);

            // Build parameter map for search
            Map<String, Object> params = new HashMap<>();
            if (searchForm.getEquipmentType() != null && !searchForm.getEquipmentType().isEmpty()) {
                params.put("equipmentType", searchForm.getEquipmentType());
            }
            if (searchForm.getVoltageLevel() != null && !searchForm.getVoltageLevel().isEmpty()) {
                params.put("voltageLevel", searchForm.getVoltageLevel());
            }
            if (searchForm.getYearFrom() != null && !searchForm.getYearFrom().isEmpty()) {
                params.put("yearFrom", searchForm.getYearFrom());
            }
            if (searchForm.getYearTo() != null && !searchForm.getYearTo().isEmpty()) {
                params.put("yearTo", searchForm.getYearTo());
            }
            if (searchForm.getMaintenanceRank() != null && !searchForm.getMaintenanceRank().isEmpty()) {
                params.put("maintenanceRank", searchForm.getMaintenanceRank());
            }
            if (searchForm.getDeptName() != null && !searchForm.getDeptName().isEmpty()) {
                params.put("deptName", searchForm.getDeptName());
            }

            // Count total records
            int totalCount = dao.count(params);
            int totalPages = (int) Math.ceil((double) totalCount / PAGE_SIZE);
            int currentPage = searchForm.getPage();
            if (currentPage < 1) currentPage = 1;
            if (currentPage > totalPages && totalPages > 0) currentPage = totalPages;

            int offset = (currentPage - 1) * PAGE_SIZE;
            params.put("offset", offset);
            params.put("limit", PAGE_SIZE);

            List<EqpDto> list = dao.search(params);

            // Build paging URL with current search parameters
            StringBuilder pagingUrl = new StringBuilder();
            pagingUrl.append(request.getContextPath()).append("/mst/eqp/list.do?");
            appendParam(pagingUrl, "equipmentType", searchForm.getEquipmentType());
            appendParam(pagingUrl, "voltageLevel", searchForm.getVoltageLevel());
            appendParam(pagingUrl, "yearFrom", searchForm.getYearFrom());
            appendParam(pagingUrl, "yearTo", searchForm.getYearTo());
            appendParam(pagingUrl, "maintenanceRank", searchForm.getMaintenanceRank());
            appendParam(pagingUrl, "deptName", searchForm.getDeptName());

            request.setAttribute("eqpList", list);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("pagingUrl", pagingUrl.toString());

            String editParam = request.getParameter("edit");
            if ("new".equals(editParam)) {
                return mapping.findForward("new");
            }
            if (request.getParameter("editCode") != null) {
                return mapping.findForward("edit");
            }
        }

        return mapping.findForward("success");
    }

    private void exportCsv(HttpServletResponse response, EqpSearchForm searchForm) throws Exception {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            EqpDao dao = sqlSession.getMapper(EqpDao.class);

            Map<String, Object> params = new HashMap<>();
            if (searchForm.getEquipmentType() != null && !searchForm.getEquipmentType().isEmpty()) {
                params.put("equipmentType", searchForm.getEquipmentType());
            }
            if (searchForm.getVoltageLevel() != null && !searchForm.getVoltageLevel().isEmpty()) {
                params.put("voltageLevel", searchForm.getVoltageLevel());
            }
            if (searchForm.getYearFrom() != null && !searchForm.getYearFrom().isEmpty()) {
                params.put("yearFrom", searchForm.getYearFrom());
            }
            if (searchForm.getYearTo() != null && !searchForm.getYearTo().isEmpty()) {
                params.put("yearTo", searchForm.getYearTo());
            }
            if (searchForm.getMaintenanceRank() != null && !searchForm.getMaintenanceRank().isEmpty()) {
                params.put("maintenanceRank", searchForm.getMaintenanceRank());
            }
            if (searchForm.getDeptName() != null && !searchForm.getDeptName().isEmpty()) {
                params.put("deptName", searchForm.getDeptName());
            }

            List<EqpDto> list = dao.search(params);

            response.setContentType("text/csv; charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"equipment_list.csv\"");

            try (PrintWriter pw = response.getWriter()) {
                // BOM for Excel compatibility
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

    private void appendCsvField(StringBuilder sb, String value) {
        if (sb.length() > 0) sb.append(',');
        if (value == null) {
            sb.append("");
            return;
        }
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

    private void deleteSelected(EqpSearchForm searchForm) {
        String[] selected = searchForm.getSelectedItems();
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
}
