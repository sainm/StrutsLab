package com.strutslab.action.mst;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.strutslab.form.mst.EqpForm;
import com.strutslab.service.mst.EqpSaveService;

public class EqpSaveAction extends DispatchAction {

    private final EqpSaveService service = new EqpSaveService();

    @Override
    protected ActionForward dispatchMethod(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response, String name)
            throws Exception {
        if ("new".equals(name)) {
            return newEquipment(mapping, form, request, response);
        }
        return super.dispatchMethod(mapping, form, request, response, name);
    }

    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        EqpForm eqpForm = (EqpForm) form;
        service.loadEquipment(eqpForm, request.getParameter("equipmentCode"));
        return mapping.findForward("success");
    }

    public ActionForward newEquipment(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
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

    public ActionForward save(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        EqpForm eqpForm = (EqpForm) form;

        String errorMsg = service.validate(eqpForm);
        if (errorMsg != null) {
            request.setAttribute("errorMessage", getResources(request).getMessage(
                    errorMsg.split(":")[0], errorMsg.contains(":") ? errorMsg.substring(errorMsg.indexOf(":") + 1) : ""));
            return mapping.getInputForward();
        }

        String parentError = service.checkParentNotAbolished(eqpForm.getParentEquipmentCode());
        if (parentError != null) {
            request.setAttribute("errorMessage", getResources(request).getMessage(parentError));
            return mapping.getInputForward();
        }

        String uploadDir = request.getSession().getServletContext().getRealPath("/attachments/eqp/");
        service.save(eqpForm, uploadDir);

        return mapping.findForward("success");
    }

    public ActionForward delete(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        service.delete(request.getParameter("equipmentCode"));
        return mapping.findForward("success");
    }
}
