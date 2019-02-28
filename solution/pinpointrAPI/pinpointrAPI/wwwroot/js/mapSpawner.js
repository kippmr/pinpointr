var __map;

var __startingCoords = [43.240, -79.848];
var __zoomLevel = 5;
var __shapefileLayers = [];
var __activatedLayers = [];

function addDebugButton() {
    L.easyButton("Debug", function() {
        console.log("Debug enabled");
    },
    "Enable debug/test mode").addTo(__map);
}

function initMapData() {
    var __map = L.map('map').setView(__startingCoords, __zoomLevel);
    console.log("map created")
    L.esri.basemaplayer("Topographic").addTo(__map);
    console.log("topographic layer added");
    console.log("Map initalized");
}

function main() {
    getToken().then( function(tokenData) {
        accessToken = JSON.parse(tokenData).access_token;
        initMapData();

        __map.invalidateSize();

        addDebugButton();
    });
}

initMapData();