package com.strutslab.action.counter;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.strutslab.form.counter.CapaForm;
import com.strutslab.service.counter.CapaService;

public class CapaAction extends Action {

    private final CapaService service = new CapaService();

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        CapaForm capaForm = (CapaForm) form;

        if (request.getParameter("save") != null) {
            ActionMessages errors = new ActionMessages();
            if (isEmpty(capaForm.getIncidentNo()))
                errors.add("incidentNo", new ActionMessage("errors.required", "異常報告番号"));
            if (isEmpty(capaForm.getWhy1()))
                errors.add("why1", new ActionMessage("errors.required", "なぜ①"));
            if (isEmpty(capaForm.getWhy2()))
                errors.add("why2", new ActionMessage("errors.required", "なぜ②"));
            if (isEmpty(capaForm.getWhy3()))
                errors.add("why3", new ActionMessage("errors.required", "なぜ③"));
            if (isEmpty(capaForm.getWhy4()))
                errors.add("why4", new ActionMessage("errors.required", "なぜ④"));
            if (isEmpty(capaForm.getWhy5()))
                errors.add("why5", new ActionMessage("errors.required", "なぜ⑤"));
            if (isEmpty(capaForm.getCountermeasure()))
                errors.add("countermeasure", new ActionMessage("errors.required", "是正処置"));
            if (isEmpty(capaForm.getVerifyMethod()))
                errors.add("verifyMethod", new ActionMessage("errors.required", "検証方法"));
            if (isEmpty(capaForm.getVerifyDate()))
                errors.add("verifyDate", new ActionMessage("errors.required", "検証期限"));

            if (!errors.isEmpty()) {
                saveErrors(request, errors);
                return mapping.getInputForward();
            }

            if (service.findByIncidentNo(capaForm.getIncidentNo()) != null) {
                errors.add("incidentNo", new ActionMessage("errors.duplicate", "是正処置報告書"));
                saveErrors(request, errors);
                return mapping.getInputForward();
            }

            service.save(capaForm);

            ActionMessages messages = new ActionMessages();
            messages.add("message", new ActionMessage("label.update", "是正処置報告書を提出しました。"));
            saveMessages(request, messages);

            return mapping.findForward("success");
        }

        // Show form
        String incidentNo = request.getParameter("incidentNo");
        if (incidentNo != null && !incidentNo.isEmpty()) {
            capaForm.setIncidentNo(incidentNo);
            Map<String, Object> incidentInfo = service.loadIncidentInfo(incidentNo);
            if (incidentInfo != null) {
                request.setAttribute("incidentInfo", incidentInfo);
            }
        }

        return mapping.findForward("input");
    }

    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
}
