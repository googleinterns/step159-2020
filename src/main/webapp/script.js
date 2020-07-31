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

function setUserID() {
  //   const auth2 = gapi.auth2.getAuthInstance();
  //   const profile = auth2.currentUser.get().getBasicProfile();
  setParam("ID", "Laura");
}

function autofillForm() {
  // Idea is to grab from get function a hash map, access each thing and fill out form.
  fetch("/data")
    .then((response) => response.json())
    .then((jsonVersionHashtable) => {
      if (Object.keys(jsonVersionHashtable).length == 0) {
        document.getElementById("clear-message").innerHTML =
          "You do not have a previous rating";
      } else {
        document.getElementById("term-input").innerHTML =
          jsonVersionHashtable["comments-term"];
        document.getElementById("rating-term").innerHTML = Number(
          jsonVersionHashtable["perception-term"]
        );
        document.getElementById("hoursOfWork").innerHTML = Number(
          jsonVersionHashtable["hours"]
        );
        document.getElementById("difficulty").innerHTML = Number(
          jsonVersionHashtable["difficulty"]
        );
        document.getElementById("prof-input").innerHTML =
          jsonVersionHashtable["comments-professor"];
        document.getElementById("rating-prof").innerHTML = Number(
          jsonVersionHashtable["perception-professor"]
        );
      }
    });
}

function clearForm() {
  document.getElementById("termForm").reset();
}
