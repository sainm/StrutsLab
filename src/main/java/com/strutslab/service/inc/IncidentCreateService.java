package com.strutslab.service.inc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.struts.upload.FormFile;

import com.strutslab.dao.IncidentDao;
import com.strutslab.dao.PlanDao;
import com.strutslab.db.MyBatisUtil;
import com.strutslab.dto.IncidentDto;
import com.strutslab.dto.PlanDto;
import com.strutslab.dto.TimelineDto;
import com.strutslab.form.inc.IncidentForm;

public class IncidentCreateService {

    public String generateIncidentNo() {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            IncidentDao dao = sqlSession.getMapper(IncidentDao.class);
            return dao.generateIncidentNo();
        }
    }

    public List<IncidentDto> searchSimilar(String incidentType, String incidentPart) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            IncidentDao dao = sqlSession.getMapper(IncidentDao.class);
            Map<String, Object> params = new HashMap<>();
            if (incidentType != null) params.put("type", incidentType);
            if (incidentPart != null) params.put("part", incidentPart);
            return dao.searchSimilar(params);
        }
    }

    public String save(IncidentForm form, String finder, FormFile[] attachments, String uploadRoot) throws Exception {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            IncidentDao dao = sqlSession.getMapper(IncidentDao.class);

            String incidentNo = dao.generateIncidentNo();

            IncidentDto dto = new IncidentDto();
            dto.setIncidentNo(incidentNo);
            dto.setResultId(form.getResultId() != null && form.getResultId() > 0 ? form.getResultId() : null);
            dto.setIncidentDatetime(parseIncidentDateTime(form.getIncidentDateTime()));
            dto.setFinder(finder);
            dto.setEquipmentCode(form.getEquipmentCode());
            dto.setWeather(form.getWeather());
            dto.setTemperature(form.getTemperature());
            dto.setIncidentType(form.getIncidentType());
            dto.setSeverity(form.getSeverity());
            dto.setIncidentPart(form.getIncidentPart());
            dto.setIncidentDetail(form.getIncidentDetail());
            dto.setTmpAction(form.getTmpAction());
            dto.setTmpActionPerson(form.getTmpActionPerson());
            dto.setTmpActionDate(form.getTmpActionDate());
            dto.setStatus("未了");
            dao.insert(dto);

            TimelineDto tl = new TimelineDto();
            tl.setIncidentNo(incidentNo);
            tl.setActionDatetime(new Timestamp(System.currentTimeMillis()));
            tl.setActionUser(finder);
            tl.setActionContent("異常報告 作成");
            tl.setStatusFrom(null);
            tl.setStatusTo("未了");
            dao.insertTimeline(tl);

            if (attachments != null && attachments.length > 0) {
                String dir = uploadRoot + "/attachments/inc/" + incidentNo + "/";
                File attachDir = new File(dir);
                if (!attachDir.exists()) attachDir.mkdirs();

                for (FormFile f : attachments) {
                    if (f != null && f.getFileSize() > 0) {
                        String safeName = f.getFileName().replaceAll("[\\\\/:*?\"<>|]", "_");
                        File dest = new File(attachDir, safeName);
                        try (InputStream is = f.getInputStream();
                                FileOutputStream fos = new FileOutputStream(dest)) {
                            byte[] buf = new byte[4096];
                            int len;
                            while ((len = is.read(buf)) > 0) {
                                fos.write(buf, 0, len);
                            }
                        }
                    }
                }
            }

            sqlSession.commit();
            return incidentNo;
        }
    }

    private Timestamp parseIncidentDateTime(String dateTimeStr) {
        if (dateTimeStr != null && !dateTimeStr.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                return new Timestamp(sdf.parse(dateTimeStr).getTime());
            } catch (Exception e) {
                // fall through to default
            }
        }
        return new Timestamp(System.currentTimeMillis());
    }

    public PlanDto findPlanById(int planId) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            PlanDao dao = sqlSession.getMapper(PlanDao.class);
            return dao.findById(planId);
        }
    }
}
