import React, { useMemo, useState } from "react";
import { Modal, Button, CardText, RadioButtons, CardLabel, LabelFieldPair } from "@egovernments/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom/cjs/react-router-dom.min";
import { InfoCard } from "@egovernments/digit-ui-components";
import ApplicationInfoComponent from "../../components/ApplicationInfoComponent";
import DocumentModal from "../../components/DocumentModal";
import { formatDate } from "../../../../hearings/src/utils";
import usePaymentProcess from "../../../../home/src/hooks/usePaymentProcess";
import { DRISTIService } from "@egovernments/digit-ui-module-dristi/src/services";
import { ordersService } from "../../hooks/services";
import { Urls } from "../../hooks/services/Urls";
import { useEffect } from "react";
import { paymentType } from "../../utils/paymentType";

const modeOptions = [
  { label: "E-Post (3-5 days)", value: "e-post" },
  { label: "Registered Post (10-15 days)", value: "registered-post" },
];

const mockSubmitModalInfo = {
  header: "CS_HEADER_FOR_SUMMON_POST",
  subHeader: "CS_SUBHEADER_TEXT_FOR_Summon_POST",
  caseInfo: [
    {
      key: "Case Number",
      value: "FSM-2019-04-23-898898",
    },
  ],
  isArrow: false,
  showTable: true,
};

const PaymentForSummonComponent = ({ infos, links, feeOptions, orderDate, paymentLoader }) => {
  const { t } = useTranslation();
  const CustomErrorTooltip = window?.Digit?.ComponentRegistryService?.getComponent("CustomErrorTooltip");

  const [selectedOption, setSelectedOption] = useState({});

  const getDateWithMonthName = (orderDate) => {
    let today = new Date();

    today.setDate(today.getDate() - 15);

    // Array of month names
    const monthNames = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];

    let dd = String(today.getDate()).padStart(2, "0");
    let mm = monthNames[today.getMonth()];
    let yyyy = today.getFullYear();

    let formattedDate = `${dd} ${mm} ${yyyy}`;

    return formattedDate; // Output: formatted date 15 days ago with month name
  };

  return (
    <div className="payment-for-summon">
      <InfoCard
        variant={"warning"}
        label={"Complete in 2 days"}
        additionalElements={[
          <p>
            It takes 10-15 days via physical post and 3-5 days via e-post for Summon Delivery. Pay by{" "}
            <span style={{ fontWeight: "bold" }}>{getDateWithMonthName(orderDate)}</span> for on-time delivery before next hearing.
          </p>,
        ]}
        inline
        textStyle={{}}
        className={`custom-info-card warning`}
      />
      <ApplicationInfoComponent infos={infos} links={links} />
      <LabelFieldPair className="case-label-field-pair">
        <div className="join-case-tooltip-wrapper">
          <CardLabel className="case-input-label">{t("Select preferred mode of post to pay")}</CardLabel>
          <CustomErrorTooltip message={t("Select date")} showTooltip={true} icon />
        </div>
        <RadioButtons
          additionalWrapperClass="mode-of-post-pay"
          options={modeOptions}
          selectedOption={selectedOption}
          optionsKey={"label"}
          onSelect={(value) => setSelectedOption(value)}
          disabled={paymentLoader}
        />
      </LabelFieldPair>
      {selectedOption?.value && (
        <div className="summon-payment-action-table">
          {feeOptions[selectedOption?.value]?.map((action, index) => (
            <div className={`${index === 0 ? "header-row" : "action-row"}`}>
              <div className="payment-label">{t(action?.label)}</div>
              <div className="payment-amount">{index === 0 ? action?.amount : `Rs. ${action?.amount}/-`}</div>
              <div className="payment-action">
                {index === 0 ? (
                  t(action?.action)
                ) : action?.action !== "offline-process" ? (
                  <Button label={t(action.action)} onButtonClick={action.onClick} isDisabled={paymentLoader} />
                ) : (
                  <p className="offline-process-text">
                    This is an offline process. <span className="learn-more-text">Learn More</span>
                  </p>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

const PaymentForSummonModal = ({ path }) => {
  const history = useHistory();
  const { filingNumber, orderNumber } = Digit.Hooks.useQueryParams();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const [caseId, setCaseId] = useState();

  const { data: caseData } = Digit.Hooks.dristi.useSearchCaseService(
    {
      criteria: [
        {
          filingNumber: filingNumber,
        },
      ],
      tenantId,
    },
    {},
    "dristi",
    filingNumber,
    Boolean(filingNumber)
  );

  useEffect(() => {
    if (caseData) {
      const id = caseData?.criteria?.[0]?.responseList?.[0]?.id;
      if (id) {
        console.log(id, "id");
        setCaseId(id); // Set the caseId in state
      } else {
        console.error("caseId is undefined or not available");
      }
    }
  }, [caseData]);

  const caseDetails = useMemo(() => {
    return caseData?.criteria?.[0]?.responseList?.[0];
  }, [caseData]);

  const onViewOrderClick = () => {
    console.log(caseId, "caseID");
    history.push(
      `/${window.contextPath}/citizen/dristi/home/view-case?caseId=${caseData?.criteria?.[0]?.responseList?.[0]?.id}&filingNumber=${filingNumber}&tab=Orders`
    );
  };

  console.log("caseData :>> ", caseData?.criteria?.[0]?.responseList?.[0]?.id);
  const todayDate = new Date().getTime();
  const dayInMillisecond = 24 * 3600 * 1000;
  const { data: orderData, isloading: isOrdersLoading } = Digit.Hooks.orders.useSearchOrdersService(
    { tenantId, criteria: { orderNumber: orderNumber } },
    { tenantId },
    orderNumber,
    Boolean(orderNumber)
  );

  const { data: hearingsData } = Digit.Hooks.hearings.useGetHearings(
    {
      hearing: { tenantId },
      criteria: {
        tenantID: tenantId,
        filingNumber: filingNumber,
        hearingId: orderData?.list?.[0]?.hearingNumber,
      },
    },
    { applicationNumber: "", cnrNumber: "" },
    orderData?.list?.[0]?.hearingNumber,
    Boolean(orderData?.list?.[0]?.hearingNumber)
  );

  const { data: tasksData } = Digit.Hooks.hearings.useGetTaskList(
    {
      criteria: {
        tenantId: tenantId,
        filingNumber: filingNumber,
      },
    },
    {},
    filingNumber,
    Boolean(filingNumber)
  );

  const filteredTasks = useMemo(() => {
    if (!tasksData || !tasksData.list) return [];

    const tasksWithMatchingOrderId = tasksData.list.filter((task) => task.orderId === orderData?.list?.[0]?.id);

    const tasksWithPostChannel = tasksWithMatchingOrderId.filter((task) => {
      try {
        const taskDetails = task?.taskDetails ? JSON.parse(task.taskDetails) : null;
        return taskDetails?.deliveryChannels?.channelName === "Post";
      } catch (error) {
        console.error("Error parsing taskDetails JSON:", error);
        return false;
      }
    });

    return tasksWithPostChannel;
  }, [tasksData, orderData]);

  console.log("taskData", filteredTasks);

  console.log("hearingsData :>> ", hearingsData);

  console.log("orderData :>> ", orderData);

  const { fetchBill, openPaymentPortal, paymentLoader, showPaymentModal, setShowPaymentModal, billPaymentStatus } = usePaymentProcess({
    tenantId,
    consumerCode: filteredTasks?.[0]?.taskNumber,
    service: paymentType.TASK_SUMMON,
    caseDetails,
    totalAmount: "4",
  });

  const { data: billResponse, isLoading: isBillLoading } = Digit.Hooks.dristi.useBillSearch(
    {},
    { tenantId, consumerCode: filteredTasks?.[0]?.taskNumber, service: paymentType.TASK_SUMMON },
    "dristi",
    Boolean(filteredTasks?.[0]?.taskNumber)
  );

  const onPayOnline = async () => {
    console.log("clikc");
    try {
      if (billResponse?.Bill?.length === 0) {
        await DRISTIService.createDemand({
          Demands: [
            {
              tenantId,
              consumerCode: filteredTasks?.[0]?.taskNumber,
              consumerType: paymentType.TASK_SUMMON,
              businessService: paymentType.TASK_SUMMON,
              taxPeriodFrom: Date.now().toString(),
              taxPeriodTo: Date.now().toString(),
              demandDetails: [
                {
                  taxHeadMasterCode: paymentType.TASK_SUMMON_ADVANCE_CARRYFORWARD,
                  taxAmount: 4,
                  collectionAmount: 0,
                },
              ],
            },
          ],
        });
      }
      const bill = await fetchBill(filteredTasks?.[0]?.taskNumber, tenantId, paymentType.TASK_SUMMON);
      if (bill?.Bill?.length) {
        const billPaymentStatus = await openPaymentPortal(bill);
        console.log(billPaymentStatus);
        if (billPaymentStatus === true) {
          console.log("YAAAYYYYY");
          const fileStoreId = await DRISTIService.fetchBillFileStoreId({}, { billId: bill?.Bill?.[0]?.id, tenantId });

          await Promise.all([
            ordersService.customApiService(Urls.orders.pendingTask, {
              pendingTask: {
                name: "Show Summon-Warrant Status",
                entityType: paymentType.ORDER_MANAGELIFECYCLE,
                referenceId: hearingsData?.HearingList?.[0]?.hearingId,
                status: paymentType.ORDER_MANAGELIFECYCLE,
                assignedTo: [],
                assignedRole: ["JUDGE_ROLE"],
                cnrNumber: filteredTasks?.[0]?.cnrNumber,
                filingNumber: filingNumber,
                isCompleted: false,
                stateSla: 3 * dayInMillisecond + todayDate,
                additionalDetails: {
                  hearingId: hearingsData?.list?.[0]?.hearingId,
                },
                tenantId,
              },
            }),
            ordersService.customApiService(Urls.orders.pendingTask, {
              pendingTask: {
                name: `MAKE_PAYMENT_FOR_SUMMONS_POST`,
                entityType: paymentType.ASYNC_ORDER_SUBMISSION_MANAGELIFECYCLE,
                referenceId: `MANUAL_${orderNumber}_Post`,
                status: paymentType.PAYMENT_PENDING_POST,
                assignedTo: [],
                assignedRole: [],
                cnrNumber: filteredTasks?.[0]?.cnrNumber,
                filingNumber: filingNumber,
                isCompleted: true,
                stateSla: "",
                additionalDetails: {},
                tenantId,
              },
            }),
          ]);

          fileStoreId &&
            history.push(`/${window?.contextPath}/citizen/home/post-payment-screen`, {
              state: {
                success: true,
                receiptData: {
                  ...mockSubmitModalInfo,
                  caseInfo: [
                    {
                      key: "Case Name & ID",
                      value: caseDetails?.caseTitle + "," + caseDetails?.filingNumber,
                      copyData: false,
                    },
                    {
                      key: "ORDER ID",
                      value: orderData?.list?.[0]?.orderNumber,
                      copyData: false,
                    },
                    {
                      key: "Transaction ID",
                      value: filteredTasks?.[0]?.taskNumber,
                      copyData: true,
                    },
                  ],
                  isArrow: false,
                  showTable: true,
                  showCopytext: true,
                },
                fileStoreId: fileStoreId?.Document?.fileStore,
              },
            });
        } else {
          console.log("NAAAYYYYY");
          history.push(`/${window?.contextPath}/citizen/home/post-payment-screen`, {
            state: {
              success: false,
              receiptData: {
                ...mockSubmitModalInfo,
                caseInfo: [
                  {
                    key: "Case Name & ID",
                    value: caseDetails?.caseTitle + "," + caseDetails?.filingNumber,
                    copyData: false,
                  },
                  {
                    key: "ORDER ID",
                    value: orderData?.list?.[0]?.orderNumber,
                    copyData: false,
                  },
                  {
                    key: "Transaction ID",
                    value: filteredTasks?.[0]?.taskNumber,
                    copyData: true,
                  },
                ],
                isArrow: false,
                showTable: true,
                showCopytext: true,
              },
              caseId: caseDetails?.filingNumber,
            },
          });
        }
      }
    } catch (error) {
      console.error(error);
    }
  };

  const feeOptions = useMemo(() => {
    const taskAmount = filteredTasks?.[0]?.amount?.amount || 0;

    return {
      "e-post": [
        {
          label: "Fee Type",
          amount: "Amount",
          action: "Actions",
        },
        { label: "Court Fees", amount: taskAmount, action: "Pay Online", onClick: onPayOnline },
        { label: "Delivery Partner Fee", amount: taskAmount, action: "offline-process", onClick: onPayOnline },
      ],
      "registered-post": [
        {
          label: "Fee Type",
          amount: "Amount",
          action: "Actions",
        },
        { label: "Court Fees", amount: taskAmount, action: "Pay Online", onClick: onPayOnline },
        { label: "Delivery Partner Fee", amount: taskAmount, action: "offline-process", onClick: onPayOnline },
      ],
    };
  }, [filteredTasks, onPayOnline]);

  const handleClose = () => {
    if (paymentLoader === false) {
      history.goBack();
    }
  };

  const infos = useMemo(() => {
    const name = `${orderData?.list?.[0]?.additionalDetails?.formdata?.SummonsOrder?.party?.data?.firstName} ${orderData?.list?.[0]?.additionalDetails?.formdata?.SummonsOrder?.party?.data?.lastName}`;
    const addressDetails = orderData?.list?.[0]?.additionalDetails?.formdata?.SummonsOrder?.party?.data?.addressDetails?.[0]?.addressDetails;
    console.log("addressDetails :>> ", addressDetails);
    return [
      { key: "Issued to", value: name },
      { key: "Next Hearing Date", value: formatDate(new Date(hearingsData?.HearingList?.[0]?.startTime)) },
      {
        key: "Delivery Channel",
        value: `Post (${addressDetails?.locality}, ${addressDetails?.city}, ${addressDetails?.district}, ${addressDetails?.state}, ${addressDetails?.pincode})`,
      },
    ];
  }, [hearingsData?.HearingList, orderData?.list]);

  const orderDate = useMemo(() => {
    return hearingsData?.HearingList?.[0]?.startTime;
  }, [hearingsData?.HearingList]);

  const links = useMemo(() => {
    return [{ text: "View order", link: "", onClick: onViewOrderClick }];
  }, [caseData]);

  const paymentForSummonModalConfig = useMemo(() => {
    return {
      handleClose: handleClose,
      heading: { label: "Payment for Summon via post" },
      isStepperModal: false,
      modalBody: (
        <PaymentForSummonComponent infos={infos} links={links} feeOptions={feeOptions} orderDate={orderDate} paymentLoader={paymentLoader} />
      ),
    };
  }, [feeOptions, infos, links]);

  return <DocumentModal config={paymentForSummonModalConfig} />;
};

export default PaymentForSummonModal;
