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

      const difficultyData = new google.visualization.arrayToDataTable(
        data.difficulty
      );
      const diffOptions = {
        title: "Diffculty of Class",
        legend: { position: "none" },
        vAxis: { title: "# Students" },
        hAxis: { title: "Difficulty" },
        histogram: {
          hideBucketItems: true,
        },
      };
      const diffChart = new google.visualization.Histogram(
        document.getElementById("difficulty-chart")
      );
      diffChart.draw(difficultyData, diffOptions);
    });
}

// This is a temporary function, once I connect with Nina will get
// better way to create/access class ids.
function getClassID() {
  var classId = Math.floor(Math.random() * 101);
  document.getElementById("class-id").innerHTML = classId;
}
