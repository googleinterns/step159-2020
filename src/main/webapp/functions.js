google.charts.load("current", { packages: ["corechart"] });
google.charts.setOnLoadCallback(makeGraph);

function makeGraph() {
  fetch("/graph")
    .then((response) => response.json())
    .then((data) => {
      const hourData = new google.visualization.arrayToDataTable(data.hours);
      const hourOptions = {
        title: "Hours Spent per Week",
        legend: { position: "none" },
        vAxis: { title: "# Students" },
        hAxis: { title: "Hours" },
        histogram: {
          hideBucketItems: true,
        },
      };
      const hourChart = new google.visualization.Histogram(
        document.getElementById("hours-chart")
      );
      hourChart.draw(hourData, hourOptions);

      const difficultyData = new google.visualization.arrayToDataTable(
        data.difficulty
      );
      const diffOptions = {
        title: "Diffculty of Class",
        legend: { position: "none" },
        vAxis: { title: "# Students" },
        hAxis: { title: "Difficulty" },
        histogram: {
          hideBucketItems: true,
        },
      };
      const diffChart = new google.visualization.Histogram(
        document.getElementById("difficulty-chart")
      );
      diffChart.draw(difficultyData, diffOptions);
    });
}

function loadBody() {
  verifyLogin();
}

function passData() {
  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  const courseName = urlParams.get("course-name");
  const term = urlParams.get("term");
  const profName = urlParams.get("prof-name");
  const units = urlParams.get("num-units");
  const schoolName = urlParams.get("school-name");

  const termInput = document.getElementsByName("term-input")[0].value;
  const profInput = document.getElementsByName("prof-input")[0].value;
  const ratingTerm = document.getElementById("rating-term").value;
  const ratingProf = document.getElementById("rating-prof").value;
  const hours = document.getElementById("hoursOfWork").value;
  const diff = document.getElementById("difficulty").value;

  const url = new URL("/data", window.location.origin);
  url.searchParams.set("course-name", courseName);
  url.searchParams.set("prof-name", profName);
  url.searchParams.set("num-units", units);
  url.searchParams.set("term", term);
  url.searchParams.set("school-name", schoolName);

  url.searchParams.set("hourOfWork", hours);
  url.searchParams.set("difficulty", diff);
  url.searchParams.set("term-input", termInput);
  url.searchParams.set("prof-input", profInput);
  url.searchParams.set("rating-term", ratingTerm);
  url.searchParams.set("rating-prof", ratingProf);
  fetch(url, { method: "POST" });
}

function setUserID() {
  const auth2 = gapi.auth2.getAuthInstance();
  const profile = auth2.currentUser.get().getBasicProfile();
  const id = profile.getId();

  const url = new URL("/data", window.location.origin);
  url.searchParams.set("ID", id);
}
