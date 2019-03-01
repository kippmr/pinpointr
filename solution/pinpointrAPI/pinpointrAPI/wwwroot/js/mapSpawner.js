var __map;
var __startingCoords = [43.2633, -79.918];
var __zoomLevel = 15;
var __shapefileLayers = [];
var __activatedLayers = [];
var __debug = true;
var __points = [];
var singleSubmissionURL = "/api/Submission/GetSubmission/"
var allSubmissionURL = "/api/Submission/GetAllSubmissions"
var getTagsURL = "/api/Submission/GetTags/"
var lastSelected;

var previewPoint = {
    "id": 12,
    "user_id": 0,
    "image_url": "30ee03f5-4955-4bcd-9430-0018ef8d7778",
    "coordinates": { "x": 3.0, "y": 3.0 },
    "altitude": 0.0,
    "is_completed": false,
    "obs_est": "2019-02-11T00:00:00",
    "gen_est": "2019-02-10T00:00:00"
};

var htmlTemplate = {
    submissionID: "Submission ID:",
    userID: "Author ID:",
    imgWrap: ["<img class=\"popupimg\" src=\"", "\">"],
    tagWrap: "Tags:",
    genWrap: "Submitted at:",
    obsWrap: "Obsolete by:"
}

function addDebugButton() {
    //console.log(__map);
    L.easyButton("D", function() {
        __debug = true;
    },
    "Enable debug/test mode").addTo(__map);
}

function initMapData() {
    __map = L.map('map').setView(__startingCoords, __zoomLevel);
    console.log("map created")
    L.esri.basemapLayer("Topographic").addTo(__map);
    console.log("topographic layer added");
    console.log("Map initalized");
}

function pointClick(e) {
    console.log(e);
    lastSelected = e.target._leaflet_id;
}

function completeTask() {
    completedID = lastSelected;
    //TODO
    //mark tasks as completed
}

function makeImgUrl(s3UUID) {
    return "https://s3.us-east-2.amazonaws.com/pinpointrbucket/" + s3UUID;
}

function getAllPoints() {
    var getPromise = $.get(allSubmissionURL, function (data, status) {
        //console.log(data);
    });

    var realPromise = Promise.resolve(getPromise);
    realPromise.then(function (val) {
        for (i in val) {

            plotPoint(val[i]);
        }
    })
}

function getPoint(submissionID) {
    var getPromise = $.get(singleSubmissionURL + submissionID, function (data, status) {});

    var realPromise = Promise.resolve(getPromise);
    realPromise.then(function (val) {
        plotPoint(val);
    });
}

function getTags(submissionID) {
    console.log(submissionID);
    var getPromise = $.get(getTagsURL + submissionID, function (data, status) { });

    var realPromise = Promise.resolve(getPromise);
    return getPromise;

}


function plotPoint(submissionData) {

    currentPoint = submissionData;

    if (!currentPoint["is_completed"]) {
        var pointCoords = currentPoint["coordinates"];
        var pos = [pointCoords["y"], pointCoords["x"]];

        var newPoint = L.marker(pos);

        imgurl = makeImgUrl(currentPoint["image_url"]);
        tagPromise = getTags(currentPoint["id"]);
        var realPromise = Promise.resolve(tagPromise);

        realPromise.then(function (val) {
            console.log(val);
            newPoint.bindPopup(htmlTemplate.imgWrap[0] + imgurl + htmlTemplate.imgWrap[1] + "<hr>" +
                htmlTemplate.submissionID + val[0].submission_id + "<br>" +
                htmlTemplate.userID + currentPoint["user_id"] + "<br>" +
                htmlTemplate.genWrap + currentPoint["gen_est"] + "<br>" +
                htmlTemplate.obsWrap + currentPoint["obs_est"] + "<br>" +
                htmlTemplate.tagWrap + val[0].name + "<br>");

            __points.push([newPoint, currentPoint["id"]]);
            newPoint.addTo(__map).on("click", pointClick);
        });
    }
}

function main() {
    getToken().then( function(tokenData) {
        accessToken = JSON.parse(tokenData).access_token;
        initMapData();
        addDebugButton();

        getAllPoints();
    });
}

main();