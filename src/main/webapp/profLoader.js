/* using IIFE https://flaviocopes.com/javascript-iife/ */
(() => {
  google.charts.load("current", { packages: ["corechart"] });
  google.charts.load("current", { packages: ["bar"] });

  const average = (list) =>
    list.reduce((prev, curr) => prev + curr, 0) / list.length;

  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  const profKey = urlParams.get("prof-key");
  const profName = urlParams.get("prof-name");

  function populateData() {
    fetch(`/prof-data?prof-key=${profKey}`)
      .then((response) => response.json())
      .then((profInfo) => {
        document.getElementById("prof-name").innerHTML = profName;
        google.charts.setOnLoadCallback(() => {
          makeDiffComparisonChart(profInfo);
          makePerceptionComparisonChart(profInfo);
        });
      });
  }

  document.addEventListener("DOMContentLoaded", function () {
    populateData();
  });

  function makeDiffComparisonChart(profData) {
    const courseList = [];
    const difficultyAvgList = [];

    for (let dataHolder of profData) {
      courseList.push(dataHolder.term);
      difficultyAvgList.push(
        average(dummyGradeData.concat(dataHolder.difficultyList))
      );
    }

    const difficultyData = google.visualization.arrayToDataTable([
      [" "].concat(courseList),
      [" "].concat(difficultyAvgList),
    ]);

    const options = {
      colors: ["#81b8ec", "#f1a79d", "#f1d19d"],
      title: "Average Course Difficulty Comparison",
      height: 450,
      bars: "horizontal",
      bar: { groupWidth: "30%" },
      hAxis: { title: "Avg Term Perception" },
    };

    const chart = new google.charts.Bar(
      document.getElementById("difficulty-comp")
    );
    chart.draw(difficultyData, google.charts.Bar.convertOptions(options));
  }

  function makePerceptionComparisonChart(profData) {
    const courseList = [];
    const perceptionAvgList = [];
    const commentList = [];

    for (let dataHolder of profData) {
      courseList.push(dataHolder.term);
      perceptionAvgList.push(
        average(dummyGradeData.concat(dataHolder.perceptionList))
      );

      for (let comment of dummyComments.concat(dataHolder.commentsList)) {
        commentList.push(comment);
      }
    }

    const perceptionData = google.visualization.arrayToDataTable([
      [" "].concat(courseList),
      [" "].concat(perceptionAvgList),
    ]);

    const options = {
      colors: ["#f1a79d", "#81b8ec", "#f1d19d"],
      title: "Average Course Perception Comparison",
      height: 450,
      bars: "horizontal",
      bar: { groupWidth: "30%" },
      hAxis: { title: "Avg Term Perception" },
    };

    const chart = new google.charts.Bar(
      document.getElementById("perception-comp")
    );
    chart.draw(perceptionData, google.charts.Bar.convertOptions(options));
    loadComments(commentList);
  }

  function createComment(commentText) {
    const commentContainer = document.getElementById("prof-comments");

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

  function loadComments(commentList) {
    for (let comment of commentList) {
      createComment(comment);
    }
  }
})();
