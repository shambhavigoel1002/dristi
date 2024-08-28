import { CloseSvg, InfoCard } from "@egovernments/digit-ui-components";
import React, { useState, useMemo, useEffect } from "react";
import Modal from "./Modal";
import { Button } from "@egovernments/digit-ui-react-components";
import { FileUploadIcon } from "../icons/svgIndex";
import { Urls } from "../hooks";

const Heading = (props) => {
  return <h1 className="heading-m">{props.label}</h1>;
};

const CloseBtn = (props) => {
  return (
    <div onClick={props?.onClick} style={{ height: "100%", display: "flex", alignItems: "center", paddingRight: "20px", cursor: "pointer" }}>
      <CloseSvg />
    </div>
  );
};

function ESignSignatureModal({
  t,
  handleIssueOrder,
  handleGoBackSignatureModal,
  saveOnsubmitLabel,
  doctype,
  documentSubmission,
  formUploadData,
  isSigned,
  setIsSigned,
}) {
  const { handleEsign, checkSignStatus } = Digit.Hooks.orders.useESign();
  const [formData, setFormData] = useState({}); // storing the file upload data
  const [openUploadSignatureModal, setOpenUploadSignatureModal] = useState(false);
  const UploadSignatureModal = window?.Digit?.ComponentRegistryService?.getComponent("UploadSignatureModal");
  const [fileStoreId, setFileStoreId] = useState("c162c182-103f-463e-99b6-18654ed7a5b1"); // have to set the uploaded fileStoreID
  const [pageModule, setPageModule] = useState("en");
  const tenantId = window?.Digit.ULBService.getCurrentTenantId();
  const uri = `${window.location.origin}${Urls.FileFetchById}?tenantId=${tenantId}&fileStoreId=${fileStoreId}`;
  const { uploadDocuments } = Digit.Hooks.orders.useDocumentUpload();
  const name = "Signature";
  const uploadModalConfig = useMemo(() => {
    return {
      key: "uploadSignature",
      populators: {
        inputs: [
          {
            name: name,
            documentHeader: "Signature",
            type: "DragDropComponent",
            uploadGuidelines: "Ensure the image is not blurry and under 5MB.",
            maxFileSize: 5,
            maxFileErrorMessage: "CS_FILE_LIMIT_5_MB",
            fileTypes: ["JPG", "PNG", "JPEG"],
            isMultipleUpload: false,
          },
        ],
        validation: {},
      },
    };
  }, [name]);

  const onSelect = (key, value) => {
    if (value?.Signature === null) {
      setFormData({});
      setIsSigned(false);
    } else {
      setFormData((prevData) => ({
        ...prevData,
        [key]: value,
      }));
    }
  };

  // check the data from upload
  useEffect(() => {
    const upload = async () => {
      if (formData?.uploadSignature?.Signature?.length > 0) {
        // const uploadedFileId = await uploadDocuments(formData?.uploadSignature?.Signature, tenantId);
        // setSignedDocumentUploadID(uploadedFileId?.[0]?.fileStoreId);
        setIsSigned(true);
      }
    };

    upload();
  }, [formData]);

  useEffect(() => {
    checkSignStatus(name, formData, uploadModalConfig, onSelect, setIsSigned);
  }, [checkSignStatus]);

  const saveFileToLocalStorage = (docData) => {
    const file = docData?.file;

    if (file) {
      const reader = new FileReader();
      reader.onload = function (e) {
        const base64String = e.target.result;

        const fileData = {
          fileName: file.name,
          base64String: base64String,
          lastModified: file.lastModified,
          size: file.size,
          type: file.type,
        };

        const storedData = {
          ...formUploadData,
          SelectUserTypeComponent: {
            ...formUploadData.SelectUserTypeComponent,
            doc: [[docData[0], { ...docData[1], fileData }]],
          },
        };

        localStorage.setItem("EvidenceFile", JSON.stringify(storedData));
      };
      reader.readAsDataURL(file);
    }
  };

  return !openUploadSignatureModal ? (
    <Modal
      headerBarMain={<Heading label={t("ADD_SIGNATURE")} />}
      headerBarEnd={<CloseBtn onClick={handleGoBackSignatureModal} />}
      actionCancelLabel={t("CS_COMMON_BACK")}
      actionCancelOnSubmit={handleGoBackSignatureModal}
      actionSaveLabel={t(saveOnsubmitLabel)}
      isDisabled={!isSigned}
      actionSaveOnSubmit={() => {
        handleIssueOrder();
      }}
      className={"add-signature-modal"}
    >
      <div className="add-signature-main-div">
        <InfoCard
          variant={"default"}
          label={t("PLEASE_NOTE")}
          additionalElements={[
            <p>
              {t("YOU_ARE_ADDING_YOUR_SIGNATURE_TO_THE")} <span style={{ fontWeight: "bold" }}>{t(`${doctype}`)}</span>
            </p>,
          ]}
          inline
          textStyle={{}}
          className={`custom-info-card`}
        />

        {!isSigned ? (
          <div className="not-signed">
            <h1>{t("YOUR_SIGNATURE")}</h1>
            <div className="sign-button-wrap">
              <Button
                label={t("CS_ESIGN")}
                onButtonClick={() => {
                  // setOpenAadharModal(true);
                  // setIsSigned(true);
                  localStorage.setItem("docSubmission", JSON.stringify(documentSubmission));
                  localStorage.setItem("formUploadData", JSON.stringify(formUploadData));
                  saveFileToLocalStorage(formUploadData?.SelectUserTypeComponent?.doc?.[0]?.[1]);
                  handleEsign(name, pageModule, formUploadData?.SelectUserTypeComponent?.doc?.[0]?.[1]?.fileStoreId?.fileStoreId);
                }}
                className={"aadhar-sign-in"}
                labelClassName={"aadhar-sign-in"}
              />
              <Button
                icon={<FileUploadIcon />}
                label={t("UPLOAD_DIGITAL_SIGN_CERTI")}
                onButtonClick={() => {
                  // setOpenUploadSignatureModal(true);
                  setIsSigned(true);
                  // setOpenUploadSignatureModal(true);
                }}
                className={"upload-signature"}
                labelClassName={"upload-signature-label"}
              />
            </div>
            <div className="donwload-submission">
              <h2>{t("WANT_TO_DOWNLOAD")}</h2>
              <a href={uri} target="_blank" rel="noreferrer" style={{ color: "#007E7E", cursor: "pointer", textDecoration: "underline" }}>
                {t("CLICK_HERE")}
              </a>
            </div>
          </div>
        ) : (
          <div className="signed">
            <h1>{t("YOUR_SIGNATURE")}</h1>
            <h2>{t("SIGNED")}</h2>
          </div>
        )}
      </div>
    </Modal>
  ) : (
    <UploadSignatureModal
      t={t}
      key={name}
      name={name}
      setOpenUploadSignatureModal={setOpenUploadSignatureModal}
      onSelect={onSelect}
      config={uploadModalConfig}
      formData={formData}
    />
  );
}

export default ESignSignatureModal;