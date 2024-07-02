package org.pucar.dristi.repository.querybuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.common.contract.models.AuditDetails;
import org.egov.tracer.model.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HearingQueryBuilderTest {

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private HearingQueryBuilder hearingQueryBuilder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getHearingSearchQuery_NoCriteria() {
        // Arrange
        List<Object> preparedStmtList = new ArrayList<>();
        String cnrNumber = null;
        String applicationNumber = null;
        String hearingId = null;
        String filingNumber = null;
        String tenantId = null;
        LocalDate fromDate = null;
        LocalDate toDate = null;
        Integer limit = 10;
        Integer offset = 0;
        String sortBy = null;

        // Act
        String query = hearingQueryBuilder.getHearingSearchQuery(preparedStmtList, cnrNumber, applicationNumber, hearingId, filingNumber, tenantId, fromDate, toDate, limit, offset, sortBy);

        // Assert
        assertNotNull(query);
        assertTrue(query.contains("SELECT * FROM dristi_hearing WHERE 1=1"));
        assertTrue(query.contains("ORDER BY id"));
        assertTrue(query.contains("LIMIT ?"));
        assertTrue(query.contains("OFFSET ?"));
        assertEquals(2, preparedStmtList.size());
        assertEquals(10, preparedStmtList.get(0));
        assertEquals(0, preparedStmtList.get(1));
    }

    @Test
    void getHearingSearchQuery_WithCriteria() {
        // Arrange
        List<Object> preparedStmtList = new ArrayList<>();
        String cnrNumber = "CNR123";
        String applicationNumber = "APP456";
        String hearingId = "HEARING789";
        String filingNumber = "FILE123";
        String tenantId = "tenant1";
        LocalDate fromDate = LocalDate.of(2023, 1, 1);
        LocalDate toDate = LocalDate.of(2023, 12, 31);
        Integer limit = 10;
        Integer offset = 0;
        String sortBy = "startTime";

        // Act
        String query = hearingQueryBuilder.getHearingSearchQuery(preparedStmtList, cnrNumber, applicationNumber, hearingId, filingNumber, tenantId, fromDate, toDate, limit, offset, sortBy);

        // Assert
        assertNotNull(query);
        assertTrue(query.contains("AND cnrNumbers @> ?::jsonb"));
        assertTrue(query.contains("AND applicationNumbers @> ?::jsonb"));
        assertTrue(query.contains("AND hearingid = ?"));
        assertTrue(query.contains("AND filingNumber @> ?::jsonb"));
        assertTrue(query.contains("AND tenantId = ?"));
        assertTrue(query.contains("AND startTime >= ?"));
        assertTrue(query.contains("AND startTime <= ?"));
        assertTrue(query.contains("ORDER BY startTime DESC"));
        assertEquals(9, preparedStmtList.size());
    }

    @Test
    void getHearingSearchQuery_Exception() {
        // Arrange
        List<Object> preparedStmtList = null;
        String cnrNumber = "CNR123";
        String applicationNumber = "APP456";
        String hearingId = "HEARING789";
        String filingNumber = "FILE123";
        String tenantId = "tenant1";
        LocalDate fromDate = LocalDate.of(2023, 1, 1);
        LocalDate toDate = LocalDate.of(2023, 12, 31);
        Integer limit = 10;
        Integer offset = 0;
        String sortBy = "startTime";

        // Act & Assert
        assertThrows(CustomException.class, () -> hearingQueryBuilder.getHearingSearchQuery(preparedStmtList, cnrNumber, applicationNumber, hearingId, filingNumber, tenantId, fromDate, toDate, limit, offset, sortBy));
    }

    @Test
    void getDocumentSearchQuery() {
        // Arrange
        List<String> ids = List.of("doc1", "doc2");
        List<Object> preparedStmtList = new ArrayList<>();

        // Act
        String query = hearingQueryBuilder.getDocumentSearchQuery(ids, preparedStmtList);

        // Assert
        assertNotNull(query);
        assertTrue(query.contains("FROM dristi_hearing_document doc"));
        assertTrue(query.contains("WHERE doc.hearingid IN (?,?)"));
        assertEquals(2, preparedStmtList.size());
        assertEquals("doc1", preparedStmtList.get(0));
        assertEquals("doc2", preparedStmtList.get(1));
    }

    @Test
    void getDocumentSearchQuery_EmptyIds() {
        // Arrange
        List<String> ids = new ArrayList<>();
        List<Object> preparedStmtList = new ArrayList<>();

        // Act
        String query = hearingQueryBuilder.getDocumentSearchQuery(ids, preparedStmtList);

        // Assert
        assertNotNull(query);
        assertTrue(query.contains("FROM dristi_hearing_document doc"));
        assertFalse(query.contains("WHERE doc.hearingid IN"));
        assertTrue(preparedStmtList.isEmpty());
    }

    @Test
    void getDocumentSearchQuery_Exception() {
        // Arrange
        List<String> ids = null;
        List<Object> preparedStmtList = new ArrayList<>();

        // Act & Assert
        assertThrows(CustomException.class, () -> hearingQueryBuilder.getDocumentSearchQuery(ids, preparedStmtList));
    }

    @Test
    void buildUpdateTranscriptQuery_Success() throws JsonProcessingException {
        // Arrange
        List<Object> preparedStmtList = new ArrayList<>();
        String hearingId = "hearing123";
        String tenantId = "tenant1";
        List<String> transcriptList = List.of("transcript1", "transcript2");
        AuditDetails auditDetails = new AuditDetails();
        auditDetails.setLastModifiedBy("user1");
        auditDetails.setLastModifiedTime(123456789L);

        String transcriptJson = "[\"transcript1\",\"transcript2\"]";
        when(mapper.writeValueAsString(transcriptList)).thenReturn(transcriptJson);

        // Act
        String query = hearingQueryBuilder.buildUpdateTranscriptQuery(preparedStmtList, hearingId, tenantId, transcriptList, auditDetails);

        // Assert
        assertEquals("UPDATE dristi_hearing SET transcript = ?::jsonb , lastModifiedBy = ? , lastModifiedTime = ? WHERE hearingId = ? AND tenantId = ?", query);
        assertEquals(5, preparedStmtList.size());
        assertEquals(transcriptJson, preparedStmtList.get(0));
        assertEquals("user1", preparedStmtList.get(1));
        assertEquals(123456789L, preparedStmtList.get(2));
        assertEquals(hearingId, preparedStmtList.get(3));
        assertEquals(tenantId, preparedStmtList.get(4));
    }

    @Test
    void buildUpdateTranscriptQuery_JsonProcessingException() throws JsonProcessingException {
        // Arrange
        List<Object> preparedStmtList = new ArrayList<>();
        String hearingId = "hearing123";
        String tenantId = "tenant1";
        List<String> transcriptList = List.of("transcript1", "transcript2");
        AuditDetails auditDetails = new AuditDetails();
        auditDetails.setLastModifiedBy("user1");
        auditDetails.setLastModifiedTime(123456789L);

        when(mapper.writeValueAsString(transcriptList)).thenThrow(new JsonProcessingException("Error") {});

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> hearingQueryBuilder.buildUpdateTranscriptQuery(preparedStmtList, hearingId, tenantId, transcriptList, auditDetails));

        assertEquals("Error parsing transcript list to JSON : Error", exception.getMessage());
        verify(mapper, times(1)).writeValueAsString(transcriptList);
        assertTrue(preparedStmtList.isEmpty());
    }
}