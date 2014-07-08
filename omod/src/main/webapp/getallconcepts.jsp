<%@page import="org.openmrs.module.sensorreading.web.controller.SensorReadingManageController"%>
<%@ page import="java.util.List" %>

<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

<%@ include file="template/localHeader.jsp"%>

<p>Hello ${user.systemId}!</p>
<form action="getallconcepts.form" method="POST">
Enter the sensor id: <input type="text" name="sid"><br>
<input type="submit"/>
</form>

<%
SensorReadingManageController obj = new SensorReadingManageController();
List temp1 = obj.getlist();
if(temp1!=null)
{
	int i;	
	out.println("Concept ids are -: ");
	for(i=0;i<a.length;i++)
	{
		out.print(a[i] + ", ");
	}
	out.println("\b");
	
	//out.println(a[0]);
}
%>


<%@ include file="/WEB-INF/template/footer.jsp"%>