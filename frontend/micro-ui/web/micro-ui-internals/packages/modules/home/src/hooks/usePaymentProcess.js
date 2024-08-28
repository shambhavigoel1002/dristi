import { useState } from "react";
import { useHistory } from "react-router-dom";
import { DRISTIService } from "@egovernments/digit-ui-module-dristi/src/services";
import { useToast } from "@egovernments/digit-ui-module-dristi/src/components/Toast/useToast";
import { Urls } from "@egovernments/digit-ui-module-dristi/src/hooks";

const usePaymentProcess = ({ tenantId, consumerCode, service, path, caseDetails, totalAmount, mockSubmitModalInfo, scenario }) => {
  const history = useHistory();
  const toast = useToast();
  const [paymentLoader, setPaymentLoader] = useState(false);
  const [showPaymentModal, setShowPaymentModal] = useState(false);
  const [billPaymentStatus, setBillPaymentStatus] = useState();

  const fetchBill = async (consumerCode, tenantId, service) => {
    return await DRISTIService.callFetchBill({}, { consumerCode: consumerCode, tenantId, businessService: service });
  };

  const openPaymentPortal = async (bill) => {
    try {
      const gateway = await DRISTIService.callETreasury(
        {
          ChallanData: {
            ChallanDetails: {
              FROM_DATE: "26/02/2020",
              TO_DATE: "26/02/2020",
              PAYMENT_MODE: "E",
              NO_OF_HEADS: "1",
              HEADS_DET: [
                {
                  AMOUNT: "4",
                  HEADID: "00374",
                },
              ],
              CHALLAN_AMOUNT: "4",
              PARTY_NAME: caseDetails?.additionalDetails?.payerName,
              DEPARTMENT_ID: bill?.Bill?.[0]?.billDetails?.[0]?.id,
              TSB_RECEIPTS: "N",
            },
            billId: bill?.Bill?.[0]?.billDetails?.[0]?.billId,
            serviceNumber: consumerCode,
            businessService: service,
            totalDue: totalAmount,
            mobileNumber: "9876543210",
            paidBy: "COMMON_OWNER",
            tenantId: tenantId,
          },
        },
        {}
      );
      if (gateway) {
        const status = await handleButtonClick(gateway?.payload?.url, gateway?.payload?.data, gateway?.payload?.headers);
        return status;
      } else {
        handleError("Error calling e-Treasury.");
        return true;
      }
    } catch (e) {
      return true;
    }
  };

  const handleButtonClick = (url, data, header) => {
    return new Promise((resolve) => {
      const popup = window.open("", "popupWindow", "width=1000,height=1000,scrollbars=yes");
      if (popup) {
        const form = document.createElement("form");
        form.method = "POST";
        form.action = url;

        const inputDataField = document.createElement("input");
        inputDataField.type = "hidden";
        inputDataField.name = "input_data";
        inputDataField.value = data;
        form.appendChild(inputDataField);

        const inputHeadersField = document.createElement("input");
        inputHeadersField.type = "hidden";
        inputHeadersField.name = "input_headers";
        inputHeadersField.value = header;
        form.appendChild(inputHeadersField);

        popup.document.body.appendChild(form);
        form.submit();
        setPaymentLoader(true);
        popup.document.body.removeChild(form);
      }
      const checkPopupClosed = setInterval(async () => {
        if (popup.closed) {
          setPaymentLoader(false);
          const billAfterPayment = await DRISTIService.callSearchBill({}, { tenantId, consumerCode, service });
          clearInterval(checkPopupClosed);
          resolve(true);
        }
      }, 1000);
      if (scenario !== "applicationSubmission") {
        setShowPaymentModal(false);
      }
    });
  };
  const handleError = (message) => {
    toast.error(message);
  };

  return { fetchBill, openPaymentPortal, paymentLoader, showPaymentModal, setShowPaymentModal, billPaymentStatus };
};

export default usePaymentProcess;
