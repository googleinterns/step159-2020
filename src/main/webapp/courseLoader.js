google.charts.load("current", { packages: ["corechart"] });

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

async function postRatingProperties(url, data = {}) {
  // Default options are marked with *.
  const response = await fetch(url, {
    method: "POST", // *GET, POST, PUT, DELETE, etc.
    cache: "no-cache",
    credentials: "same-origin", // Include, *same-origin, omit.
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data), // Body data type must match "Content-Type" header.
  });
  return response.json(); // Parses JSON response into native JavaScript objects and returns a Promise.
}

function onLoad() {
  gapi.load("auth2", function () {
    gapi.auth2.init();
  });
}

async function verify() {
  const auth2 = gapi.auth2.getAuthInstance();
  const googleUser = auth2.currentUser.get();
  const token = googleUser.getAuthResponse().id_token;
  const url = new URL("/login", window.location.origin);
  url.searchParams.set("token", token);
  const response = await fetch(url, { method: "POST" });
  const userInfo = await response.json();
  const id = userInfo.id;
  return id;
}

async function getRatingPropertiesToStore() {
  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  const ratingProperties = {
    courseName: urlParams.get("course-name"),
    term: urlParams.get("term"),
    profName: urlParams.get("prof-name"),
    units: urlParams.get("num-units"),
    schoolName: urlParams.get("school-name"),
    termInput: document.getElementById("term-input").value,
    profInput: document.getElementById("prof-input").value,
    ratingTerm: document.getElementById("rating-term").value,
    ratingProf: document.getElementById("rating-prof").value,
    hours: document.getElementById("hours").value,
    difficulty: document.getElementById("difficulty").value,
  };
  document.getElementById("term-form").reset();

  const url = newURL(
    ratingProperties.schoolName,
    ratingProperties.courseName,
    ratingProperties.profName,
    ratingProperties.units,
    ratingProperties.term,
    ratingProperties.termInput,
    ratingProperties.profInput,
    ratingProperties.ratingTerm,
    ratingProperties.ratingProf,
    ratingProperties.hours,
    ratingProperties.difficulty
  );

  return [url, ratingProperties];
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

  url.searchParams.set("hour", hours);
  url.searchParams.set("difficulty", difficulty);
  url.searchParams.set("term-input", termInput);
  url.searchParams.set("prof-input", profInput);
  url.searchParams.set("rating-term", ratingTerm);
  url.searchParams.set("rating-prof", ratingProf);

  return url;
}

async function passRatingProperties() {
  const urlAndData = await getRatingPropertiesToStore();
  postRatingProperties(urlAndData[0], urlAndData[1]);
}

document.addEventListener("DOMContentLoaded", function () {
  populateData();
});

function createComment(commentText, isCourse) {
  const commentContainer = isCourse
    ? document.getElementById("course-comments")
    : document.getElementById("prof-comments");

  const commentWrapper = document.createElement("div");
  commentWrapper.setAttribute("class", "media text-muted pt-3");

  const commentUser = document.createElement("strong");
  commentUser.setAttribute("class", "d-block text-gray-dark");
  commentUser.innerHTML = "@username";

  const commentBody = document.createElement("p");
  commentBody.setAttribute(
    "class",
    "media-body pb-3 mb-0 small lh-125 border-bottom border-gray"
  );
  commentBody.innerHTML = commentText;

  commentBody.insertAdjacentElement("afterbegin", commentUser);
  commentWrapper.appendChild(commentBody);
  commentContainer.appendChild(commentWrapper);
}

function loadComments(commentList, isCourse) {
  for (let comment of commentList) {
    createComment(comment, isCourse);
  }
}

function makeGraphs(termDataObject) {
  const termCommentsList = termDataObject.termCommentsList;
  const dummyComments = ["dummy comment 1", "dummy comment 2"].concat(
    termCommentsList
  );
  loadComments(dummyComments, /* isCourse*/ true);
  loadComments(dummyComments, /* isCourse*/ false);

  const tempHoursList = termDataObject.hoursList;
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

  const tempDiffList = termDataObject.difficultyList;
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

  const tempTermPerceptionList = termDataObject.termPerceptionList;
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

$(function () {
  $('[data-toggle="tooltip"]').tooltip();
});
