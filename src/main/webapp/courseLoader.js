google.charts.load("current", { packages: ["corechart"] });
google.charts.setOnLoadCallback(makeGraphs);

function fillTitles() {
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    const courseName = urlParams.get("course-name");
    const term = urlParams.get("term");
    document.getElementById("course-name").innerHTML = courseName;
    document.getElementById("term-name").innerHTML = term;
}

function populateData(){
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    const courseName = urlParams.get("course-name");
    const term = urlParams.get("term");
    const profName = urlParams.get("prof-name");
    const units = urlParams.get("num-units")
    const schoolName = urlParams.get("school-name")

  fetch(`/term-live?school-name=${schoolName}&course-name=${courseName}&term=${term}&prof-name=${profName}&num-units=${units}`)
    .then((response) => response.json())
    .then((data => {
        makeGraphs(data);
    }));
}

function makeGraphs(dataObject){
    const tempHoursList = dataObject.hoursList;
    const hoursList = [["hours"], /* dummyHourRating */ [3], /* dummyHourRating */ [8]].concat(tempHoursList);
    const hourData = new google.visualization.arrayToDataTable(hoursList)
    const hourOptions = {
        title: "Hours Spent per Week",
        legend: {position: "none"},
        vAxis: {title: "# Students"},
        hAxis: {title: "Hours"},
        histogram: {
          hideBucketItems: true
        }
      };
    const hourChart = new google.visualization.Histogram(document.getElementById("hours-chart"));
    hourChart.draw(hourData, hourOptions);


    const tempDiffList = dataObject.hoursList;
    const diffList = [["difficulty"], /* dummyDifficultyRating */ [1], /* dummyDifficultyRating */ [4]].concat(tempDiffList);
    const diffData = new google.visualization.arrayToDataTable(diffList)
    const diffOptions = {
        title: "Difficulty of Class",
        legend: {position: "none"},
        vAxis: {title: "# Students"},
        hAxis: {title: "Difficulty"},
        histogram: {
          hideBucketItems: true
        }
      };
    const diffChart = new google.visualization.Histogram(document.getElementById("diff-chart"));
    diffChart.draw(diffData, diffOptions);


    const tempTermPerceptionList = dataObject.termPerceptionList;
    const termPerceptionList = [["Term Perception"], /* dummyPerceptionRating */ [11], /* dummyPerceptionRating */ [5]].concat(tempTermPerceptionList);
    const termPerceptionData = new google.visualization.arrayToDataTable(termPerceptionList);
    const termPerceptionOptions = {
        title: "Perception of Term Reviews",
        legend: {position: "none"},
        vAxis: {title: "Perception"},
        hAxis: {title: "Comment Quantity"},
        histogram: {
          hideBucketItems: true
       }
    };
    const termPerceptionChart = new google.visualization.Histogram(document.getElementById("term-chart"));
    termPerceptionChart.draw(termPerceptionData, termPerceptionOptions);


    const tempProfPerceptionList = dataObject.termPerceptionList;
    const profPerceptionList = [["Professor Perception"], /* dummyPerceptionRating */ [2], /* dummyPerceptionRating */ [9]].concat(tempProfPerceptionList);
    const profPerceptionData = new google.visualization.arrayToDataTable(profPerceptionList);
    const profPerceptionOptions = {
        title: "Perception of Professor Reviews",
        legend: {position: "none"},
        vAxis: {title: "Perception"},
        hAxis: {title: "Comment Quantity"},
        histogram: {
          hideBucketItems: true
        }
      };
    const profPerceptionChart = new google.visualization.Histogram(document.getElementById("prof-chart"));
    profPerceptionChart.draw(profPerceptionData, profPerceptionOptions);
}


//TODO : Move params into request body

function passData(){
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    const courseName = urlParams.get("course-name");
    const term = urlParams.get("term");
    const profName = urlParams.get("prof-name");
    const units = urlParams.get("num-units")
    const schoolName = urlParams.get("school-name")

    const termInput = document.getElementById("term-input")[0].value;
    const profInput = document.getElementById("prof-input")[0].value;
    const ratingTerm = document.getElementById("rating-term").value;
    const ratingProf = document.getElementById("rating-prof").value;
    const hours = document.getElementById("hoursOfWork").value;
    const diff = document.getElementById("difficulty").value;

    const url = new URL("/data", window.location.origin);
    url.searchParams.set("course-name", courseName);
    url.searchParams.set("prof-name", profName);
    url.searchParams.set("num-units", units);
    url.searchParams.set("term", term);
    url.searchParams.set("school-name", schoolName);
    
    url.searchParams.set("hourOfWork", hours);
    url.searchParams.set("difficulty", diff);
    url.searchParams.set("term-input", termInput);
    url.searchParams.set("prof-input", profInput);
    url.searchParams.set("rating-term", ratingTerm);
    url.searchParams.set("rating-prof", ratingProf);
    fetch(url, { method: "POST" });
}

$(function () {
  $('[data-toggle="tooltip"]').tooltip()
})