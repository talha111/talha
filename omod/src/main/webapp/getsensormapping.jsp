<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

<%@ include file="template/localHeader.jsp"%>

<p>Hello ${user.systemId}!</p>
<form action="getsensormapping.form" method="POST">
Enter the sensor id: <input type="text" name="sid"><br>
<pre>

</pre>
<input type="submit"/>
</form>
<pre>

</pre>
<table border = "5" bordercolor ="black">
<tr>
<td><pre>Sensor Id		</pre></td>
<td><pre>Sensor Name	</pre></td>
</tr>
<tr>
<td><pre>${sid_get}		</pre></td> 
<td><pre>${sname_get}	</pre></td> 
</tr>
</table>
<%@ include file="/WEB-INF/template/footer.jsp"%>