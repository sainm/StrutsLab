package com.strutslab.service.mst;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.struts.upload.FormFile;

import com.strutslab.dao.EqpDao;
import com.strutslab.db.MyBatisUtil;
import com.strutslab.dto.EqpDto;
import com.strutslab.form.mst.EqpForm;

public class EqpSaveService {

    public void loadEquipment(EqpForm form, String equipmentCode) {
        if (equipmentCode == null || equipmentCode.isEmpty()) return;

        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            EqpDao dao = sqlSession.getMapper(EqpDao.class);
            EqpDto dto = dao.findById(equipmentCode);
            if (dto != null) {
                form.setEquipmentCode(dto.getEquipmentCode());
                form.setEquipmentName(dto.getEquipmentName());
                form.setEquipmentType(dto.getEquipmentType());
                form.setVoltageLevel(dto.getVoltageLevel());
                form.setRatedCapacity(dto.getRatedCapacity());
                form.setRatedCurrent(dto.getRatedCurrent());
                form.setFrequency(dto.getFrequency());
                form.setParentEquipmentCode(dto.getParentEquipmentCode());
                form.setInstallDate(dto.getInstallDate());
                form.setLocationAddress(dto.getLocationAddress());
                form.setCoordinates(dto.getCoordinates());
                form.setMaintenanceRank(dto.getMaintenanceRank());
                form.setInspectionInterval(dto.getInspectionInterval());
                form.setLastInspectionDate(dto.getLastInspectionDate());
                form.setNextInspectionDate(dto.getNextInspectionDate());
                form.setStatus(dto.getStatus());
                form.setNote(dto.getNote());
            }
        }
    }

    public String validate(EqpForm form) {
        if (form.getEquipmentName() == null || form.getEquipmentName().trim().isEmpty()) {
            return "errors.required:label.equipmentName";
        }
        if (form.getEquipmentName().length() > 100) {
            return "errors.maxlength:label.equipmentName";
        }
        if (form.getEquipmentType() == null || form.getEquipmentType().isEmpty()) {
            return "errors.required:label.equipmentType";
        }
        if (form.getStatus() == null || form.getStatus().isEmpty()) {
            return "errors.required:label.status";
        }
        return null;
    }

    public String checkParentNotAbolished(String parentCode) {
        if (parentCode == null || parentCode.isEmpty()) return null;

        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            EqpDao dao = sqlSession.getMapper(EqpDao.class);
            EqpDto parent = dao.findById(parentCode);
            if (parent != null && "廃止".equals(parent.getStatus())) {
                return "errors.parent.abolished";
            }
        }
        return null;
    }

    public String save(EqpForm form, String uploadDir) throws Exception {
        EqpDto dto = formToDto(form);

        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            EqpDao dao = sqlSession.getMapper(EqpDao.class);

            boolean isUpdate = dto.getEquipmentCode() != null
                    && !dto.getEquipmentCode().isEmpty()
                    && dao.findById(dto.getEquipmentCode()) != null;

            if (isUpdate) {
                dao.update(dto);
            } else {
                dto.setEquipmentCode(generateNewCode(dao));
                dao.insert(dto);
            }

            // File upload
            FormFile uploadFile = form.getAttachFile();
            if (uploadFile != null && uploadFile.getFileSize() > 0) {
                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdirs();
                String fileName = dto.getEquipmentCode() + "_" + uploadFile.getFileName();
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
        return dto.getEquipmentCode();
    }

    public void delete(String equipmentCode) {
        if (equipmentCode == null || equipmentCode.isEmpty()) return;

        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            EqpDao dao = sqlSession.getMapper(EqpDao.class);
            dao.delete(equipmentCode);
            sqlSession.commit();
        }
    }

    private EqpDto formToDto(EqpForm form) {
        EqpDto dto = new EqpDto();
        dto.setEquipmentCode(form.getEquipmentCode());
        dto.setEquipmentName(form.getEquipmentName());
        dto.setEquipmentType(form.getEquipmentType());
        dto.setVoltageLevel(form.getVoltageLevel());
        dto.setRatedCapacity(form.getRatedCapacity());
        dto.setRatedCurrent(form.getRatedCurrent());
        dto.setFrequency(form.getFrequency());
        dto.setParentEquipmentCode(form.getParentEquipmentCode());
        dto.setInstallDate(form.getInstallDate());
        dto.setLocationAddress(form.getLocationAddress());
        dto.setCoordinates(form.getCoordinates());
        dto.setMaintenanceRank(form.getMaintenanceRank());
        dto.setInspectionInterval(form.getInspectionInterval());
        dto.setLastInspectionDate(form.getLastInspectionDate());
        dto.setNextInspectionDate(form.getNextInspectionDate());
        dto.setStatus(form.getStatus());
        dto.setNote(form.getNote());
        return dto;
    }

    private synchronized String generateNewCode(EqpDao dao) {
        List<EqpDto> all = dao.findAll();
        int maxNum = 0;
        for (EqpDto dto : all) {
            String code = dto.getEquipmentCode();
            if (code != null && code.startsWith("EQ-")) {
                try {
                    int num = Integer.parseInt(code.substring(3));
                    if (num > maxNum) maxNum = num;
                } catch (NumberFormatException e) {
                    // skip
                }
            }
        }
        return String.format("EQ-%04d", maxNum + 1);
    }
}
