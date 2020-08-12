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
        makeTermRatingChart(data), makeTermPerceptionChart(data);
        loadAllComments(data);
      });
  }

  document.addEventListener("DOMContentLoaded", function () {
    fillTitles();
    populateData();
  });

  function loadAllComments(termDataObject) {
    const termCommentsList = termDataObject.termCommentsList;
    const dummyComments = ["dummy comment 1", "dummy comment 2"].concat(
      termCommentsList
    );
    loadComments(dummyComments, /* isCourse */ true);
    loadComments(dummyComments, /* isCourse */ false);
  }

  function makeGraphs(termDataObject) {
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
  }

  async function makeTermRatingChart(termDataObject) {
    const [prevTerm1, prevTerm2] = await getPrevTermName();
    console.log(prevTerm1);
    console.log(prevTerm2);
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
      [" ", term, prevTerm1, prevTerm2],
      [" ", currentTermRatingAvg, prevTermRatingAvg, prevTermRatingAvg2],
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

  async function makeTermPerceptionChart(termDataObject) {
    const [prevTerm1, prevTerm2] = await getPrevTermName();

    const [prevTermData1, prevTermData2] = await Promise.all([
      getPreviousTermData(prevTerm1),
      getPreviousTermData(prevTerm2),
    ]);

    const average = (list) =>
      list.reduce((prev, curr) => prev + curr) / list.length;

    const currentPerceptionRatingAvg = average(
      /* adds dummy data */ [17, 8, 5].concat(termDataObject.termPerceptionList)
    );
    const prevPerceptionRatingAvg = average(
      /* adds dummy data */ [14, 7, 13].concat(prevTermData1.termPerceptionList)
    );
    const prevPerceptionRatingAvg2 = average(
      /* adds dummy data */ [5, 6, 9].concat(prevTermData2.termPerceptionList)
    );

    const perceptionData = google.visualization.arrayToDataTable([
      [" ", term, prevTerm1, prevTerm2],
      [
        " ",
        currentPerceptionRatingAvg,
        prevPerceptionRatingAvg,
        prevPerceptionRatingAvg2,
      ],
    ]);

    const options = {
      title: "Average Term Perception Comparison",
      height: 450,
      bars: "horizontal",
      bar: { groupWidth: "30%" },
      hAxis: { title: "Avg Term Perception" },
    };

    const chart = new google.charts.Bar(
      document.getElementById("term-perception-comp")
    );
    chart.draw(perceptionData, google.charts.Bar.convertOptions(options));
  }

  function findPrevTerms(termName) {
    //TODO: Make this work for all term types

    const currentTerm = termName
      .split(/(\s+)/)
      .filter((entry) => entry.trim() != "");
    let prevTerm1;
    let prevTerm2;

    if (currentTerm[0] == "Spring") {
      prevTerm1 = `Fall ${currentTerm[1]}`;
      prevTerm2 = `Spring ${String(parseInt(currentTerm[1] - 1))}`;
    } else {
      prevTerm1 = `Spring ${currentTerm[1]}`;
      prevTerm2 = `Fall ${String(parseInt(currentTerm[1] - 1))}`;
    }
    return [prevTerm1, prevTerm2];
  }

  async function getPreviousTermData(prevTerm) {
    const url = `/term-live?school-name=${schoolName}&course-name=${courseName}&term=${prevTerm}&prof-name=${profName}&num-units=${units}`;
    const response = await fetch(url);
    const prevTermData = await response.json();
    return prevTermData;
  }

  async function getPrevTermName() {
    const url = `/prev-terms?school-name=${schoolName}&course-name=${courseName}&term=${term}&prof-name=${profName}&num-units=${units}`;
    const response = await fetch(url);
    const prevTermData = response.json();
    const [prevTermName1, prevTermName2] = [
      prevTermData[0].properties.term,
      prevTermData[1].properties.term,
    ];
    return [prevTermName1, prevTermName2];
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

  $(function () {
    $('[data-toggle="tooltip"]').tooltip();
  });
})();
