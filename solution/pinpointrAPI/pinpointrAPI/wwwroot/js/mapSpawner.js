var myMap;


function initMapData() {
    var myMap = L.map('map').setView([51.505, -0.009], 13);
    L.tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
        subdomains: ['a', 'b', 'c']
    }).addTo(myMap);
    console.log("Map initalized");
}

initMapData();