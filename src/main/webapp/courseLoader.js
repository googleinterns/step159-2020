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
      colors: ["#f1a79d"],
      title: "Hours Spent per Week",
      titleTextStyle: {
        color: "#808080",
        fontSize: 16,
        bold: false,
        italic: false,
      },
      legend: { position: "none" },
      hAxis: {
        title: "Hours",
        titleTextStyle: {
          color: "#808080",
          fontSize: 12,
          bold: false,
          italic: false,
          fontName: "Roboto",
        },
        textStyle: {
          color: "#808080",
          fontName: "Roboto",
          fontSize: 11,
          bold: false,
          italic: false,
        },
      },
      vAxis: {
        textStyle: {
          color: "#808080",
          fontName: "Roboto",
          fontSize: 11,
          bold: false,
          italic: false,
        },
      },
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
      titleTextStyle: {
        color: "#808080",
        fontSize: 16,
        bold: false,
        italic: false,
      },
      legend: { position: "none" },
      hAxis: {
        title: "Difficulty",
        titleTextStyle: {
          color: "#808080",
          fontSize: 12,
          bold: false,
          italic: false,
          fontName: "Roboto",
        },
        textStyle: {
          color: "#808080",
          fontName: "Roboto",
          fontSize: 11,
          bold: false,
          italic: false,
        },
      },
      vAxis: {
        textStyle: {
          color: "#808080",
          fontName: "Roboto",
          fontSize: 11,
          bold: false,
          italic: false,
        },
      },
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

  function makeGradeChart() {
    data = new google.visualization.DataTable();
    data.addColumn("number", "X Value");
    data.addColumn("number", "Y Value");
    data.addColumn({ type: "boolean", role: "scope" });
    data.addColumn({ type: "string", role: "style" });
    data.addRows(createBellData(dummyGradeData));

    const options = {
      height: 600,
      width: $(window).width(),
      colors: ["#81b8ec"],
      areaOpacity: 0.75,
      lineWidth: 3,
      chartArea: { left: 0 },
      title: "Term Grade Distribution",
      legend: { position: "none" },
      titleTextStyle: {
        color: "#808080",
        fontSize: 16,
        bold: false,
        italic: false,
      },
      hAxis: {
        titleTextStyle: {
          color: "#808080",
          fontSize: 12,
          bold: false,
          italic: false,
          fontName: "Roboto",
        },
        title: "Grades",
        gridlines: {
          count: 0,
        },
        minorGridlines: {
          count: 0,
          color: "#FFFFFF	",
        },
        textStyle: {
          color: "#808080",
          fontName: "Roboto",
          fontSize: 11,
          bold: false,
          italic: false,
        },
      },
      vAxis: {
        titleTextStyle: {
          color: "#808080",
          fontSize: 12,
          bold: false,
          italic: false,
        },
        minorGridlines: {
          count: 0,
          color: "transparent",
        },
        textStyle: {
          color: "#808080",
          fontName: "Roboto",
          fontSize: 11,
          bold: false,
          italic: false,
        },
      },
    };

    const chart = new google.visualization.AreaChart(
      document.getElementById("grade-chart")
    );
    chart.draw(data, options);
  }

  function createBellData(data) {
    jData = jStat(data);
    mean = jData.mean();
    stndDev = jData.stdev();
    xMin = jData.min();
    xMax = jData.max();

    let chartData = new Array([]);
    let index = 0;
    for (var i = xMin; i <= xMax; i += 0.01) {
      chartData[index] = new Array(4);
      chartData[index][0] = i;
      chartData[index][1] = jStat.normal.pdf(i, mean, stndDev);
      index++;
    }
    return chartData;
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
