package com.strutslab.service.parts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.strutslab.dao.PartsDao;
import com.strutslab.db.MyBatisUtil;
import com.strutslab.dto.PartsUsageDto;

public class PartsUsageService {

    private static final int PAGE_SIZE = 20;

    public static class UsageResult {
        public final List<PartsUsageDto> list;
        public final int currentPage;
        public final int totalPages;
        public final int totalCount;

        UsageResult(List<PartsUsageDto> list, int currentPage, int totalPages, int totalCount) {
            this.list = list;
            this.currentPage = currentPage;
            this.totalPages = totalPages;
            this.totalCount = totalCount;
        }
    }

    public UsageResult search(String dateFrom, String dateTo, String equipmentType, String partCode, int page) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            PartsDao dao = sqlSession.getMapper(PartsDao.class);
            Map<String, Object> params = new HashMap<>();
            if (dateFrom != null && !dateFrom.isEmpty()) params.put("dateFrom", dateFrom);
            if (dateTo != null && !dateTo.isEmpty()) params.put("dateTo", dateTo);
            if (equipmentType != null && !equipmentType.isEmpty()) params.put("equipmentType", equipmentType);
            if (partCode != null && !partCode.isEmpty()) params.put("partCode", partCode);

            int totalCount = dao.countUsage(params);
            int totalPages = (int) Math.ceil((double) totalCount / PAGE_SIZE);
            int currentPage = page;
            if (currentPage < 1) currentPage = 1;
            if (currentPage > totalPages && totalPages > 0) currentPage = totalPages;

            params.put("offset", (currentPage - 1) * PAGE_SIZE);
            params.put("limit", PAGE_SIZE);

            List<PartsUsageDto> usageList = dao.searchUsage(params);

            for (PartsUsageDto u : usageList) {
                if (u.getStockBefore() != null && u.getStockAfter() != null) {
                    int diff = u.getStockBefore() - u.getStockAfter();
                    if (diff != u.getQuantity()) {
                        u.setNote("在庫数不整合");
                    }
                }
            }

            return new UsageResult(usageList, currentPage, totalPages, totalCount);
        }
    }

    public String buildPagingUrl(String contextPath, String dateFrom, String dateTo,
            String equipmentType, String partCode) {
        StringBuilder sb = new StringBuilder();
        sb.append(contextPath).append("/parts/usage.do?");
        appendParam(sb, "dateFrom", dateFrom);
        appendParam(sb, "dateTo", dateTo);
        appendParam(sb, "equipmentType", equipmentType);
        appendParam(sb, "partCode", partCode);
        return sb.toString();
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
