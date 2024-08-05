var config = require("./config");
var axios = require("axios").default;
var url = require("url");
var producer = require("./producer").producer;
var logger = require("./logger").logger;
const { Pool } = require('pg');
const get = require('lodash/get');

const pool = new Pool({
  user: config.DB_USER,
  host: config.DB_HOST,
  database: config.DB_NAME,
  password: config.DB_PASSWORD,
  port: config.DB_PORT,
});

auth_token = config.auth_token;

async function search_case(cnrNumber1, tenantId1, requestinfo) {
  return await axios({
    method: "post",
    url: url.resolve(config.host.case, config.paths.case_search),
    data: {
      "RequestInfo": requestinfo.RequestInfo,
      "tenantId": tenantId1,
      "criteria": [
		{
      "cnrNumber": cnrNumber1,
		}
	]
    },
  });
}

async function search_order(tenantId1, orderId1, requestinfo) {
  return await axios({
    method: "post",
    url: url.resolve(config.host.order, config.paths.order_search),
    data: {
      "RequestInfo": requestinfo.RequestInfo,
      "tenantId": tenantId1,
      "criteria": {
	      "tenantId": tenantId1,
        "id": orderId1
	    }
    },
  });
}

async function search_hearing(tenantId, cnrNumber, requestinfo) {
  return await axios({
    method: "post",
    url: url.resolve(config.host.hearing, config.paths.hearing_search),
    data: {
      "RequestInfo": requestinfo.RequestInfo,
      "criteria": {
	      "tenantId": tenantId
        // "cnrNumber": cnrNumber
	    },
      "pagination": {
        "limit": 10,
        "offset": 0,
        "sortBy": "createdTime",
        "order": "desc"
      }
    },
  });
}

async function search_mdms_order(uniqueIdentifier, schemaCode, tenantID,requestInfo) {
  return await axios({
    method: "post",
    url: url.resolve(config.host.mdms, config.paths.mdms_search),
    data: {
      RequestInfo: requestInfo.RequestInfo,
      MdmsCriteria: {
        tenantId: tenantID,
        schemaCode: schemaCode,
        uniqueIdentifiers: [uniqueIdentifier]
    }
    },
  });
}

async function search_hrms(tenantId, employeeTypes, courtRooms, requestinfo) {
  var params = {
    tenantId: tenantId,
    employeetypes: employeeTypes,
    courtrooms: courtRooms,
    limit:10,
    offset:0,
  };
  return await axios({
    method: "post",
    url: url.resolve(config.host.hrms, config.paths.hrms_search),
    data: {
      RequestInfo: requestinfo.RequestInfo
    },
    params,
  });
}

async function search_individual(tenantId, individualId, requestinfo) {
  var params = {
    tenantId: tenantId,
    limit:10,
    offset:0,
  };
  return await axios({
    method: "post",
    url: url.resolve(config.host.individual, config.paths.individual_search),
    data: {
      RequestInfo: requestinfo.RequestInfo,
      Individual: {
        individualId: individualId
      }
    },
    params,
  });
}

async function search_individual_uuid(tenantId, individualId, requestinfo) {
  var params = {
    tenantId: tenantId,
    limit:10,
    offset:0,
  };
  return await axios({
    method: "post",
    url: url.resolve(config.host.individual, config.paths.individual_search),
    data: {
      RequestInfo: requestinfo.RequestInfo,
      Individual: {
        id:[individualId]
      }
    },
    params,
  });
}

async function search_application(tenantId1, applicationId1, requestinfo) {
  return await axios({
    method: "post",
    url: url.resolve(config.host.application, config.paths.application_search),
    data: {
      "RequestInfo": requestinfo.RequestInfo,
      "tenantId": tenantId1,
      "criteria": {
	      "tenantId": tenantId1,
        "applicationNumber":applicationId1
	    }
    },
  });
}

async function search_sunbirdrc_credential_service(tenantId, code, uuid, requestinfo) {
  return await axios({
    method: "post",
    url: url.resolve(config.host.sunbirdrc_credential_service, config.paths.sunbirdrc_credential_service_search),
    data: {
      RequestInfo: requestinfo.RequestInfo,
      tenantId: tenantId,
      code: code,
      uuid: uuid
    },
  });
}

async function create_pdf(tenantId, key, data, requestinfo) {
  var oj = Object.assign(requestinfo, data);
  return await axios({
    responseType: "stream",
    method: "post",
    url: url.resolve(config.host.pdf, config.paths.pdf_create),
    data: Object.assign(requestinfo, data),
    params: {
      tenantId: tenantId,
      key: key,
    },
  });
}

module.exports = {
  pool,
  create_pdf,  
  search_hrms,
  search_case,
  search_order,
  search_mdms_order,
  search_individual,
  search_hearing,
  search_sunbirdrc_credential_service,
  search_individual_uuid,
  search_application
};
