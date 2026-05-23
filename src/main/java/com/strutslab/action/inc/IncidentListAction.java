package com.strutslab.action.inc;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
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

import com.strutslab.dao.IncidentDao;
import com.strutslab.db.MyBatisUtil;
import com.strutslab.dto.IncidentDto;
import com.strutslab.form.inc.IncidentSearchForm;

public class IncidentListAction extends Action {

    private static final int PAGE_SIZE = 10;

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        IncidentSearchForm searchForm = (IncidentSearchForm) form;
        HttpSession session = request.getSession();

        // Handle clear action
        if (request.getParameter("clear") != null) {
            searchForm.setIncDateFrom(null);
            searchForm.setIncDateTo(null);
            searchForm.setEquipmentType(null);
            searchForm.setIncidentType(null);
            searchForm.setStatus(null);
            searchForm.setSeverity(null);
            searchForm.setTeam(null);
            searchForm.setKeyword(null);
            searchForm.setPage(1);
            searchForm.setSelectedItems(null);
            searchForm.setBulkStatus(null);
            return mapping.findForward("success");
        }

        // Handle save search condition
        if (request.getParameter("saveCondition") != null) {
            session.setAttribute("savedIncSearchCondition", cloneForm(searchForm));
            request.setAttribute("message", "検索条件を保存しました。");
            return buildForward(mapping, request, searchForm);
        }

        // Handle load search condition
        if (request.getParameter("loadCondition") != null) {
            IncidentSearchForm saved = (IncidentSearchForm) session.getAttribute("savedIncSearchCondition");
            if (saved != null) {
                searchForm.setIncDateFrom(saved.getIncDateFrom());
                searchForm.setIncDateTo(saved.getIncDateTo());
                searchForm.setEquipmentType(saved.getEquipmentType());
                searchForm.setIncidentType(saved.getIncidentType());
                searchForm.setStatus(saved.getStatus());
                searchForm.setSeverity(saved.getSeverity());
                searchForm.setTeam(saved.getTeam());
                searchForm.setKeyword(saved.getKeyword());
            }
            return buildForward(mapping, request, searchForm);
        }

        // Handle CSV export
        if ("true".equals(request.getParameter("csv"))) {
            exportCsv(response, searchForm);
            return null;
        }

        // Handle bulk status update
        if (request.getParameter("bulkUpdate") != null) {
            String[] selected = searchForm.getSelectedItems();
            String newStatus = searchForm.getBulkStatus();
            if (selected != null && selected.length > 0 && newStatus != null && !newStatus.isEmpty()) {
                try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
                    IncidentDao dao = sqlSession.getMapper(IncidentDao.class);
                    Map<String, Object> bulkParams = new HashMap<>();
                    bulkParams.put("incidentNos", selected);
                    bulkParams.put("newStatus", newStatus);
                    dao.bulkUpdateStatus(bulkParams);
                    sqlSession.commit();
                }
            }
            searchForm.setPage(1);
        }

        // Main search
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            IncidentDao dao = sqlSession.getMapper(IncidentDao.class);

            Map<String, Object> params = buildParamMap(searchForm);

            // Count total
            int totalCount = dao.count(params);
            int totalPages = (int) Math.ceil((double) totalCount / PAGE_SIZE);
            int currentPage = searchForm.getPage();
            if (currentPage < 1) currentPage = 1;
            if (currentPage > totalPages && totalPages > 0) currentPage = totalPages;

            int offset = (currentPage - 1) * PAGE_SIZE;
            params.put("offset", offset);
            params.put("limit", PAGE_SIZE);

            List<IncidentDto> list = dao.search(params);

            // Build paging URL
            StringBuilder pagingUrl = new StringBuilder();
            pagingUrl.append(request.getContextPath()).append("/inc/list.do?");
            appendParam(pagingUrl, "incDateFrom", searchForm.getIncDateFrom());
            appendParam(pagingUrl, "incDateTo", searchForm.getIncDateTo());
            appendParam(pagingUrl, "equipmentType", searchForm.getEquipmentType());
            appendParam(pagingUrl, "incidentType", searchForm.getIncidentType());
            appendParam(pagingUrl, "status", searchForm.getStatus());
            appendParam(pagingUrl, "severity", searchForm.getSeverity());
            appendParam(pagingUrl, "team", searchForm.getTeam());
            appendParam(pagingUrl, "keyword", searchForm.getKeyword());

            request.setAttribute("incidentList", list);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("pagingUrl", pagingUrl.toString());

            // Store equipment types for search form dropdown
            request.setAttribute("equipmentTypeList", getAllEquipmentTypes());

        } catch (Exception e) {
            request.setAttribute("org.apache.struts.action.ERROR", e.getMessage());
        }

        // Persist search conditions in session
        session.setAttribute("incidentSearchForm", searchForm);

        return mapping.findForward("success");
    }

    private ActionForward buildForward(ActionMapping mapping, HttpServletRequest request, IncidentSearchForm searchForm) throws Exception {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            IncidentDao dao = sqlSession.getMapper(IncidentDao.class);
            Map<String, Object> params = buildParamMap(searchForm);

            int totalCount = dao.count(params);
            int totalPages = (int) Math.ceil((double) totalCount / PAGE_SIZE);
            int currentPage = searchForm.getPage();
            if (currentPage < 1) currentPage = 1;
            if (currentPage > totalPages && totalPages > 0) currentPage = totalPages;

            params.put("offset", (currentPage - 1) * PAGE_SIZE);
            params.put("limit", PAGE_SIZE);

            List<IncidentDto> list = dao.search(params);

            StringBuilder pagingUrl = new StringBuilder();
            pagingUrl.append(request.getContextPath()).append("/inc/list.do?");
            appendParam(pagingUrl, "incDateFrom", searchForm.getIncDateFrom());
            appendParam(pagingUrl, "incDateTo", searchForm.getIncDateTo());
            appendParam(pagingUrl, "equipmentType", searchForm.getEquipmentType());
            appendParam(pagingUrl, "incidentType", searchForm.getIncidentType());
            appendParam(pagingUrl, "status", searchForm.getStatus());
            appendParam(pagingUrl, "severity", searchForm.getSeverity());
            appendParam(pagingUrl, "team", searchForm.getTeam());
            appendParam(pagingUrl, "keyword", searchForm.getKeyword());

            request.setAttribute("incidentList", list);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("pagingUrl", pagingUrl.toString());
            request.setAttribute("equipmentTypeList", getAllEquipmentTypes());
        }
        return mapping.findForward("success");
    }

    private Map<String, Object> buildParamMap(IncidentSearchForm searchForm) {
        Map<String, Object> params = new HashMap<>();
        if (searchForm.getIncDateFrom() != null && !searchForm.getIncDateFrom().isEmpty()) {
            params.put("incDateFrom", searchForm.getIncDateFrom());
        }
        if (searchForm.getIncDateTo() != null && !searchForm.getIncDateTo().isEmpty()) {
            params.put("incDateTo", searchForm.getIncDateTo());
        }
        if (searchForm.getEquipmentType() != null && !searchForm.getEquipmentType().isEmpty()) {
            params.put("equipmentType", searchForm.getEquipmentType());
        }
        if (searchForm.getIncidentType() != null && !searchForm.getIncidentType().isEmpty()) {
            params.put("incidentType", searchForm.getIncidentType());
        }
        if (searchForm.getStatus() != null && !searchForm.getStatus().isEmpty()) {
            params.put("status", searchForm.getStatus());
        }
        if (searchForm.getSeverity() != null && !searchForm.getSeverity().isEmpty()) {
            params.put("severity", searchForm.getSeverity());
        }
        if (searchForm.getTeam() != null && !searchForm.getTeam().isEmpty()) {
            params.put("team", searchForm.getTeam());
        }
        if (searchForm.getKeyword() != null && !searchForm.getKeyword().isEmpty()) {
            params.put("keyword", searchForm.getKeyword());
        }
        return params;
    }

    private void exportCsv(HttpServletResponse response, IncidentSearchForm searchForm) throws Exception {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            IncidentDao dao = sqlSession.getMapper(IncidentDao.class);
            Map<String, Object> params = buildParamMap(searchForm);
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

    private IncidentSearchForm cloneForm(IncidentSearchForm src) {
        IncidentSearchForm dst = new IncidentSearchForm();
        dst.setIncDateFrom(src.getIncDateFrom());
        dst.setIncDateTo(src.getIncDateTo());
        dst.setEquipmentType(src.getEquipmentType());
        dst.setIncidentType(src.getIncidentType());
        dst.setStatus(src.getStatus());
        dst.setSeverity(src.getSeverity());
        dst.setTeam(src.getTeam());
        dst.setKeyword(src.getKeyword());
        return dst;
    }

    private String[] getAllEquipmentTypes() {
        return new String[]{"変圧器", "遮断器", "開閉器", "ケーブル", "母線", "保護継電器", "計器用変成器"};
    }
}
