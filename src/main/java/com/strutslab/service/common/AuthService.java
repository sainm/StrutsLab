package com.strutslab.service.common;

import java.security.MessageDigest;

import org.apache.ibatis.session.SqlSession;

import com.strutslab.dao.EmpDao;
import com.strutslab.db.MyBatisUtil;
import com.strutslab.dto.EmpDto;

public class AuthService {

    public static class AuthResult {
        public final EmpDto emp;
        public final String errorKey;

        private AuthResult(EmpDto emp, String errorKey) {
            this.emp = emp;
            this.errorKey = errorKey;
        }

        public static AuthResult success(EmpDto emp) { return new AuthResult(emp, null); }
        public static AuthResult fail(String errorKey) { return new AuthResult(null, errorKey); }
    }

    public AuthResult authenticate(String loginId, String password) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            EmpDao dao = sqlSession.getMapper(EmpDao.class);
            EmpDto emp = dao.findByLoginId(loginId);

            if (emp == null) {
                return AuthResult.fail("errors.login.failed");
            }

            String salt = emp.getPasswordSalt() != null ? emp.getPasswordSalt() : "";
            String hash = sha256(salt + password);
            if (!hash.equals(emp.getPasswordHash())) {
                return AuthResult.fail("errors.login.failed");
            }

            if (emp.getIsLocked() != null && emp.getIsLocked()) {
                return AuthResult.fail("errors.account.locked");
            }

            return AuthResult.success(emp);
        } catch (Exception e) {
            return AuthResult.fail("errors.login.failed");
        }
    }

    private String sha256(String s) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(s.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
