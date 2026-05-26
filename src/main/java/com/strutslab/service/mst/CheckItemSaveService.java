package com.strutslab.service.mst;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.strutslab.dao.ChkItemDao;
import com.strutslab.db.MyBatisUtil;
import com.strutslab.dto.ChkItemDto;
import com.strutslab.dto.ChkTmplDto;
import com.strutslab.form.mst.CheckItemForm;

public class CheckItemSaveService {

    public void loadTemplate(CheckItemForm form, int templateId) {
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            ChkItemDao dao = sqlSession.getMapper(ChkItemDao.class);
            ChkTmplDto tmpl = dao.findById(templateId);
            if (tmpl != null) {
                form.setTemplateId(tmpl.getTemplateId());
                form.setTemplateName(tmpl.getTemplateName());
                form.setEquipmentType(tmpl.getEquipmentType());
                form.setInspectionKind(tmpl.getInspectionKind());

                List<ChkItemDto> allItems = dao.findItemsByTemplate(templateId);

                // Separate by level
                List<ChkItemDto> cat1List = new ArrayList<>();
                List<ChkItemDto> cat2List = new ArrayList<>();
                List<ChkItemDto> leafList = new ArrayList<>();
                for (ChkItemDto dto : allItems) {
                    if (dto.getItemLevel() == 1) cat1List.add(dto);
                    else if (dto.getItemLevel() == 2) cat2List.add(dto);
                    else leafList.add(dto);
                }

                int cat1Count = cat1List.size();
                form.setCat1Names(new String[cat1Count]);
                form.setCat1Ids(new int[cat1Count]);
                form.setCat2Names(new String[cat1Count][]);

                Map<Integer, List<ChkItemDto>> cat2Map = new HashMap<>();
                for (ChkItemDto dto : cat2List) {
                    cat2Map.computeIfAbsent(dto.getParentItemId(), k -> new ArrayList<>()).add(dto);
                }

                Map<Integer, List<ChkItemDto>> leafMap = new HashMap<>();
                for (ChkItemDto dto : leafList) {
                    leafMap.computeIfAbsent(dto.getParentItemId(), k -> new ArrayList<>()).add(dto);
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
                    form.getCat1Names()[ci] = cat1.getItemName();
                    form.getCat1Ids()[ci] = cat1.getItemId();

                    List<ChkItemDto> cat2s = cat2Map.getOrDefault(cat1.getItemId(), new ArrayList<>());
                    form.getCat2Names()[ci] = new String[cat2s.size()];

                    for (int cj = 0; cj < cat2s.size(); cj++) {
                        ChkItemDto cat2 = cat2s.get(cj);
                        form.getCat2Names()[ci][cj] = cat2.getItemName();

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

                form.setItemNames(itemNamesList.toArray(new String[0]));
                form.setItemJudgeCriterias(itemJudgeList.toArray(new String[0]));
                form.setItemNormalRanges(itemRangeList.toArray(new String[0]));
                form.setItemUnits(itemUnitList.toArray(new String[0]));
                form.setItemIds(toIntArray(itemIdList));
                form.setItemCat1Idxs(toIntArray(itemCat1IdxList));
                form.setItemCat2Idxs(toIntArray(itemCat2IdxList));
            }
        }
    }

    public void save(CheckItemForm form) {
        SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();
        try {
            ChkItemDao dao = sqlSession.getMapper(ChkItemDao.class);

            ChkTmplDto tmplDto = new ChkTmplDto();
            tmplDto.setTemplateName(form.getTemplateName());
            tmplDto.setEquipmentType(form.getEquipmentType());
            tmplDto.setInspectionKind(form.getInspectionKind());

            int templateId;
            if (form.getTemplateId() > 0) {
                tmplDto.setTemplateId(form.getTemplateId());
                dao.update(tmplDto);
                templateId = form.getTemplateId();
            } else {
                dao.insert(tmplDto);
                templateId = tmplDto.getTemplateId();
                form.setTemplateId(templateId);
            }

            // Delete existing items bottom-up
            dao.deleteItemsByLevel(templateId, 3);
            dao.deleteItemsByLevel(templateId, 2);
            dao.deleteItemsByLevel(templateId, 1);

            // Insert cat1
            String[] cat1Names = form.getCat1Names();
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

            // Insert cat2
            String[][] cat2Names = form.getCat2Names();
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

            // Insert leaf items
            String[] itemNames = form.getItemNames();
            if (itemNames != null) {
                for (int k = 0; k < itemNames.length; k++) {
                    ChkItemDto dto = new ChkItemDto();
                    dto.setTemplateId(templateId);
                    dto.setItemLevel(3);
                    dto.setItemName(itemNames[k] != null ? itemNames[k].trim() : "");
                    dto.setJudgeCriteria(form.getItemJudgeCriterias() != null ? form.getItemJudgeCriterias()[k] : null);
                    dto.setNormalRange(form.getItemNormalRanges() != null ? form.getItemNormalRanges()[k] : null);
                    dto.setUnit(form.getItemUnits() != null ? form.getItemUnits()[k] : null);
                    dto.setSortOrder(k);

                    int ci = form.getItemCat1Idxs() != null && k < form.getItemCat1Idxs().length ? form.getItemCat1Idxs()[k] : 0;
                    int cj = form.getItemCat2Idxs() != null && k < form.getItemCat2Idxs().length ? form.getItemCat2Idxs()[k] : 0;
                    int parentId = (ci < cat2IdGrid.length && cj < cat2IdGrid[ci].length) ? cat2IdGrid[ci][cj] : 0;
                    dto.setParentItemId(parentId > 0 ? parentId : null);

                    dao.insertItem(dto);
                }
            }

            sqlSession.commit();
        } catch (Exception e) {
            sqlSession.rollback();
            throw new RuntimeException("保存中にエラーが発生しました。", e);
        } finally {
            sqlSession.close();
        }
    }

    private static int[] toIntArray(List<Integer> list) {
        int[] arr = new int[list.size()];
        for (int i = 0; i < list.size(); i++) arr[i] = list.get(i);
        return arr;
    }
}
