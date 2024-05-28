package org.pucar.dristi.validator;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pucar.dristi.repository.ApplicationRepository;
import org.pucar.dristi.web.models.Application;
import org.pucar.dristi.web.models.ApplicationRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ApplicationValidatorTest {

    @Mock
    private ApplicationRepository repository;

    @InjectMocks
    private ApplicationValidator validator;

    private ApplicationRequest applicationRequest;
    private Application application;
    private RequestInfo requestInfo;

    @BeforeEach
    public void setUp() {
        requestInfo = new RequestInfo();
        application = new Application();
        applicationRequest = new ApplicationRequest();
        applicationRequest.setRequestInfo(requestInfo);
        applicationRequest.setApplication(application);
    }

    @Test
    public void testValidateApplication_Success() {
        User user = new User();
        List<UUID> onBehalfOf = new ArrayList<>();
        onBehalfOf.add(UUID.randomUUID());
        application.setTenantId("tenantId");
        application.setCreatedDate("2024-05-01");
        application.setCreatedBy(UUID.randomUUID());
        requestInfo.setUserInfo(user); // Simulating non-empty user info
        application.setOnBehalfOf(onBehalfOf);
        application.setCnrNumber("cnrNumber");
        application.setFilingNumber("filingNumber");
        application.setReferenceId(UUID.randomUUID());

        assertDoesNotThrow(() -> validator.validateApplication(applicationRequest));
    }

    @Test
    public void testValidateApplication_MissingTenantId() {
        Exception exception = assertThrows(CustomException.class, () -> validator.validateApplication(applicationRequest));
        assertEquals("tenantId is mandatory for creating application", exception.getMessage());
    }

    @Test
    public void testValidateApplication_MissingCreatedDate() {
        application.setTenantId("tenantId");

        Exception exception = assertThrows(CustomException.class, () -> validator.validateApplication(applicationRequest));
        assertEquals("createdDate is mandatory for creating application", exception.getMessage());
    }

    @Test
    public void testValidateApplication_MissingUserInfo() {
        application.setTenantId("tenantId");
        application.setCreatedDate("2024-05-01");
        application.setCreatedBy(UUID.randomUUID());

        Exception exception = assertThrows(CustomException.class, () -> validator.validateApplication(applicationRequest));
        assertEquals("user info is mandatory for creating application", exception.getMessage());
    }

    @Test
    public void testValidateApplicationExistence_Success() {
        application.setId(UUID.randomUUID());
        application.setCnrNumber("cnrNumber");
        application.setFilingNumber("filingNumber");
        application.setReferenceId(UUID.randomUUID());
        List<Application> existingApplications = new ArrayList<>();
        existingApplications.add(application);

        when(repository.getApplications(any(), anyString(), anyString(), any(), eq(null), eq(null)))
                .thenReturn(existingApplications);

        Application result = validator.validateApplicationExistence(requestInfo, application);
        assertEquals(application, result);
    }

    @Test
    public void testValidateApplicationExistence_MissingId() {
        application.setCnrNumber("cnrNumber");
        application.setFilingNumber("filingNumber");
        application.setReferenceId(UUID.randomUUID());

        Exception exception = assertThrows(CustomException.class, () -> validator.validateApplicationExistence(requestInfo, application));
        assertEquals("id is mandatory for updating application", exception.getMessage());
    }

    @Test
    public void testValidateApplicationExistence_ApplicationDoesNotExist() {
        application.setId(UUID.randomUUID());
        application.setCnrNumber("cnrNumber");
        application.setFilingNumber("filingNumber");
        application.setReferenceId(UUID.randomUUID());

        when(repository.getApplications(any(), anyString(), anyString(), any(), any(), any()))
                .thenReturn(null);

        Exception exception = assertThrows(CustomException.class, () -> validator.validateApplicationExistence(requestInfo, application));
        assertEquals("Application does not exist", exception.getMessage());
    }
}