google.charts.load("current", { packages: ["corechart"] });
google.charts.setOnLoadCallback(makeGraphs);

function fillTitles() {
  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  const courseName = urlParams.get("course-name");
  const term = urlParams.get("term");
  document.getElementById("course-name").innerHTML = courseName;
  document.getElementById("term-name").innerHTML = term;
}

function populateData() {
  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  const courseName = urlParams.get("course-name");
  const term = urlParams.get("term");
  const profName = urlParams.get("prof-name");
  const units = urlParams.get("num-units");
  const schoolName = urlParams.get("school-name");

  fetch(
    `/term-live?school-name=${schoolName}&course-name=${courseName}&term=${term}&prof-name=${profName}&num-units=${units}`
  )
    .then((response) => response.json())
    .then((data) => {
      makeGraphs(data);
    });
}

function makeGraphs(dataObject) {
  const tempHoursList = dataObject.hoursList;
  const hoursList = [
    ["hours"],
    /* dummyHourRating */ [3],
    /* dummyHourRating */ [8],
  ].concat(tempHoursList);
  const hourData = new google.visualization.arrayToDataTable(hoursList);
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

  const tempDiffList = dataObject.hoursList;
  const diffList = [
    ["difficulty"],
    /* dummyDifficultyRating */ [1],
    /* dummyDifficultyRating */ [4],
  ].concat(tempDiffList);
  const diffData = new google.visualization.arrayToDataTable(diffList);
  const diffOptions = {
    title: "Difficulty of Class",
    legend: { position: "none" },
    vAxis: { title: "# Students" },
    hAxis: { title: "Difficulty" },
    histogram: {
      hideBucketItems: true,
    },
  };
  const diffChart = new google.visualization.Histogram(
    document.getElementById("diff-chart")
  );
  diffChart.draw(diffData, diffOptions);

  const tempTermPerceptionList = dataObject.termPerceptionList;
  const termPerceptionList = [
    ["Term Perception"],
    /* dummyPerceptionRating */ [11],
    /* dummyPerceptionRating */ [5],
  ].concat(tempTermPerceptionList);
  const termPerceptionData = new google.visualization.arrayToDataTable(
    termPerceptionList
  );
  const termPerceptionOptions = {
    title: "Perception of Term Reviews",
    legend: { position: "none" },
    vAxis: { title: "Perception" },
    hAxis: { title: "Comment Quantity" },
    histogram: {
      hideBucketItems: true,
    },
  };
  const termPerceptionChart = new google.visualization.Histogram(
    document.getElementById("term-chart")
  );
  termPerceptionChart.draw(termPerceptionData, termPerceptionOptions);

  const tempProfPerceptionList = dataObject.termPerceptionList;
  const profPerceptionList = [
    ["Professor Perception"],
    /* dummyPerceptionRating */ [2],
    /* dummyPerceptionRating */ [9],
  ].concat(tempProfPerceptionList);
  const profPerceptionData = new google.visualization.arrayToDataTable(
    profPerceptionList
  );
  const profPerceptionOptions = {
    title: "Perception of Professor Reviews",
    legend: { position: "none" },
    vAxis: { title: "Perception" },
    hAxis: { title: "Comment Quantity" },
    histogram: {
      hideBucketItems: true,
    },
  };
  const profPerceptionChart = new google.visualization.Histogram(
    document.getElementById("prof-chart")
  );
  profPerceptionChart.draw(profPerceptionData, profPerceptionOptions);
}

async function postData(url, data = {}) {
  // Default options are marked with *
  const response = await fetch(url, {
    method: "POST", // *GET, POST, PUT, DELETE, etc.
    cache: "no-cache",
    credentials: "same-origin", // include, *same-origin, omit
    headers: {
      "Content-Type": "application/json",
      // 'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: JSON.stringify(data), // body data type must match "Content-Type" header
  });
  return response.json(); // parses JSON response into native JavaScript objects
}

function storeData() {
  var data = {};
  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  const courseName = urlParams.get("course-name");
  const term = urlParams.get("term");
  const profName = urlParams.get("prof-name");
  const units = urlParams.get("num-units");
  const schoolName = urlParams.get("school-name");

  data["course-name"] = courseName;
  data["term"] = term;
  data["prof-name"] = profName;
  data["units"] = units;
  data["school-name"] = schoolName;

  const termInput = document.getElementById("term-input").value;
  const profInput = document.getElementById("prof-input").value;
  const ratingTerm = document.getElementById("rating-term").value;
  const ratingProf = document.getElementById("rating-prof").value;
  const hours = document.getElementById("hoursOfWork").value;
  const diff = document.getElementById("difficulty").value;
  document.getElementById("termForm").reset();

  data["term-input"] = termInput;
  data["prof-input"] = profInput;
  data["rating-term"] = ratingTerm;
  data["rating-prof"] = ratingProf;
  data["hours"] = hours;
  data["difficulty"] = diff;

  const url = newURL(
    schoolName,
    courseName,
    profName,
    units,
    term,
    termInput,
    profInput,
    ratingTerm,
    ratingProf,
    hours,
    diff
  );

  return [url, data];
}

function newURL(
  schoolName,
  courseName,
  profName,
  units,
  term,
  termInput,
  profInput,
  ratingTerm,
  ratingProf,
  hours,
  difficulty
) {
  const url = new URL("/data", window.location.origin);
  url.searchParams.set("course-name", courseName);
  url.searchParams.set("prof-name", profName);
  url.searchParams.set("num-units", units);
  url.searchParams.set("term", term);
  url.searchParams.set("school-name", schoolName);

  url.searchParams.set("hourOfWork", hours);
  url.searchParams.set("difficulty", difficulty);
  url.searchParams.set("term-input", termInput);
  url.searchParams.set("prof-input", profInput);
  url.searchParams.set("rating-term", ratingTerm);
  url.searchParams.set("rating-prof", ratingProf);

  return url;
}

function postDataForm() {
  const urlAndData = storeData();
  postData(urlAndData[0], urlAndData[1]);
}
