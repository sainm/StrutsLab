package com.strutslab.form.mst;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.ValidatorForm;

public class CheckItemForm extends ValidatorForm {
    private int templateId;
    private String templateName;
    private String equipmentType;
    private String inspectionKind;

    private String[] cat1Names;
    private int[] cat1Ids;
    private String[][] cat2Names;

    private String[] itemNames;
    private String[] itemJudgeCriterias;
    private String[] itemNormalRanges;
    private String[] itemUnits;
    private int[] itemIds;

    private int[] itemCat1Idxs;
    private int[] itemCat2Idxs;

    private String method;

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

    @Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        super.reset(mapping, request);

        int cat1Count = countIndexedParams(request, "cat1Names");
        cat1Names = new String[cat1Count];
        cat1Ids = new int[cat1Count];
        cat2Names = new String[cat1Count][];

        int itemCount = countIndexedParams(request, "itemNames");
        itemNames = new String[itemCount];
        itemJudgeCriterias = new String[itemCount];
        itemNormalRanges = new String[itemCount];
        itemUnits = new String[itemCount];
        itemIds = new int[itemCount];
        itemCat1Idxs = new int[itemCount];
        itemCat2Idxs = new int[itemCount];
    }

    private int countIndexedParams(HttpServletRequest request, String prefix) {
        int max = -1;
        String pattern = prefix + "[";
        Enumeration<String> names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            if (name.startsWith(pattern)) {
                int end = name.indexOf(']', pattern.length());
                if (end > 0) {
                    try {
                        int idx = Integer.parseInt(name.substring(pattern.length(), end));
                        if (idx > max) max = idx;
                    } catch (NumberFormatException e) { }
                }
            }
        }
        return max + 1;
    }
}
