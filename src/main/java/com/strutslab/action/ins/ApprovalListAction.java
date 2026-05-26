package com.strutslab.action.ins;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.strutslab.form.ins.ApprovalForm;
import com.strutslab.service.ins.ApprovalService;

public class ApprovalListAction extends Action {

    private final ApprovalService service = new ApprovalService();

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        ApprovalForm approvalForm = (ApprovalForm) form;

        if (request.getParameter("bulkApprove") != null) {
            int[] selected = approvalForm.getSelectedItems();
            if (selected == null || selected.length == 0) {
                ActionMessages errors = new ActionMessages();
                errors.add("selectedItems", new ActionMessage("errors.required", "承認対象"));
                saveErrors(request, errors);
            } else {
                service.bulkApprove(selected);
                ActionMessages messages = new ActionMessages();
                messages.add("message", new ActionMessage("label.update", selected.length + "件承認しました"));
                saveMessages(request, messages);
            }
        }

        if (request.getParameter("bulkReject") != null) {
            int[] selected = approvalForm.getSelectedItems();
            String rejectReason = approvalForm.getRejectReason();
            if (selected == null || selected.length == 0) {
                ActionMessages errors = new ActionMessages();
                errors.add("selectedItems", new ActionMessage("errors.required", "差戻対象"));
                saveErrors(request, errors);
            } else if (rejectReason == null || rejectReason.trim().isEmpty()) {
                ActionMessages errors = new ActionMessages();
                errors.add("rejectReason", new ActionMessage("errors.required", "差戻理由"));
                saveErrors(request, errors);
            } else {
                service.bulkReject(selected, rejectReason);
                ActionMessages messages = new ActionMessages();
                messages.add("message", new ActionMessage("label.update", selected.length + "件差戻しました"));
                saveMessages(request, messages);
            }
        }

        request.setAttribute("approvalList",
                service.search(approvalForm.getDateFrom(), approvalForm.getDateTo(),
                        approvalForm.getTeam(), approvalForm.getStatus()));
        request.setAttribute("totalCount",
                service.countPending(approvalForm.getDateFrom(), approvalForm.getDateTo(),
                        approvalForm.getTeam(), approvalForm.getStatus()));
        request.setAttribute("teamList", service.loadTeamList());

        return mapping.findForward("success");
    }
}
