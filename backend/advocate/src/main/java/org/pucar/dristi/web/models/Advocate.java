package org.pucar.dristi.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.models.AuditDetails;
import org.egov.common.contract.models.Document;
import org.egov.common.contract.models.Workflow;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Advocate
 */
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-04T05:55:27.937918+05:30[Asia/Kolkata]")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Advocate {

	@JsonProperty("id")
	@Valid
	private UUID id = null;

	@JsonProperty("tenantId")
	@NotNull
	@Size(min = 2, max = 128)
	private String tenantId = null;

	@JsonProperty("applicationNumber")
	@Size(min = 2, max = 64)
	private String applicationNumber = null;

	@JsonProperty("status")
	private String status=null;

	@JsonProperty("barRegistrationNumber")
	@Size(min = 2, max = 64)
	private String barRegistrationNumber = null;

	@JsonProperty("advocateType")
	@Size(min = 2, max = 64)
	private String advocateType = null;

	@JsonProperty("organisationID")
	@Valid
	private UUID organisationID = null;

	@JsonProperty("individualId")
	private String individualId = null;

	@JsonProperty("isActive")
	private Boolean isActive = true;

	@JsonProperty("workflow")
	@Valid
	private Workflow workflow = null;

	@JsonProperty("documents")
	@Valid
	private List<Document> documents = new ArrayList<>();

	@JsonProperty("auditDetails")
	@Valid
	private AuditDetails auditDetails = null;

	@JsonProperty("additionalDetails")
	private Object additionalDetails = null;

	public Advocate addDocumentsItem(Document documentsItem) {
		this.documents.add(documentsItem);
		return this;
	}

}
