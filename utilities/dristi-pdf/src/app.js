var createError = require("http-errors");
var express = require("express");
var path = require("path");
var cookieParser = require("cookie-parser");
var logger = require("morgan");
var config = require("./config");

const summonsIssue = require("./routes/summonsIssue");
const orderGeneric = require("./routes/orderGeneric");
const caseTransfer = require("./routes/caseTransfer");
const caseSettlementAcceptance = require("./routes/caseSettlementAcceptance");
const caseSettlementRejection = require("./routes/caseSettlementRejection");
const adrCaseReferral = require("./routes/adrCaseReferral");
const mandatoryAsyncSubmissionsResponses = require("./routes/mandatoryAsyncSubmissionsResponses");
const rescheduleRequestJudge = require("./routes/rescheduleRequestJudge");
const newHearingDateAfterReschedule = require("./routes/newHearingDateAfterReschedule");
const scheduleHearingDate =require("./routes/scheduleHearingDate");
const acceptReschedulingRequest=require("./routes/acceptReschedulingRequest");
const rejectReschedulingRequest=require("./routes/rejectReschedulingRequest");
const acceeptAdrApplication = require("./routes/acceptAdrApplication");
const rejectAdrApplication = require("./routes/rejectAdrApplication");

// var {listenConsumer} = require("./consumer")

var app = express();
app.disable('x-powered-by');

// view engine setup
app.set("views", path.join(__dirname, "views"));
app.set("view engine", "jade");

app.use(logger("dev"));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, "public")));

app.use(config.app.contextPath + "/download/summonsIssue", summonsIssue);
app.use(config.app.contextPath + "/download/orderGeneric", orderGeneric);
app.use(config.app.contextPath + "/download/caseTransfer", caseTransfer);
app.use(config.app.contextPath + "/download/caseSettlementAcceptance", caseSettlementAcceptance);
app.use(config.app.contextPath + "/download/caseSettlementRejection", caseSettlementRejection);
app.use(config.app.contextPath + "/download/adrCaseReferral", adrCaseReferral);
app.use(config.app.contextPath + "/download/mandatoryAsyncSubmissionsResponses", mandatoryAsyncSubmissionsResponses);
app.use(config.app.contextPath + "/download/rescheduleRequestJudge", rescheduleRequestJudge);
app.use(config.app.contextPath + "/download/newHearingDateAfterReschedule", newHearingDateAfterReschedule);
app.use(config.app.contextPath + "/download/scheduleHearingDate", scheduleHearingDate);
app.use(config.app.contextPath + "/download/acceptReschedulingRequest", acceptReschedulingRequest);
app.use(config.app.contextPath + "/download/rejectReschedulingRequest", rejectReschedulingRequest);
app.use(config.app.contextPath + "/download/acceptAdrApplication", acceeptAdrApplication);
app.use(config.app.contextPath + "/download/rejectAdrApplication", rejectAdrApplication);

// catch 404 and forward to error handler
app.use(function (req, res, next) {
  next(createError(404));
});

// error handler
app.use(function (err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get("env") === "development" ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render("error");
});

// Commenting consumer listener becuase excel bill gen is not required. IFMS adapter will process the payment.
// listenConsumer();

module.exports = app;
