<%@page import="org.openmrs.module.sensorreading.web.controller.SensorReadingManageController"%>

<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

<%@ include file="template/localHeader.jsp"%>
<p>Hello ${user.systemId}!</p>
<form action = "sensorreading.form" method="POST">
Enter the Patient id: <input type="text" name="pid"><br>
Enter the sensor id: <input type="text" name="sid"><br>
<pre>

</pre>
<input type="submit" name="submit">
</form>

