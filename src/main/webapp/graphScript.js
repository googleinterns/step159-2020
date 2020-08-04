google.charts.load("current", { packages: ["corechart"] });
google.charts.setOnLoadCallback(makeGraph);

function makeGraph() {
  fetch("/graph")
    .then((response) => response.json())
    .then((data) => {
      const hourData = new google.visualization.arrayToDataTable(data.hours);
      const hourOptions = {
          title: "Hours Spent per Week",
          legend: { position: "none" },
          vAxis: {title: "# Students"},
          hAxis: {title: "Hours"},
          histogram: {
            hideBucketItems: true
          }
        };
        const hourChart = new google.visualization.Histogram(document.getElementById("hours-chart"));
        const hourChart2 = new google.visualization.Histogram(document.getElementById("hours-chart2"));
        hourChart.draw(hourData, hourOptions);
        hourChart2.draw(hourData, hourOptions);

      const difficultyData = new google.visualization.arrayToDataTable(data.difficulty);
      const diffOptions = {
          title: "Difficulty of Class",
          legend: { position: "none" },
          vAxis: {title: "# Students"},
          hAxis: {title: "Difficulty"},
          histogram: {
            hideBucketItems: true,
          }
        };
        const diffChart = new google.visualization.Histogram(document.getElementById("difficulty-chart"));
        const diffChart2 = new google.visualization.Histogram(document.getElementById("difficulty-chart2"));
        diffChart.draw(difficultyData, diffOptions);
        diffChart2.draw(difficultyData, diffOptions);
    });
}