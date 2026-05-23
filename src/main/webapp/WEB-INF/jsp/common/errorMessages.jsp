<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<html:errors/>
<%
    String errMsg = (String) request.getAttribute("errorMessage");
    if (errMsg != null) {
%>
    <div style="color:#c33;background:#fcc;padding:8px;margin-bottom:12px;border:1px solid #c33;">
        <%= errMsg %>
    </div>
<%
    }
%>
