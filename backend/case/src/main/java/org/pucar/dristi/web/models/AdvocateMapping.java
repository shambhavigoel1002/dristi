package org.pucar.dristi.web.models;

<<<<<<< HEAD
import java.util.List;

import org.egov.common.contract.models.AuditDetails;
import org.egov.common.contract.models.Document;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
=======
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
>>>>>>> main
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
<<<<<<< HEAD
=======
import org.egov.common.contract.models.AuditDetails;
import org.egov.common.contract.models.Document;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
>>>>>>> main

/**
 * AdvocateMapping
 */
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-19T15:42:53.131831400+05:30[Asia/Calcutta]")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdvocateMapping   {
        @JsonProperty("id")

<<<<<<< HEAD
        @Valid         private String id = null;

        @JsonProperty("tenantId")
        //  @NotNull
=======
        @Size(min=2,max=64)         private String id = null;

        @JsonProperty("tenantId")
          @NotNull
>>>>>>> main

                private String tenantId = null;

        @JsonProperty("advocateId")

                private String advocateId = null;

        @JsonProperty("caseId")

                private String caseId = null;

        @JsonProperty("representing")
          @Valid
<<<<<<< HEAD
       // @Size(min=1)
        private List<Party> representing = null;
=======
        @Size(min=1)         private List<Party> representing = null;
>>>>>>> main

        @JsonProperty("isActive")

                private Boolean isActive = true;

        @JsonProperty("documents")
          @Valid
                private List<Document> documents = null;

        @JsonProperty("auditDetails")

          @Valid
                private AuditDetails auditDetails = null;

        @JsonProperty("additionalDetails")

                private Object additionalDetails = null;


<<<<<<< HEAD
=======
        public AdvocateMapping addRepresentingItem(Party representingItem) {
            if (this.representing == null) {
            this.representing = new ArrayList<>();
            }
        this.representing.add(representingItem);
        return this;
        }

        public AdvocateMapping addDocumentsItem(Document documentsItem) {
            if (this.documents == null) {
            this.documents = new ArrayList<>();
            }
        this.documents.add(documentsItem);
        return this;
        }

>>>>>>> main
}
