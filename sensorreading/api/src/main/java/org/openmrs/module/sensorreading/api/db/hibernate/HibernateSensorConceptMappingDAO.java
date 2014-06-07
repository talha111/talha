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
package org.openmrs.module.sensorreading.api.db.hibernate;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.openmrs.Concept;
import org.openmrs.module.sensorreading.SensorConceptMapping;
import org.openmrs.module.sensorreading.api.db.SensorConceptMappingDAO;
import org.openmrs.module.sensorreading.api.db.SensorReadingDAO;
import org.openmrs.module.sensorreading.utils.SensorConceptUtil;

/**
 * It is a default implementation of  {@link SensorReadingDAO}.
 */
public class HibernateSensorConceptMappingDAO implements SensorConceptMappingDAO {
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private SessionFactory sessionFactory;
	
	/**
     * @param sessionFactory the sessionFactory to set
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
	    this.sessionFactory = sessionFactory;
    }
    
	/**
     * @return the sessionFactory
     */
    public SessionFactory getSessionFactory() {
	    return sessionFactory;
    }

	@Override
	public SensorConceptMapping setSensorConceptMapping(
			SensorConceptMapping sensorConceptMapping) {
		Set<Concept> concepts =  sensorConceptMapping.getConcepts();
		for(Concept concept : concepts){
			SensorConceptUtil scu = new SensorConceptUtil();
			scu.setConcept(concept);
			scu.setSensor(sensorConceptMapping.getSensor());
			sessionFactory.getCurrentSession().save(scu);
		}
		return sensorConceptMapping;
	}
	
//TODO write get method
	@Override
	public SensorConceptMapping getSensorConceptMapping(int sensor_id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SensorConceptMapping deleteSensorConceptMapping(
			SensorConceptMapping sensorConceptMapping) {
		// TODO Auto-generated method stub
		Set<Concept> concepts =  sensorConceptMapping.getConcepts();
		for(Concept concept : concepts){
			SensorConceptUtil scu = new SensorConceptUtil();
			// Adding sensor_id and concept_id (check)
			scu.setConcept(concept);
			scu.setSensor(sensorConceptMapping.getSensor());
			scu.setConcept(concept);
			scu.setSensor(sensorConceptMapping.getSensor());
			sessionFactory.getCurrentSession().delete(scu);
		}
		return sensorConceptMapping;
	}

	
}