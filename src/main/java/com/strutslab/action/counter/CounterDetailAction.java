package com.strutslab.action.counter;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.actions.DispatchAction;

import com.strutslab.dto.CounterDetailDto;
import com.strutslab.dto.CounterDto;
import com.strutslab.form.counter.CounterDetailForm;
import com.strutslab.service.counter.CounterDetailService;

public class CounterDetailAction extends DispatchAction {

    private final CounterDetailService service = new CounterDetailService();

    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        CounterDetailForm df = (CounterDetailForm) form;

        String orderNo = request.getParameter("orderNo");
        if (orderNo == null || orderNo.isEmpty()) orderNo = df.getOrderNo();
        if (orderNo == null || orderNo.isEmpty()) {
            ActionMessages errors = new ActionMessages();
            errors.add("orderNo", new ActionMessage("errors.required", "指示番号"));
            saveErrors(request, errors);
            return mapping.findForward("success");
        }

        CounterDto order = service.findOrder(orderNo);
        List<CounterDetailDto> details = service.findDetails(orderNo);

        if (order == null) {
            ActionMessages errors = new ActionMessages();
            errors.add("orderNo", new ActionMessage("errors.required", "指示が見つかりません"));
            saveErrors(request, errors);
            return mapping.findForward("success");
        }

        request.setAttribute("order", order);
        request.setAttribute("details", details);

        return mapping.findForward("input");
    }

    public ActionForward completeDetail(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        String orderNo = request.getParameter("orderNo");
        String detailIdx = request.getParameter("detailIndex");

        if (orderNo == null || orderNo.isEmpty()) {
            ActionMessages errors = new ActionMessages();
            errors.add("orderNo", new ActionMessage("errors.required", "指示番号"));
            saveErrors(request, errors);
            return mapping.findForward("success");
        }

        int idx = 0;
        if (detailIdx != null) {
            try { idx = Integer.parseInt(detailIdx); } catch (NumberFormatException e) { }
        }

        CounterDto order = service.findOrder(orderNo);
        List<CounterDetailDto> details = service.findDetails(orderNo);

        request.setAttribute("order", order);
        request.setAttribute("details", details);
        request.setAttribute("completeIndex", idx);

        return mapping.findForward("complete");
    }

    public ActionForward saveCompletion(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        CounterDetailForm df = (CounterDetailForm) form;

        String orderNo = df.getOrderNo();
        Integer detailIdx = df.getDetailIndex();
        if (orderNo == null || orderNo.isEmpty() || detailIdx == null) {
            ActionMessages errors = new ActionMessages();
            errors.add("params", new ActionMessage("errors.required", "パラメータ"));
            saveErrors(request, errors);
            return mapping.findForward("success");
        }

        List<CounterDetailDto> details = service.findDetails(orderNo);
        if (detailIdx < 0 || detailIdx >= details.size()) {
            ActionMessages errors = new ActionMessages();
            errors.add("detailIndex", new ActionMessage("errors.required", "明細インデックスが不正"));
            saveErrors(request, errors);
            return mapping.findForward("success");
        }

        CounterDetailDto detail = details.get(detailIdx);
        service.completeDetail(orderNo, detail,
                df.getActualHour(detailIdx),
                df.getUsedPartCode(detailIdx),
                df.getUsedQuantity(detailIdx),
                df.getNote(detailIdx));

        ActionMessages messages = new ActionMessages();
        messages.add("message", new ActionMessage("label.update", "明細を完了しました。"));
        saveMessages(request, messages);

        return unspecified(mapping, form, request, response);
    }
}
