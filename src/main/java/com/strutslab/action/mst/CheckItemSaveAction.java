package com.strutslab.action.mst;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.strutslab.dao.ChkItemDao;
import com.strutslab.db.MyBatisUtil;
import com.strutslab.dto.ChkItemDto;
import com.strutslab.dto.ChkTmplDto;
import com.strutslab.form.mst.CheckItemForm;

public class CheckItemSaveAction extends DispatchAction {

    /**
     * Edit entry point (no method param) / method=edit.
     * Loads template by templateId and populates the 3-level tree form.
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        CheckItemForm f = (CheckItemForm) form;
        String templateIdStr = request.getParameter("templateId");

        if (templateIdStr != null && !templateIdStr.isEmpty()) {
            int templateId = Integer.parseInt(templateIdStr);
            try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
                ChkItemDao dao = sqlSession.getMapper(ChkItemDao.class);
                ChkTmplDto tmpl = dao.findById(templateId);
                if (tmpl != null) {
                    f.setTemplateId(tmpl.getTemplateId());
                    f.setTemplateName(tmpl.getTemplateName());
                    f.setEquipmentType(tmpl.getEquipmentType());
                    f.setInspectionKind(tmpl.getInspectionKind());

                    // Load items
                    List<ChkItemDto> allItems = dao.findItemsByTemplate(templateId);

                    // Separate by level
                    List<ChkItemDto> cat1List = new ArrayList<>();
                    List<ChkItemDto> cat2List = new ArrayList<>();
                    List<ChkItemDto> leafList = new ArrayList<>();
                    for (ChkItemDto dto : allItems) {
                        if (dto.getItemLevel() == 1) {
                            cat1List.add(dto);
                        } else if (dto.getItemLevel() == 2) {
                            cat2List.add(dto);
                        } else {
                            leafList.add(dto);
                        }
                    }

                    int cat1Count = cat1List.size();
                    f.setCat1Names(new String[cat1Count]);
                    f.setCat1Ids(new int[cat1Count]);
                    f.setCat2Names(new String[cat1Count][]);

                    // Build cat2 mapping: parentItemId -> list of cat2
                    java.util.Map<Integer, List<ChkItemDto>> cat2Map = new java.util.HashMap<>();
                    for (ChkItemDto dto : cat2List) {
                        int pid = dto.getParentItemId();
                        cat2Map.computeIfAbsent(pid, k -> new ArrayList<>()).add(dto);
                    }

                    // Build leaf mapping: parentItemId -> list of items
                    java.util.Map<Integer, List<ChkItemDto>> leafMap = new java.util.HashMap<>();
                    for (ChkItemDto dto : leafList) {
                        int pid = dto.getParentItemId();
                        leafMap.computeIfAbsent(pid, k -> new ArrayList<>()).add(dto);
                    }

                    List<String> itemNamesList = new ArrayList<>();
                    List<String> itemJudgeList = new ArrayList<>();
                    List<String> itemRangeList = new ArrayList<>();
                    List<String> itemUnitList = new ArrayList<>();
                    List<Integer> itemIdList = new ArrayList<>();
                    List<Integer> itemCat1IdxList = new ArrayList<>();
                    List<Integer> itemCat2IdxList = new ArrayList<>();

                    for (int ci = 0; ci < cat1Count; ci++) {
                        ChkItemDto cat1 = cat1List.get(ci);
                        f.getCat1Names()[ci] = cat1.getItemName();
                        f.getCat1Ids()[ci] = cat1.getItemId();

                        List<ChkItemDto> cat2s = cat2Map.getOrDefault(cat1.getItemId(), new ArrayList<>());
                        f.getCat2Names()[ci] = new String[cat2s.size()];

                        for (int cj = 0; cj < cat2s.size(); cj++) {
                            ChkItemDto cat2 = cat2s.get(cj);
                            f.getCat2Names()[ci][cj] = cat2.getItemName();

                            // Items under this cat2
                            List<ChkItemDto> items = leafMap.getOrDefault(cat2.getItemId(), new ArrayList<>());
                            for (ChkItemDto item : items) {
                                itemNamesList.add(item.getItemName());
                                itemJudgeList.add(item.getJudgeCriteria());
                                itemRangeList.add(item.getNormalRange());
                                itemUnitList.add(item.getUnit());
                                itemIdList.add(item.getItemId());
                                itemCat1IdxList.add(ci);
                                itemCat2IdxList.add(cj);
                            }
                        }
                    }

                    f.setItemNames(itemNamesList.toArray(new String[0]));
                    f.setItemJudgeCriterias(itemJudgeList.toArray(new String[0]));
                    f.setItemNormalRanges(itemRangeList.toArray(new String[0]));
                    f.setItemUnits(itemUnitList.toArray(new String[0]));
                    f.setItemIds(toIntArray(itemIdList));
                    f.setItemCat1Idxs(toIntArray(itemCat1IdxList));
                    f.setItemCat2Idxs(toIntArray(itemCat2IdxList));
                }
            }
        }

        return mapping.findForward("success");
    }

    /**
     * Save template + items. Deletes existing items first, then re-inserts
     * all items with proper parent_item_id linking. Uses transaction.
     */
    public ActionForward save(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        CheckItemForm f = (CheckItemForm) form;

        // --- Validation ---
        String errorMsg = validate(f, request);
        if (errorMsg != null) {
            request.setAttribute("errorMessage", errorMsg);
            return mapping.getInputForward();
        }

        SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();
        try {
            ChkItemDao dao = sqlSession.getMapper(ChkItemDao.class);

            // --- Save template ---
            ChkTmplDto tmplDto = new ChkTmplDto();
            tmplDto.setTemplateName(f.getTemplateName());
            tmplDto.setEquipmentType(f.getEquipmentType());
            tmplDto.setInspectionKind(f.getInspectionKind());

            int templateId;
            if (f.getTemplateId() > 0) {
                tmplDto.setTemplateId(f.getTemplateId());
                dao.update(tmplDto);
                templateId = f.getTemplateId();
            } else {
                dao.insert(tmplDto);
                templateId = tmplDto.getTemplateId();
                f.setTemplateId(templateId);
            }

            // --- Delete existing items (bottom-up to respect FK) ---
            dao.deleteItemsByLevel(templateId, 3);
            dao.deleteItemsByLevel(templateId, 2);
            dao.deleteItemsByLevel(templateId, 1);

            // --- Insert cat1 (level 1) items ---
            String[] cat1Names = f.getCat1Names();
            int[] cat1Ids = new int[cat1Names != null ? cat1Names.length : 0];
            if (cat1Names != null) {
                for (int i = 0; i < cat1Names.length; i++) {
                    if (cat1Names[i] == null || cat1Names[i].trim().isEmpty()) continue;
                    ChkItemDto dto = new ChkItemDto();
                    dto.setTemplateId(templateId);
                    dto.setParentItemId(null);
                    dto.setItemLevel(1);
                    dto.setItemName(cat1Names[i].trim());
                    dto.setSortOrder(i);
                    dao.insertItem(dto);
                    cat1Ids[i] = dto.getItemId();
                }
            }

            // --- Insert cat2 (level 2) items ---
            String[][] cat2Names = f.getCat2Names();
            int[][] cat2IdGrid = new int[cat1Ids.length][];
            for (int i = 0; i < cat1Ids.length; i++) {
                int cat2Count = (cat2Names != null && i < cat2Names.length && cat2Names[i] != null)
                        ? cat2Names[i].length : 0;
                cat2IdGrid[i] = new int[cat2Count];
                for (int j = 0; j < cat2Count; j++) {
                    if (cat2Names[i][j] == null || cat2Names[i][j].trim().isEmpty()) continue;
                    ChkItemDto dto = new ChkItemDto();
                    dto.setTemplateId(templateId);
                    dto.setParentItemId(cat1Ids[i] > 0 ? cat1Ids[i] : null);
                    dto.setItemLevel(2);
                    dto.setItemName(cat2Names[i][j].trim());
                    dto.setSortOrder(j);
                    dao.insertItem(dto);
                    cat2IdGrid[i][j] = dto.getItemId();
                }
            }

            // --- Insert leaf items (level 3) ---
            String[] itemNames = f.getItemNames();
            if (itemNames != null) {
                for (int k = 0; k < itemNames.length; k++) {
                    ChkItemDto dto = new ChkItemDto();
                    dto.setTemplateId(templateId);
                    dto.setItemLevel(3);
                    dto.setItemName(itemNames[k] != null ? itemNames[k].trim() : "");
                    dto.setJudgeCriteria(f.getItemJudgeCriterias() != null ? f.getItemJudgeCriterias()[k] : null);
                    dto.setNormalRange(f.getItemNormalRanges() != null ? f.getItemNormalRanges()[k] : null);
                    dto.setUnit(f.getItemUnits() != null ? f.getItemUnits()[k] : null);
                    dto.setSortOrder(k);

                    // Link to parent cat2
                    int ci = f.getItemCat1Idxs() != null && k < f.getItemCat1Idxs().length ? f.getItemCat1Idxs()[k] : 0;
                    int cj = f.getItemCat2Idxs() != null && k < f.getItemCat2Idxs().length ? f.getItemCat2Idxs()[k] : 0;
                    int parentId = (ci < cat2IdGrid.length && cj < cat2IdGrid[ci].length) ? cat2IdGrid[ci][cj] : 0;
                    dto.setParentItemId(parentId > 0 ? parentId : null);

                    dao.insertItem(dto);
                }
            }

            sqlSession.commit();
        } catch (Exception e) {
            sqlSession.rollback();
            request.setAttribute("errorMessage", "保存中にエラーが発生しました: " + e.getMessage());
            return mapping.getInputForward();
        } finally {
            sqlSession.close();
        }

        return mapping.findForward("success");
    }

    /**
     * Add an empty 大分類 (cat1) row.
     */
    public ActionForward addCat1(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        CheckItemForm f = (CheckItemForm) form;
        f.setCat1Names(append(f.getCat1Names(), ""));
        f.setCat1Ids(append(f.getCat1Ids(), 0));
        f.setCat2Names(append2d(f.getCat2Names(), new String[0]));
        return mapping.getInputForward();
    }

    /**
     * Add an empty 中分類 (cat2) under the specified 大分類 index.
     */
    public ActionForward addCat2(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        CheckItemForm f = (CheckItemForm) form;
        String idxStr = request.getParameter("cat1Idx");
        int ci = (idxStr != null) ? Integer.parseInt(idxStr) : 0;

        String[][] c2 = f.getCat2Names();
        if (c2 == null || ci >= c2.length) {
            // Need to make room
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

    /**
     * Add an empty 項目 under the specified 中分類.
     */
    public ActionForward addItem(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        CheckItemForm f = (CheckItemForm) form;
        String cat1IdxStr = request.getParameter("cat1Idx");
        String cat2IdxStr = request.getParameter("cat2Idx");
        int ci = (cat1IdxStr != null) ? Integer.parseInt(cat1IdxStr) : 0;
        int cj = (cat2IdxStr != null) ? Integer.parseInt(cat2IdxStr) : 0;

        f.setItemNames(append(f.getItemNames(), ""));
        f.setItemJudgeCriterias(append(f.getItemJudgeCriterias(), ""));
        f.setItemNormalRanges(append(f.getItemNormalRanges(), ""));
        f.setItemUnits(append(f.getItemUnits(), ""));
        f.setItemIds(append(f.getItemIds(), 0));
        f.setItemCat1Idxs(append(f.getItemCat1Idxs(), ci));
        f.setItemCat2Idxs(append(f.getItemCat2Idxs(), cj));

        return mapping.getInputForward();
    }

    /**
     * Delete an item (by flat index).
     */
    public ActionForward delRow(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        CheckItemForm f = (CheckItemForm) form;
        String idxStr = request.getParameter("rowIdx");
        if (idxStr == null) return mapping.getInputForward();
        int idx = Integer.parseInt(idxStr);

        f.setItemNames(removeAt(f.getItemNames(), idx));
        f.setItemJudgeCriterias(removeAt(f.getItemJudgeCriterias(), idx));
        f.setItemNormalRanges(removeAt(f.getItemNormalRanges(), idx));
        f.setItemUnits(removeAt(f.getItemUnits(), idx));
        f.setItemIds(removeAt(f.getItemIds(), idx));
        f.setItemCat1Idxs(removeAt(f.getItemCat1Idxs(), idx));
        f.setItemCat2Idxs(removeAt(f.getItemCat2Idxs(), idx));

        return mapping.getInputForward();
    }

    // ---- private helpers ----

    private String validate(CheckItemForm f, HttpServletRequest request) {
        if (f.getTemplateName() == null || f.getTemplateName().trim().isEmpty()) {
            return "テンプレート名は必須です。";
        }
        if (f.getEquipmentType() == null || f.getEquipmentType().isEmpty()) {
            return "設備種別は必須です。";
        }
        if (f.getInspectionKind() == null || f.getInspectionKind().isEmpty()) {
            return "点検種別は必須です。";
        }
        return null;
    }

    private static int[] toIntArray(List<Integer> list) {
        int[] arr = new int[list.size()];
        for (int i = 0; i < list.size(); i++) arr[i] = list.get(i);
        return arr;
    }

    private static String[] append(String[] arr, String val) {
        if (arr == null) return new String[]{val};
        String[] n = Arrays.copyOf(arr, arr.length + 1);
        n[arr.length] = val;
        return n;
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
        String[][] n = Arrays.copyOf(arr, minLen);
        return n;
    }

    private static String[] removeAt(String[] arr, int idx) {
        if (arr == null || idx < 0 || idx >= arr.length) return arr;
        String[] n = new String[arr.length - 1];
        System.arraycopy(arr, 0, n, 0, idx);
        System.arraycopy(arr, idx + 1, n, idx, arr.length - idx - 1);
        return n;
    }

    private static int[] removeAt(int[] arr, int idx) {
        if (arr == null || idx < 0 || idx >= arr.length) return arr;
        int[] n = new int[arr.length - 1];
        System.arraycopy(arr, 0, n, 0, idx);
        System.arraycopy(arr, idx + 1, n, idx, arr.length - idx - 1);
        return n;
    }
}
