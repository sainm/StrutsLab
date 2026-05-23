<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<h1><tiles:getAsString name="title"/></h1>
<div class="search-area"><tiles:insert attribute="searchArea"/></div>
<div class="list-area"><tiles:insert attribute="listArea"/></div>
