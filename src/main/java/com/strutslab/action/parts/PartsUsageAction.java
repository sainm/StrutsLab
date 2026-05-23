package com.strutslab.action.parts;

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

import com.strutslab.dao.PartsDao;
import com.strutslab.db.MyBatisUtil;
import com.strutslab.dto.PartsUsageDto;
import com.strutslab.form.parts.PartsUsageSearchForm;

public class PartsUsageAction extends Action {

    private static final int PAGE_SIZE = 20;

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        PartsUsageSearchForm searchForm = (PartsUsageSearchForm) form;

        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            PartsDao dao = sqlSession.getMapper(PartsDao.class);

            Map<String, Object> params = new HashMap<>();
            if (searchForm.getDateFrom() != null && !searchForm.getDateFrom().isEmpty()) {
                params.put("dateFrom", searchForm.getDateFrom());
            }
            if (searchForm.getDateTo() != null && !searchForm.getDateTo().isEmpty()) {
                params.put("dateTo", searchForm.getDateTo());
            }
            if (searchForm.getEquipmentType() != null && !searchForm.getEquipmentType().isEmpty()) {
                params.put("equipmentType", searchForm.getEquipmentType());
            }
            if (searchForm.getPartCode() != null && !searchForm.getPartCode().isEmpty()) {
                params.put("partCode", searchForm.getPartCode());
            }

            int totalCount = dao.countUsage(params);
            int totalPages = (int) Math.ceil((double) totalCount / PAGE_SIZE);

            String pageStr = request.getParameter("page");
            int currentPage = 1;
            if (pageStr != null) {
                try { currentPage = Integer.parseInt(pageStr); } catch (NumberFormatException e) { }
            }
            if (currentPage < 1) currentPage = 1;
            if (currentPage > totalPages && totalPages > 0) currentPage = totalPages;

            int offset = (currentPage - 1) * PAGE_SIZE;
            params.put("offset", offset);
            params.put("limit", PAGE_SIZE);

            List<PartsUsageDto> usageList = dao.searchUsage(params);

            // Check stock auto-deduction (compare stock_before/stock_after)
            for (PartsUsageDto u : usageList) {
                if (u.getStockBefore() != null && u.getStockAfter() != null) {
                    int diff = u.getStockBefore() - u.getStockAfter();
                    if (diff != u.getQuantity()) {
                        u.setNote("在庫数不整合"); // Mark discrepancy
                    }
                }
            }

            StringBuilder pagingUrl = new StringBuilder();
            pagingUrl.append(request.getContextPath()).append("/parts/usage.do?");
            if (searchForm.getDateFrom() != null) appendParam(pagingUrl, "dateFrom", searchForm.getDateFrom());
            if (searchForm.getDateTo() != null) appendParam(pagingUrl, "dateTo", searchForm.getDateTo());
            if (searchForm.getEquipmentType() != null) appendParam(pagingUrl, "equipmentType", searchForm.getEquipmentType());
            if (searchForm.getPartCode() != null) appendParam(pagingUrl, "partCode", searchForm.getPartCode());

            request.setAttribute("usageList", usageList);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("pagingUrl", pagingUrl.toString());
        }

        return mapping.findForward("success");
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
