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

// This is a temporary function, once I connect with Nina will get
// better way to create/access class ids.
function getClassID() {
  var classId = Math.floor(Math.random() * 101);
  document.getElementById("class-id").innerHTML = classId;
}

// function setParam(name, value) {
//   var currentUrl = window.location;
//   var parameters = {};
//   var symbols = /(?:\??)([^=&?]+)=?([^&?]*)/g;
//   var queryString = currentUrl.search;

//   // Goes through all matches between symbols and the queryString.
//   for (var i = symbols.exec(queryString); i; i = symbols.exec(queryString)) {
//     // Decodes URL.
//     i[1] = decodeURIComponent(i[1]);
//     if (!i[2]) {
//       i[2] = "%%";
//     }
//     parameters[i[1]] = i[2];
//   }
//   parameters[name] = encodeURIComponent(value);
//   var search = [];
//   for (var j in parameters) {
//     var encodedElement = encodeURIComponent(j);
//     var symbol = parameters[j];
//     if (symbol != "%%") {
//       encodedElement += "=" + symbol;
//     }
//     search.push(encodedElement);
//   }
//   search = search.join("&");
//   currentUrl.search = search;
// }

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
