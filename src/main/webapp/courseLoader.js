/* using IIFE https://flaviocopes.com/javascript-iife/ */
(() => {
  google.charts.load("current", { packages: ["corechart"] });
  google.charts.load("current", { packages: ["bar"] });

  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  const courseName = urlParams.get("course-name");
  const profName = urlParams.get("prof-name");
  const term = urlParams.get("term");
  const units = urlParams.get("num-units");
  const schoolName = urlParams.get("school-name");

  function fillTitles() {
    document.getElementById("course-name").innerHTML = courseName;
    document.getElementById("term-name").innerHTML = term;
  }

  function populateData() {
    fetch(
      `/term-live?school-name=${schoolName}&course-name=${courseName}&term=${term}&prof-name=${profName}&num-units=${units}`
    )
      .then((response) => response.json())
      .then((data) => {
        makeGraphs(data);
      });
  }

  document.addEventListener("DOMContentLoaded", function () {
    fillTitles();
    populateData();
  });

  function makeGraphs(termDataObject) {
    const termCommentsList = termDataObject.termCommentsList;
    const dummyComments = ["dummy comment 1", "dummy comment 2"].concat(
      termCommentsList
    );
    loadComments(dummyComments, /* isCourse */ true);
    loadComments(dummyComments, /* isCourse */ false);

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

    return makeTermRatingChart(termDataObject);
  }

  async function makeTermRatingChart(termDataObject) {
    //splits term name into [season,year]
    const currentTerm = term
      .split(/(\s+)/)
      .filter((entry) => entry.trim() != "");
    let prevTerm1;
    let prevTerm2;

    //TODO: Make this work for all term types
    if (currentTerm[0] == "Spring") {
      prevTerm1 = `Fall ${currentTerm[1]}`;
      prevTerm2 = `Spring ${String(parseInt(currentTerm[1] - 1))}`;
    } else {
      prevTerm1 = `Spring ${currentTerm[1]}`;
      prevTerm2 = `Fall ${String(parseInt(currentTerm[1] - 1))}`;
    }

    const [prevTermData1, prevTermData2] = await Promise.all([
      getPreviousTermData(prevTerm1),
      getPreviousTermData(prevTerm2),
    ]);

    const average = (list) =>
      list.reduce((prev, curr) => prev + curr, 0) / list.length;

    const currentTermRatingAvg = average(
      /* adds dummy data */ [4, 8, 17].concat(termDataObject.termScoreList)
    );
    const prevTermRatingAvg = average(
      /* adds dummy data */ [12, 5, 3].concat(prevTermData1.termScoreList)
    );
    const prevTermRatingAvg2 = average(
      /* adds dummy data */ [10, 8, 6].concat(prevTermData2.termScoreList)
    );

    const comparisonData = google.visualization.arrayToDataTable([
      ["year", `${currentTerm[0]}  ${currentTerm[1]}`, prevTerm1, prevTerm2],
      [
        currentTerm[1],
        currentTermRatingAvg,
        prevTermRatingAvg,
        prevTermRatingAvg2,
      ],
    ]);

    const options = {
      title: "Average Term Rating Comparison",
      height: 450,
      bars: "horizontal",
      bar: { groupWidth: "30%" },
      hAxis: { title: "Avg Term Rating" },
    };

    const chart = new google.charts.Bar(
      document.getElementById("term-rating-comp")
    );
    chart.draw(comparisonData, google.charts.Bar.convertOptions(options));
  }

  async function getPreviousTermData(prevTerm) {
    const url = `/term-live?school-name=${schoolName}&course-name=${courseName}&term=${prevTerm}&prof-name=${profName}&num-units=${units}`;
    const response = await fetch(url);
    const prevTermData = await response.json();
    return prevTermData;
  }

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

  async function postRatingProperties(url, data = {}) {
    console.log("working");
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
    return userInfo.id;
  }

  async function getRatingPropertiesToStore() {
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    const ratingProperties = {
      courseKey: urlParams.get("course-key"),
      termKey: urlParams.get("term-key"),
      termInput: document.getElementById("term-input").value,
      profInput: document.getElementById("prof-input").value,
      ratingTerm: document.getElementById("rating-term").value,
      ratingProf: document.getElementById("rating-prof").value,
      hours: document.getElementById("hours").value,
      difficulty: document.getElementById("difficulty").value,
      id: await verify(),
    };
    document.getElementById("term-form").reset();

    const url = newURL(
      ratingProperties.courseKey,
      ratingProperties.termKey,
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
    termKey,
    courseKey,
    termInput,
    profInput,
    ratingTerm,
    ratingProf,
    hours,
    difficulty
  ) {
    const url = new URL("/data", window.location.origin);
    url.searchParams.set("course-name", courseKey);
    url.searchParams.set("term-key", termKey);

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
    console.log(urlAndData[1]);
    postRatingProperties(urlAndData[0], urlAndData[1]);
  }

  //   $(function () {
  //     $('[data-toggle="tooltip"]').tooltip();
  //   });

  document
    .getElementById("button")
    .addEventListener("click", passRatingProperties);
})();
