package com.strutslab.action.org;

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

import com.strutslab.dao.EmpDao;
import com.strutslab.db.MyBatisUtil;
import com.strutslab.dto.EmpDto;
import com.strutslab.form.org.EmpSearchForm;

public class EmpListAction extends Action {

    private static final int PAGE_SIZE = 15;

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        EmpSearchForm searchForm = (EmpSearchForm) form;
        HttpSession session = request.getSession();

        // Persist search conditions in session
        session.setAttribute("empSearchForm", searchForm);

        String paramCsv = request.getParameter("csv");
        String paramLock = request.getParameter("lock");
        String paramUnlock = request.getParameter("unlock");

        if ("true".equals(paramCsv)) {
            exportCsv(response, searchForm);
            return null;
        }

        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            EmpDao dao = sqlSession.getMapper(EmpDao.class);

            // Lock/unlock selected
            if ("true".equals(paramLock) || "true".equals(paramUnlock)) {
                String[] selected = searchForm.getQualifications(); // reuse qualifications field for selected items
                if (selected != null) {
                    boolean lock = "true".equals(paramLock);
                    for (String empNo : selected) {
                        if (empNo != null && !empNo.trim().isEmpty()) {
                            EmpDto emp = dao.findById(empNo.trim());
                            if (emp != null) {
                                emp.setIsLocked(lock);
                                dao.update(emp);
                            }
                        }
                    }
                    sqlSession.commit();
                }
            }

            // Build parameter map
            Map<String, Object> params = new HashMap<>();
            if (searchForm.getDeptCode() != null && !searchForm.getDeptCode().isEmpty()) {
                params.put("deptCode", searchForm.getDeptCode());
            }
            if (searchForm.getPosition() != null && !searchForm.getPosition().isEmpty()) {
                params.put("position", searchForm.getPosition());
            }
            if (searchForm.getYearFrom() != null && !searchForm.getYearFrom().isEmpty()) {
                params.put("yearFrom", searchForm.getYearFrom());
            }
            if (searchForm.getYearTo() != null && !searchForm.getYearTo().isEmpty()) {
                params.put("yearTo", searchForm.getYearTo());
            }
            if (searchForm.getQualifications() != null && searchForm.getQualifications().length > 0) {
                // For search, qualifications are passed in the request, not the form
                String[] quals = request.getParameterValues("qualifications");
                if (quals != null && quals.length > 0) {
                    params.put("qualifications", quals);
                }
            }

            int totalCount = dao.count(params);
            int totalPages = (int) Math.ceil((double) totalCount / PAGE_SIZE);
            int currentPage = searchForm.getPage();
            if (currentPage < 1) currentPage = 1;
            if (currentPage > totalPages && totalPages > 0) currentPage = totalPages;

            int offset = (currentPage - 1) * PAGE_SIZE;
            params.put("offset", offset);
            params.put("limit", PAGE_SIZE);

            List<EmpDto> list = dao.search(params);

            StringBuilder pagingUrl = new StringBuilder();
            pagingUrl.append(request.getContextPath()).append("/org/emp/list.do?");
            appendParam(pagingUrl, "deptCode", searchForm.getDeptCode());
            appendParam(pagingUrl, "position", searchForm.getPosition());
            appendParam(pagingUrl, "yearFrom", searchForm.getYearFrom());
            appendParam(pagingUrl, "yearTo", searchForm.getYearTo());

            request.setAttribute("empList", list);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("pagingUrl", pagingUrl.toString());
        }

        if (request.getParameter("editCode") != null) {
            return mapping.findForward("edit");
        }

        return mapping.findForward("success");
    }

    private void exportCsv(HttpServletResponse response, EmpSearchForm searchForm) throws Exception {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            EmpDao dao = sqlSession.getMapper(EmpDao.class);

            Map<String, Object> params = new HashMap<>();
            if (searchForm.getDeptCode() != null && !searchForm.getDeptCode().isEmpty()) {
                params.put("deptCode", searchForm.getDeptCode());
            }
            if (searchForm.getPosition() != null && !searchForm.getPosition().isEmpty()) {
                params.put("position", searchForm.getPosition());
            }
            if (searchForm.getYearFrom() != null && !searchForm.getYearFrom().isEmpty()) {
                params.put("yearFrom", searchForm.getYearFrom());
            }
            if (searchForm.getYearTo() != null && !searchForm.getYearTo().isEmpty()) {
                params.put("yearTo", searchForm.getYearTo());
            }

            List<EmpDto> list = dao.search(params);

            response.setContentType("text/csv; charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"employee_list.csv\"");

            try (PrintWriter pw = response.getWriter()) {
                pw.write('﻿');
                pw.println("社員番号,氏名,氏名カナ,部署コード,職位,入社年月,点検員ランク,認定期限,ロック");
                for (EmpDto e : list) {
                    StringBuilder line = new StringBuilder();
                    appendCsvField(line, e.getEmpNo());
                    appendCsvField(line, e.getName());
                    appendCsvField(line, e.getNameKana());
                    appendCsvField(line, e.getDeptCode());
                    appendCsvField(line, e.getPosition());
                    appendCsvField(line, e.getJoinDate());
                    appendCsvField(line, e.getInspectionRank());
                    appendCsvField(line, e.getInspectionCertExpire());
                    appendCsvField(line, e.getIsLocked() != null && e.getIsLocked() ? "ロック中" : "");
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
