<%@page import="org.openmrs.module.sensorreading.web.controller.SensorReadingManageController"%>
<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

<%@ include file="template/localHeader.jsp"%>

<p>Hello ${user.systemId}!</p>
<form action="getsensorconcept.form" method="POST">
Enter the sensor id: <input type="text" name="sid"><br>
<pre>

</pre>
<input type="submit"/>
</form>

<%
SensorReadingManageController obj = new SensorReadingManageController();
int a[] = obj.getarray();
if(a!=null)
{
	int i;	
	out.println("Concept ids are -: ");
	for(i=0;i<a.length-1;i++)
	{
		out.print(a[i] + ", ");
	}
	out.println(a[i]);
	
	//out.println(a[0]);
}
%>


<%@ include file="/WEB-INF/template/footer.jsp"%>