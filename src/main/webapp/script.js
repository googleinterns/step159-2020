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

function loadBody() {
  verifyLogin();
}

/** Contacts Login servlet to verify if user is logged in. */
function verifyLogin() {
  fetch("/login")
    .then((response) => response.json())
    .then((userDetails) => {
      const loginElement = document.getElementById("login-box");
      const schoolName = document.getElementById("school-name");
      if (!userDetails.loggedIn) {
        schoolName.style.display = "none";
        loginElement.innerHTML += `<p><a href= ${userDetails.loginURL} > Click here to log in! </a></p>`;
      } else {
        console.log(userDetails);
        schoolName.style.display = "block";
        if (userDetails.hasOwnProperty("schoolName")) {
          schoolName.innerHTML += userDetails.schoolName;
        }
        loginElement.innerHTML += `<p><a href= ${userDetails.logoutURL} + > Click here to log out! </a></p>`;
      }
    });
}

function showClasses() {
  const classes = document.getElementById("search-results");
  const profName = document.getElementById("search-prof").value;
  classes.innerHTML = "";
  const url = new URL("/search", window.location.origin);
  url.searchParams.set("profName", profName);
  fetch(url)
    .then((response) => response.json())
    .then((response) => {
      return JSON.parse(response.classNames);
    })
    .then((classNames) => {
      classNames.forEach((name) =>
        classes.appendChild(createListElement(name))
      );
    });
}

/* Creates an <li> element containing text. */
function createListElement(text) {
  const liElement = document.createElement("li");
  liElement.innerText = text;
  return liElement;
}

function setParam(name, value) {
  var currentUrl = new URL(window.location);
  currentUrl.searchParams.set(name, value);
  window.location = currentUrl;
}

function setAllParams() {
  var schoolName = document.getElementById("school-name").value;
  var className = document.getElementById("class-name").value;
  var profName = document.getElementById("prof-name").value;

  setParam("school", schoolName);
  setParam("class", className);
  setParam("professor", profName);
}
