package com.strutslab.action.parts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.strutslab.dto.PartsDto;
import com.strutslab.form.parts.PartsForm;
import com.strutslab.service.parts.PartsService;

public class PartsSaveAction extends DispatchAction {

    private final PartsService service = new PartsService();

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
            PartsDto dto = service.findById(partCode);
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

                List<Map<String, Object>> relations = service.findEquipmentRelations(partCode);
                String[] codes = new String[relations.size()];
                for (int i = 0; i < relations.size(); i++) {
                    codes[i] = (String) relations.get(i).get("equipmentCode");
                }
                partsForm.setApplicableEquipmentCodes(codes);
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

        String errorMsg = validate(partsForm);
        if (errorMsg != null) {
            request.setAttribute("errorMessage", errorMsg);
            return mapping.getInputForward();
        }

        if (partsForm.getOrderPoint() != null && partsForm.getSafetyStock() != null) {
            if (partsForm.getOrderPoint() <= partsForm.getSafetyStock()) {
                request.setAttribute("errorMessage",
                    getResources(request).getMessage("errors.orderpoint.gt.safety"));
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

        Map<String, String> equipTypes = new HashMap<>();
        String[] equipCodes = partsForm.getApplicableEquipmentCodes();
        if (equipCodes != null) {
            for (String eqCode : equipCodes) {
                if (eqCode != null && !eqCode.trim().isEmpty()) {
                    String type = request.getParameter("equipmentType_" + eqCode.trim());
                    if (type != null) equipTypes.put(eqCode.trim(), type);
                }
            }
        }

        String uploadRoot = getServlet().getServletContext().getRealPath("/");
        service.save(dto, equipCodes, equipTypes, partsForm.getAttachFile(), uploadRoot);

        return mapping.findForward("success");
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
