google.charts.load("current", {packages:["corechart"]});
google.charts.setOnLoadCallback(makeGraph);
function makeGraph() {
  fetch('/graph')
    .then((response) => response.json())
    .then((data) => {
      console.log(data);
      const hourData = new google.visualization.arrayToDataTable(data.hours);
      const hourOptions = {
          title: "Hours Spent per Week",
          legend: { position: 'none' },
          vAxis: {title: "# Students"},
          hAxis: {title: "Hours"},
          histogram: {
            hideBucketItems: true
          }
        };
        const hourChart = new google.visualization.Histogram(document.getElementById('chart_div'));
        hourChart.draw(hourData, hourOptions);


      const difficultyData = new google.visualization.arrayToDataTable(data.difficulty);
      const diffOptions = {
          title: "Diffculty of Class",
          legend: { position: 'none' },
          vAxis: {title: "# Students"},
          hAxis: {title: "Difficulty"},
          histogram: {
            hideBucketItems: true,
          }
        };
        const diffChart = new google.visualization.Histogram(document.getElementById('chart_div2'));
        diffChart.draw(difficultyData, diffOptions);
    });
}