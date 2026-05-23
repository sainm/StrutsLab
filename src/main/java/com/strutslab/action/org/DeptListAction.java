package com.strutslab.action.org;

import java.io.PrintWriter;
import java.util.ArrayList;
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

import com.strutslab.dao.DeptDao;
import com.strutslab.db.MyBatisUtil;
import com.strutslab.dto.DeptDto;
import com.strutslab.form.org.DeptSearchForm;

public class DeptListAction extends Action {

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        DeptSearchForm searchForm = (DeptSearchForm) form;
        HttpSession session = request.getSession();

        // Persist search conditions in session
        session.setAttribute("deptSearchForm", searchForm);

        String paramCsv = request.getParameter("csv");
        if ("true".equals(paramCsv)) {
            exportCsv(response, searchForm);
            return null;
        }

        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            DeptDao dao = sqlSession.getMapper(DeptDao.class);

            Map<String, Object> params = new HashMap<>();
            if (searchForm.getDeptCode() != null && !searchForm.getDeptCode().isEmpty()) {
                params.put("deptCode", searchForm.getDeptCode());
            }
            if (searchForm.getDeptName() != null && !searchForm.getDeptName().isEmpty()) {
                params.put("deptName", searchForm.getDeptName());
            }

            List<DeptDto> allDepts = dao.search(params);

            // Build hierarchical tree (4 levels)
            List<DeptDto> tree = buildTree(allDepts);

            request.setAttribute("deptTree", tree);
            request.setAttribute("allDepts", allDepts);
        }

        return mapping.findForward("success");
    }

    private List<DeptDto> buildTree(List<DeptDto> allDepts) {
        List<DeptDto> roots = new ArrayList<>();
        Map<String, DeptDto> map = new HashMap<>();

        for (DeptDto d : allDepts) {
            map.put(d.getDeptCode(), d);
        }

        for (DeptDto d : allDepts) {
            if (d.getParentDeptCode() == null || d.getParentDeptCode().isEmpty()) {
                roots.add(d);
            } else {
                DeptDto parent = map.get(d.getParentDeptCode());
                if (parent != null) {
                    List<DeptDto> children = parent.getChildren();
                    if (children == null) {
                        children = new ArrayList<>();
                        parent.setChildren(children);
                    }
                    children.add(d);
                } else {
                    roots.add(d);
                }
            }
        }

        return roots;
    }

    private void exportCsv(HttpServletResponse response, DeptSearchForm searchForm) throws Exception {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            DeptDao dao = sqlSession.getMapper(DeptDao.class);

            Map<String, Object> params = new HashMap<>();
            if (searchForm.getDeptCode() != null && !searchForm.getDeptCode().isEmpty()) {
                params.put("deptCode", searchForm.getDeptCode());
            }
            if (searchForm.getDeptName() != null && !searchForm.getDeptName().isEmpty()) {
                params.put("deptName", searchForm.getDeptName());
            }

            List<DeptDto> list = dao.search(params);

            response.setContentType("text/csv; charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"dept_list.csv\"");

            try (PrintWriter pw = response.getWriter()) {
                pw.write('﻿');
                pw.println("部署コード,部署名,親部署コード,階層,種別,開始日,終了日,住所,電話番号");
                for (DeptDto d : list) {
                    StringBuilder line = new StringBuilder();
                    appendCsvField(line, d.getDeptCode());
                    appendCsvField(line, d.getDeptName());
                    appendCsvField(line, d.getParentDeptCode());
                    appendCsvField(line, String.valueOf(d.getDeptLevel()));
                    appendCsvField(line, d.getDeptType());
                    appendCsvField(line, d.getStartDate());
                    appendCsvField(line, d.getEndDate());
                    appendCsvField(line, d.getAddress());
                    appendCsvField(line, d.getTel());
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
