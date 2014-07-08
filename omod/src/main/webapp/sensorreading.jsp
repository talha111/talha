<%@page import="org.openmrs.module.sensorreading.web.controller.SensorReadingManageController"%>

<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

<%@ include file="template/localHeader.jsp"%>

<p>Hello ${user.systemId}!</p>


<form action="getsensorpatient.form" method="POST">

<%
SensorReadingManageController obj = new SensorReadingManageController();

int a[] = obj.getarray();
//String cid [] = obj.getarray2();
//int cid [] = obj.getarray2();
if(a!=null)
{
	//int cid [] = new int[a.length];
	int i;	
	out.println("Value for Concept ids -: ");
	for(i=0;i<a.length;i++)
	{
		//out.print(a[i] + ", ");
		%>
		<TR>
			<TD>
				<%= a[i] %>
					<input type="text"  name="b[]">
			</TD>
		</TR>
		
		<%
		//out.println(request.getParameter("s"));
		//cid[i] = Integer.parseInt(request.getParameter("s"));
	}
	out.println("\b");

	/*for(int j =0;j<cid.length;j++)
	{
		cid[i] = Integer.parseInt(request.getParameter("s"));
		//cid[j] = 11;
	}*/
//	obj.returnarray(cid);
}	
	//out.println(a[0]);
%>
<pre>

</pre>
<input type="submit" value = "confirm" name="submit">
<%
String x = request.getParameter("submit"); 
if(x!=null && x.equals("confirm"))
{
//	obj.returnarray(cid);	
}
%>
 
</form>

<%@ include file="/WEB-INF/template/footer.jsp"%>