/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.sensorreading.web.controller;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import ca.uhn.hl7v2.util.Terser;
import ca.uhn.hl7v2.validation.impl.RuleBinding;
import ca.uhn.hl7v2.validation.impl.ValidationContextImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterRole;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.Provider;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.module.sensorreading.SensorConceptMapping;
import org.openmrs.module.sensorreading.SensorMapping;
import org.openmrs.module.sensorreading.SensorReading;
import org.openmrs.module.sensorreading.api.SensorConceptMappingService;
import org.openmrs.module.sensorreading.api.SensorMappingService;
import org.openmrs.module.sensorreading.api.SensorReadingService;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

//import ca.uhn.hl7v2.DefaultHapiContext;
//import ca.uhn.hl7v2.HapiContext;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.Varies;
import ca.uhn.hl7v2.model.v22.datatype.NM;
import ca.uhn.hl7v2.model.v25.datatype.CX;
import ca.uhn.hl7v2.model.v25.group.ORU_R01_OBSERVATION;
import ca.uhn.hl7v2.model.v25.group.ORU_R01_ORDER_OBSERVATION;
import ca.uhn.hl7v2.model.v25.message.ADT_A05;
import ca.uhn.hl7v2.model.v25.message.ORU_R01;
import ca.uhn.hl7v2.model.v25.segment.EVN;
import ca.uhn.hl7v2.model.v25.segment.MSH;
import ca.uhn.hl7v2.model.v25.segment.OBR;
import ca.uhn.hl7v2.model.v25.segment.OBX;
import ca.uhn.hl7v2.model.v25.segment.PID;
import ca.uhn.hl7v2.model.v25.segment.PV1;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.model.Message;







/**
 * The main controller.
 */
@Controller
public class  SensorReadingManageController {
	
	protected final Log log = LogFactory.getLog(getClass());
	public List scm2;
	public List temp_1;
	public static int array [];

	public static int cid [];
	public static String b [];
	
	Integer patient_finalid;
	Integer sensor_finalid;
	
		
	public void decode(int p_id,int cc[],String dd[]) throws HL7Exception
	{
		SensorReadingManageController obj=new SensorReadingManageController();
		//int a[]={1, 2, 3};
		//String b[]={"a", "b", "c"};
		String s=obj.createHL7Message(p_id, cc, dd);
		System.out.println(s);
		int i, l=s.length(), count=0;
		for (i=0; i<l-2; i++)
		{
			if (s.charAt(i)=='O' && s.charAt(i+1)=='B' &&s.charAt(i+2)=='X')
			{
				count++;
			}
		}
		//s contains the HL7 message
		PipeParser pipeParser=new PipeParser();
		//
		ValidationContextImpl context = (ValidationContextImpl)pipeParser.getValidationContext();
		List<?> list = context.getPrimitiveRuleBindings();
		for (Object object : list) {
		    RuleBinding rb = (RuleBinding) object;
		    if(rb.getScope().equals("NM")) {
		       rb.setActive(false);
		    }
		}
		Message hapiMsg=pipeParser.parse(s);
//		try
//		{
//			hapiMsg=pipeParser.parse(s);
////			if (hapiMsg instanceof ACK)
////			{
////				ACK ack=(ACK) hapiMsg;
////				ack.getMSH().getProcessingID().getProcessingMode().setValue("P");
////			}
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
//		for (Method field: hapiMsg.getClass().getDeclaredMethods())
//		{
//			field.setAccessible(true);
//			String name=field.getName();
//			System.out.println(name);
//		}
//		
		ORU_R01 msg=(ORU_R01) hapiMsg;
		MSH msh=msg.getMSH();
		String msgType=msh.getMessageType().getMessage().getName();
		System.out.println(msgType);
		String msgTrigger=msh.getMessageType().getTriggerEvent().getName();
		System.out.println(msgTrigger);
		//PN patientName=msg.getPATIENT_RESULT().getName();
		ORU_R01_ORDER_OBSERVATION orderObservation = msg
				.getPATIENT_RESULT().getORDER_OBSERVATION();
		ca.uhn.hl7v2.model.v25.segment.PID pid=msg.getPATIENT_RESULT().getPATIENT().getPID();
		for (Method field: pid.getPatientID().getClass().getDeclaredMethods())
		{
			field.setAccessible(true);
			String name=field.getName();
			System.out.println(name);
		}
		Terser t25=new Terser(hapiMsg);
		System.out.println("Patient ID: "+t25.get("/PATIENT_RESULT/PATIENT/PID-3-1"));
		System.out.println("Vals: ");
		for (i=0; i<count; i++)
			System.out.println(t25.get("/.OBSERVATION("+i+")/OBX-3-1"));
		System.out.println("Concepts: ");
		for (i=0; i<count; i++)
			System.out.println(t25.get("/.OBSERVATION("+i+")/OBX-5-1"));
	
		
		
//		for (int i = 0; i < 5; i++) {
//			System.out.print("First: ");
//			System.out.println(terser.get("/PATIENT_RESULT/ORDER_OBSERVATION/OBSERVATION(" + i
//					+ ")/OBX-1"));
//			System.out.print("Second: ");
//			System.out.println(terser.get("/PATIENT_RESULT/ORDER_OBSERVATION/OBSERVATION(" + i
//					+ ")/OBX-3"));
//			System.out.print("Third: ");
//			System.out.println(terser.get("/PATIENT_RESULT/ORDER_OBSERVATION/OBSERVATION(" + i
//					+ ")/OBX-5"));
//		}

	
	}
	
	
	
	
	
	
	
	
	public String createHL7Message(int p_id, int concept_id[],String val[]) throws HL7Exception {

		ORU_R01 message=new ORU_R01();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);
		//message.initQuickstart("ORU", "R01", "T");
		//message.getFieldSeparator();
		//Apparently initQuickstart does not work, so I have to populate it manually
		MSH msh=message.getMSH();
		msh.getFieldSeparator().setValue("|");
		msh.getEncodingCharacters().setValue("^~\\&");
		msh.getSendingApplication().getNamespaceID().setValue("TestSendingSystem");
		msh.getReceivingApplication().getNamespaceID().setValue("PAMSimulator");
		msh.getProcessingID().getProcessingID().setValue("P");
		msh.getSequenceNumber().setValue("123");
		msh.getMessageType().getTriggerEvent().setValue("A28");
		msh.getMessageType().getMessageStructure().setValue("ORU_R01");
		msh.getMessageType().getMessageCode().setValue("ORU");
		msh.getVersionID().getVersionID().setValue("2.5");
		msh.getMessageControlID().setValue(sdf.format(Calendar.getInstance().getTime()));
		msh.getSendingFacility().getNamespaceID().setValue("OpenMRS");
		msh.getReceivingFacility().getNamespaceID().setValue("IHE");
		msh.getDateTimeOfMessage().getTime().setValue(sdf.format(Calendar.getInstance().getTime()));
		
		ORU_R01_ORDER_OBSERVATION orderObservation = message
				.getPATIENT_RESULT().getORDER_OBSERVATION();
		ca.uhn.hl7v2.model.v25.segment.PID pid=message.getPATIENT_RESULT().getPATIENT().getPID();
		
		Patient patient = (Patient)Context.getPatientService().getPatient(p_id);
	//	String aaa = ;
		
		System.out.println(String.valueOf(p_id) + "  "  + patient.getGivenName() + "  " + patient.getFamilyName());
		
		//Patient patient = 
		pid.getPatientName(0).getFamilyName().getSurname().setValue(patient.getFamilyName());
		pid.getPatientName(0).getGivenName().setValue(patient.getGivenName());
		pid.getPatientIdentifierList(0).getIDNumber().setValue(String.valueOf(p_id));
		
		System.out.println();
		//Parser parser = new PipeParser();
		//String encodedMessage = null;
			//encodedMessage = parser.encode(message);
		//System.out.println(encodedMessage);	
		
		// Populate the OBR
		OBR obr = orderObservation.getOBR();
		obr.getSetIDOBR().setValue("1");
		obr.getFillerOrderNumber().getEntityIdentifier().setValue("1234");
		obr.getFillerOrderNumber().getNamespaceID().setValue("SensorReading");
		obr.getUniversalServiceIdentifier().getIdentifier().setValue("88304");
		Varies value = null;
		//Varies value[] = new Varies[4];
		for(int i =0;i<concept_id.length;i++)
		{
			ORU_R01_OBSERVATION observation = orderObservation.getOBSERVATION(i);
			OBX obx2 = observation.getOBX();
			obx2.getSetIDOBX().setValue(String.valueOf(i));
			obx2.getObservationIdentifier().getIdentifier().setValue(String.valueOf(concept_id[i]));
			obx2.getValueType().setValue("NM");
			NM nm = new NM(message);
			//int a = 3300 + i;
			//String aa = String.valueOf(a);
			nm.setValue(val[i]);

			value = obx2.getObservationValue(0);
			value.setData(nm);
		}
		Parser parser = new PipeParser();
		String encodedMessage = null;
			encodedMessage = parser.encode(message);
			/* try {
		          File file = new File("example.txt");
		          BufferedWriter output = new BufferedWriter(new FileWriter(file));
		          output.write(encodedMessage);
		          output.close();
		          System.out.println("Printed in a file\n\n");
		        } catch (IOException e) {
		        	System.out.println("Not printed!!!");
		           e.printStackTrace();
		        }*/
			//		System.out.println(encodedMessage);	
			return encodedMessage;
		//System.out.println(message.encode());


	}

	
	
	
	
	
	
	
	@RequestMapping(value = "/module/sensorreading/sensormapping", method = RequestMethod.POST)
	public void savesensorname(@RequestParam("SensorName") String sname,ModelMap mo)
	{
	
		
		
		
		/*
		ORU_R01 message = new ORU_R01();
		
		MSH mshSegment = message.getMSH();
		try {
			mshSegment.getFieldSeparator().setValue("|");
		} catch (DataTypeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			mshSegment.getEncodingCharacters().setValue("^~\\&");
		} catch (DataTypeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			mshSegment.getSendingApplication().getNamespaceID().setValue("TestSendingSystem");
		} catch (DataTypeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			mshSegment.getReceivingApplication().getNamespaceID().setValue("PAMSimulator");
		} catch (DataTypeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		
		
		ORU_R01_ORDER_OBSERVATION orderObservation = message
				.getPATIENT_RESULT().getORDER_OBSERVATION();
		ca.uhn.hl7v2.model.v25.segment.PID pid=message.getPATIENT_RESULT().getPATIENT().getPID();
		try {
			pid.getPatientName(0).getFamilyName().getSurname().setValue("Baijal");
		} catch (DataTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HL7Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			pid.getPatientName(0).getGivenName().setValue("Rishi");
		} catch (DataTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HL7Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			pid.getPatientIdentifierList(0).getIDNumber().setValue("123456");
		} catch (DataTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HL7Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Populate the OBR
		OBR obr = orderObservation.getOBR();
		try {
			obr.getSetIDOBR().setValue("1");
		} catch (DataTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			obr.getFillerOrderNumber().getEntityIdentifier().setValue("1234");
		} catch (DataTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			obr.getFillerOrderNumber().getNamespaceID().setValue("LAB");
		} catch (DataTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			obr.getUniversalServiceIdentifier().getIdentifier().setValue("88304");
		} catch (DataTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ORU_R01_OBSERVATION observation = null;
		try {
			observation = orderObservation.getOBSERVATION(0);
		} catch (HL7Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		OBX obx2 = observation.getOBX();
		try {
			obx2.getSetIDOBX().setValue("3");
		} catch (DataTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			obx2.getObservationIdentifier().getIdentifier().setValue("1");
		} catch (DataTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			obx2.getValueType().setValue("NM");
		} catch (DataTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		NM nm = new NM(message);
		try {
			nm.setValue("300");
		} catch (DataTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Varies value = null;
		try {
			value = obx2.getObservationValue(0);
		} catch (HL7Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			value.setData(nm);
		} catch (DataTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Parser parser = new PipeParser();
		String encodedMessage = null;
		try {
			encodedMessage = parser.encode(message);
		} catch (HL7Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(encodedMessage);	
		
		//System.out.println(message.encode());

		*/
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
				
		
		
		
		
		
		
		
		
		System.out.println("pehle Yahaan aayaa!!!\n");
		SensorMapping sensor = new SensorMapping();
		sensor.setSensor_name(sname);
		Context.getService(SensorMappingService.class).saveSensorMapping(sensor);
		//SensorMapping sensor = (SensorMapping)Context.getService(SensorMappingService.class).retrieveSensorMapping(sid);		
	}
	
	@RequestMapping(value = "/module/sensorreading/sensormapping", method = RequestMethod.GET)
	public void manage(ModelMap model) {
		model.addAttribute("user", Context.getAuthenticatedUser());
	}
	
	
	
	/*
	 * Getting The entire Sensor - Concept table
	 * 
	 * 
	@RequestMapping(value = "/module/sensorreading/getallconcepts", method = RequestMethod.POST)
	public void getallconcepts(ModelMap mo)
	{
		
		System.out.println("pehle Yahaan aayaa!!!\n");
		scm2 = (List)Context.getService(SensorConceptMappingService.class).getAll();
		SensorConceptMapping ses = (SensorConceptMapping)scm.get(0);
		SensorMapping s2 = ses.getSensor();
		System.out.println("\n\n" + s2.getSensor_id());	
		mo.addAttribute("sid_get", sid_get);
	    mo.addAttribute("sname_get", sname_get);
	    safeRedirect6("http://localhost:8080/openmrs/module/sensorreading/getallconcept");
	    
	}
	
	public static RedirectView safeRedirect6(String url) {
	    RedirectView rv = new RedirectView(url);
	    rv.setExposeModelAttributes(false);
	    return rv;
	}
	
	@RequestMapping(value = "/module/sensorreading/getallconcepts", method = RequestMethod.GET)
	public void showPage(ModelMap modelMap) {
		log.error("In the 'showPage' method..kdld");
	}
	*/
	
	
	@RequestMapping(value = "/module/sensorreading/getsensormapping", method = RequestMethod.POST)
	public void getBpReading(@RequestParam("sid") Integer sid,ModelMap mo)
	{
		
		//@SuppressWarnings("deprecation")
		//Date dd = new Date(2014,07,07);
		//@SuppressWarnings("deprecation")
		//Date dd2 = new Date(2014,07,06);
		
		/*
		Date d2 = new Date();
		 Calendar cal = Calendar.getInstance();
	        cal.set(Calendar.MONTH, 07);
	        cal.set(Calendar.DATE, 07);
	        cal.set(Calendar.YEAR, 2014);
	    //    cal.set(Calendar.HOUR,00);
	     //   cal.set(Calendar.MINUTE,00);
	     //   cal.set(Calendar.SECOND,00);
	        d2 = cal.getTime();
	    Date d = new Date(System.currentTimeMillis());
	    			
	        
		Person person = (Person) Context.getPatientService().getPatient(2);
		//List<Person> person = (List<Person>)Context.getPatientService().get
		List <Person> pp = new ArrayList<Person>();
		pp.add(person);
		//pp.add(person);
		List<Obs> oo  =(List<Obs>) Context.getObsService().getObservations(pp, null, null, null, null, null, null, null, null, dd2, dd, false);//.getObservationCount(pp, null, null, null, null, null, null, null, null, (Boolean) null);
		
		
		
		
//		List<Obs> oo  =(List<Obs>) Context.getObsService().getObservationsByPerson(person);
//		Set<Obs> set = new HashSet<Obs>();
		System.out.println("date aayo kya??");
		for(Obs o: oo)
		{	
			System.out.println("haan aayi");
			
	//			set.add(o);
				System.out.println(o.getId());
			
		}
		*/
		
		
		//ObsService obsService=Context.getObsService();

		//Person person = (Person) Context.getPatientService().getPatient(2);
		//List<Person> person = (List<Person>)Context.getPatientService().get
		//List <Person> pp = new ArrayList<Person>();
		//pp.add(person);
		
		//List<Obs> obsList=obsService.getObservationsByPerson(new Person(1));
	
		
		
		Calendar cal = Calendar.getInstance();
		
		Date start_date = new Date();
		cal.set(Calendar.MONTH, 06);
	    cal.set(Calendar.DATE, 06);
	    cal.set(Calendar.YEAR, 2014);
	    cal.set(Calendar.HOUR,00);
	    cal.set(Calendar.MINUTE,00);
	    cal.set(Calendar.SECOND,00);
	    start_date = cal.getTime();
	    
		
	        
	    Date end_date = new Date();
		cal.set(Calendar.MONTH, 06);
        cal.set(Calendar.DATE, 07);
        cal.set(Calendar.YEAR, 2014);
		cal.set(Calendar.HOUR,00);
		cal.set(Calendar.MINUTE,00);
		cal.set(Calendar.SECOND,00);
		end_date = cal.getTime();
		    
		
		//Date dd = new Date(2014-07-06);
		//Date dd2 = new Date(2014-07-07);
		System.out.println(start_date + "  " + end_date);
		
		List<Person> personList=new ArrayList<Person>();
		personList.add(Context.getPersonService().getPerson(2));
		
		System.out.println("aasdf");
		for(Person pp : personList)
		{
			System.out.println(pp.getFamilyName());
			System.out.println(pp.getGivenName());
		}
		
		List<Concept> cc=new ArrayList<Concept>();
		//personList.add(Context.getPersonService().getPerson(2));
		cc.add(Context.getConceptService().getConcept(4));
		
		List<Obs> obsList1=Context.getObsService().getObservations(personList, null, cc,null, null, null, null, null, null, start_date, end_date, false);
		
		for (Obs object : obsList1)
		{
				System.out.println("Concept id - : " + object.getConcept().getConceptId() + "\nConcept value - : "  + object.getValueNumeric());
		}
		
		
		
		System.out.println("pehle Yahaan aayaa!!!\n");
		SensorMapping ss = (SensorMapping)Context.getService(SensorMappingService.class).retrieveSensorMapping(sid);
		//temp_1 = HibernateSensorMappingDAO.userList_1; 
		//SensorMapping obj = (SensorMapping)temp_1.get(0);
	    //int sid_get = obj.getSensor_id();
	    //String sname_get = obj.getSensor_name();
		//aa = obj.getBp_reading();
		int sid_get = ss.getSensor_id();
	    String sname_get = ss.getSensor_name();
		
		mo.addAttribute("sid_get", sid_get);
	    mo.addAttribute("sname_get", sname_get);
	 //   return "redirect:http://localhost:8080/openmrs/module/bpReadingModule/examplewithcontroller";
	    //System.out.println("Value returned isssss " + aa);
	    safeRedirect("http://localhost:8080/openmrs/module/sensorreading/getsensormapping");
	    
	}
	
	public static RedirectView safeRedirect(String url) {
	    RedirectView rv = new RedirectView(url);
	    rv.setExposeModelAttributes(false);
	    return rv;
	}
	
	@RequestMapping(value = "/module/sensorreading/getsensormapping", method = RequestMethod.GET)
	public void showPage(ModelMap modelMap) {
		log.error("In the 'showPage' method..kdld");
	}
	
	@RequestMapping(value = "/module/sensorreading/sensorconcept", method = RequestMethod.POST)
	public void savesensorname(@RequestParam("sid") Integer sid,@RequestParam("cid") String cid,ModelMap mo)
	{
		System.out.println("pehle Yahaan aayaa!!!\n");
		
		SensorMapping ss = (SensorMapping)Context.getService(SensorMappingService.class).retrieveSensorMapping(sid);
		SensorConceptMapping scm = new SensorConceptMapping();
		scm.setSensor(ss);
		Set<Concept> concepts = new HashSet<Concept>();
		StringTokenizer st=new StringTokenizer(cid, ", ");
		int l=st.countTokens();
		int i;
		int w[]=new int[l];
		for (i=0; i<l; i++)
		{
			w[i]=Integer.parseInt(st.nextToken());
		}
		for(i=0;i<l;i++)
		{
			Concept concept = (Concept)Context.getConceptService().getConcept(w[i]);		
			concepts.add(concept);
		}
		scm.setConcepts(concepts);
		Context.getService(SensorConceptMappingService.class).saveSensorConceptMapping(scm);
		
		
		//SensorMapping sensor = new SensorMapping();
		//sensor.setId(sid);
		//sensor.setSensor_name(sname);
		//Context.getService(SensorConceptMappingService.class).saveSensorMapping(sensor);
		//SensorMapping sensor = (SensorMapping)Context.getService(SensorMappingService.class).retrieveSensorMapping(sid);		
	}
	
	@RequestMapping(value = "/module/sensorreading/sensorconcept", method = RequestMethod.GET)
	public void manage2(ModelMap model) {
		model.addAttribute("user", Context.getAuthenticatedUser());
	}
	
	
	@RequestMapping(value = "/module/sensorreading/getsensorconcept", method = RequestMethod.POST)
	public void getsensorconcept(@RequestParam("sid") Integer sid,ModelMap mo)
	{
		//SensorReadingManageController obj = new SensorReadingManageController();
		//obj.savesensorreading();

		System.out.println("IS IT COMMING2222222!!!!!!!\n\n\n");
		//savesensorreading();
		System.out.println("pehle Yahaan aayaa!!!\n");
		//SensorMapping ss = (SensorMapping)Context.getService(SensorMappingService.class).retrieveSensorMapping(sid);
		SensorConceptMapping scm = (SensorConceptMapping)Context.getService(SensorConceptMappingService.class).retrieveSensorConceptMapping(sid);
		Set<Concept> concepts = new HashSet<Concept>();
		concepts = scm.getConcepts();
		
		int i, count=0;
		
		if(array!=null)
		{
			for(i=0;i<array.length;i++)
			{
				array[i] = 0;
			}
		}
		
		array = new int[concepts.size()];
		Iterator iter = concepts.iterator();
		while(iter.hasNext())
		{
			Concept cc =(Concept)iter.next();
			array[count] = cc.getId();
			count++;
		}
		
		int s_array[] = new int [array.length];
		for(i=0;i<s_array.length;i++)
		{
			s_array[i] = sid;
		}
		
		System.out.println("Printing the values of Concept id\n\n");
		for(int j=0;j<array.length;j++)
		{
			System.out.println(array[j]);
//			System.out.println(getarray()[j]);
		}
		mo.addAttribute("s_array", s_array);
		mo.addAttribute("array", array);
			
		//temp_1 = HibernateSensorMappingDAO.userList_1; 
		//SensorMapping obj = (SensorMapping)temp_1.get(0);
	    //int sid_get = obj.getSensor_id();
	    //String sname_get = obj.getSensor_name();
		//aa = obj.getBp_reading();
		//int sid_get = ss.getSensor_id();
	    //String sname_get = ss.getSensor_name();
		
	 //   return "redirect:http://localhost:8080/openmrs/module/bpReadingModule/examplewithcontroller";
	    //System.out.println("Value returned isssss " + aa);
	    safeRedirect2("http://localhost:8080/openmrs/module/sensorreading/getsensorconcept");
	    
	}

	
	public int [] getarray()
	{
		return array;
	}
	
	
	public void returnarray(int bb[])
	{	System.out.println("Array length is  " + bb.length);
		int i =0;
		for(i=0;i<bb.length;i++)
		{
			cid[i] = bb[i];
		}
	}
	
	
	public int [] getarray2()
	{
		cid = new int[array.length];
		
//		for(int i=0;i<array.length;i++)
//		{
//			cid [i] = array[i];
//		}
		return cid;
	}
	
	
	/*public String[] getarray2()
	{
		b=new String[array.length];
		return b;
	}*/
	
	public List getlist()
	{
		return scm2;
	}
	
	public static RedirectView safeRedirect2(String url) {
	    RedirectView rv = new RedirectView(url);
	    rv.setExposeModelAttributes(false);
	    return rv;
	}
	
	@RequestMapping(value = "/module/sensorreading/getsensorconcept", method = RequestMethod.GET)
	public void showPage2(ModelMap modelMap) {
		log.error("In the 'showPage' method..kdld");
	}
	//@RequestParam("a") Integer[] cid,

	@RequestMapping(value = "/module/sensorreading/getsensorpatient", method = RequestMethod.GET)
	public void empty()
	{
		
	}
	/*
	@RequestMapping(value = "/module/sensorreading/getsensorpatient", method = RequestMethod.POST)
	public void empty2()
	{
		
	}
	*/
	@RequestMapping(value = "/module/sensorreading/sensorreading", params = "submit", method = RequestMethod.POST)
	public void getsensorpatient(@RequestParam("pid") Integer pid,@RequestParam("sid") Integer sid,ModelMap mo)
	{
		System.out.println("pehle Yahaan aayaa kya??????!!!\n");
		
		patient_finalid = pid;
		sensor_finalid = sid;
		System.out.println("Patient id is " + patient_finalid);
		System.out.println("Sensor id is " + sensor_finalid);
		
		SensorConceptMapping scm = (SensorConceptMapping)Context.getService(SensorConceptMappingService.class).retrieveSensorConceptMapping(sid);
		Set<Concept> concepts = new HashSet<Concept>();
		concepts = scm.getConcepts();
		
		int i, count=0;
		
		if(array!=null)
		{
			for(i=0;i<array.length;i++)
			{
				array[i] = 0;
			}
		}
		
		array = new int[concepts.size()];
		Iterator iter = concepts.iterator();
		while(iter.hasNext())
		{
			Concept cc =(Concept)iter.next();
			array[count] = cc.getId();
			count++;
		}
		int totalconcepts = array.length;	
		safeRedirect4("http://localhost:8080/openmrs/module/sensorreading/sensorreading");
	    
	}
	
	public static RedirectView safeRedirect4(String url) {
	    RedirectView rv = new RedirectView(url);
	    rv.setExposeModelAttributes(false);
	    return rv;
	}
	

	
	@RequestMapping(value = "/module/sensorreading/sensorreading", method = RequestMethod.GET)
	public void manage3(ModelMap model) {
		model.addAttribute("user", Context.getAuthenticatedUser());
	}
	
//	@RequestMapping(value = "/module/sensorreading/sensorreading", method = RequestMethod.POST)
//	public void manage4(ModelMap model) {
//		model.addAttribute("user", Context.getAuthenticatedUser());
//	}
	
	@RequestMapping(value = "/module/sensorreading/getsensorpatient",params = "submit", method = RequestMethod.POST)
	public void savesensorreading(@RequestParam(value = "b[]")String[] b,ModelMap mo)	
//	public void savesensorreading()//@RequestParam(value = "cid")Integer cid[],ModelMap mo)	

	{
	//	String cid[] = {"12"};
		System.out.println("\n\nDId it come Here!!!???\n\n");	
		SensorConceptMapping scm = (SensorConceptMapping)Context.getService(SensorConceptMappingService.class).retrieveSensorConceptMapping(sensor_finalid);
		Set<Concept> concepts = new HashSet<Concept>();
		concepts = scm.getConcepts();
		
		int i, count=0;
		
		if(array!=null)
		{
			for(i=0;i<array.length;i++)
			{
				array[i] = 0;
			}
		}
		
		array = new int[concepts.size()];
		Iterator iter = concepts.iterator();
		while(iter.hasNext())
		{
			Concept cc =(Concept)iter.next();
			array[count] = cc.getId();
			count++;
		}
		int totalconcepts = array.length;
//		cid = new int[array.length];

		/*		for(i=0;i<array.length;i++)
		{
			cid[i] = array[i];
		}*/
		
//		System.out.println("Sensor Reading\n");
//		for(i=0;i<totalconcepts;i++)
//		{
//			System.out.println("this " + array[i]);
//		}
		
		
		
		Patient patient = (Patient)Context.getPatientService().getPatient(patient_finalid);
		
		Encounter enc = new Encounter();
		Date d = new Date(System.currentTimeMillis());
		enc.setEncounterDatetime(d);
		enc.setPatient(patient);
		enc.setEncounterType((EncounterType)Context.getEncounterService().getEncounterType(1));
		enc.setProvider((EncounterRole)Context.getEncounterService().getEncounterRole(1),(Provider) Context.getProviderService().getProvider(1));
//		Encounter enc_formed = (Encounter)Context.getEncounterService().saveEncounter(enc);
		
		
		
		//List scm2 = (List)Context.getService(SensorConceptMappingService.class).getAll();
		//List<Obs> oo  =(List<Obs>) Context.getObsService().getObservations(scm2, d, d);
		//List<Obs> oo  =(List<Obs>) Context.getObsService().
		//List cc<concepts>;
		//List<Obs> oo  =(List<Obs>) Context.getObsService().getObservations((List<Concept>) scm, d,d);
		//List<Obs> ooo = (List<Obs>)Context.getObsService().findObsByGroupId(d);//getObservations(cc, d, d, true);
		
		Person person = (Person) Context.getPatientService().getPatient(patient_finalid);
//		List<Obs> oo  =(List<Obs>) Context.getObsService().getObservationsByPerson(person);
//		Set<Obs> set = new HashSet<Obs>();
//		
//		
//		for(Obs o: oo)
//		{
//			if (o.getObsDatetime() == d)
//			{
//				set.add(o);
//			}
//		}
//		
		/*
		 * Setting multiple concepts by creating multiple observations and
		 * adding it to encounter everytime.
		 */
		SensorConceptMapping sensorConcepts = (SensorConceptMapping)Context.getService(SensorConceptMappingService.class).retrieveSensorConceptMapping(sensor_finalid);
		Set<Concept> retrievedConcepts = sensorConcepts.getConcepts();
		//int counter = 0;
		Set<Obs> observations = new HashSet<Obs>();
		int l =0;
		for (Concept retrievedElement : retrievedConcepts){
				System.out.println("in loop");
				Obs obs = new Obs();
				obs.setPerson(person);
				//change 5090 by retreiveElement.getConceptId();
				
				Concept concept = (Concept)Context.getConceptService().getConcept(array[l]);		
				obs.setConcept(concept);
//				obs.setValueNumeric((double) cid[l]);
				obs.setValueNumeric((double) Integer.parseInt(b[l]));
				System.out.println("Value of concept is " + Integer.parseInt(b[l]));
//				obs.setValueNumeric((double) Integer.parseInt(cid[l]));

				//System.out.println("Value of concept is " + cid[l]);

				//obs.setValueNumeric((double) cid[l]);


	//			Concept concept = (Concept)Context.getConceptService().getConcept(5090);
				obs.setObsDatetime(d);
	//			obs.setConcept(concept);
	//			int height = counter + 170;
	//			String val = Integer.toString(height);
	//			System.out.println("heigh is " + val);
	//			try { 
	//				obs.setValueAsString(val);
	//			} catch (ParseException e) {
	//				// TODO Auto-generated catch block
	//				e.printStackTrace();
	//			}
	//			counter++;
				enc.addObs(obs);
				observations.add(obs);
				l++;
		}
		
		/*
		 * 
		 * Creating an HL7 message of the same
		 * 
		 * 
		 */
	
		
		/*String msg = null;
		try {
			 msg = createHL7Message(patient_finalid,array,b);
		} catch (HL7Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		//System.out.println(msg);
		
		
		try {
			decode(patient_finalid,array,b);
		} catch (HL7Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		Encounter enc_formed = (Encounter)Context.getEncounterService().saveEncounter(enc);
		SensorReading sensorReading = new SensorReading();
		sensorReading.setPatient(patient);
		SensorMapping sensor = (SensorMapping)Context.getService(SensorMappingService.class).retrieveSensorMapping(sensor_finalid);
		sensorReading.setSensor(sensor);
		sensorReading.setEncounter(enc_formed);
		sensorReading.setObservations(observations);
		sensorReading.setDate(d);
		sensorReading.setEncounter_id(enc_formed.getEncounterId());
		Context.getService(SensorReadingService.class).saveSensorReading(sensorReading);
		safeRedirect6("http://localhost:8080/openmrs/module/getsensorpatient/sensorreading");
	    
	}
	
	public static RedirectView safeRedirect6(String url) {
	    RedirectView rv = new RedirectView(url);
	    rv.setExposeModelAttributes(false);
	    return rv;
	}
			
	
	
	@RequestMapping(value = "/module/sensorreading/getsensorreading", method = RequestMethod.POST)
	public void getsensorreading(@RequestParam("eid") Integer eid,ModelMap mo)
	{
		System.out.println("pehle Yahaan aayaa!!!\n");
		
		SensorReading sr = (SensorReading)Context.getService(SensorReadingService.class).readSensorReading(eid);
		//Obs obs = sr.getObservation();
		//int oid = obs.getId();
		
		Patient patient = sr.getPatient();
		int pid = patient.getId();
		
		SensorMapping ss = sr.getSensor();
		int sid = ss.getSensor_id();
		
		Date td = sr.getDate();
		
		//SensorMapping ss = (SensorMapping)Context.getService(SensorMappingService.class).retrieveSensorMapping(sid);
		//temp_1 = HibernateSensorMappingDAO.userList_1; 
		//SensorMapping obj = (SensorMapping)temp_1.get(0);
	    //int sid_get = obj.getSensor_id();
	    //String sname_get = obj.getSensor_name();
		//aa = obj.getBp_reading();
		//int sid_get = ss.getSensor_id();
	    //String sname_get = ss.getSensor_name();
		
		mo.addAttribute("eid", eid);
	    mo.addAttribute("sid", sid);
	    mo.addAttribute("td", td);
	    mo.addAttribute("pid", pid);
	    //mo.addAttribute("oid", oid);
	    
	    
	 //   return "redirect:http://localhost:8080/openmrs/module/bpReadingModule/examplewithcontroller";
	    //System.out.println("Value returned isssss " + aa);
	    safeRedirect3("http://localhost:8080/openmrs/module/sensorreading/getsensorreading");
	    
	}
	
	public static RedirectView safeRedirect3(String url) {
	    RedirectView rv = new RedirectView(url);
	    rv.setExposeModelAttributes(false);
	    return rv;
	}
	
	@RequestMapping(value = "/module/sensorreading/getsensorreading", method = RequestMethod.GET)
	public void showPage3(ModelMap modelMap) {
		log.error("In the 'showPage' method..kdld");
	}
	
}



//Integer encounter_id = 17;
//SensorReading sr = (SensorReading)Context.getService(SensorReadingService.class).readSensorReading(encounter_id);
//enc.setEncounterId(encounterId);

//List<Patient> patients = (List<Patient>) Context.getPatientService().getAllPatients();
//for (Patient p : patients)
//       System.out.println("id is" + p.getPatientId() + p.getNames());
//System.out.println("IS IT COMMING333333!!!!!!!\n\n\n");
////System.out.println("Patient id is " + patient.getNames());

//
////@InitBinder
////public void initBinder(WebDataBinder binder) { 
////binder.registerCustomEditor(Date.class, new CustomDateEditor(Context.getDateFormat(), false)); 
////}    
////
//
////@RequestMapping(method=RequestMethod.GET)
////public void populateForm(ModelMap map) { 
////List<Date> dates = new ArrayList<Date>();  
////List<Location> locations = Context.getLocationService().getAllLocations();
////map.put("dates", dates);
////map.put("locations", locations);
////}	
//
//
///**
//* @should set gender to male
//* @should return non null patient
//* @should return patient with patient id 
//* @param map
//*/
////@ModelAttribute("patient")
////public Patient formBackingObject(
////	@RequestParam(value = "patientId", required = false) Integer patientId) { 
////Patient patient = Context.getPatientService().getPatient(patientId);
////log.error("Patient: " + patient);
////
////
////if (patient == null) { 
////	patient = new Patient();
////	patient.setGender("male");
////}
////
////
////return patient;
////}
////
///**
//* @should save observation for visit date
//* @param patient
//* @param visitDate
//*/
//@RequestMapping(method=RequestMethod.POST)
//public ModelAndView processForm(
////	@ModelAttribute("patient") Patient patient,
//	@RequestParam("patientId") int patientId,
////	@RequestParam("sensorId") int sensorId,
//
//	HttpServletRequest request) {
//ModelAndView model = new ModelAndView();
//


//		//Integer patientId = Integer.parseInt(request.getParameter("patientId"));		
//		//Patient patient = 
//		int encounterId=2451;
//		int sensorId=1;
//		System.out.println("\n\nthe patient id id  " + patientId);
//		
//		Encounter enc = new Encounter();
////		enc.setEncounterId(encounterId);
//		Date d = new Date(System.currentTimeMillis());
//		enc.setEncounterDatetime(d);
//		enc.setPatient(patient);
////		List<EncounterType> et = (List<EncounterType>)Context.getEncounterService().getAllEncounterTypes();
//		
//		
//		enc.setEncounterType((EncounterType)Context.getEncounterService().getEncounterType(1));
//		List<Provider> provider = (List<Provider> )Context.getProviderService().getAllProviders();
//		for(Provider p : provider)
//			System.out.println("provider is" + p.getProviderId() + p.getId() + p.getAttributes() + p.getName());
//		EncounterRole er = (EncounterRole)Context.getEncounterService().getEncounterRole(1);
//		System.out.println("er is " +  er.getName() + er.getDescription());
//		
//		enc.setProvider((EncounterRole)Context.getEncounterService().getEncounterRole(1),(Provider) Context.getProviderService().getProvider(1));
//		Encounter enc_formed = (Encounter)Context.getEncounterService().saveEncounter(enc);
//		Person person = (Person) Context.getPatientService().getPatient(patientId);
//		
//		Obs obs = new Obs();
////		obs.setObsId(1012);
//		obs.setPerson(person);
//		Concept concept = (Concept)Context.getConceptService().getConcept(5090);
//		obs.setObsDatetime(d);;
//		obs.setConcept(concept);
//		obs.setValueNumeric((double) 170);
////		obs.setEncounter(enc);
////		Context.getObsService().s
//		Obs obs_formed = (Obs)Context.getObsService().saveObs(obs, "");
//		SensorReading sensorReading = new SensorReading();
//		sensorReading.setEncounter_id(enc_formed.getEncounterId());
//		sensorReading.setPatient(patient);
//		SensorMapping sensor = (SensorMapping)Context.getService(SensorMappingService.class).retrieveSensorMapping(10);
//		sensorReading.setSensor(sensor);
//		sensorReading.setEncounter(enc_formed);
//		sensorReading.setObservation(obs_formed);
//		Context.getService(SensorReadingService.class).saveSensorReading(sensorReading);
//		Integer encounter_id = 17;
//		SensorReading sr = (SensorReading)Context.getService(SensorReadingService.class).readSensorReading(encounter_id);
//		
//		System.out.println("date is " + sr.getObservation().getObsDatetime());
//		SensorMapping sensorMapping = new SensorMapping();
////		sensorMapping.setSensor_id(11);
//		sensorMapping.setSensor_name("pulse meter");
//		
//		Context.getService(SensorMappingService.class).saveSensorMapping(sensorMapping);
//		SensorMapping sm = (SensorMapping)Context.getService(SensorMappingService.class).retrieveSensorMapping(12);
//		
//		System.out.println("done" + sm.getSensor_name());
//		
//		SensorConceptMapping sensorConceptMapping = new SensorConceptMapping();
//		sensorConceptMapping.setSensor(sm);
//		Concept concept2 = (Concept)Context.getConceptService().getConcept(5092);
//		Set<Concept> concepts = new HashSet<Concept>();
//		concepts.add(concept);
//		concepts.add(concept2);
//		sensorConceptMapping.setConcepts(concepts);
//		Context.getService(SensorConceptMappingService.class).saveSensorConceptMapping(sensorConceptMapping);
//		
//		//		System.out.println("\n\nthe gender of patient is " + patient.getGender());
////		List<Patient> patients = (List<Patient>)Context.getPatientService().getAllPatients();
////		System.out.println("\niterating over patients");
////		for (Patient p : patients){
////			System.out.println("patient id is " + p.getPatientId() + p.getGivenName());
////		}
//		//Get the users
////		List <User> providers =  (List<User>)Context.getUserService().getAllUsers();
////		for (User p: providers){
////			System.out.println(p.getUsername() + p.getUserId());
////		}
////		List<User> provider2 =  (List <User>) Context.getUserService().getAllUsers();
////		for (User p : provider2){
////			System.out.println("id = "+ p.getId() + p.getUsername()+ p.getFamilyName() + p.getDescription() + p.getRoles());
////		}
////		User provider = new User();
////		provider =  (User) Context.getUserService().getUser(1);
////		Location location = new Location();
////		location = (Location)Context.getLocationService().getLocation(1);
////		Date d2 = DateUtils.addHours(d, 60);
////		Appointment appt = new Appointment();
////		appt.setEndDatetime(d2);
////		appt.setStartDatetime(d);
////		appt.setPatient(patient);
////		appt.setLocation(location);
////		appt.setProvider(provider);
////		appt.setId(7);
////		Context.getService(AppointmentService.class).scheduleAppointment(appt);
////		
//		
////		Obs obs = new Obs(patient, new Concept(5096), new Date(), new Location(1));
////		Concept concept = Context.getConceptService().getConcept(5096);
////		Obs obs = new Obs();
////		obs.setPerson(patient);
////		obs.setConcept(concept);
//////		obs.setPerson(patient);
////		obs.setObsDatetime(visitDate);
////		obs.setValueDatetime(d2);
//////		obs.setValueDatetime(visitDate);
////		System.out.println("observations made");
////		
////		System.out.println("Date is " + d);
////		System.out.println("observation id before saving is " + obs.getObsId());
//////			obs.setObsId(1202);
////		obs = Context.getObsService().saveObs(obs, "because");
////		System.out.println("observation id after saving is " + obs.getObsId());
////		model.addObject("obs", obs);
//		
//		return model;
//	}
//}


///**
// * The contents of this file are subject to the OpenMRS Public License
// * Version 1.0 (the "License"); you may not use this file except in
// * compliance with the License. You may obtain a copy of the License at
// * http://license.openmrs.org
// *
// * Software distributed under the License is distributed on an "AS IS"
// * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
// * License for the specific language governing rights and limitations
// * under the License.
// *
// * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
// */
//package org.openmrs.module.sensorreading.web.controller;
//
//import java.text.ParseException;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//import javax.servlet.http.HttpServletRequest;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.openmrs.Concept;
//import org.openmrs.ConceptAnswer;
//import org.openmrs.Encounter;
//import org.openmrs.EncounterRole;
//import org.openmrs.EncounterType;
//import org.openmrs.Obs;
//import org.openmrs.Patient;
//import org.openmrs.Person;
//import org.openmrs.Provider;
//import org.openmrs.api.context.Context;
//import org.openmrs.module.sensorreading.SensorConceptMapping;
//import org.openmrs.module.sensorreading.SensorMapping;
//import org.openmrs.module.sensorreading.SensorReading;
//import org.openmrs.module.sensorreading.api.SensorMappingService;
//import org.openmrs.module.sensorreading.api.SensorConceptMappingService;
//import org.openmrs.module.sensorreading.api.SensorMappingService;
//import org.openmrs.module.sensorreading.api.SensorReadingService;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.ModelMap;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.servlet.ModelAndView;
//
///**
// * The main controller.
// */
//@Controller
//public class  SensorReadingManageController {
//	
//	protected final Log log = LogFactory.getLog(getClass());
//	
//	@RequestMapping(value = "/module/sensorreading/manage", method = RequestMethod.GET)
//	public void manage(ModelMap model) {
//		model.addAttribute("user", Context.getAuthenticatedUser());
//	}
//	
////	@InitBinder
////    public void initBinder(WebDataBinder binder) { 
////    	binder.registerCustomEditor(Date.class, new CustomDateEditor(Context.getDateFormat(), false)); 
////    }    
////    
//
////	@RequestMapping(method=RequestMethod.GET)
////	public void populateForm(ModelMap map) { 
////		List<Date> dates = new ArrayList<Date>();  
////		List<Location> locations = Context.getLocationService().getAllLocations();
////		map.put("dates", dates);
////		map.put("locations", locations);
////	}	
//	
//	
//	/**
//	 * @should set gender to male
//	 * @should return non null patient
//	 * @should return patient with patient id 
//	 * @param map
//	 */
////	@ModelAttribute("patient")
////	public Patient formBackingObject(
////			@RequestParam(value = "patientId", required = false) Integer patientId) { 
////		Patient patient = Context.getPatientService().getPatient(patientId);
////		log.error("Patient: " + patient);
////
////		
////		if (patient == null) { 
////			patient = new Patient();
////			patient.setGender("male");
////		}
////		
////		
////		return patient;
////	}
////	
//	/**
//	 * @should save observation for visit date
//	 * @param patient
//	 * @param visitDate
//	 */
//	@RequestMapping(method=RequestMethod.POST)
//	public ModelAndView processForm(
////			@ModelAttribute("patient") Patient patient,
//			@RequestParam("patientId") int patientId,
////			@RequestParam("sensorId") int sensorId,
//
//			HttpServletRequest request) {
//		ModelAndView model = new ModelAndView();
//		
//		System.out.println("\n\nthe patient id id  " + patientId);
//		Patient patient = (Patient) Context.getPatientService().getPatient(patientId);
//		
//		/*
//		 * Set an encounter, make an observation, add that observation to the
//		 * encounter save the encounter. get other parameters and along with
//		 * encounter and observation form the sensorReading object and save it
//		 */
//		
//		Encounter enc = new Encounter();
//		Date d = new Date(System.currentTimeMillis());
//		enc.setEncounterDatetime(d);
//		enc.setPatient(patient);
//		
//		
//		enc.setEncounterType((EncounterType)Context.getEncounterService().getEncounterType(1));
////		List<Provider> provider = (List<Provider> )Context.getProviderService().getAllProviders();
////		for(Provider p : provider)
////			System.out.println("provider is" + p.getProviderId() + p.getId() + p.getAttributes() + p.getName());
//		EncounterRole er = (EncounterRole)Context.getEncounterService().getEncounterRole(1);
//		System.out.println("er is " +  er.getName() + er.getDescription());
//		
//		enc.setProvider((EncounterRole)Context.getEncounterService().getEncounterRole(1),(Provider) Context.getProviderService().getProvider(1));
//		Person person = (Person) Context.getPatientService().getPatient(patientId);
//		
//		/*
//		 * Setting multiple concepts by creating multiple observations and
//		 * adding it to encounter everytime.
//		 */
//		SensorConceptMapping sensorConcepts = (SensorConceptMapping)Context.getService(SensorConceptMappingService.class).retrieveSensorConceptMapping(12);
//		Set<Concept> retrievedConcepts = sensorConcepts.getConcepts();
//		int counter = 0;
//		Set<Obs> observations = new HashSet<Obs>();
//		
//		for (Concept retrievedElement : retrievedConcepts){
//				System.out.println("in loop");
//				Obs obs = new Obs();
//				obs.setPerson(person);
//				//change 5090 by retreiveElement.getConceptId();
//				Concept concept = (Concept)Context.getConceptService().getConcept(5090);
//				obs.setObsDatetime(d);
//				obs.setConcept(concept);
//				int height = counter + 170;
//				String val = Integer.toString(height);
//				System.out.println("heigh is " + val);
//				try { 
//					obs.setValueAsString(val);
//				} catch (ParseException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				counter++;
//				enc.addObs(obs);
//				observations.add(obs);
//		}
//		
//		
//		
//		Encounter enc_formed = (Encounter)Context.getEncounterService().saveEncounter(enc);
//		SensorReading sensorReading = new SensorReading();
//		sensorReading.setPatient(patient);
//		SensorMapping sensor = (SensorMapping)Context.getService(SensorMappingService.class).retrieveSensorMapping(10);
//		sensorReading.setSensor(sensor);
//		sensorReading.setEncounter(enc_formed);
//		sensorReading.setObservations(observations);
//		sensorReading.setDate(d);
//		
//		
//		
//		
//		
//		Context.getService(SensorReadingService.class).saveSensorReading(sensorReading);
////		SensorReading sr = (SensorReading)Context.getService(SensorReadingService.class).readSensorReading(encounter_id);
////		
////		System.out.println("date is " + sr.getObservation().getObsDatetime());
//		SensorMapping sensorMapping = new SensorMapping();
////		sensorMapping.setSensor_id(11);
//		sensorMapping.setSensor_name("pulse meter");
//		
//		Context.getService(SensorMappingService.class).saveSensorMapping(sensorMapping);
//		SensorMapping sm = (SensorMapping)Context.getService(SensorMappingService.class).retrieveSensorMapping(12);
//		
//		System.out.println("done" + sm.getSensor_name());
//		
//		/*
//		 * Adding concepts to a sensor
//		 */
//		SensorConceptMapping sensorConceptMapping = new SensorConceptMapping();
//		sensorConceptMapping.setSensor(sm);
//		Concept concept2 = (Concept)Context.getConceptService().getConcept(5092);
//		Set<Concept> concepts = new HashSet<Concept>();
//		
//
//		/*
//		 * Commented out as it requires new pair every time(composite id)
//		 */
////		concepts.add(concept);
////		concepts.add(concept2);
////		sensorConceptMapping.setConcepts(concepts);
////		Context.getService(SensorConceptMappingService.class).saveSensorConceptMapping(sensorConceptMapping);
//		
//	
//		
//		return model;
//	}
//}
