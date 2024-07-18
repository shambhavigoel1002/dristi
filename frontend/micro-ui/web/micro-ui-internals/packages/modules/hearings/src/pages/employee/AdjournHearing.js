import React, { useEffect, useMemo } from "react";
import { useHistory } from "react-router-dom";
import { useState } from "react";
import { Button, Modal } from "@egovernments/digit-ui-react-components";
import { FormComposerV2 } from "@egovernments/digit-ui-components";
import SummaryModal from "../../components/SummaryModal";
import NextHearingModal from "../../components/NextHearingModal";
const AdjournHearing = (props) => {
  const { tenantId, hearing } = props;
  const { hearingId: hearingId } = Digit.Hooks.useQueryParams(); // query paramas
  const [disable, setDisable] = useState(true);
  const [stepper, setStepper] = useState(1);
  const [caseDetails, setCaseDetails] = useState();
  const [selectedDate, setSelectedDate] = useState(null);
  const [reasonFormData, setReasonFormData] = useState({});

  const history = useHistory();

  useEffect(() => {
    getCaseDetails();
  }, []);

  const getCaseDetails = async () => {
    try {
      const response = await DRISTIService.searchCaseService(
        {
          criteria: [
            {
              filingNumber: latestText?.HearingList[0]?.filingNumber[0],
            },
          ],
          tenantId,
        },
        {}
      );
      setCaseDetails(response);
    } catch (error) {
      const response = {
        Case_Number: "FSM-29-04-23-898898",
        Court_Name: "Kerala City Criminal Court",
        Case_Type: "NIA S 138",
      };
      setCaseDetails(response);
    }
  };

  const handleNavigate = (path) => {
    const contextPath = window?.contextPath || "";
    history.push(`/${contextPath}${path}`);
  };

  const onSubmit = (data) => {
    setStepper(stepper + 1);
    setDisable(true);
  };
  const onGenerateOrder = () => {};

  const config = [
    {
      head: "Purpose of Adjournment",
      body: [
        {
          isMandatory: true,
          type: "dropdown",
          key: "reasons",
          disable: false,
          inline: false,
          populators: {
            name: "reason",
            optionsKey: "name",
            error: "",
            required: true,
            options: [
              {
                code: "Option1",
                name: "Lack of Time",
                isEnabled: true,
              },
              {
                code: "Option2",
                name: "Lack of Witness",
                isEnabled: true,
              },
              {
                code: "Option3",
                name: "Lack of Evidence",
                isEnabled: true,
              },
            ],
            optionsCustomStyle: {
              top: "40px",
            },
          },
        },
      ],
    },
  ];

  const handleConfirmationModal = () => {
    handleNavigate(`/employee/hearings/inside-hearing?hearingId=${hearingId}`);
  };

  const Close = () => (
    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
      <g>
        <path d="M19 6.41L17.59 5L12 10.59L6.41 5L5 6.41L10.59 12L5 17.59L6.41 19L12 13.41L17.59 19L19 17.59L13.41 12L19 6.41Z" fill="#0A0A0A" />
      </g>
    </svg>
  );

  const CloseBtn = (props) => {
    return (
      <div style={{ padding: "10px" }} onClick={props.onClick}>
        <Close />
      </div>
    );
  };

  const Heading = (props) => {
    return <h1 className="heading-m">{props.label}</h1>;
  };
  const onFormValueChange = (setValue, formData, formState) => {
    if (formData?.reason) {
      if (formData.reason !== reasonFormData.reason) {
        setReasonFormData(formData);
      }
      if (formData.reason.code !== "Select a Reason") setDisable(false);
    }
  };

  return (
    <div>
      {stepper === 1 && (
        <Modal
          headerBarMain={<Heading label={"Are you sure you wish to adjourn this hearing?"} />}
          headerBarEnd={<CloseBtn onClick={() => handleNavigate(`/employee/hearings/inside-hearing?hearingId=${hearingId}`)} />}
          actionSaveLabel="Adjourn Hearing"
          actionSaveOnSubmit={onSubmit}
          style={{ marginTop: "5px" }}
          isDisabled={disable}
        >
          <FormComposerV2
            config={config}
            onFormValueChange={onFormValueChange}
            isDisabled={true}
            defaultValues={
              reasonFormData?.reason
                ? {
                    reason: {
                      code: reasonFormData?.reason?.code,
                      name: reasonFormData?.reason?.name,
                      isEnabled: true,
                    },
                  }
                : {
                    reason: {
                      code: "Select a Reason",
                      name: "Select a Reason",
                      isEnabled: true,
                    },
                  }
            }
          ></FormComposerV2>
        </Modal>
      )}
      {stepper === 2 && (
        <SummaryModal handleConfirmationModal={handleConfirmationModal} hearingId={hearingId} stepper={stepper} setStepper={setStepper} />
      )}
      {stepper === 3 && <NextHearingModal hearingId={hearingId} hearing={hearing} stepper={stepper} setStepper={setStepper} />}
    </div>
  );
};

export default AdjournHearing;
