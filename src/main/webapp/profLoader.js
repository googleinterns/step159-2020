/* using IIFE https://flaviocopes.com/javascript-iife/ */
(() => {
  google.charts.load("current", { packages: ["corechart"] });
  google.charts.load("current", { packages: ["bar"] });

  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  const profKey = urlParams.get("prof-key");
  const profName = urlParams.get("prof-name");

  function populateData() {
    fetch(`/prof-data?prof-key=${profKey}`)
      .then((response) => response.json())
      .then((profInfo) => {
        document.getElementById("prof-name").innerHTML = profName;
        makeDiffComparisonChart(profInfo);
      });
  }

  document.addEventListener("DOMContentLoaded", function () {
    populateData();
  });

  function makeDiffComparisonChart(profData) {
    const average = (list) =>
      list.reduce((prev, curr) => prev + curr, 0) / list.length;

    const courseList = [];
    const difficultyAvgList = [];

    for (let dataHolder of profData) {
      courseList.push(dataHolder.term);
      difficultyAvgList.push(
        average(dummyDiffData.concat(dataHolder.difficultyList))
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
    const average = (list) =>
      list.reduce((prev, curr) => prev + curr, 0) / list.length;

    const courseList = [];
    const perceptionAvgList = [];

    for (let dataHolder of profData) {
      courseList.push(dataHolder.term);
      perceptionAvgList.push(
        average(dummyGradeData.concat(dataHolder.perceptionList))
      );
    }

    const perceptionData = google.visualization.arrayToDataTable([
      [" "].concat(courseList),
      [" "].concat(difficultyAvgList),
    ]);

    const options = {
      colors: ["#81b8ec", "#f1a79d", "#f1d19d"],
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
  }
})();

// for(let comment of dummyComments.concat(dataHolder.commentsList)){
//     commentsList.push(comment);
// }
