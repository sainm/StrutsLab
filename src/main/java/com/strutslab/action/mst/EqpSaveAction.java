package com.strutslab.action.mst;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.apache.struts.upload.FormFile;

import com.strutslab.dao.EqpDao;
import com.strutslab.db.MyBatisUtil;
import com.strutslab.dto.EqpDto;
import com.strutslab.form.mst.EqpForm;

public class EqpSaveAction extends DispatchAction {

    /**
     * Override to route method=new to newEquipment()
     * (since "new" is a Java reserved keyword and cannot be a method name).
     */
    @Override
    protected ActionForward dispatchMethod(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response, String name)
            throws Exception {
        if ("new".equals(name)) {
            return newEquipment(mapping, form, request, response);
        }
        return super.dispatchMethod(mapping, form, request, response, name);
    }

    /**
     * GET / edit entry point (no method param).
     * If equipmentCode is provided, loads existing equipment data.
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        EqpForm eqpForm = (EqpForm) form;
        String equipmentCode = request.getParameter("equipmentCode");

        if (equipmentCode != null && !equipmentCode.isEmpty()) {
            try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
                EqpDao dao = sqlSession.getMapper(EqpDao.class);
                EqpDto dto = dao.findById(equipmentCode);
                if (dto != null) {
                    eqpForm.setEquipmentCode(dto.getEquipmentCode());
                    eqpForm.setEquipmentName(dto.getEquipmentName());
                    eqpForm.setEquipmentType(dto.getEquipmentType());
                    eqpForm.setVoltageLevel(dto.getVoltageLevel());
                    eqpForm.setRatedCapacity(dto.getRatedCapacity());
                    eqpForm.setRatedCurrent(dto.getRatedCurrent());
                    eqpForm.setFrequency(dto.getFrequency());
                    eqpForm.setParentEquipmentCode(dto.getParentEquipmentCode());
                    eqpForm.setInstallDate(dto.getInstallDate());
                    eqpForm.setLocationAddress(dto.getLocationAddress());
                    eqpForm.setCoordinates(dto.getCoordinates());
                    eqpForm.setMaintenanceRank(dto.getMaintenanceRank());
                    eqpForm.setInspectionInterval(dto.getInspectionInterval());
                    eqpForm.setLastInspectionDate(dto.getLastInspectionDate());
                    eqpForm.setNextInspectionDate(dto.getNextInspectionDate());
                    eqpForm.setStatus(dto.getStatus());
                    eqpForm.setNote(dto.getNote());
                }
            }
        }
        return mapping.findForward("success");
    }

    /**
     * New equipment blank form (method=new).
     */
    public ActionForward newEquipment(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Reset form to defaults
        EqpForm eqpForm = (EqpForm) form;
        eqpForm.setEquipmentCode(null);
        eqpForm.setEquipmentName(null);
        eqpForm.setEquipmentType(null);
        eqpForm.setVoltageLevel(null);
        eqpForm.setRatedCapacity(null);
        eqpForm.setRatedCurrent(null);
        eqpForm.setFrequency(null);
        eqpForm.setParentEquipmentCode(null);
        eqpForm.setInstallDate(null);
        eqpForm.setLocationAddress(null);
        eqpForm.setCoordinates(null);
        eqpForm.setMaintenanceRank(null);
        eqpForm.setInspectionInterval(null);
        eqpForm.setLastInspectionDate(null);
        eqpForm.setNextInspectionDate(null);
        eqpForm.setStatus("運用中");
        eqpForm.setNote(null);
        return mapping.findForward("success");
    }

    /**
     * Save equipment (insert or update).
     */
    public ActionForward save(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        EqpForm eqpForm = (EqpForm) form;

        // --- Validation ---
        String errorMsg = validate(eqpForm, request);
        if (errorMsg != null) {
            request.setAttribute("errorMessage", errorMsg);
            return mapping.getInputForward();
        }

        // Check parent equipment is not 廃止
        if (eqpForm.getParentEquipmentCode() != null
                && !eqpForm.getParentEquipmentCode().isEmpty()) {
            try (SqlSession chk = MyBatisUtil.getSqlSessionFactory().openSession()) {
                EqpDao dao = chk.getMapper(EqpDao.class);
                EqpDto parent = dao.findById(eqpForm.getParentEquipmentCode());
                if (parent != null && "廃止".equals(parent.getStatus())) {
                    request.setAttribute("errorMessage", "親設備が廃止されています。");
                    return mapping.getInputForward();
                }
            }
        }

        // --- Convert to DTO ---
        EqpDto dto = new EqpDto();
        dto.setEquipmentCode(eqpForm.getEquipmentCode());
        dto.setEquipmentName(eqpForm.getEquipmentName());
        dto.setEquipmentType(eqpForm.getEquipmentType());
        dto.setVoltageLevel(eqpForm.getVoltageLevel());
        dto.setRatedCapacity(eqpForm.getRatedCapacity());
        dto.setRatedCurrent(eqpForm.getRatedCurrent());
        dto.setFrequency(eqpForm.getFrequency());
        dto.setParentEquipmentCode(eqpForm.getParentEquipmentCode());
        dto.setInstallDate(eqpForm.getInstallDate());
        dto.setLocationAddress(eqpForm.getLocationAddress());
        dto.setCoordinates(eqpForm.getCoordinates());
        dto.setMaintenanceRank(eqpForm.getMaintenanceRank());
        dto.setInspectionInterval(eqpForm.getInspectionInterval());
        dto.setLastInspectionDate(eqpForm.getLastInspectionDate());
        dto.setNextInspectionDate(eqpForm.getNextInspectionDate());
        dto.setStatus(eqpForm.getStatus());
        dto.setNote(eqpForm.getNote());

        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            EqpDao dao = sqlSession.getMapper(EqpDao.class);

            boolean isUpdate = dto.getEquipmentCode() != null
                    && !dto.getEquipmentCode().isEmpty()
                    && dao.findById(dto.getEquipmentCode()) != null;

            if (isUpdate) {
                dao.update(dto);
            } else {
                String newCode = generateNewCode(dao);
                dto.setEquipmentCode(newCode);
                dao.insert(dto);
            }

            // --- File upload ---
            FormFile uploadFile = eqpForm.getAttachFile();
            if (uploadFile != null && uploadFile.getFileSize() > 0) {
                String uploadDir = request.getSession()
                        .getServletContext().getRealPath("/attachments/eqp/");
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
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

        return mapping.findForward("success");
    }

    /**
     * Delete equipment by equipmentCode.
     */
    public ActionForward delete(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        String equipmentCode = request.getParameter("equipmentCode");
        if (equipmentCode != null && !equipmentCode.isEmpty()) {
            try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
                EqpDao dao = sqlSession.getMapper(EqpDao.class);
                dao.delete(equipmentCode);
                sqlSession.commit();
            }
        }
        return mapping.findForward("success");
    }

    // ---- private helpers ----

    /**
     * Basic validation.
     * Returns an error message string or null if valid.
     */
    private String validate(EqpForm form, HttpServletRequest request) {
        if (form.getEquipmentName() == null || form.getEquipmentName().trim().isEmpty()) {
            return "設備名称は必須です。";
        }
        if (form.getEquipmentName().length() > 100) {
            return "設備名称は100文字以内で入力してください。";
        }
        if (form.getEquipmentType() == null || form.getEquipmentType().isEmpty()) {
            return "設備種別は必須です。";
        }
        if (form.getStatus() == null || form.getStatus().isEmpty()) {
            return "ステータスは必須です。";
        }
        return null;
    }

    /**
     * Generate a new equipment code in EQ-XXXX format.
     * Finds the current max numeric suffix and increments by 1.
     */
    private String generateNewCode(EqpDao dao) {
        List<EqpDto> all = dao.findAll();
        int maxNum = 0;
        for (EqpDto dto : all) {
            String code = dto.getEquipmentCode();
            if (code != null && code.startsWith("EQ-")) {
                try {
                    int num = Integer.parseInt(code.substring(3));
                    if (num > maxNum) {
                        maxNum = num;
                    }
                } catch (NumberFormatException e) {
                    // skip non-numeric suffixes
                }
            }
        }
        return String.format("EQ-%04d", maxNum + 1);
    }
}
