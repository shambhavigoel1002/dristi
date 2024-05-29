package org.pucar.dristi.validators;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import net.minidev.json.JSONArray;
import org.apache.kafka.common.protocol.types.Field;
import org.egov.common.contract.models.Document;
import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.egov.common.contract.request.RequestInfo;
import org.pucar.dristi.config.Configuration;
import org.pucar.dristi.repository.CaseRepository;
import org.pucar.dristi.service.IndividualService;
import org.pucar.dristi.util.AdvocateUtil;
import org.pucar.dristi.util.FileStoreUtil;
import org.pucar.dristi.util.MdmsUtil;
import org.pucar.dristi.web.models.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.print.Doc;
import java.time.LocalDate;
import java.util.*;

@ExtendWith(MockitoExtension.class)
public class CaseRegistrationValidatorTest {

    @Mock
    private IndividualService individualService;
    @Mock
    private CaseRepository caseRepository;

    @InjectMocks
    private CaseRegistrationValidator validator;

    @Mock
    private MdmsUtil mdmsUtil;

    @Mock
    private FileStoreUtil fileStoreUtil;

    @Mock
    private AdvocateUtil advocateUtil;

    @Mock
    private Configuration configuration;

    @BeforeEach
    void setUp() {
        // Setup done before each test
    }

    @Test
    void testValidateCaseRegistration_WithValidData() {
        // Creating RequestInfo with necessary user info
        RequestInfo requestInfo = new RequestInfo();
        User userInfo = new User();
        userInfo.setId(1234L);
        userInfo.setUserName("test-user");
        requestInfo.setUserInfo(userInfo);

        // Setting up CaseRequest with valid data
        CaseRequest request = new CaseRequest();
        request.setRequestInfo(requestInfo);

        CourtCase courtCase = new CourtCase();
        courtCase.setTenantId("pg");
        courtCase.setCaseCategory("civil");

        Document document = new Document();
        document.setFileStore("123");
        courtCase.setDocuments(List.of(document));

        AdvocateMapping advocateMapping = new AdvocateMapping();
        advocateMapping.setAdvocateId("123");
        courtCase.setRepresentatives(List.of(advocateMapping));

        Party party = new Party();
        party.setIndividualId("123");
        courtCase.setLitigants(List.of(party));

        courtCase.setStatutesAndSections(List.of(new StatuteSection()));
        courtCase.setFilingDate(LocalDate.parse("2021-12-12"));

        request.setCases(courtCase);

        Map<String, Map<String, JSONArray>> mdmsRes = new HashMap<>();
        mdmsRes.put("case", new HashMap<>());

        List<String> masterList = new ArrayList<>();
        masterList.add("ComplainantType");
        masterList.add("CaseCategory");
        masterList.add("PaymentMode");
        masterList.add("ResolutionMechanism");

        // Setting necessary stubbings to lenient
        lenient().when(mdmsUtil.fetchMdmsData(requestInfo, "pg", "case", masterList)).thenReturn(mdmsRes);
        lenient().when(individualService.searchIndividual(requestInfo, "123")).thenReturn(true);
        lenient().when(fileStoreUtil.fileStore("pg", "123")).thenReturn(true);
        lenient().when(advocateUtil.fetchAdvocateDetails(requestInfo, "123")).thenReturn(true);

        // Validate the case registration
        assertDoesNotThrow(() -> validator.validateCaseRegistration(request));
    }



    @Test
    void testValidateCaseRegistration_WithMissingTenantId() {
        CaseRequest request = new CaseRequest();
        request.setRequestInfo(new RequestInfo());
        CourtCase courtCase = new CourtCase();
        request.setCases(courtCase);

        Exception exception = assertThrows(CustomException.class, () -> validator.validateCaseRegistration(request));
    }

    @Test
    void testValidateApplicationExistence_ExistingApplication() {
        CourtCase courtCase = new CourtCase();
        courtCase.setTenantId("pg");
        courtCase.setCaseCategory("civil");
        Document document = new Document();
        document.setFileStore("123");
        courtCase.setDocuments(List.of(document));
        AdvocateMapping advocateMapping = new AdvocateMapping();
        advocateMapping.setAdvocateId("123");
        courtCase.setRepresentatives(List.of(advocateMapping));
        Party party = new Party();
        party.setIndividualId("123");
        courtCase.setLitigants(List.of(party));
        courtCase.setStatutesAndSections(List.of(new StatuteSection()));
        courtCase.setFilingDate(LocalDate.parse("2021-12-12"));
        Map<String, Map<String, JSONArray>> mdmsRes = new HashMap<>();
        mdmsRes.put("case", new HashMap<>());
        List<String> masterList = new ArrayList<>();
        masterList.add("ComplainantType");
        masterList.add("CaseCategory");
        masterList.add("PaymentMode");
        masterList.add("ResolutionMechanism");

        when(mdmsUtil.fetchMdmsData(new RequestInfo(),"pg","case", masterList)).thenReturn(mdmsRes);

        when(individualService.searchIndividual(new RequestInfo(), "123")).thenReturn(true);
        when(fileStoreUtil.fileStore("pg","123")).thenReturn(true);
        when(advocateUtil.fetchAdvocateDetails(new RequestInfo(), "123")).thenReturn(true);
        when(configuration.getCaseBusinessServiceName()).thenReturn("case");

        when(caseRepository.getApplications(any())).thenReturn((List.of(CaseCriteria.builder().filingNumber(courtCase.getFilingNumber()).caseId(String.valueOf(courtCase.getId()))
                .cnrNumber(courtCase.getCnrNumber()).courtCaseNumber(courtCase.getCourCaseNumber()).build())));

        Boolean result = validator.validateApplicationExistence(courtCase, new RequestInfo());
        assertTrue(result);
    }

    @Test
    void testValidateApplicationExistence_NoExistingApplication() {
        CourtCase courtCase = new CourtCase();
        courtCase.setFilingNumber("Filing123");

       lenient().when(caseRepository.getApplications(any())).thenReturn(new ArrayList<>());

        Exception exception = assertThrows(CustomException.class, () -> validator.validateApplicationExistence(courtCase, new RequestInfo()));
    }
}
