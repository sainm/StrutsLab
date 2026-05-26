package com.strutslab.action.mst;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.strutslab.form.mst.CheckItemForm;
import com.strutslab.service.mst.CheckItemSaveService;

public class CheckItemSaveAction extends DispatchAction {

    private final CheckItemSaveService service = new CheckItemSaveService();

    @Override
    protected ActionForward dispatchMethod(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response, String name)
            throws Exception {
        if ("new".equals(name)) {
            return newTemplate(mapping, form, request, response);
        }
        return super.dispatchMethod(mapping, form, request, response, name);
    }

    public ActionForward newTemplate(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        CheckItemForm f = (CheckItemForm) form;
        f.setTemplateId(0);
        f.setTemplateName(null);
        f.setEquipmentType(null);
        f.setInspectionKind(null);
        f.setCat1Names(new String[]{"大分類1"});
        f.setCat1Ids(new int[]{0});
        f.setCat2Names(new String[][]{new String[]{}});
        f.setItemNames(new String[0]);
        f.setItemJudgeCriterias(new String[0]);
        f.setItemNormalRanges(new String[0]);
        f.setItemUnits(new String[0]);
        f.setItemIds(new int[0]);
        f.setItemCat1Idxs(new int[0]);
        f.setItemCat2Idxs(new int[0]);
        return mapping.findForward("success");
    }

    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        CheckItemForm f = (CheckItemForm) form;
        String templateIdStr = request.getParameter("templateId");

        if (templateIdStr != null && !templateIdStr.isEmpty()) {
            int templateId;
            try {
                templateId = Integer.parseInt(templateIdStr);
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "テンプレートIDの形式が正しくありません。");
                return mapping.findForward("success");
            }
            service.loadTemplate(f, templateId);
        }

        return mapping.findForward("success");
    }

    public ActionForward save(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        CheckItemForm f = (CheckItemForm) form;

        if (f.getTemplateName() == null || f.getTemplateName().trim().isEmpty()) {
            request.setAttribute("errorMessage",
                getResources(request).getMessage("errors.required",
                    getResources(request).getMessage("label.templateName")));
            return mapping.getInputForward();
        }
        if (f.getEquipmentType() == null || f.getEquipmentType().isEmpty()) {
            request.setAttribute("errorMessage",
                getResources(request).getMessage("errors.required",
                    getResources(request).getMessage("label.equipmentType")));
            return mapping.getInputForward();
        }
        if (f.getInspectionKind() == null || f.getInspectionKind().isEmpty()) {
            request.setAttribute("errorMessage",
                getResources(request).getMessage("errors.required",
                    getResources(request).getMessage("label.inspectionKind")));
            return mapping.getInputForward();
        }

        try {
            service.save(f);
        } catch (RuntimeException e) {
            request.setAttribute("errorMessage", e.getMessage());
            return mapping.getInputForward();
        }

        return mapping.findForward("success");
    }

    public ActionForward addCat1(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        CheckItemForm f = (CheckItemForm) form;
        f.setCat1Names(append(f.getCat1Names(), ""));
        f.setCat1Ids(append(f.getCat1Ids(), 0));
        f.setCat2Names(append2d(f.getCat2Names(), new String[0]));
        return mapping.getInputForward();
    }

    public ActionForward addCat2(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        CheckItemForm f = (CheckItemForm) form;
        String idxStr = request.getParameter("cat1Idx");
        int ci;
        try {
            ci = (idxStr != null) ? Integer.parseInt(idxStr) : 0;
        } catch (NumberFormatException e) { ci = 0; }

        String[][] c2 = f.getCat2Names();
        if (c2 == null || ci >= c2.length) {
            c2 = ensure2dLength(c2, Math.max(ci + 1, (f.getCat1Names() != null ? f.getCat1Names().length : 0)));
            f.setCat2Names(c2);
        }
        String[] row = c2[ci];
        if (row == null) row = new String[0];
        row = append(row, "");
        c2[ci] = row;
        f.setCat2Names(c2);
        return mapping.getInputForward();
    }

    public ActionForward addItem(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        CheckItemForm f = (CheckItemForm) form;
        String cat1IdxStr = request.getParameter("cat1Idx");
        String cat2IdxStr = request.getParameter("cat2Idx");
        int ci, cj;
        try {
            ci = (cat1IdxStr != null) ? Integer.parseInt(cat1IdxStr) : 0;
            cj = (cat2IdxStr != null) ? Integer.parseInt(cat2IdxStr) : 0;
        } catch (NumberFormatException e) { ci = 0; cj = 0; }

        f.setItemNames(append(f.getItemNames(), ""));
        f.setItemJudgeCriterias(append(f.getItemJudgeCriterias(), ""));
        f.setItemNormalRanges(append(f.getItemNormalRanges(), ""));
        f.setItemUnits(append(f.getItemUnits(), ""));
        f.setItemIds(append(f.getItemIds(), 0));
        f.setItemCat1Idxs(append(f.getItemCat1Idxs(), ci));
        f.setItemCat2Idxs(append(f.getItemCat2Idxs(), cj));

        return mapping.getInputForward();
    }

    public ActionForward delCat1(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        CheckItemForm f = (CheckItemForm) form;
        String idxStr = request.getParameter("cat1Idx");
        int ci;
        try {
            ci = (idxStr != null) ? Integer.parseInt(idxStr) : 0;
        } catch (NumberFormatException e) { return mapping.getInputForward(); }

        int[] cat1Ids = f.getCat1Ids();
        String[] cat1Names = f.getCat1Names();
        String[][] cat2Names = f.getCat2Names();
        if (cat1Names == null || ci >= cat1Names.length) return mapping.getInputForward();

        f.setCat1Ids(removeAt(cat1Ids, ci));
        f.setCat1Names(removeAt(cat1Names, ci));
        f.setCat2Names(remove2dAt(cat2Names, ci));

        // Remove all items under this cat1
        removeItemsUnderCat1(f, ci);

        return mapping.getInputForward();
    }

    public ActionForward delCat2(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        CheckItemForm f = (CheckItemForm) form;
        String cat1IdxStr = request.getParameter("cat1Idx");
        String cat2IdxStr = request.getParameter("cat2Idx");
        int ci, cj;
        try {
            ci = (cat1IdxStr != null) ? Integer.parseInt(cat1IdxStr) : 0;
            cj = (cat2IdxStr != null) ? Integer.parseInt(cat2IdxStr) : 0;
        } catch (NumberFormatException e) { return mapping.getInputForward(); }

        String[][] c2 = f.getCat2Names();
        if (c2 == null || ci >= c2.length || c2[ci] == null || cj >= c2[ci].length)
            return mapping.getInputForward();

        c2[ci] = removeAt(c2[ci], cj);
        f.setCat2Names(c2);

        // Remove all items under this cat2
        removeItemsUnderCat2(f, ci, cj);

        return mapping.getInputForward();
    }

    public ActionForward delRow(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        CheckItemForm f = (CheckItemForm) form;
        String idxStr = request.getParameter("rowIdx");
        if (idxStr == null) return mapping.getInputForward();
        int idx;
        try {
            idx = Integer.parseInt(idxStr);
        } catch (NumberFormatException e) { return mapping.getInputForward(); }

        f.setItemNames(removeAt(f.getItemNames(), idx));
        f.setItemJudgeCriterias(removeAt(f.getItemJudgeCriterias(), idx));
        f.setItemNormalRanges(removeAt(f.getItemNormalRanges(), idx));
        f.setItemUnits(removeAt(f.getItemUnits(), idx));
        f.setItemIds(removeAt(f.getItemIds(), idx));
        f.setItemCat1Idxs(removeAt(f.getItemCat1Idxs(), idx));
        f.setItemCat2Idxs(removeAt(f.getItemCat2Idxs(), idx));

        return mapping.getInputForward();
    }

    // -- form array helpers --

    private static String[] append(String[] arr, String val) {
        if (arr == null) return new String[]{val};
        return Arrays.copyOf(arr, arr.length + 1);
    }

    private static int[] append(int[] arr, int val) {
        if (arr == null) return new int[]{val};
        int[] n = Arrays.copyOf(arr, arr.length + 1);
        n[arr.length] = val;
        return n;
    }

    private static String[][] append2d(String[][] arr, String[] val) {
        if (arr == null) return new String[][]{val};
        String[][] n = Arrays.copyOf(arr, arr.length + 1);
        n[arr.length] = val;
        return n;
    }

    private static String[][] ensure2dLength(String[][] arr, int minLen) {
        if (arr == null) return new String[minLen][];
        if (arr.length >= minLen) return arr;
        return Arrays.copyOf(arr, minLen);
    }

    private static String[] removeAt(String[] arr, int idx) {
        if (arr == null || idx < 0 || idx >= arr.length) return arr;
        String[] n = new String[arr.length - 1];
        System.arraycopy(arr, 0, n, 0, idx);
        System.arraycopy(arr, idx + 1, n, idx, arr.length - idx - 1);
        return n;
    }

    private static String[][] remove2dAt(String[][] arr, int idx) {
        if (arr == null || idx < 0 || idx >= arr.length) return arr;
        String[][] n = new String[arr.length - 1][];
        System.arraycopy(arr, 0, n, 0, idx);
        System.arraycopy(arr, idx + 1, n, idx, arr.length - idx - 1);
        return n;
    }

    private void removeItemsUnderCat1(CheckItemForm f, int ci) {
        int[] ic1 = f.getItemCat1Idxs();
        int[] ic2 = f.getItemCat2Idxs();
        if (ic1 == null) return;

        int len = ic1.length;
        for (int i = len - 1; i >= 0; i--) {
            if (ic1[i] == ci) {
                f.setItemNames(removeAt(f.getItemNames(), i));
                f.setItemJudgeCriterias(removeAt(f.getItemJudgeCriterias(), i));
                f.setItemNormalRanges(removeAt(f.getItemNormalRanges(), i));
                f.setItemUnits(removeAt(f.getItemUnits(), i));
                f.setItemIds(removeAt(f.getItemIds(), i));
                f.setItemCat1Idxs(removeAt(f.getItemCat1Idxs(), i));
                f.setItemCat2Idxs(removeAt(f.getItemCat2Idxs(), i));
            } else if (ic1[i] > ci) {
                // Adjust index for cat1s after the removed one
                int[] newIc1 = f.getItemCat1Idxs();
                newIc1[i]--;
            }
        }
    }

    private void removeItemsUnderCat2(CheckItemForm f, int ci, int cj) {
        int[] ic1 = f.getItemCat1Idxs();
        int[] ic2 = f.getItemCat2Idxs();
        if (ic1 == null || ic2 == null) return;

        for (int i = ic1.length - 1; i >= 0; i--) {
            if (ic1[i] == ci && ic2[i] == cj) {
                f.setItemNames(removeAt(f.getItemNames(), i));
                f.setItemJudgeCriterias(removeAt(f.getItemJudgeCriterias(), i));
                f.setItemNormalRanges(removeAt(f.getItemNormalRanges(), i));
                f.setItemUnits(removeAt(f.getItemUnits(), i));
                f.setItemIds(removeAt(f.getItemIds(), i));
                f.setItemCat1Idxs(removeAt(f.getItemCat1Idxs(), i));
                f.setItemCat2Idxs(removeAt(f.getItemCat2Idxs(), i));
            } else if (ic1[i] == ci && ic2[i] > cj) {
                int[] newIc2 = f.getItemCat2Idxs();
                newIc2[i]--;
            }
        }
    }

    private static int[] removeAt(int[] arr, int idx) {
        if (arr == null || idx < 0 || idx >= arr.length) return arr;
        int[] n = new int[arr.length - 1];
        System.arraycopy(arr, 0, n, 0, idx);
        System.arraycopy(arr, idx + 1, n, idx, arr.length - idx - 1);
        return n;
    }
}
