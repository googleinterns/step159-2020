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

  async function makeDiffComparisonChart(profData) {
    const average = (list) =>
      list.reduce((prev, curr) => prev + curr) / list.length;

    const courseList = [];
    const diffAvgList = [];

    for (let termHolder of profData) {
      courseList.push(termHolder.properties.term);
      diffAvgList.push(average(termHolder.properties.difficultyList));
    }

    console.log(courseList);
    console.log(diffAvgList);

    // const currentPerceptionRatingAvg = average(
    //   /* adds dummy data */ [21, 11, 9].concat(
    //     termDataObject.termPerceptionList
    //   )
    // );

    // const prevTermData = [];
    // const avgData = [];
    // const prevTermNameList = await getPrevTermName(2);

    // for (let term of prevTermNameList) {
    //   prevTermData.push(await getPreviousTermData(term));
    // }

    // for (let termData of prevTermData) {
    //   avgData.push(average(termData.termPerceptionList.concat([17, 8, 5])));
    // }

    // const perceptionData = google.visualization.arrayToDataTable([
    //   [" ", document.getElementById("term-name").innerHTML].concat(
    //     prevTermNameList
    //   ),
    //   [" ", currentPerceptionRatingAvg].concat(avgData),
    // ]);

    // const options = {
    //   colors: ["#81b8ec", "#f1a79d", "#f1d19d"],
    //   title: "Average Term Perception Comparison",
    //   height: 450,
    //   bars: "horizontal",
    //   bar: { groupWidth: "30%" },
    //   hAxis: { title: "Avg Term Perception" },
    // };

    // const chart = new google.charts.Bar(
    //   document.getElementById("term-perception-comp")
    // );
    // chart.draw(perceptionData, google.charts.Bar.convertOptions(options));
  }
})();
