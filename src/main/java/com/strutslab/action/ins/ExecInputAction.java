package com.strutslab.action.ins;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.strutslab.dto.ChkItemDto;
import com.strutslab.dto.ExecItemResultDto;
import com.strutslab.dto.ExecResultDto;
import com.strutslab.form.ins.ExecForm;
import com.strutslab.service.ins.ExecInputService;

public class ExecInputAction extends Action {

    private final ExecInputService service = new ExecInputService();

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        ExecForm execForm = (ExecForm) form;

        if (request.getParameter("save") != null) {
            return doSave(mapping, execForm, request);
        }
        return doInput(mapping, execForm, request);
    }

    private ActionForward doInput(ActionMapping mapping, ExecForm form,
            HttpServletRequest request) throws Exception {

        String planIdStr = request.getParameter("planId");
        String resultIdStr = request.getParameter("resultId");

        if (planIdStr == null || planIdStr.isEmpty()) {
            ActionMessages errors = new ActionMessages();
            errors.add("planId", new ActionMessage("errors.required", "計画ID"));
            saveErrors(request, errors);
            return mapping.findForward("input");
        }

        int planId;
        try {
            planId = Integer.parseInt(planIdStr);
        } catch (NumberFormatException e) {
            ActionMessages errors = new ActionMessages();
            errors.add("planId", new ActionMessage("errors.required", "計画IDが不正です"));
            saveErrors(request, errors);
            return mapping.findForward("input");
        }
        form.setPlanId(planId);

        ExecResultDto planInfo = service.loadPlanInfo(planId);
        if (planInfo == null) {
            ActionMessages errors = new ActionMessages();
            errors.add("planId", new ActionMessage("errors.required", "計画が見つかりません"));
            saveErrors(request, errors);
            return mapping.findForward("input");
        }
        request.setAttribute("planInfo", planInfo);

        List<ChkItemDto> items = service.loadChecklistItems(planInfo.getTemplateId());
        request.setAttribute("checklistItems", items);
        form.setMaxItems(service.countLevel3Items(items));

        if (resultIdStr != null && !resultIdStr.isEmpty()) {
            int resultId;
            try {
                resultId = Integer.parseInt(resultIdStr);
            } catch (NumberFormatException e) {
                ActionMessages errors = new ActionMessages();
                errors.add("resultId", new ActionMessage("errors.required", "結果IDが不正です"));
                saveErrors(request, errors);
                return mapping.findForward("input");
            }
            form.setResultId(resultId);

            ExecResultDto existing = service.findExistingResult(resultId);
            if (existing != null) {
                form.setExecutedDate(existing.getExecutedDate());
                form.setSummaryJudge(existing.getSummaryJudge());
                form.setSummaryNote(existing.getSummaryNote());
                request.setAttribute("existingResult", existing);
            }

            List<ExecItemResultDto> existingItems = service.findExistingItems(resultId);
            request.setAttribute("existingItemResults", existingItems);
        }

        if (form.getExecutedDate() == null || form.getExecutedDate().isEmpty()) {
            form.setExecutedDate(service.getDefaultExecutedDate());
        }

        return mapping.findForward("input");
    }

    private ActionForward doSave(ActionMapping mapping, ExecForm form,
            HttpServletRequest request) throws Exception {

        ActionMessages errors = new ActionMessages();
        if (form.getSummaryJudge() == null || form.getSummaryJudge().isEmpty()) {
            errors.add("summaryJudge", new ActionMessage("errors.required", "総合判定"));
        }
        if (!errors.isEmpty()) {
            saveErrors(request, errors);
            return doInput(mapping, form, request);
        }

        String userCode = (String) request.getSession().getAttribute("userCode");
        if (userCode == null) userCode = "SYSTEM";

        List<ChkItemDto> checklistItems = (List<ChkItemDto>) request.getAttribute("checklistItems");
        if (checklistItems == null) {
            checklistItems = service.loadChecklistItems(
                    service.loadPlanInfo(form.getPlanId()).getTemplateId());
        }

        String[] judges = request.getParameterValues("execJudge");
        String[] values = request.getParameterValues("execValue");
        String[] notes = request.getParameterValues("execNote");

        String realPath = getServlet().getServletContext().getRealPath("/");
        service.saveResult(form, userCode, checklistItems, judges, values, notes,
                form.getExecPhoto(), realPath);

        if ("ABNORMAL".equals(form.getSummaryJudge())) {
            request.getSession().setAttribute("incidentFromExec", form);
            return mapping.findForward("incident");
        }

        return mapping.findForward("success");
    }
}
