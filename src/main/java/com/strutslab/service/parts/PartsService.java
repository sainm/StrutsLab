package com.strutslab.service.parts;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.struts.upload.FormFile;

import com.strutslab.dao.PartsDao;
import com.strutslab.db.MyBatisUtil;
import com.strutslab.dto.PartsDto;

public class PartsService {

    private static final int PAGE_SIZE = 10;

    public static class SearchResult {
        public final List<PartsDto> list;
        public final int currentPage;
        public final int totalPages;
        public final int totalCount;

        SearchResult(List<PartsDto> list, int currentPage, int totalPages, int totalCount) {
            this.list = list;
            this.currentPage = currentPage;
            this.totalPages = totalPages;
            this.totalCount = totalCount;
        }
    }

    public SearchResult search(String equipmentType, String partType, String stockStatus, String keyword, int page) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            PartsDao dao = sqlSession.getMapper(PartsDao.class);
            Map<String, Object> params = new HashMap<>();
            if (equipmentType != null && !equipmentType.isEmpty()) params.put("equipmentType", equipmentType);
            if (partType != null && !partType.isEmpty()) params.put("partType", partType);
            if (stockStatus != null && !stockStatus.isEmpty()) params.put("stockStatus", stockStatus);
            if (keyword != null && !keyword.isEmpty()) params.put("keyword", keyword);

            int totalCount = dao.count(params);
            int totalPages = (int) Math.ceil((double) totalCount / PAGE_SIZE);
            int currentPage = page;
            if (currentPage < 1) currentPage = 1;
            if (currentPage > totalPages && totalPages > 0) currentPage = totalPages;

            params.put("offset", (currentPage - 1) * PAGE_SIZE);
            params.put("limit", PAGE_SIZE);

            List<PartsDto> list = dao.search(params);
            return new SearchResult(list, currentPage, totalPages, totalCount);
        }
    }

    public Map<String, String> computeStockBadges(List<PartsDto> list) {
        Map<String, String> stockBadgeMap = new LinkedHashMap<>();
        for (PartsDto p : list) {
            Integer stock = p.getCurrentStock();
            Integer orderPoint = p.getOrderPoint();
            if (stock == null) stock = 0;
            if (orderPoint == null) orderPoint = 0;

            String badge;
            if (stock == 0) {
                badge = "out";
            } else if (stock <= orderPoint) {
                badge = "low";
            } else {
                badge = "ok";
            }
            stockBadgeMap.put(p.getPartCode(), badge);
        }
        return stockBadgeMap;
    }

    public void exportCsv(HttpServletResponse response, String equipmentType,
            String partType, String stockStatus, String keyword) throws Exception {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            PartsDao dao = sqlSession.getMapper(PartsDao.class);
            Map<String, Object> params = new HashMap<>();
            if (equipmentType != null && !equipmentType.isEmpty()) params.put("equipmentType", equipmentType);
            if (partType != null && !partType.isEmpty()) params.put("partType", partType);
            if (stockStatus != null && !stockStatus.isEmpty()) params.put("stockStatus", stockStatus);
            if (keyword != null && !keyword.isEmpty()) params.put("keyword", keyword);

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

    public String buildPagingUrl(String contextPath, String equipmentType,
            String partType, String stockStatus, String keyword) {
        StringBuilder sb = new StringBuilder();
        sb.append(contextPath).append("/parts/list.do?");
        appendParam(sb, "equipmentType", equipmentType);
        appendParam(sb, "partType", partType);
        appendParam(sb, "stockStatus", stockStatus);
        appendParam(sb, "keyword", keyword);
        return sb.toString();
    }

    public PartsDto findById(String partCode) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            PartsDao dao = sqlSession.getMapper(PartsDao.class);
            return dao.findById(partCode);
        }
    }

    public List<Map<String, Object>> findEquipmentRelations(String partCode) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            PartsDao dao = sqlSession.getMapper(PartsDao.class);
            return dao.findEquipmentRelations(partCode);
        }
    }

    public void save(PartsDto dto, String[] equipmentCodes, Map<String, String> equipmentTypes,
            FormFile uploadFile, String uploadRoot) throws Exception {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            PartsDao dao = sqlSession.getMapper(PartsDao.class);

            boolean isUpdate = dto.getPartCode() != null && !dto.getPartCode().isEmpty()
                    && dao.findById(dto.getPartCode()) != null;

            if (isUpdate) {
                dao.update(dto);
            } else {
                dto.setPartCode(generateNewCode(dao));
                dao.insert(dto);
            }

            dao.deleteEquipmentRelations(dto.getPartCode());
            if (equipmentCodes != null) {
                for (String eqCode : equipmentCodes) {
                    if (eqCode != null && !eqCode.trim().isEmpty()) {
                        Map<String, Object> rel = new HashMap<>();
                        rel.put("partCode", dto.getPartCode());
                        rel.put("equipmentCode", eqCode.trim());
                        rel.put("equipmentType", equipmentTypes != null ? equipmentTypes.get(eqCode.trim()) : null);
                        dao.insertEquipmentRelation(rel);
                    }
                }
            }

            if (uploadFile != null && uploadFile.getFileSize() > 0) {
                String uploadDir = uploadRoot + "/attachments/parts/";
                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdirs();
                String fileName = dto.getPartCode() + "_" + uploadFile.getFileName();
                File dest = new File(dir, fileName);
                try (InputStream is = uploadFile.getInputStream();
                        FileOutputStream fos = new FileOutputStream(dest)) {
                    byte[] buf = new byte[4096];
                    int len;
                    while ((len = is.read(buf)) > 0) {
                        fos.write(buf, 0, len);
                    }
                }
            }

            sqlSession.commit();
        }
    }

    private String generateNewCode(PartsDao dao) {
        List<PartsDto> all = dao.findAll();
        int maxNum = 0;
        for (PartsDto p : all) {
            String code = p.getPartCode();
            if (code != null) {
                try {
                    int num = Integer.parseInt(code.replaceAll("\\D", ""));
                    if (num > maxNum) maxNum = num;
                } catch (NumberFormatException e) { }
            }
        }
        return String.format("P-%04d", maxNum + 1);
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
