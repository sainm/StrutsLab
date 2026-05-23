<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<h1><tiles:getAsString name="title"/></h1>
<div class="wizard-steps"><tiles:insert attribute="wizardSteps" ignore="true"/></div>
<tiles:insert attribute="wizardContent"/>
