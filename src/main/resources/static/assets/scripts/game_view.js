createGrid("topGrid");
createGrid("bottomGrid");
getViewData();
activateButtons();
enablePlacingShips();
enablePlacingSalvos();
var partId = location.search.slice(6);
var ships = [
    {
        category: "Aircraft Carrier",
        length: 5
    }, 
    {
        category: "Battleship",
        length: 4
    },
    {
        category: "Submarine",
        length: 3
    },
    {
        category: "Destroyer",
        length: 3
    },
    {
        category: "Patrol Boat",
        length: 2
    }
];

function activateButtons() {
    $("#toGames").click(function() {window.location.href = "/manager.html"});
    $("#placeShip").click(placeShip);
    $("#fireSalvos").click(placeSalvos);
    $("#shipType").change(deselectAll);
    $("#shipGridBtn").click(toggleGrid);
    $("#salvoGridBtn").click(toggleGrid);
}

function toggleGrid() {
    $("#topGrid").toggleClass("hidden");
    $("#bottomGrid").toggleClass("hidden");
    $("#shipGridBtn").toggleClass("active");
    $("#salvoGridBtn").toggleClass("active");
}

function enablePlacingShips() {
    $(".topGridCell").click(selectShipPosition);
}

function enablePlacingSalvos() {
    $(".bottomGridCell").click(selectSalvoPosition);
}

function disablePlacingShips() {
    $(".topGridCell").unbind("click");
    $("#shipType").prop("disabled", true);
    $("input[name=orientation]").prop("disabled", true);
    $("#placeShip").prop("disabled", true);
}

function disablePlacingSalvos() {
    $(".bottomGridCell").unbind("click");
}

function selectShipPosition() {
    $(".topGridCell").removeClass("preSelected");
    $(".topGridCell").removeClass("wrongSelected");
    var cellId = $(this).attr("id").slice(7);
    var orientation = $("input[name=orientation]:checked").val();

    if (orientation == "vertical") {
        selectSubsequentVertical(cellId);
    } else {
        selectSubsequentHorizontal(cellId);
    }
}

function selectSalvoPosition() {

    var cellId = $(this).attr('id').slice(10);
    getPreselectedSalvoLocations(cellId);

    $(this).toggleClass("preSelected");

}

function getPreselectedSalvoLocations(cellId) {
    var rows = 10;
    var cols = 10;
    var rowHeaders = ["", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J"];
    var preselectedSalvos = [];
    
    if(!$("#bottomGrid" + cellId).hasClass("preSelected")) {
        preselectedSalvos.push(cellId);
    }

    for(var i = 1; i <= rows ; i++){
        for(var j = 1; j <= cols; j++){
            var newSalvoLocation = "#bottomGrid" + rowHeaders[i] + j;
            if($(newSalvoLocation).hasClass("preSelected") && newSalvoLocation != "#bottomGrid" + cellId) {
                preselectedSalvos.push(newSalvoLocation.slice(11));
            }
        }
    }
    
    if(preselectedSalvos.length == 5) {$("#fireSalvos").prop("disabled", false);} else {$("#fireSalvos").prop("disabled", true);}
}

function deselectAll() {
    $(".topGridCell").removeClass("preSelected");
    $(".topGridCell").removeClass("wrongSelected");
    $("#placeShip").prop("disabled", true);
}

function selectSubsequentVertical(cellId) {
    var rowHeaders = ["", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K"];
    var locations = [];
    var col = cellId.slice(1);
    var row = rowHeaders.indexOf(cellId.slice(0,1));
    var shipLength = ships[$("#shipType").val()].length;

    for( var i = row; i < row + shipLength; i++) {
        locations.push(rowHeaders[i] + col)
        $("#topGrid" + rowHeaders[i] + col).addClass("preSelected");
    }

    validateLocations(locations);
}

function selectSubsequentHorizontal(cellId) {
    var locations = [];
    var col = parseInt(cellId.slice(1));
    var row = cellId.slice(0,1);
    var shipLength = ships[$("#shipType").val()].length;

    for( var i = col; i < col + shipLength; i++) {
        locations.push(row + i);
        $("#topGrid" + row + i).addClass("preSelected");
    }

    validateLocations(locations);
}

function validateLocations(locations) {

    $("#placeShip").prop("disabled", false);

    if (locations[locations.length-1].slice(1) > 10 || locations.indexOf("K" + locations[0].slice(1)) != -1) {
        locations.forEach(function(location) {
            $("#placeShip").prop("disabled", true);
            $("#topGrid" + location).removeClass("preSelected");
            $("#topGrid" + location).addClass("wrongSelected");
        })
    }  
}

function createGrid(element) {
    var rows = 10;
    var cols = 10;
    var rowHeaders = ["", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J"];

    for(var i = 0; i <= rows ; i++){
        var row = document.createElement("tr");
        $("#" + element).append(row);

        for(var j = 0; j <= cols; j++){
            var col = document.createElement("td");
            if(j === 0){
                col = document.createElement("th");
                col.innerHTML = rowHeaders[i];
            }
            else if(i === 0){
                col = document.createElement("th");
                col.innerHTML = j;
            }
            col.setAttribute("id", element + rowHeaders[i] + j);
            col.setAttribute("class", element + "Cell");
            row.append(col);
        }
    }
}

function getParameterFromUrl(parameter) {
    var url = window.location.href;
    parameter = parameter.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + parameter + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function getViewData() {
    var id = getParameterFromUrl("part")

    $.get("api/game_view/" + id)
        .done(function(data) {

        var ships = data.ships;
        var shipNumber = data.ships.length;
        var user = data.players.filter(function(player) {return player.id == id;})[0];
        var username = user.player.username;
        var userSalvos = user.salvos;
        var opponent = data.players.filter(function(player) {return player.id != id;})[0];
        var opponentName = "opponent";
        var opponentSalvos = [];

        getPlacedShips(ships);
        if(shipNumber > 4) {disablePlacingShips();}

        if(opponent !== undefined) {
            opponentSalvos = opponent.salvos;
            opponentName = opponent.player.username;
        }

        printElement(ships, "top", "ship");
        printElement(userSalvos, "bottom", "salvo");
        printElement(opponentSalvos, "top", "hiddenSalvo");
        printViewData(username, opponentName);
        printHits();
    })
        .fail(function( jqXHR, textStatus ) {
        console.log("Request Failed");
    });
}

function getPlacedShips(ships) {
    $("#shipList").html("");
    ships.forEach(function(ship) {
        $("#shipList").append($("<li></li>").text(ship.category));
    })
}

function printElement(element, grid, css) {
    element.forEach(function(item){
        var loc = item.locations;
        loc.forEach(function(location) {
            $("#" + grid + "Grid" + location).addClass(css);
            $("#" + grid + "Grid" + location).text(item.turn);
        })
    })
}

function printViewData(user, opponent) {
    $("#user").html(user);
    $("#opponent").html(opponent);
}

function printHits() {
    var rowHeaders = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"];

    for(var i = 1; i <= 10; i++) {
        for(var j = 0; j< 10; j++) {
            var cell = "#topGrid" + rowHeaders[j] + i
            if($(cell).hasClass("ship") && $(cell).hasClass("hiddenSalvo")) {
                $(cell).removeClass("hiddenSalvo");
                $(cell).addClass("salvo");
            };
        }
    }
}

function getPreselectedLocations(grid) {
    var rows = 10;
    var cols = 10;
    var rowHeaders = ["", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J"];
    var locations = [];

    for(var i = 1; i <= rows ; i++){
        for(var j = 1; j <= cols; j++){
            if($(grid + rowHeaders[i] + j).hasClass("preSelected")) {
                locations.push(rowHeaders[i] + j);
            }
        }
    }
    return locations;
}

function placeShip(event) {

    var shipCategory = ships[$("#shipType").val()].category;
    var locations = getPreselectedLocations("#topGrid");
    event.preventDefault();

    $.post({
        url: "api/game_view/" + partId +"/ships", 
        data: JSON.stringify({ category: shipCategory, locations: locations}),
        contentType: "application/json"
    })
        .done(function (response, status, jqXHR) {
        window.location.href = "/game_view.html?part=" + partId;
        console.log(response);
    })
        .fail(function (jqXHR, status, httpError) {
        console.log("Failed: " + status + " " + httpError);
    })
}

function placeSalvos(event) {

    var locations = getPreselectedLocations("#bottomGrid");
    event.preventDefault();

    $.post({
        url: "api/game_view/" + partId +"/salvos", 
        data: JSON.stringify({ turn: 1, locations: locations}),
        contentType: "application/json"
    })
        .done(function (response, status, jqXHR) {
        window.location.href = "/game_view.html?part=" + partId;
        //$("#salvoGridBtn").click();
        console.log(response);
    })
        .fail(function (jqXHR, status, httpError) {
        console.log("Failed: " + status + " " + httpError);
    })
}