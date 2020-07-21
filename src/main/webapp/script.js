function makeGraph() {
  fetch('/graph')
    .then((response) => response.json())
    .then((data) => {
      dataObject = JSON.parse(data);
      console.log(dataObject);
    });
}