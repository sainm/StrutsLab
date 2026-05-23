package com.strutslab.action.parts;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.apache.struts.upload.FormFile;

import com.strutslab.dao.PartsDao;
import com.strutslab.db.MyBatisUtil;
import com.strutslab.dto.PartsDto;
import com.strutslab.form.parts.PartsForm;

public class PartsSaveAction extends DispatchAction {

    @Override
    protected ActionForward dispatchMethod(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response, String name)
            throws Exception {
        if ("new".equals(name)) {
            return newMethod(mapping, form, request, response);
        }
        return super.dispatchMethod(mapping, form, request, response, name);
    }

    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        PartsForm partsForm = (PartsForm) form;
        String partCode = request.getParameter("partCode");

        if (partCode != null && !partCode.isEmpty()) {
            try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
                PartsDao dao = sqlSession.getMapper(PartsDao.class);
                PartsDto dto = dao.findById(partCode);
                if (dto != null) {
                    partsForm.setPartCode(dto.getPartCode());
                    partsForm.setPartName(dto.getPartName());
                    partsForm.setPartType(dto.getPartType());
                    partsForm.setUnit(dto.getUnit());
                    partsForm.setOrderPoint(dto.getOrderPoint());
                    partsForm.setSafetyStock(dto.getSafetyStock());
                    partsForm.setUnitPrice(dto.getUnitPrice());
                    partsForm.setSupplier(dto.getSupplier());
                    partsForm.setNote(dto.getNote());

                    // Load applicable equipment
                    List<Map<String, Object>> relations = dao.findEquipmentRelations(partCode);
                    String[] codes = new String[relations.size()];
                    for (int i = 0; i < relations.size(); i++) {
                        codes[i] = (String) relations.get(i).get("equipmentCode");
                    }
                    partsForm.setApplicableEquipmentCodes(codes);
                }
            }
        }
        return mapping.findForward("success");
    }

    public ActionForward newMethod(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        PartsForm partsForm = (PartsForm) form;
        partsForm.setPartCode(null);
        partsForm.setPartName(null);
        partsForm.setPartType(null);
        partsForm.setUnit(null);
        partsForm.setOrderPoint(null);
        partsForm.setSafetyStock(null);
        partsForm.setUnitPrice(null);
        partsForm.setSupplier(null);
        partsForm.setNote(null);
        partsForm.setApplicableEquipmentCodes(null);
        return mapping.findForward("success");
    }

    public ActionForward save(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        PartsForm partsForm = (PartsForm) form;

        // Validate
        String errorMsg = validate(partsForm);
        if (errorMsg != null) {
            request.setAttribute("errorMessage", errorMsg);
            return mapping.getInputForward();
        }

        // Validate orderPoint > safetyStock
        if (partsForm.getOrderPoint() != null && partsForm.getSafetyStock() != null) {
            if (partsForm.getOrderPoint() <= partsForm.getSafetyStock()) {
                request.setAttribute("errorMessage", "発注点は安全在庫より大きく設定してください。");
                return mapping.getInputForward();
            }
        }

        PartsDto dto = new PartsDto();
        dto.setPartCode(partsForm.getPartCode());
        dto.setPartName(partsForm.getPartName());
        dto.setPartType(partsForm.getPartType());
        dto.setUnit(partsForm.getUnit());
        dto.setOrderPoint(partsForm.getOrderPoint());
        dto.setSafetyStock(partsForm.getSafetyStock());
        dto.setUnitPrice(partsForm.getUnitPrice());
        dto.setSupplier(partsForm.getSupplier());
        dto.setNote(partsForm.getNote());

        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            PartsDao dao = sqlSession.getMapper(PartsDao.class);

            boolean isUpdate = dto.getPartCode() != null
                    && !dto.getPartCode().isEmpty()
                    && dao.findById(dto.getPartCode()) != null;

            if (isUpdate) {
                dao.update(dto);
            } else {
                String newCode = generateNewCode(dao);
                dto.setPartCode(newCode);
                dao.insert(dto);
            }

            // Save applicable equipment relations
            dao.deleteEquipmentRelations(dto.getPartCode());
            String[] equipCodes = partsForm.getApplicableEquipmentCodes();
            if (equipCodes != null) {
                for (String eqCode : equipCodes) {
                    if (eqCode != null && !eqCode.trim().isEmpty()) {
                        Map<String, Object> rel = new HashMap<>();
                        rel.put("partCode", dto.getPartCode());
                        rel.put("equipmentCode", eqCode.trim());
                        rel.put("equipmentType", request.getParameter("equipmentType_" + eqCode.trim()));
                        dao.insertEquipmentRelation(rel);
                    }
                }
            }

            // File upload
            FormFile uploadFile = partsForm.getAttachFile();
            if (uploadFile != null && uploadFile.getFileSize() > 0) {
                String uploadDir = request.getSession()
                        .getServletContext().getRealPath("/attachments/parts/");
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

        return mapping.findForward("success");
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

    private String validate(PartsForm form) {
        if (form.getPartName() == null || form.getPartName().trim().isEmpty()) {
            return "部品名は必須です。";
        }
        if (form.getPartName().length() > 100) {
            return "部品名は100文字以内で入力してください。";
        }
        if (form.getUnit() == null || form.getUnit().isEmpty()) {
            return "単位は必須です。";
        }
        if (form.getOrderPoint() != null && form.getOrderPoint() < 0) {
            return "発注点は0以上で入力してください。";
        }
        if (form.getSafetyStock() != null && form.getSafetyStock() < 0) {
            return "安全在庫は0以上で入力してください。";
        }
        return null;
    }
}
