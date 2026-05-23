package com.strutslab.form.mst;

import org.apache.struts.validator.ValidatorForm;

public class CheckItemForm extends ValidatorForm {
    // Template basic info
    private int templateId;
    private String templateName;
    private String equipmentType;
    private String inspectionKind;

    // Level 1 : 大分類
    private String[] cat1Names;
    private int[] cat1Ids;

    // Level 2 : 中分類 (indexed by cat1)
    private String[][] cat2Names;

    // Level 3 : 項目 (flat list, traversed in tree order)
    private String[] itemNames;
    private String[] itemJudgeCriterias;
    private String[] itemNormalRanges;
    private String[] itemUnits;
    private int[] itemIds;

    // Parent tracking for items (which cat1/cat2 each item belongs to)
    private int[] itemCat1Idxs;
    private int[] itemCat2Idxs;

    // Action method
    private String method;

    // ---- Getters / Setters ----

    public int getTemplateId() { return templateId; }
    public void setTemplateId(int v) { this.templateId = v; }

    public String getTemplateName() { return templateName; }
    public void setTemplateName(String v) { this.templateName = v; }

    public String getEquipmentType() { return equipmentType; }
    public void setEquipmentType(String v) { this.equipmentType = v; }

    public String getInspectionKind() { return inspectionKind; }
    public void setInspectionKind(String v) { this.inspectionKind = v; }

    public String[] getCat1Names() { return cat1Names; }
    public void setCat1Names(String[] v) { this.cat1Names = v; }

    public int[] getCat1Ids() { return cat1Ids; }
    public void setCat1Ids(int[] v) { this.cat1Ids = v; }

    public String[][] getCat2Names() { return cat2Names; }
    public void setCat2Names(String[][] v) { this.cat2Names = v; }

    public String[] getItemNames() { return itemNames; }
    public void setItemNames(String[] v) { this.itemNames = v; }

    public String[] getItemJudgeCriterias() { return itemJudgeCriterias; }
    public void setItemJudgeCriterias(String[] v) { this.itemJudgeCriterias = v; }

    public String[] getItemNormalRanges() { return itemNormalRanges; }
    public void setItemNormalRanges(String[] v) { this.itemNormalRanges = v; }

    public String[] getItemUnits() { return itemUnits; }
    public void setItemUnits(String[] v) { this.itemUnits = v; }

    public int[] getItemIds() { return itemIds; }
    public void setItemIds(int[] v) { this.itemIds = v; }

    public int[] getItemCat1Idxs() { return itemCat1Idxs; }
    public void setItemCat1Idxs(int[] v) { this.itemCat1Idxs = v; }

    public int[] getItemCat2Idxs() { return itemCat2Idxs; }
    public void setItemCat2Idxs(int[] v) { this.itemCat2Idxs = v; }

    public String getMethod() { return method; }
    public void setMethod(String v) { this.method = v; }
}
