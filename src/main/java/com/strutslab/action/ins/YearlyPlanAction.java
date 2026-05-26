package com.strutslab.action.ins;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.strutslab.form.ins.YearlyPlanForm;
import com.strutslab.service.ins.YearlyPlanService;

public class YearlyPlanAction extends Action {

    private final YearlyPlanService service = new YearlyPlanService();

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        YearlyPlanForm ypf = (YearlyPlanForm) form;
        String paramCsv = request.getParameter("csv");
        String paramLock = request.getParameter("lock");
        String paramUnlock = request.getParameter("unlock");

        if (ypf.getFiscalYear() == null || ypf.getFiscalYear().isEmpty()) {
            ypf.setFiscalYear(service.getDefaultFiscalYear());
        }

        if ("true".equals(paramCsv)) {
            service.exportCsv(response, ypf.getFiscalYear());
            return null;
        }

        if ("true".equals(paramLock)) {
            service.lockYear(ypf.getFiscalYear());
        }
        if ("true".equals(paramUnlock)) {
            service.unlockYear(ypf.getFiscalYear());
        }

        request.setAttribute("eqpList", service.loadFilteredEquipment(ypf.getEquipmentType()));
        request.setAttribute("yearlyMatrix",
                service.buildYearlyMatrix(
                        (java.util.List) request.getAttribute("eqpList"),
                        ypf.getFiscalYear()));

        return mapping.findForward("success");
    }
}
