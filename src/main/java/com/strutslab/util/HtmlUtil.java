package com.strutslab.util;

public class HtmlUtil {
    private HtmlUtil() {}

    public static String escape(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '&':  sb.append("&amp;"); break;
                case '<':  sb.append("&lt;"); break;
                case '>':  sb.append("&gt;"); break;
                case '"':  sb.append("&quot;"); break;
                case '\'': sb.append("&#39;"); break;
                default:   sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String escape(Object o) {
        return escape(o != null ? o.toString() : "");
    }

    public static String escape(int v) {
        return escape(String.valueOf(v));
    }
}
