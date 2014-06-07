package org.openmrs.module.sensorreading.utils;

import java.io.Serializable;

import org.openmrs.Concept;
import org.openmrs.module.sensorreading.SensorMapping;

public class SensorConceptUtil implements Serializable{

	private static final long serialVersionUID = 1L;
	private SensorMapping sensor;
	private Concept concept;
	
	public SensorMapping getSensor() {
		return sensor;
	}
	public void setSensor(SensorMapping sensor) {
		this.sensor = sensor;
	}
	public Concept getConcept() {
		return concept;
	}
	public void setConcept(Concept concept) {
		this.concept = concept;
	}

}
