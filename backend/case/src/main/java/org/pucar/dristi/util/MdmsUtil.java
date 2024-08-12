package org.pucar.dristi.util;

<<<<<<< HEAD
import static org.pucar.dristi.config.ServiceConstants.ERROR_WHILE_FETCHING_FROM_MDMS;
=======
import com.fasterxml.jackson.databind.ObjectMapper;
import org.pucar.dristi.config.Configuration;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
>>>>>>> main

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

<<<<<<< HEAD
import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.MdmsResponse;
import org.egov.mdms.model.ModuleDetail;
import org.pucar.dristi.config.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
=======
import static org.pucar.dristi.config.ServiceConstants.*;
>>>>>>> main

@Slf4j
@Component
public class MdmsUtil {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private Configuration configs;

	public Map<String, Map<String, JSONArray>> fetchMdmsData(RequestInfo requestInfo, String tenantId,
			String moduleName, List<String> masterNameList) {
		StringBuilder uri = new StringBuilder();
		uri.append(configs.getMdmsHost()).append(configs.getMdmsEndPoint());
		MdmsCriteriaReq mdmsCriteriaReq = getMdmsRequest(requestInfo, tenantId, moduleName, masterNameList);
<<<<<<< HEAD
		log.info("MDMS Criteria :: {}",mdmsCriteriaReq);
		Object response;
=======
		Object response = new HashMap<>();
		Integer rate = 0;
>>>>>>> main
		MdmsResponse mdmsResponse = new MdmsResponse();
		try {
			response = restTemplate.postForObject(uri.toString(), mdmsCriteriaReq, Map.class);
			mdmsResponse = mapper.convertValue(response, MdmsResponse.class);
		} catch (Exception e) {
			log.error(ERROR_WHILE_FETCHING_FROM_MDMS, e);
		}

		return mdmsResponse.getMdmsRes();
<<<<<<< HEAD
=======
		// log.info(ulbToCategoryListMap.toString());
>>>>>>> main
	}

	private MdmsCriteriaReq getMdmsRequest(RequestInfo requestInfo, String tenantId, String moduleName,
			List<String> masterNameList) {
		List<MasterDetail> masterDetailList = new ArrayList<>();
		for (String masterName : masterNameList) {
			MasterDetail masterDetail = new MasterDetail();
			masterDetail.setName(masterName);
			masterDetailList.add(masterDetail);
		}

		ModuleDetail moduleDetail = new ModuleDetail();
		moduleDetail.setMasterDetails(masterDetailList);
		moduleDetail.setModuleName(moduleName);
		List<ModuleDetail> moduleDetailList = new ArrayList<>();
		moduleDetailList.add(moduleDetail);

		MdmsCriteria mdmsCriteria = new MdmsCriteria();
		mdmsCriteria.setTenantId(tenantId.split("\\.")[0]);
		mdmsCriteria.setModuleDetails(moduleDetailList);

		MdmsCriteriaReq mdmsCriteriaReq = new MdmsCriteriaReq();
		mdmsCriteriaReq.setMdmsCriteria(mdmsCriteria);
		mdmsCriteriaReq.setRequestInfo(requestInfo);

		return mdmsCriteriaReq;
	}
}