package com.strutslab.service.mst;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.strutslab.dao.ChkItemDao;
import com.strutslab.db.MyBatisUtil;
import com.strutslab.dto.ChkTmplDto;
import com.strutslab.form.mst.CheckItemSearchForm;

public class CheckItemListService {

    private static final int PAGE_SIZE = 10;

    public static class SearchResult {
        public final List<ChkTmplDto> list;
        public final int currentPage;
        public final int totalPages;

        SearchResult(List<ChkTmplDto> list, int currentPage, int totalPages) {
            this.list = list;
            this.currentPage = currentPage;
            this.totalPages = totalPages;
        }
    }

    public void copyTemplate(int templateId) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            ChkItemDao dao = sqlSession.getMapper(ChkItemDao.class);
            dao.copyTemplate(templateId);
            sqlSession.commit();
        }
    }

    public void moveUp(int currentId, CheckItemSearchForm form) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            ChkItemDao dao = sqlSession.getMapper(ChkItemDao.class);
            Map<String, Object> params = buildSearchParams(form);
            params.remove("offset");
            params.remove("limit");
            List<ChkTmplDto> allList = dao.search(params);
            for (int i = 0; i < allList.size(); i++) {
                if (allList.get(i).getTemplateId() == currentId && i > 0) {
                    dao.swapOrder(currentId, allList.get(i - 1).getTemplateId());
                    sqlSession.commit();
                    break;
                }
            }
        }
    }

    public void moveDown(int currentId, CheckItemSearchForm form) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            ChkItemDao dao = sqlSession.getMapper(ChkItemDao.class);
            Map<String, Object> params = buildSearchParams(form);
            params.remove("offset");
            params.remove("limit");
            List<ChkTmplDto> allList = dao.search(params);
            for (int i = 0; i < allList.size(); i++) {
                if (allList.get(i).getTemplateId() == currentId && i < allList.size() - 1) {
                    dao.swapOrder(currentId, allList.get(i + 1).getTemplateId());
                    sqlSession.commit();
                    break;
                }
            }
        }
    }

    public SearchResult search(CheckItemSearchForm form) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            ChkItemDao dao = sqlSession.getMapper(ChkItemDao.class);

            Map<String, Object> params = buildSearchParams(form);

            int totalCount = dao.count(params);
            int totalPages = (int) Math.ceil((double) totalCount / PAGE_SIZE);
            int currentPage = form.getPage();
            if (currentPage < 1) currentPage = 1;
            if (currentPage > totalPages && totalPages > 0) currentPage = totalPages;

            int offset = (currentPage - 1) * PAGE_SIZE;
            params.put("offset", offset);
            params.put("limit", PAGE_SIZE);

            List<ChkTmplDto> list = dao.search(params);
            return new SearchResult(list, currentPage, totalPages);
        }
    }

    public String buildPagingUrl(String contextPath, CheckItemSearchForm form) {
        StringBuilder pagingUrl = new StringBuilder();
        pagingUrl.append(contextPath).append("/mst/chkitem/list.do?");
        appendParam(pagingUrl, "equipmentType", form.getEquipmentType());
        appendParam(pagingUrl, "inspectionKind", form.getInspectionKind());
        return pagingUrl.toString();
    }

    private Map<String, Object> buildSearchParams(CheckItemSearchForm form) {
        Map<String, Object> params = new HashMap<>();
        if (form.getEquipmentType() != null && !form.getEquipmentType().isEmpty())
            params.put("equipmentType", form.getEquipmentType());
        if (form.getInspectionKind() != null && !form.getInspectionKind().isEmpty())
            params.put("inspectionKind", form.getInspectionKind());
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
