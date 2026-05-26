package com.strutslab.action.counter;

import java.util.ArrayList;
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
import com.strutslab.form.counter.CounterForm;
import com.strutslab.service.counter.CounterCreateService;

public class CounterCreateAction extends DispatchAction {

    private final CounterCreateService service = new CounterCreateService();

    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        CounterForm cf = (CounterForm) form;
        String incidentNo = request.getParameter("incidentNo");

        if (incidentNo != null && !incidentNo.isEmpty()) {
            cf.setIncidentNo(incidentNo);
            request.setAttribute("incidentNo", incidentNo);
        }

        if (cf.getOrderDate() == null || cf.getOrderDate().isEmpty()) {
            cf.setOrderDate(service.getDefaultOrderDate());
        }

        if (cf.getDetailCount() == 0) {
            cf.setDetailWorkContents(new String[1]);
            cf.setDetailPersons(new String[1]);
            cf.setDetailDeadlines(new String[1]);
            cf.setDetailPriorities(new String[1]);
        }

        return mapping.findForward("input");
    }

    public ActionForward addRow(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        CounterForm cf = (CounterForm) form;
        int oldLen = cf.getDetailCount();
        int newLen = oldLen + 1;

        String[] newWork = new String[newLen];
        String[] newPersons = new String[newLen];
        String[] newDeadlines = new String[newLen];
        String[] newPriorities = new String[newLen];

        for (int i = 0; i < oldLen; i++) {
            newWork[i] = cf.getDetailWorkContent(i);
            newPersons[i] = cf.getDetailPerson(i);
            newDeadlines[i] = cf.getDetailDeadline(i);
            newPriorities[i] = cf.getDetailPriority(i);
        }

        cf.setDetailWorkContents(newWork);
        cf.setDetailPersons(newPersons);
        cf.setDetailDeadlines(newDeadlines);
        cf.setDetailPriorities(newPriorities);

        return mapping.findForward("addRow");
    }

    public ActionForward delRow(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        CounterForm cf = (CounterForm) form;

        int removeIdx = 0;
        String idxStr = request.getParameter("index");
        if (idxStr != null) {
            try { removeIdx = Integer.parseInt(idxStr); } catch (NumberFormatException e) { }
        }

        int oldLen = cf.getDetailCount();
        if (oldLen <= 1) {
            ActionMessages errors = new ActionMessages();
            errors.add("detail", new ActionMessage("errors.required", "明細は最低1行必要です。"));
            saveErrors(request, errors);
            return mapping.findForward("delRow");
        }

        int newLen = oldLen - 1;
        String[] newWork = new String[newLen];
        String[] newPersons = new String[newLen];
        String[] newDeadlines = new String[newLen];
        String[] newPriorities = new String[newLen];

        int j = 0;
        for (int i = 0; i < oldLen; i++) {
            if (i == removeIdx) continue;
            newWork[j] = cf.getDetailWorkContent(i);
            newPersons[j] = cf.getDetailPerson(i);
            newDeadlines[j] = cf.getDetailDeadline(i);
            newPriorities[j] = cf.getDetailPriority(i);
            j++;
        }

        cf.setDetailWorkContents(newWork);
        cf.setDetailPersons(newPersons);
        cf.setDetailDeadlines(newDeadlines);
        cf.setDetailPriorities(newPriorities);

        return mapping.findForward("delRow");
    }

    public ActionForward save(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        CounterForm cf = (CounterForm) form;

        ActionMessages errors = new ActionMessages();
        if (cf.getDetailCount() == 0 || cf.getDetailWorkContents() == null) {
            errors.add("detail", new ActionMessage("errors.required", "明細は最低1行必要です。"));
        } else {
            for (int i = 0; i < cf.getDetailCount(); i++) {
                String wc = cf.getDetailWorkContent(i);
                if (wc == null || wc.trim().isEmpty()) {
                    errors.add("detailWorkContents",
                            new ActionMessage("errors.required", (i + 1) + "行目の指示内容"));
                }
                if (isEmpty(cf.getDetailPerson(i))) {
                    errors.add("detailPersons",
                            new ActionMessage("errors.required", (i + 1) + "行目の担当者"));
                }
            }
        }

        if (!errors.isEmpty()) {
            saveErrors(request, errors);
            return mapping.getInputForward();
        }

        CounterDto header = new CounterDto();
        header.setIncidentNo(cf.getIncidentNo());
        header.setOrderDate(cf.getOrderDate());
        header.setIssuer(cf.getIssuer());
        header.setOverallDeadline(cf.getOverallDeadline());
        header.setOverallPriority(cf.getOverallPriority());

        List<CounterDetailDto> details = new ArrayList<>();
        for (int i = 0; i < cf.getDetailCount(); i++) {
            CounterDetailDto d = new CounterDetailDto();
            d.setWorkContent(cf.getDetailWorkContent(i));
            d.setPersonName(cf.getDetailPerson(i));
            d.setDeadline(cf.getDetailDeadline(i));
            d.setPriority(cf.getDetailPriority(i));
            details.add(d);
        }

        try {
            String orderNo = service.save(header, details);
            ActionMessages messages = new ActionMessages();
            messages.add("message", new ActionMessage("label.update", "対応指示を登録しました。番号:" + orderNo));
            saveMessages(request, messages);
        } catch (RuntimeException e) {
            errors.add("detail", new ActionMessage("errors.required", e.getMessage()));
            saveErrors(request, errors);
            return mapping.getInputForward();
        }

        return mapping.findForward("success");
    }

    private boolean isEmpty(String s) { return s == null || s.trim().isEmpty(); }
}
