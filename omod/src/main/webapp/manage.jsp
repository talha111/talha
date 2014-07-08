<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

<%@ include file="template/localHeader.jsp"%>

<p>Hello ${user.systemId}!</p>

<h1>Patient Visit</h1>


  <form action="manage.form" method="POST">

<h2>Patient Demographics</h2>
 Gender: ${patient.gender}<br/>
Birth Date: ${patient.birthdate}</br>
<br/> 
Patient ID: <input type="text" name="patientId" value="${param.patientId}"/> <br/> 
Visit Date: <input type="text" name="visitDate"/>


<input type="submit"/>

</form> 

<%@ include file="/WEB-INF/template/footer.jsp"%>