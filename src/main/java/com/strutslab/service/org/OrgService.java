package com.strutslab.service.org;

import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;

import com.strutslab.dao.DeptDao;
import com.strutslab.dao.EmpDao;
import com.strutslab.db.MyBatisUtil;
import com.strutslab.dto.DeptDto;
import com.strutslab.dto.EmpDto;

public class OrgService {

    private static final int EMP_PAGE_SIZE = 15;

    // ---- Department ----

    public List<DeptDto> searchDepts(String deptCode, String deptName) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            DeptDao dao = sqlSession.getMapper(DeptDao.class);
            Map<String, Object> params = new HashMap<>();
            if (deptCode != null && !deptCode.isEmpty()) params.put("deptCode", deptCode);
            if (deptName != null && !deptName.isEmpty()) params.put("deptName", deptName);
            return dao.search(params);
        }
    }

    public List<DeptDto> buildDeptTree(List<DeptDto> allDepts) {
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

    public void exportDeptCsv(HttpServletResponse response, String deptCode, String deptName) throws Exception {
        List<DeptDto> list = searchDepts(deptCode, deptName);

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

    public DeptDto findDeptById(String deptCode) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            DeptDao dao = sqlSession.getMapper(DeptDao.class);
            return dao.findById(deptCode);
        }
    }

    public void saveDept(DeptDto dto) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            DeptDao dao = sqlSession.getMapper(DeptDao.class);

            boolean isUpdate = dto.getDeptCode() != null && !dto.getDeptCode().isEmpty()
                    && dao.findById(dto.getDeptCode()) != null;

            if (isUpdate) {
                dao.update(dto);
            } else {
                if (dto.getDeptCode() == null || dto.getDeptCode().isEmpty()) {
                    dto.setDeptCode(generateDeptCode(dao));
                }
                dao.insert(dto);
            }

            sqlSession.commit();
        }
    }

    private String generateDeptCode(DeptDao dao) {
        List<DeptDto> all = dao.findAll();
        int maxNum = 0;
        for (DeptDto d : all) {
            String code = d.getDeptCode();
            if (code != null) {
                try {
                    int num = Integer.parseInt(code.replaceAll("\\D", ""));
                    if (num > maxNum) maxNum = num;
                } catch (NumberFormatException e) { }
            }
        }
        return String.format("DEPT-%04d", maxNum + 1);
    }

    // ---- Employee ----

    public static class EmpSearchResult {
        public final List<EmpDto> list;
        public final int currentPage;
        public final int totalPages;
        public final int totalCount;

        EmpSearchResult(List<EmpDto> list, int currentPage, int totalPages, int totalCount) {
            this.list = list;
            this.currentPage = currentPage;
            this.totalPages = totalPages;
            this.totalCount = totalCount;
        }
    }

    public EmpSearchResult searchEmps(String deptCode, String position,
            String yearFrom, String yearTo, String[] qualifications, int page) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            EmpDao dao = sqlSession.getMapper(EmpDao.class);
            Map<String, Object> params = new HashMap<>();
            if (deptCode != null && !deptCode.isEmpty()) params.put("deptCode", deptCode);
            if (position != null && !position.isEmpty()) params.put("position", position);
            if (yearFrom != null && !yearFrom.isEmpty()) params.put("yearFrom", yearFrom);
            if (yearTo != null && !yearTo.isEmpty()) params.put("yearTo", yearTo);
            if (qualifications != null && qualifications.length > 0) params.put("qualifications", qualifications);

            int totalCount = dao.count(params);
            int totalPages = (int) Math.ceil((double) totalCount / EMP_PAGE_SIZE);
            int currentPage = page;
            if (currentPage < 1) currentPage = 1;
            if (currentPage > totalPages && totalPages > 0) currentPage = totalPages;

            params.put("offset", (currentPage - 1) * EMP_PAGE_SIZE);
            params.put("limit", EMP_PAGE_SIZE);

            List<EmpDto> list = dao.search(params);
            return new EmpSearchResult(list, currentPage, totalPages, totalCount);
        }
    }

    public void exportEmpCsv(HttpServletResponse response, String deptCode,
            String position, String yearFrom, String yearTo) throws Exception {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            EmpDao dao = sqlSession.getMapper(EmpDao.class);
            Map<String, Object> params = new HashMap<>();
            if (deptCode != null && !deptCode.isEmpty()) params.put("deptCode", deptCode);
            if (position != null && !position.isEmpty()) params.put("position", position);
            if (yearFrom != null && !yearFrom.isEmpty()) params.put("yearFrom", yearFrom);
            if (yearTo != null && !yearTo.isEmpty()) params.put("yearTo", yearTo);

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

    public String buildEmpPagingUrl(String contextPath, String deptCode,
            String position, String yearFrom, String yearTo) {
        StringBuilder sb = new StringBuilder();
        sb.append(contextPath).append("/org/emp/list.do?");
        appendParam(sb, "deptCode", deptCode);
        appendParam(sb, "position", position);
        appendParam(sb, "yearFrom", yearFrom);
        appendParam(sb, "yearTo", yearTo);
        return sb.toString();
    }

    public void lockUnlockEmps(String[] empNos, boolean lock) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            EmpDao dao = sqlSession.getMapper(EmpDao.class);
            for (String empNo : empNos) {
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

    public EmpDto findEmpById(String empNo) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            EmpDao dao = sqlSession.getMapper(EmpDao.class);
            return dao.findById(empNo);
        }
    }

    public EmpDto findEmpByLoginId(String loginId) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            EmpDao dao = sqlSession.getMapper(EmpDao.class);
            return dao.findByLoginId(loginId);
        }
    }

    public void saveEmp(EmpDto dto, String password) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            EmpDao empDao = sqlSession.getMapper(EmpDao.class);

            boolean isUpdate = dto.getEmpNo() != null && !dto.getEmpNo().isEmpty()
                    && empDao.findById(dto.getEmpNo()) != null;

            if (isUpdate) {
                if (password != null && !password.isEmpty()) {
                    String salt = generateSalt();
                    dto.setPasswordSalt(salt);
                    dto.setPasswordHash(hashPassword(password, salt));
                } else {
                    EmpDto existing = empDao.findById(dto.getEmpNo());
                    if (existing != null) {
                        dto.setPasswordHash(existing.getPasswordHash());
                        dto.setPasswordSalt(existing.getPasswordSalt());
                    }
                }
                EmpDto existing = empDao.findById(dto.getEmpNo());
                if (existing != null) {
                    dto.setIsLocked(existing.getIsLocked());
                }
                empDao.update(dto);
            } else {
                String newNo = generateEmpNo(empDao);
                dto.setEmpNo(newNo);
                if (password != null && !password.isEmpty()) {
                    String salt = generateSalt();
                    dto.setPasswordSalt(salt);
                    dto.setPasswordHash(hashPassword(password, salt));
                }
                dto.setIsLocked(false);
                empDao.insert(dto);
            }

            sqlSession.commit();
        }
    }

    private String generateEmpNo(EmpDao dao) {
        List<EmpDto> all = dao.findAll();
        int maxNum = 0;
        for (EmpDto e : all) {
            String no = e.getEmpNo();
            if (no != null && no.startsWith("EMP-")) {
                try {
                    int num = Integer.parseInt(no.substring(4));
                    if (num > maxNum) maxNum = num;
                } catch (NumberFormatException ex) { }
            }
        }
        return String.format("EMP-%04d", maxNum + 1);
    }

    private String generateSalt() {
        SecureRandom rng = new SecureRandom();
        byte[] salt = new byte[16];
        rng.nextBytes(salt);
        StringBuilder sb = new StringBuilder();
        for (byte b : salt) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest((salt + password).getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | java.io.UnsupportedEncodingException e) {
            throw new RuntimeException("Password hashing failed", e);
        }
    }

    // ---- CSV helpers ----

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
