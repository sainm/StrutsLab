package com.strutslab.taglib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.strutslab.dto.ChkItemDto;

public class InspectionChecklistTag extends TagSupport {

    @Override
    public int doStartTag() throws JspException {
        JspWriter out = pageContext.getOut();

        List<ChkItemDto> allItems = (List<ChkItemDto>) pageContext.getRequest().getAttribute("checklistItems");
        if (allItems == null) {
            allItems = new ArrayList<>();
        }

        // Separate items by level
        List<ChkItemDto> level1 = new ArrayList<>();
        List<ChkItemDto> level2 = new ArrayList<>();
        List<ChkItemDto> level3 = new ArrayList<>();
        for (ChkItemDto item : allItems) {
            Integer lvl = item.getItemLevel();
            if (lvl == null) continue;
            if (lvl == 1) level1.add(item);
            else if (lvl == 2) level2.add(item);
            else if (lvl == 3) level3.add(item);
        }

        // Build parent->children maps
        Map<String, List<ChkItemDto>> l2ByParent = new HashMap<>();
        for (ChkItemDto item : level2) {
            l2ByParent.computeIfAbsent(item.getParentItemId(), k -> new ArrayList<>()).add(item);
        }
        Map<String, List<ChkItemDto>> l3ByParent = new HashMap<>();
        for (ChkItemDto item : level3) {
            l3ByParent.computeIfAbsent(item.getParentItemId(), k -> new ArrayList<>()).add(item);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<table class=\"inspection-checklist\">\n");

        int globalIdx = 0;

        for (ChkItemDto l1 : level1) {
            // Level 1 header row
            sb.append("  <tr><th colspan=\"4\">")
              .append(escapeHtml(l1.getItemName()))
              .append("</th></tr>\n");

            List<ChkItemDto> l2Children = l2ByParent.get(l1.getItemId());
            if (l2Children == null) continue;

            for (ChkItemDto l2 : l2Children) {
                // Level 2 header row
                sb.append("  <tr><th colspan=\"4\" style=\"padding-left:20px\">")
                  .append(escapeHtml(l2.getItemName()))
                  .append("</th></tr>\n");

                List<ChkItemDto> l3Children = l3ByParent.get(l2.getItemId());
                if (l3Children == null) continue;

                for (ChkItemDto l3 : l3Children) {
                    // Level 3 data row
                    String idxStr = String.valueOf(globalIdx);
                    sb.append("  <tr>");
                    sb.append("<td>").append(escapeHtml(l3.getItemName())).append("</td>");

                    // Judge radio buttons
                    sb.append("<td>");
                    sb.append("<input type=\"radio\" name=\"items[").append(idxStr).append("].judge\" value=\"○\"/> ○");
                    sb.append("<input type=\"radio\" name=\"items[").append(idxStr).append("].judge\" value=\"×\"/> ×");
                    sb.append("<input type=\"radio\" name=\"items[").append(idxStr).append("].judge\" value=\"-\"/> -");
                    sb.append("</td>");

                    // Value input
                    sb.append("<td><input type=\"text\" name=\"items[").append(idxStr).append("].value\" size=\"10\"/></td>");

                    // Note textarea
                    sb.append("<td><textarea name=\"items[").append(idxStr).append("].note\" rows=\"2\" cols=\"20\"></textarea></td>");

                    sb.append("</tr>\n");

                    globalIdx++;
                }
            }
        }

        sb.append("</table>");

        try {
            out.print(sb.toString());
        } catch (IOException e) {
            throw new JspException(e);
        }

        return SKIP_BODY;
    }

    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
}
