/* using IIFE https://flaviocopes.com/javascript-iife/ */
(() => {
  google.charts.load("current", { packages: ["corechart"] });
  google.charts.load("current", { packages: ["bar"] });

  function average(list) {
    const flatList = [].concat.apply([], list);
    return flatList.reduce((prev, curr) => prev + curr, 0) / flatList.length;
  }

  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  const termKey = urlParams.get("term-key");
  const courseKey = urlParams.get("course-key");

  function setProfessorUrl(profName, profKey) {
    const profLink = document.getElementById("prof-link");
    const url = new URL("/professor.html", window.location.origin);
    url.searchParams.set("prof-key", profKey);
    url.searchParams.set("prof-name", profName);
    profLink.setAttribute("href", url);
  }

  function fillTitles() {
    fetch(`/term-info?term-key=${termKey}&course-key=${courseKey}`)
      .then((response) => response.json())
      .then((termInfo) => {
        document.getElementById("course-name").innerHTML = termInfo[0];
        document.getElementById("term-name").innerHTML = termInfo[1];
        document.getElementById("num-enrolled").innerHTML = termInfo[2];
        document.getElementById("prof-name").innerHTML = termInfo[3];
        setProfessorUrl(termInfo[3], termInfo[4]);
      });
  }

  function populateData() {
    fetch(`/term-data?term-key=${termKey}`)
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
    loadComments(termCommentsList, /* isCourse */ true);
  }

  function makeGraphs(termDataObject) {
    if (termDataObject.hoursList.length < 2) {
      document.getElementById("histograms").remove();
    } else {
      const hourList = [["Hours"]].concat(termDataObject.hoursList);
      const hourData = new google.visualization.arrayToDataTable(hourList);
      const hourOptions = {
        width: 800,
        height: 600,
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
          width: "80%",
        },
      };
      const hourChart = new google.visualization.Histogram(
        document.getElementById("hours-chart")
      );
      hourChart.draw(hourData, hourOptions);
    }

    if (termDataObject.difficultyList.length < 2) {
      document.getElementById("histograms").remove();
    } else {
      const diffList = [["Difficulty"]].concat(termDataObject.difficultyList);
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
          bucketSize: 1,
        },
        chartArea: {
          left: 120,
          width: "80%",
        },
      };
      const diffChart = new google.visualization.Histogram(
        document.getElementById("diff-chart")
      );
      diffChart.draw(diffData, diffOptions);
    }
  }

  async function makeTermRatingChart(termDataObject) {
    const currentTermRatingAvg = average(termDataObject.termScoreList);
    const prevTermData = [];
    const avgData = [];
    const prevTermNameList = await getPrevTermName(2);

    for (let term of prevTermNameList) {
      prevTermData.push(await getPreviousTermData(term));
    }

    for (let termData of prevTermData) {
      avgData.push(average(termData.termScoreList));
    }

    const comparisonData = google.visualization.arrayToDataTable([
      [" ", document.getElementById("term-name").innerHTML].concat(
        prevTermNameList
      ),
      [" ", currentTermRatingAvg].concat(avgData),
    ]);

    const options = {
      chartArea: { width: "65%" },
      colors: ["#f1a79d", "#81b8ec", "#f1d19d"],
      title: "Average Term Rating Comparison",
      height: 250,
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
    const currentPerceptionRatingAvg = average(
      termDataObject.termPerceptionList
    );

    const prevTermData = [];
    const avgData = [];
    const prevTermNameList = await getPrevTermName(2);

    for (let term of prevTermNameList) {
      prevTermData.push(await getPreviousTermData(term));
    }

    for (let termData of prevTermData) {
      avgData.push(average(termData.termPerceptionList));
    }

    const perceptionData = google.visualization.arrayToDataTable([
      [" ", document.getElementById("term-name").innerHTML].concat(
        prevTermNameList
      ),
      [" ", currentPerceptionRatingAvg].concat(avgData),
    ]);

    const options = {
      chartArea: { width: "65%" },
      colors: ["#f1d19d", "#81b8ec", "#f1a79d"],
      title: "Average Term Perception Comparison",
      height: 250,
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
    const keyUrl = `/prev-key?course-key=${courseKey}&term=${prevTerm}`;
    const response = await fetch(keyUrl);
    const prevTermKey = await response.json();
    const prevTermDataUrl = `/term-data?term-key=${prevTermKey}`;
    const dataResponse = await fetch(prevTermDataUrl);
    return dataResponse.json();
  }

  async function getPrevTermName(termLimit) {
    const url = `/prev-terms?term-key=${termKey}&course-key=${courseKey}&term-limit=${termLimit}`;
    const response = await fetch(url);
    const prevTermData = await response.json();
    return prevTermData.map((data) => data.properties.term);
  }

  function makeGradeChart(termDataObject) {
    // prettier-ignore
    const gradeMapper = {
      "A+": 97.5,
      "A": 95,
      "A-": 92.5,
      "B+": 87.5,
      "B": 85,
      "B-": 82.5,
      "C+": 77.5,
      "C": 75,
      "C-": 72.5,
      "D+": 67.5,
      "D": 65,
      "D-": 62.5,
      "F": 55,
    };

    const numericalGrade = [];
    for (let grade of termDataObject.gradesList) {
      if (grade != "I") {
        numericalGrade.push(gradeMapper[grade]);
      }
    }

    data = new google.visualization.DataTable();
    data.addColumn("number", "X Value");
    data.addColumn("number", "Y Value");
    data.addColumn({ type: "boolean", role: "scope" });
    data.addColumn({ type: "string", role: "style" });
    data.addRows(createBellData(numericalGrade));

    const options = {
      height: 400,
      width: $(window).width() * 0.8,
      colors: ["#81b8ec"],
      areaOpacity: 0.75,
      lineWidth: 3,
      chartArea: { left: 50 },
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

    const commentBody = document.createElement("p");
    commentBody.setAttribute(
      "class",
      "media-body pb-3 mb-0 small lh-125 border-bottom border-gray"
    );
    commentBody.innerHTML = commentText;
    commentWrapper.appendChild(commentBody);
    commentContainer.appendChild(commentWrapper);
  }

  function loadComments(commentList, isCourse) {
    for (let comment of commentList) {
      createComment(comment, isCourse);
    }
  }
})();
