<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

<%@ include file="template/localHeader.jsp"%>

<p>Hello ${user.systemId}!</p>
<form action="getsensorreading.form" method="POST">
Enter the Encounter id: <input type="text" name="eid"><br>
<input type="submit"/>
</form>
<pre>

</pre>
<table border = "5" bordercolor ="black">
<tr>
<td><pre>Encounter Id		</pre></td>
<td><pre>Test Date			</pre></td>
<td><pre>Patient Id			</pre></td>
<td><pre>Sensor Id			</pre></td>
</tr>
<tr>
<td><pre>${eid}			</pre></td>
<td><pre>${td}			</pre></td>
<td><pre>${pid}			</pre></td>
<td><pre>${sid}			</pre></td> 
</tr>
</table>

<%@ include file="/WEB-INF/template/footer.jsp"%>