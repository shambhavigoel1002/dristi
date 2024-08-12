package org.pucar.dristi.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.response.ResponseInfo;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

/**
 * AdvocateClerkResponse
 */
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-04T05:55:27.937918+05:30[Asia/Kolkata]")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdvocateClerkListResponse {
	@JsonProperty("responseInfo")
	@Valid
	private ResponseInfo responseInfo = null;

	@JsonProperty("clerks")
	@Valid
<<<<<<< HEAD:backend/advocate/src/main/java/org/pucar/dristi/web/models/AdvocateClerkListResponse.java
	private List<AdvocateClerkSearchCriteria> clerks = null;
=======
	private List<AdvocateClerk> clerks = new ArrayList<>();
>>>>>>> main:backend/advocate/src/main/java/org/pucar/web/models/AdvocateClerkResponse.java

	@JsonProperty("pagination")

	@Valid
	private Pagination pagination = null;

<<<<<<< HEAD:backend/advocate/src/main/java/org/pucar/dristi/web/models/AdvocateClerkListResponse.java
=======
	public AdvocateClerkResponse addClerksItem(AdvocateClerk clerksItem) {
		this.clerks.add(clerksItem);
		return this;
	}
>>>>>>> main:backend/advocate/src/main/java/org/pucar/web/models/AdvocateClerkResponse.java

}
