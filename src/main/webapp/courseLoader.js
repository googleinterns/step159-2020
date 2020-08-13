/* using IIFE https://flaviocopes.com/javascript-iife/ */
(() => {
  //will delete needed for better visualization when testing
  const dummyDiffData = [
    ["Difficulty"],
    [2],
    [7],
    [5],
    [2],
    [10],
    [1],
    [3],
    [5],
    [3],
    [4],
    [8],
    [1],
    [9],
    [4],
    [10],
    [1],
    [1],
    [2],
    [8],
    [5],
    [1],
    [3],
    [7],
    [5],
    [3],
    [7],
    [3],
    [7],
    [5],
    [10],
    [4],
    [3],
    [6],
    [8],
    [2],
    [5],
    [3],
    [1],
    [10],
    [6],
    [7],
    [7],
    [3],
    [6],
    [3],
    [10],
    [1],
    [4],
    [4],
    [10],
    [5],
    [9],
    [2],
    [1],
    [8],
    [6],
    [10],
    [10],
    [5],
    [2],
    [9],
    [9],
    [7],
    [6],
    [2],
    [7],
    [1],
    [7],
    [10],
    [4],
    [10],
    [2],
    [4],
    [5],
    [1],
    [7],
    [3],
    [1],
    [5],
    [2],
    [10],
    [2],
    [6],
    [1],
    [3],
    [5],
    [6],
    [9],
    [3],
    [2],
    [10],
    [1],
    [2],
    [3],
    [4],
    [7],
    [9],
    [4],
    [2],
    [10],
  ];

  const dummyHoursData = [
    ["Hours"],
    [13],
    [15],
    [6],
    [4],
    [16],
    [10],
    [13],
    [1],
    [14],
    [8],
    [7],
    [5],
    [2],
    [17],
    [14],
    [2],
    [15],
    [4],
    [14],
    [8],
    [2],
    [6],
    [6],
    [5],
    [16],
    [4],
    [3],
    [10],
    [19],
    [6],
    [6],
    [17],
    [20],
    [6],
    [11],
    [8],
    [10],
    [15],
    [4],
    [16],
    [1],
    [15],
    [6],
    [8],
    [13],
    [9],
    [14],
    [18],
    [15],
    [17],
    [18],
    [9],
    [18],
    [14],
    [6],
    [9],
    [20],
    [4],
    [17],
    [9],
    [3],
    [18],
    [16],
    [2],
    [17],
    [12],
    [12],
    [11],
    [15],
    [2],
    [3],
    [11],
    [10],
    [7],
    [19],
    [6],
    [2],
    [9],
    [14],
    [20],
    [11],
    [13],
    [8],
    [18],
    [11],
    [16],
    [5],
    [12],
    [19],
    [8],
    [11],
    [1],
    [15],
    [9],
    [6],
    [18],
    [10],
    [3],
    [9],
    [6],
  ];

  const dummyGradeData = [
    3.5,
    3,
    3.2,
    3.1,
    3.6,
    3.9,
    3.4,
    3.4,
    2.9,
    3.1,
    3.7,
    3.4,
    3,
    3,
    4,
    4.4,
    3.9,
    3.5,
    3.8,
    3.8,
    3.4,
    3.7,
    3.6,
    3.3,
    3.4,
    3,
    3.4,
    3.5,
    3.4,
    3.2,
    3.1,
    3.4,
    4.1,
    4.2,
    3.1,
    3.2,
    3.5,
    3.6,
    3,
    3.4,
    3.5,
    2.3,
    3.2,
    3.5,
    3.8,
    3,
    3.8,
    3.2,
    3.7,
    3.3,
    3.2,
    3.2,
    3.1,
    2.3,
    2.8,
    2.8,
    3.3,
    2.4,
    2.9,
    2.7,
    2,
    3,
    2.2,
    2.9,
    2.9,
    3.1,
    3,
    2.7,
    2.2,
    2.5,
    3.2,
    2.8,
    2.5,
    2.8,
    2.9,
    3,
    2.8,
    3,
    2.9,
    2.6,
    2.4,
    2.4,
    2.7,
    2.7,
    3,
    3.4,
    3.1,
    2.3,
    3,
    2.5,
    2.6,
    3,
    2.6,
    2.3,
    2.7,
    3,
    2.9,
    2.9,
    2.5,
    2.8,
    3.3,
    2.7,
    3,
    2.9,
    3,
    3,
    2.5,
    2.9,
    2.5,
    3.6,
    3.2,
    2.7,
    3,
    2.5,
    2.8,
    3.2,
    3,
    3.8,
    2.6,
    2.2,
    3.2,
    2.8,
    2.8,
    2.7,
    3.3,
    3.2,
    2.8,
    3,
    2.8,
    3,
    2.8,
    3.8,
    2.8,
    2.8,
    2.6,
    3,
    3.4,
    3.1,
    3,
    3.1,
    3.1,
    3.1,
    2.7,
    3.2,
    3.3,
    3,
    2.5,
    3,
    3.4,
    3,
  ];

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
        google.charts.setOnLoadCallback(() => {
          makeGraphs(data);
          makeTermRatingChart(data);
          makeTermPerceptionChart(data);
          makeGradeChart(data);
        });
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
    const hourList = dummyHoursData.concat(termDataObject.hoursList);
    const hourData = new google.visualization.arrayToDataTable(hourList);
    const hourOptions = {
      colors: ["#9dc8f1"],
      title: "Hours Spent per Week",
      legend: { position: "none" },
      hAxis: { title: "Hours" },
      histogram: {
        hideBucketItems: true,
      },
      chartArea: {
        left: 120,
      },
    };
    const hourChart = new google.visualization.Histogram(
      document.getElementById("hours-chart")
    );
    hourChart.draw(hourData, hourOptions);

    const diffList = dummyDiffData.concat(termDataObject.difficultyList);
    const diffData = new google.visualization.arrayToDataTable(diffList);
    const diffOptions = {
      colors: ["#f1d19d"],
      title: "Difficulty of Class",
      legend: { position: "none" },
      hAxis: { title: "Difficulty" },
      histogram: {
        hideBucketItems: true,
        bucketSize: 2,
      },
      chartArea: {
        left: 120,
      },
    };
    const diffChart = new google.visualization.Histogram(
      document.getElementById("diff-chart")
    );
    diffChart.draw(diffData, diffOptions);
  }

  async function makeTermRatingChart(termDataObject) {
    const [prevTerm1, prevTerm2] = await getPrevTermName();
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
      colors: ["#9dc8f1", "#f1d19d", "#f1a79d"],
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
      colors: ["#9dc8f1", "#f1d19d", "#f1a79d"],
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

  async function getPreviousTermData(prevTerm) {
    const url = `/term-live?school-name=${schoolName}&course-name=${courseName}&term=${prevTerm}&prof-name=${profName}&num-units=${units}`;
    const response = await fetch(url);
    return response.json();
  }

  async function getPrevTermName() {
    const url = `/prev-terms?school-name=${schoolName}&course-name=${courseName}&term=${term}&prof-name=${profName}&num-units=${units}`;
    const response = await fetch(url);
    const prevTermData = await response.json();
    const [prevTermName1, prevTermName2] = [
      prevTermData[0].properties.term,
      prevTermData[1].properties.term,
    ];
    return [prevTermName1, prevTermName2];
  }

  function makeGradeChart(termDataObject) {
    const gradesList = dummyGradeData.concat(termDataObject.gradesList);
    Highcharts.chart("container-bell", {
      chart: {
        height: 600,
      },
      exporting: {
        enabled: false,
      },
      title: {
        text: "Term Grade Distributions",
        margin: 30,
      },
      xAxis: [
        {
          title: {
            text: "",
          },
          alignTicks: false,
          opposite: true,
        },
        {
          title: {
            text: "Grades",
          },
          alignTicks: false,
        },
      ],

      yAxis: [
        {
          title: { text: "" },
          opposite: true,
        },
        {
          title: { text: "Probability Density" },
          opposite: false,
        },
      ],
      series: [
        {
          name: "Grade Distribution",
          type: "bellcurve",
          xAxis: 1,
          yAxis: 1,
          baseSeries: 1,
          zIndex: -1,
        },
        {
          name: "Grade Data Points",
          type: "scatter",
          data: gradesList,
          accessibility: {
            exposeAsGroupOnly: true,
          },
          marker: {
            radius: 1.5,
          },
        },
      ],
    });
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
