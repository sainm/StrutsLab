package com.strutslab.action.ins;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.strutslab.dto.ExecResultDto;
import com.strutslab.form.ins.ExecForm;
import com.strutslab.service.ins.ExecDetailService;

public class ExecDetailAction extends Action {

    private final ExecDetailService service = new ExecDetailService();

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        ExecForm execForm = (ExecForm) form;

        if (request.getParameter("modify") != null) {
            ActionMessages errors = new ActionMessages();
            String modifyReason = execForm.getModifyReason();
            if (modifyReason == null || modifyReason.trim().isEmpty()) {
                errors.add("modifyReason", new ActionMessage("errors.required", "修正理由"));
                saveErrors(request, errors);
            } else if (execForm.getResultId() <= 0) {
                errors.add("resultId", new ActionMessage("errors.required", "結果ID"));
                saveErrors(request, errors);
            } else {
                service.submitModify(execForm.getResultId(), modifyReason);
                ActionMessages messages = new ActionMessages();
                messages.add("message", new ActionMessage("label.update", "修正申請"));
                saveMessages(request, messages);
                return mapping.findForward("success");
            }
        }

        // doView
        String resultIdStr = request.getParameter("resultId");
        if (resultIdStr == null || resultIdStr.isEmpty()) {
            ActionMessages errors = new ActionMessages();
            errors.add("resultId", new ActionMessage("errors.required", "結果ID"));
            saveErrors(request, errors);
            return mapping.findForward("success");
        }

        int resultId;
        try {
            resultId = Integer.parseInt(resultIdStr);
        } catch (NumberFormatException e) {
            ActionMessages errors = new ActionMessages();
            errors.add("resultId", new ActionMessage("errors.required", "結果IDの形式が正しくありません"));
            saveErrors(request, errors);
            return mapping.findForward("success");
        }

        ExecResultDto result = service.findResult(resultId);
        if (result == null) {
            ActionMessages errors = new ActionMessages();
            errors.add("resultId", new ActionMessage("errors.required", "結果が見つかりません"));
            saveErrors(request, errors);
            return mapping.findForward("success");
        }

        request.setAttribute("execResult", result);
        request.setAttribute("execItems", service.findItems(resultId));

        execForm.setResultId(resultId);
        execForm.setSummaryJudge(result.getSummaryJudge());
        execForm.setSummaryNote(result.getSummaryNote());

        return mapping.findForward("success");
    }
}
