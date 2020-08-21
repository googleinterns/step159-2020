(() => {
  google.charts.load("current", { packages: ["corechart"] });
  google.charts.load("current", { packages: ["bar"] });

  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  const profKey = urlParams.get("prof-key");
  const profName = urlParams.get("prof-name");

  function fillTitle(profData) {
    document.getElementById("prof-name").innerHTML = profName;
    linkContainer = document.getElementById("link-container");

    for (let dataHolder of profData) {
      const newLink = document.createElement("a");
      newLink.innerHTML = `${dataHolder.course} - ${dataHolder.term}`;

      const url = new URL("/course.html", window.location.origin);
      url.searchParams.set("term-key", dataHolder.termKey);
      url.searchParams.set("course-key", dataHolder.courseKey);
      newLink.setAttribute("href", url);
      linkContainer.appendChild(newLink);
    }
  }

  function populateData() {
    fetch(`/prof-data?prof-key=${profKey}`)
      .then((response) => response.json())
      .then((profData) => {
        fillTitle(profData);
        google.charts.setOnLoadCallback(() => {
          makeDiffComparisonChart(profData);
          makePerceptionComparisonChart(profData);
        });
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
      courseList.push(`${dataHolder.course} - ${dataHolder.term}`);
      difficultyAvgList.push(
        average(dummyGradeData.concat(dataHolder.difficultyList)) + 3
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
    const commentList = [];

    for (let dataHolder of profData) {
      courseList.push(`${dataHolder.course} - ${dataHolder.term}`);
      perceptionAvgList.push(
        average(dummyGradeData.concat(dataHolder.perceptionList)) + 7
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
