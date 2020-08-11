(() => {
  let termData;
  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  const courseName = urlParams.get("course-name");
  const profName = urlParams.get("prof-name");
  const term = urlParams.get("term");
  const units = urlParams.get("num-units");
  const schoolName = urlParams.get("school-name");

  google.charts.load("current", { packages: ["corechart"] });
  google.charts.load("current", { packages: ["bar"] });

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
        termData = data;
        makeGraphs(termData);
      });
  }

  document.addEventListener("DOMContentLoaded", function () {
    fillTitles();
    populateData();
  });

  function createComment(commentText, flag) {
    const commentContainer = flag
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

  function loadComments(commentList, flag) {
    for (let comment of commentList) {
      createComment(comment, flag);
    }
  }

  function makeGraphs(dataObject) {
    const termCommentsList = dataObject.termCommentsList;
    const dummyComments = ["dummy comment 1", "dummy comment 2"].concat(
      termCommentsList
    );
    loadComments(dummyComments, /* true means course comments */ true);
    loadComments(dummyComments, /* false means professor comments */ false);

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

    const tempDiffList = dataObject.difficultyList;
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

  $(function () {
    $('[data-toggle="tooltip"]').tooltip();
  });

  async function CreateTermCompChart() {
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    const term = urlParams.get("term");

    const currentTerm = term.split(/(\s+)/).filter((e) => e.trim().length > 0);
    let prevTerm1;
    let prevTerm2;

    if (currentTerm[0] == "Spring") {
      prevTerm1 = `Fall ${currentTerm[1]}`;
      prevTerm2 = `Spring ${String(parseInt(currentTerm[1] - 1))}`;
    } else {
      prevTerm1 = `Spring ${currentTerm[1]}`;
      prevTerm2 = `Fall ${String(parseInt(currentTerm[1] - 1))}`;
    }

    const prevTermData1 = await getPreviousTermData(prevTerm1);
    const prevTermData2 = await getPreviousTermData(prevTerm2);

    const average = (list) =>
      list.reduce((prev, curr) => prev + curr) / list.length;

    const currentTermScoreAvg = average(termData.termScoreList);
    const avgTermScorePrev1 = average(prevTermData1.termScoreList);
    const avgTermScorePrev2 = average(prevTermData2.termScoreList);

    console.log(avgTermScorePrev1, "hey");
    console.log(avgTermScorePrev2, "hey");
    console.log(currentTermScoreAvg, "hey");
  }

  CreateTermCompChart();

  async function getPreviousTermData(term) {
    const url = `/term-live?school-name=${schoolName}&course-name=${courseName}&term=${term}&prof-name=${profName}&num-units=${units}`;
    const response = await fetch(url);
    const prevTermData = await response.json();
    return prevTermData;
  }
})();
