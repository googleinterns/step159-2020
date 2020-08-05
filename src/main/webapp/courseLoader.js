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
      dataObject = JSON.parse(data.value);
      console.log(dataObject);
    }));
}


setTimeout(function() { populateData(); }, 3000);