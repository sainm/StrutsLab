package com.strutslab.action.parts;

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

import com.strutslab.dao.PartsDao;
import com.strutslab.db.MyBatisUtil;
import com.strutslab.dto.PartsDto;
import com.strutslab.form.parts.PartsSearchForm;

public class PartsListAction extends Action {

    private static final int PAGE_SIZE = 10;

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        PartsSearchForm searchForm = (PartsSearchForm) form;
        HttpSession session = request.getSession();

        session.setAttribute("partsSearchForm", searchForm);

        String paramCsv = request.getParameter("csv");
        if ("true".equals(paramCsv)) {
            exportCsv(response, searchForm);
            return null;
        }

        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            PartsDao dao = sqlSession.getMapper(PartsDao.class);

            Map<String, Object> params = new HashMap<>();
            if (searchForm.getEquipmentType() != null && !searchForm.getEquipmentType().isEmpty()) {
                params.put("equipmentType", searchForm.getEquipmentType());
            }
            if (searchForm.getPartType() != null && !searchForm.getPartType().isEmpty()) {
                params.put("partType", searchForm.getPartType());
            }
            if (searchForm.getStockStatus() != null && !searchForm.getStockStatus().isEmpty()) {
                params.put("stockStatus", searchForm.getStockStatus());
            }
            if (searchForm.getKeyword() != null && !searchForm.getKeyword().isEmpty()) {
                params.put("keyword", searchForm.getKeyword());
            }

            int totalCount = dao.count(params);
            int totalPages = (int) Math.ceil((double) totalCount / PAGE_SIZE);
            int currentPage = searchForm.getPage();
            if (currentPage < 1) currentPage = 1;
            if (currentPage > totalPages && totalPages > 0) currentPage = totalPages;

            int offset = (currentPage - 1) * PAGE_SIZE;
            params.put("offset", offset);
            params.put("limit", PAGE_SIZE);

            List<PartsDto> list = dao.search(params);

            // Compute stock status for badge display
            for (PartsDto p : list) {
                Integer stock = p.getCurrentStock();
                Integer orderPoint = p.getOrderPoint();
                if (stock == null) stock = 0;
                if (orderPoint == null) orderPoint = 0;

                if (stock == 0) {
                    p.setNote("out"); // Use note field temporarily for badge status
                } else if (stock <= orderPoint) {
                    p.setNote("low");
                } else {
                    p.setNote("ok");
                }
            }

            StringBuilder pagingUrl = new StringBuilder();
            pagingUrl.append(request.getContextPath()).append("/parts/list.do?");
            appendParam(pagingUrl, "equipmentType", searchForm.getEquipmentType());
            appendParam(pagingUrl, "partType", searchForm.getPartType());
            appendParam(pagingUrl, "stockStatus", searchForm.getStockStatus());
            appendParam(pagingUrl, "keyword", searchForm.getKeyword());

            request.setAttribute("partsList", list);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("pagingUrl", pagingUrl.toString());
        }

        if (request.getParameter("editCode") != null) {
            return mapping.findForward("edit");
        }

        return mapping.findForward("success");
    }

    private void exportCsv(HttpServletResponse response, PartsSearchForm searchForm) throws Exception {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            PartsDao dao = sqlSession.getMapper(PartsDao.class);

            Map<String, Object> params = new HashMap<>();
            if (searchForm.getEquipmentType() != null && !searchForm.getEquipmentType().isEmpty()) {
                params.put("equipmentType", searchForm.getEquipmentType());
            }
            if (searchForm.getPartType() != null && !searchForm.getPartType().isEmpty()) {
                params.put("partType", searchForm.getPartType());
            }
            if (searchForm.getStockStatus() != null && !searchForm.getStockStatus().isEmpty()) {
                params.put("stockStatus", searchForm.getStockStatus());
            }
            if (searchForm.getKeyword() != null && !searchForm.getKeyword().isEmpty()) {
                params.put("keyword", searchForm.getKeyword());
            }

            List<PartsDto> list = dao.search(params);

            response.setContentType("text/csv; charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"parts_list.csv\"");

            try (PrintWriter pw = response.getWriter()) {
                pw.write('﻿');
                pw.println("部品コード,部品名,部品種別,単位,発注点,安全在庫,現在庫,単価,仕入先");
                for (PartsDto p : list) {
                    StringBuilder line = new StringBuilder();
                    appendCsvField(line, p.getPartCode());
                    appendCsvField(line, p.getPartName());
                    appendCsvField(line, p.getPartType());
                    appendCsvField(line, p.getUnit());
                    appendCsvField(line, p.getOrderPoint() != null ? String.valueOf(p.getOrderPoint()) : "");
                    appendCsvField(line, p.getSafetyStock() != null ? String.valueOf(p.getSafetyStock()) : "");
                    appendCsvField(line, p.getCurrentStock() != null ? String.valueOf(p.getCurrentStock()) : "");
                    appendCsvField(line, p.getUnitPrice() != null ? String.valueOf(p.getUnitPrice()) : "");
                    appendCsvField(line, p.getSupplier());
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
