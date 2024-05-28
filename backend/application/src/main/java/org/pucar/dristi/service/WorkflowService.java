package org.pucar.dristi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.models.Workflow;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.common.contract.workflow.ProcessInstance;
import org.egov.common.contract.workflow.ProcessInstanceRequest;
import org.egov.common.contract.workflow.ProcessInstanceResponse;
import org.egov.common.contract.workflow.State;
import org.egov.tracer.model.CustomException;
import org.pucar.dristi.config.Configuration;
import org.pucar.dristi.repository.ServiceRequestRepository;
import org.pucar.dristi.web.models.Application;
import org.pucar.dristi.web.models.ApplicationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.pucar.dristi.config.ServiceConstants.WORKFLOW_SERVICE_EXCEPTION;

@Component
@Slf4j
public class WorkflowService {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ServiceRequestRepository repository;

    @Autowired
    private Configuration config;


    public void updateWorkflowStatus(ApplicationRequest applicationRequest) {
        Application application = applicationRequest.getApplication();
        try {
                ProcessInstance processInstance = getProcessInstance(application, applicationRequest.getRequestInfo());
                ProcessInstanceRequest workflowRequest = new ProcessInstanceRequest(applicationRequest.getRequestInfo(), Collections.singletonList(processInstance));
                log.info("ProcessInstance Request :: {}", workflowRequest);
                String applicationStatus=callWorkFlow(workflowRequest).getApplicationStatus();
                log.info("Application Status :: {}", applicationStatus);
                application.setStatus(applicationStatus);
            } catch (CustomException e){
                throw e;
            } catch (Exception e) {
                log.error("Error updating workflow status: {}", e.getMessage());
                throw new CustomException(WORKFLOW_SERVICE_EXCEPTION,"Error updating workflow status: "+e.getMessage());
            }
    }
    public State callWorkFlow(ProcessInstanceRequest workflowReq) {
        try {
            StringBuilder url = new StringBuilder(config.getWfHost().concat(config.getWfTransitionPath()));
            Object optional = repository.fetchResult(url, workflowReq);
            log.info("Workflow Response :: {}", optional);
            ProcessInstanceResponse response = mapper.convertValue(optional, ProcessInstanceResponse.class);
            return response.getProcessInstances().get(0).getState();
        } catch (CustomException e){
            throw e;
        } catch (Exception e) {
            log.error("Error calling workflow: {}", e.getMessage());
            throw new CustomException(WORKFLOW_SERVICE_EXCEPTION,e.getMessage());
        }
    }

    private ProcessInstance getProcessInstance(Application application, RequestInfo requestInfo) {
        try {
            Workflow workflow = application.getWorkflow();
            ProcessInstance processInstance = new ProcessInstance();
            processInstance.setBusinessId(application.getApplicationNumber()); //TODO CHECK BUSINESS ID
            processInstance.setAction(workflow.getAction());
            processInstance.setModuleName("pucar"); // FIXME
            processInstance.setTenantId(application.getTenantId());
            processInstance.setBusinessService("application"); // FIXME
            processInstance.setDocuments(workflow.getDocuments());
            processInstance.setComment(workflow.getComments());
            if (!CollectionUtils.isEmpty(workflow.getAssignes())) {
                List<User> users = new ArrayList<>();
                workflow.getAssignes().forEach(uuid -> {
                    User user = new User();
                    user.setUuid(uuid);
                    users.add(user);
                });
                processInstance.setAssignes(users);
            }
            return processInstance;
        } catch (CustomException e){
            throw e;
        } catch (Exception e) {
            log.error("Error getting process instance for Application: {}", e.getMessage());
            throw new CustomException(WORKFLOW_SERVICE_EXCEPTION,e.getMessage());
        }
    }

//    public ProcessInstance getCurrentWorkflow(RequestInfo requestInfo, String tenantId, String businessId) {
//        try {
//            RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();
//            StringBuilder url = getSearchURLForProcessInstanceWithParams(tenantId, businessId);
//            Object res = repository.fetchResult(url, requestInfoWrapper);
//            ProcessInstanceResponse response = mapper.convertValue(res, ProcessInstanceResponse.class);
//            if (response != null && !CollectionUtils.isEmpty(response.getProcessInstances()) && response.getProcessInstances().get(0) != null)
//                return response.getProcessInstances().get(0);
//            return null;
//        } catch (CustomException e){
//            throw e;
//        } catch (Exception e) {
//            log.error("Error getting current workflow: {}", e.getMessage());
//            throw new CustomException(WORKFLOW_SERVICE_EXCEPTION, e.getMessage());
//        }
//    }
//    private BusinessService getBusinessService(CourtCase courtCase, RequestInfo requestInfo) {
//        try {
//            String tenantId = courtCase.getTenantId();
//            StringBuilder url = getSearchURLWithParams(tenantId, "CASE");
//            RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();
//            Object result = repository.fetchResult(url, requestInfoWrapper);
//            BusinessServiceResponse response = mapper.convertValue(result, BusinessServiceResponse.class);
//            if (CollectionUtils.isEmpty(response.getBusinessServices()))
//                throw new CustomException();
//            return response.getBusinessServices().get(0);
//        } catch (CustomException e){
//            throw e;
//        } catch (Exception e) {
//            log.error("Error getting business service: {}", e.getMessage());
//            throw new CustomException(WORKFLOW_SERVICE_EXCEPTION, e.getMessage());
//        }
//    }
//    private StringBuilder getSearchURLWithParams(String tenantId, String businessService) {
//        StringBuilder url = new StringBuilder(config.getWfHost());
//        url.append(config.getWfBusinessServiceSearchPath());
//        url.append("?tenantId=").append(tenantId);
//        url.append("&businessServices=").append(businessService);
//        return url;
//    }
//    private StringBuilder getSearchURLForProcessInstanceWithParams(String tenantId, String businessService) {
//        StringBuilder url = new StringBuilder(config.getWfHost());
//        url.append(config.getWfProcessInstanceSearchPath());
//        url.append("?tenantId=").append(tenantId);
//        url.append("&businessIds=").append(businessService);
//        return url;
//    }
//    public ProcessInstanceRequest getProcessInstanceRegistrationPayment(CaseRequest updateRequest) {
//        try {
//            CourtCase application = updateRequest.getCases().get(0);
//            ProcessInstance process = ProcessInstance.builder()
//                    .businessService("ADV")
//                    .businessId(application.getFilingNumber())
//                    .comment("Payment for Case registration processed")
//                    .moduleName("cases-services")
//                    .tenantId(application.getTenantId())
//                    .action("PAY")
//                    .build();
//            return ProcessInstanceRequest.builder()
//                    .requestInfo(updateRequest.getRequestInfo())
//                    .processInstances(Arrays.asList(process))
//                    .build();
//        } catch (CustomException e){
//            throw e;
//        } catch (Exception e) {
//            log.error("Error getting process instance for case registration payment: {}", e.getMessage());
//            throw new CustomException(WORKFLOW_SERVICE_EXCEPTION, e.getMessage());
//        }
//    }
//
//    public Workflow getWorkflowFromProcessInstance(ProcessInstance processInstance) {
//        if(processInstance == null) {
//            return null;
//        }
//        return Workflow.builder().action(processInstance.getState().getState()).comments(processInstance.getComment()).build();
//    }
}