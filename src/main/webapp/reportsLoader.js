function getReports() {
  fetch(`/find-reports`)
    .then((response) => response.json())
    .then((reportList) => {
      loadReports(reportList);
    });
}

function createReportItem(reportEntity) {
  const reportContainer = document.getElementById("reports-container");

  const reportWrapper = document.createElement("div");
  reportWrapper.setAttribute("class", "media text-muted pt-3");

  const errorType = document.createElement("strong");
  errorType.setAttribute("class", "d-block text-gray-dark");
  errorType.innerHTML = `Error Type: ${reportEntity.properties.type}`;

  const userEmail = document.createElement("strong");
  userEmail.setAttribute("class", "d-block text-gray-dark");
  userEmail.innerHTML = `User Email: ${reportEntity.properties.user}`;

  const reportBody = document.createElement("p");
  reportBody.setAttribute(
    "class",
    "media-body pb-3 mb-0 small lh-125 border-bottom border-gray"
  );
  reportBody.innerHTML = reportEntity.properties.report;

  reportBody.insertAdjacentElement("afterbegin", userEmail);
  reportBody.insertAdjacentElement("afterbegin", errorType);
  reportWrapper.appendChild(reportBody);
  reportContainer.appendChild(reportWrapper);
}

function loadReports(reportList) {
  for (let report of reportList) {
    createReportItem(report);
  }
}

document.addEventListener("DOMContentLoaded", function () {
  getReports();
});
