/* using IIFE https://flaviocopes.com/javascript-iife/ */
(() => {
  google.charts.load("current", { packages: ["corechart"] });
  google.charts.load("current", { packages: ["bar"] });

  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  const termKey = urlParams.get("term-key");
  const courseKey = urlParams.get("term-key");

  function fillTitles() {
    fetch(`/term-info?term-key=${termKey}&course-key=${courseKey}`)
      .then((response) => response.json())
      .then((termInfo) => {
        document.getElementById("course-name").innerHTML = termInfo[0];
        document.getElementById("term-name").innerHTML = termInfo[1];
        document.getElementById("num-enrolled").innerHTML = termInfo[2];
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
    const average = (list) =>
      list.reduce((prev, curr) => prev + curr) / list.length;
    const currentTermRatingAvg = average(
      /* adds dummy data */ [21, 11, 9].concat(
        termDataObject.termPerceptionList
      )
    );

    const prevTermData = [];
    const avgData = [];
    const prevTermNameList = await getPrevTermName(2);

    for (let term of prevTermNameList) {
      prevTermData.push(await getPreviousTermData(term));
    }

    for (let termData of prevTermData) {
      avgData.push(average(termData.termPerceptionList.concat([17, 8, 5])));
    }

    const comparisonData = google.visualization.arrayToDataTable([
      [" ", term].concat(prevTermNameList),
      [" ", currentTermRatingAvg].concat(avgData),
    ]);

    const options = {
      colors: ["#81b8ec", "#f1a79d", "#f1d19d"],
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
    const average = (list) =>
      list.reduce((prev, curr) => prev + curr) / list.length;
    const currentPerceptionRatingAvg = average(
      /* adds dummy data */ [21, 11, 9].concat(
        termDataObject.termPerceptionList
      )
    );

    const prevTermData = [];
    const avgData = [];
    const prevTermNameList = await getPrevTermName(2);

    for (let term of prevTermNameList) {
      prevTermData.push(await getPreviousTermData(term));
    }

    for (let termData of prevTermData) {
      avgData.push(average(termData.termPerceptionList.concat([17, 8, 5])));
    }

    const perceptionData = google.visualization.arrayToDataTable([
      [" ", term].concat(prevTermNameList),
      [" ", currentPerceptionRatingAvg].concat(avgData),
    ]);

    const options = {
      colors: ["#81b8ec", "#f1a79d", "#f1d19d"],
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
    const keyUrl = `/term-key?course-key=${courseKey}&term=${prevTerm}`;
    const response = await fetch(keyUrl);
    const prevTermKey = response.json();
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
      chartArea: { left: 20 },
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
    return userInfo.id;
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
      ID: await verify(),
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

  $(function () {
    $('[data-toggle="tooltip"]').tooltip();
  });
})();
