/* using IIFE https://flaviocopes.com/javascript-iife/ */
(() => {
  google.charts.load("current", { packages: ["corechart"] });
  google.charts.load("current", { packages: ["bar"] });

  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  const profKey = urlParams.get("prof-key");
  const profName = urlParams.get("prof-name");

  function fillTitles() {
    fetch(`/prof-data?prof-key=${profKey}`)
      .then((response) => response.json())
      .then((profInfo) => {
        document.getElementById("prof-name").innerHTML = profName;
        console.log(profInfo);
      });
  }

  document.addEventListener("DOMContentLoaded", function () {
    fillTitles();
  });
})();
