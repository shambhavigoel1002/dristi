package org.pucar.dristi.repository.rowmapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.models.AuditDetails;
import org.egov.tracer.model.CustomException;
import org.postgresql.util.PGobject;
import org.pucar.dristi.web.models.AdvocateMapping;
import org.pucar.dristi.web.models.CourtCase;
import org.pucar.dristi.web.models.StatuteSection;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.*;

@Component
@Slf4j
public class RepresentativeRowMapper implements ResultSetExtractor<Map<UUID, List<AdvocateMapping>>> {
    public Map<UUID, List<AdvocateMapping>> extractData(ResultSet rs) {
        Map<UUID, List<AdvocateMapping>> advocateMap = new LinkedHashMap<>();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            while (rs.next()) {
                String id = rs.getString("casenumber");
                UUID uuid = UUID.fromString(id != null ? id : "00000000-0000-0000-0000-000000000000");

                Long lastModifiedTime = rs.getLong("lastmodifiedtime");

                AuditDetails auditdetails = AuditDetails.builder()
                        .createdBy(rs.getString("createdby"))
                        .createdTime(rs.getLong("createdtime"))
                        .lastModifiedBy(rs.getString("lastmodifiedby"))
                        .lastModifiedTime(lastModifiedTime)
                        .build();
                AdvocateMapping advocateMapping = AdvocateMapping.builder()
                        .id(rs.getString("id"))
                        .advocateId(rs.getString("casenumber"))
                        .tenantId(rs.getString("tenantid"))
                        .auditDetails(auditdetails)
                        .build();

                PGobject pgObject = (PGobject) rs.getObject("additionalDetails");
                if (pgObject != null)
                    advocateMapping.setAdditionalDetails(objectMapper.readTree(pgObject.getValue()));

                if (advocateMap.containsKey(uuid)) {
                    advocateMap.get(uuid).add(advocateMapping);
                } else {
                    List<AdvocateMapping> advocateMappings = new ArrayList<>();
                    advocateMappings.add(advocateMapping);
                    advocateMap.put(uuid, advocateMappings);
                }
            }
        } catch (Exception e) {
            log.error("Error occurred while processing Case ResultSet: {}", e.getMessage());
            throw new CustomException("ROW_MAPPER_EXCEPTION", "Error occurred while processing Case ResultSet: " + e.getMessage());
        }
        return advocateMap;
    }
}
