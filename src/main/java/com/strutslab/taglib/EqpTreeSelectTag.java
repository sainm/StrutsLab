package com.strutslab.taglib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.strutslab.dto.EqpDto;

public class EqpTreeSelectTag extends TagSupport {

    private String name;
    private String property;

    public void setName(String v) { this.name = v; }
    public void setProperty(String v) { this.property = v; }

    @Override
    public int doStartTag() throws JspException {
        JspWriter out = pageContext.getOut();
        List<EqpDto> eqpList = (List<EqpDto>) pageContext.getRequest().getAttribute("eqpList");
        if (eqpList == null) {
            eqpList = new ArrayList<>();
        }

        // Build map by equipmentCode
        Map<String, EqpDto> eqpMap = new HashMap<>();
        for (EqpDto dto : eqpList) {
            eqpMap.put(dto.getEquipmentCode(), dto);
        }

        // Group children by parentEquipmentCode
        Map<String, List<EqpDto>> childrenMap = new HashMap<>();
        List<EqpDto> topLevel = new ArrayList<>();
        for (EqpDto dto : eqpList) {
            String parentCode = dto.getParentEquipmentCode();
            if (parentCode != null && !parentCode.isEmpty() && eqpMap.containsKey(parentCode)) {
                childrenMap.computeIfAbsent(parentCode, k -> new ArrayList<>()).add(dto);
            } else {
                topLevel.add(dto);
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<select name=\"").append(name).append(".").append(property).append("\">");
        sb.append("<option value=\"\">-- 選択してください --</option>");

        for (EqpDto parent : topLevel) {
            renderOption(sb, parent, 0);
            List<EqpDto> children = childrenMap.get(parent.getEquipmentCode());
            if (children != null) {
                for (EqpDto child : children) {
                    renderOption(sb, child, 1);
                }
            }
        }

        sb.append("</select>");

        try {
            out.print(sb.toString());
        } catch (IOException e) {
            throw new JspException(e);
        }

        return SKIP_BODY;
    }

    private void renderOption(StringBuilder sb, EqpDto dto, int depth) {
        sb.append("<option value=\"").append(dto.getEquipmentCode()).append("\">");
        for (int i = 0; i < depth; i++) {
            sb.append("&nbsp;&nbsp;");
        }
        sb.append(dto.getEquipmentCode()).append(" - ").append(dto.getEquipmentName());
        sb.append("</option>");
    }
}
