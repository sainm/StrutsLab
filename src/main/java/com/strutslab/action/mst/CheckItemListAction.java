package com.strutslab.action.mst;

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

import com.strutslab.dao.ChkItemDao;
import com.strutslab.db.MyBatisUtil;
import com.strutslab.dto.ChkTmplDto;
import com.strutslab.form.mst.CheckItemSearchForm;

public class CheckItemListAction extends Action {

    private static final int PAGE_SIZE = 10;

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        CheckItemSearchForm searchForm = (CheckItemSearchForm) form;
        HttpSession session = request.getSession();

        // Persist search conditions in session
        session.setAttribute("checkItemSearchForm", searchForm);

        String paramCopy = request.getParameter("copy");
        String paramMoveUp = request.getParameter("moveUp");
        String paramMoveDown = request.getParameter("moveDown");

        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            ChkItemDao dao = sqlSession.getMapper(ChkItemDao.class);

            // Handle copy
            if (paramCopy != null && !paramCopy.isEmpty()) {
                int templateId = Integer.parseInt(paramCopy);
                dao.copyTemplate(templateId);
                sqlSession.commit();
            }

            // Handle reorder: move up
            if (paramMoveUp != null && !paramMoveUp.isEmpty()) {
                int currentId = Integer.parseInt(paramMoveUp);
                // Find the two templates to swap
                Map<String, Object> allParams = buildSearchParams(searchForm);
                allParams.remove("offset");
                allParams.remove("limit");
                List<ChkTmplDto> allList = dao.search(allParams);
                for (int i = 0; i < allList.size(); i++) {
                    if (allList.get(i).getTemplateId() == currentId && i > 0) {
                        int prevId = allList.get(i - 1).getTemplateId();
                        dao.swapOrder(currentId, prevId);
                        sqlSession.commit();
                        break;
                    }
                }
            }

            // Handle reorder: move down
            if (paramMoveDown != null && !paramMoveDown.isEmpty()) {
                int currentId = Integer.parseInt(paramMoveDown);
                Map<String, Object> allParams = buildSearchParams(searchForm);
                allParams.remove("offset");
                allParams.remove("limit");
                List<ChkTmplDto> allList = dao.search(allParams);
                for (int i = 0; i < allList.size(); i++) {
                    if (allList.get(i).getTemplateId() == currentId && i < allList.size() - 1) {
                        int nextId = allList.get(i + 1).getTemplateId();
                        dao.swapOrder(currentId, nextId);
                        sqlSession.commit();
                        break;
                    }
                }
            }

            // Build search parameter map
            Map<String, Object> params = buildSearchParams(searchForm);

            // Count total records
            int totalCount = dao.count(params);
            int totalPages = (int) Math.ceil((double) totalCount / PAGE_SIZE);
            int currentPage = searchForm.getPage();
            if (currentPage < 1) currentPage = 1;
            if (currentPage > totalPages && totalPages > 0) currentPage = totalPages;

            int offset = (currentPage - 1) * PAGE_SIZE;
            params.put("offset", offset);
            params.put("limit", PAGE_SIZE);

            List<ChkTmplDto> list = dao.search(params);

            // Build paging URL with current search parameters
            StringBuilder pagingUrl = new StringBuilder();
            pagingUrl.append(request.getContextPath()).append("/mst/chkItem/list.do?");
            appendParam(pagingUrl, "equipmentType", searchForm.getEquipmentType());
            appendParam(pagingUrl, "inspectionKind", searchForm.getInspectionKind());

            request.setAttribute("templateList", list);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("pagingUrl", pagingUrl.toString());
        }

        return mapping.findForward("success");
    }

    private Map<String, Object> buildSearchParams(CheckItemSearchForm searchForm) {
        Map<String, Object> params = new HashMap<>();
        if (searchForm.getEquipmentType() != null && !searchForm.getEquipmentType().isEmpty()) {
            params.put("equipmentType", searchForm.getEquipmentType());
        }
        if (searchForm.getInspectionKind() != null && !searchForm.getInspectionKind().isEmpty()) {
            params.put("inspectionKind", searchForm.getInspectionKind());
        }
        return params;
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
