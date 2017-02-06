createGrid("shipGrid");
createGrid("salvoGrid");
setInterval(getViewData, 3000);
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
var turn;

function activateButtons() {
    $("#toGames").click(function() {window.location.href = "/manager.html"});
    $("#placeShip").click(placeShip);
    $("#fireSalvos").click(placeSalvos);
    $("#shipType").change(deselectAll);
    $("#shipGridBtn").click(changeGridDisplay);
    $("#salvoGridBtn").click(changeGridDisplay);
}

function changeGridDisplay() {
    $("#shipGrid").toggleClass("hidden");
    $("#salvoGrid").toggleClass("hidden");
    $("#shipGridBtn").toggleClass("active");
    $("#salvoGridBtn").toggleClass("active");
}

function enablePlacingShips() {
    $(".shipGridCell").click(selectShipPosition);
    $("th").unbind("click");
}

function enablePlacingSalvos() {
    $(".salvoGridCell").click(selectSalvoPosition);
    $("th").unbind("click");
}

function disablePlacingShips() {
    $(".shipGridCell").unbind("click");
    $("#shipType").prop("disabled", true);
    $("input[name=orientation]").prop("disabled", true);
    $("#placeShip").prop("disabled", true);
}

function disablePlacingSalvos() {
    $(".salvoGridCell").unbind("click");
}

function selectShipPosition() {
    $(".shipGridCell").removeClass("preSelected");
    $(".shipGridCell").removeClass("wrongSelected");
    var cellId = $(this).attr("id").slice(8);
    var orientation = $("input[name=orientation]:checked").val();

    if (orientation == "vertical") {
        selectSubsequentVertical(cellId);
    } else {
        selectSubsequentHorizontal(cellId);
    }
}

function selectSubsequentVertical(cellId) {
    var rowHeaders = ["", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K"];
    var locations = [];
    var col = cellId.slice(1);
    var row = rowHeaders.indexOf(cellId.slice(0,1));
    var shipLength = ships[$("#shipType").val()].length;

    for( var i = row; i < row + shipLength; i++) {
        locations.push(rowHeaders[i] + col)
        $("#shipGrid" + rowHeaders[i] + col).addClass("preSelected");
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
        $("#shipGrid" + row + i).addClass("preSelected");
    }

    validateLocations(locations);
}

function selectSalvoPosition() {
    var cellId = $(this).attr('id').slice(9);
    getPreselectedSalvoLocations(cellId);

    $(this).toggleClass("preSelected");

}

function getPreselectedSalvoLocations(cellId) {
    var rows = 10;
    var cols = 10;
    var rowHeaders = ["", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J"];
    var preselectedSalvos = [];

    if(!$("#salvoGrid" + cellId).hasClass("preSelected")) {
        preselectedSalvos.push(cellId);
    }

    for(var i = 1; i <= rows ; i++){
        for(var j = 1; j <= cols; j++){
            var newSalvoLocation = "#salvoGrid" + rowHeaders[i] + j;
            if($(newSalvoLocation).hasClass("preSelected") && newSalvoLocation != "#salvoGrid" + cellId) {
                preselectedSalvos.push(newSalvoLocation.slice(11));
            }
        }
    }

    if(preselectedSalvos.length == 5) {$("#fireSalvos").prop("disabled", false);} else {$("#fireSalvos").prop("disabled", true);}
}

function deselectAll() {
    $(".shipGridCell").removeClass("preSelected");
    $(".shipGridCell").removeClass("wrongSelected");
    $("#placeShip").prop("disabled", true);
}

function validateLocations(locations) {

    $("#placeShip").prop("disabled", false);

    if (locations[locations.length-1].slice(1) > 10 || locations.indexOf("K" + locations[0].slice(1)) != -1) {
        locations.forEach(function(location) {
            $("#placeShip").prop("disabled", true);
            $("#shipGrid" + location).removeClass("preSelected");
            $("#shipGrid" + location).addClass("wrongSelected");
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

function displayGameStatus(code) {
    switch(code) {
        case 1:
            statusMessage = "Place ships";
            $("#statusImage").addClass("hidden");
            break;
        case 2:
            statusMessage = "Wait for opponent to place ships.";
            $("#statusImage").removeClass("hidden");
            break;
        case 3:
            statusMessage = "Enter salvo";
            $("#statusImage").addClass("hidden");
            break;
        case 4:
            statusMessage = "Wait for opponent to enter salvos";
            $("#statusImage").removeClass("hidden");
            break;
        case 5:
            statusMessage = "Game over";
            $("#statusImage").addClass("hidden");
            $("#gameOver").removeClass("hidden");
            $.post({
                url: "api/game_view/" + partId +"/scores",
            })
                .done(function (response, status, jqXHR) {
                getViewData();
                console.log(response);
            })
                .fail(function (jqXHR, status, httpError) {
                console.log("Failed: " + status + " " + httpError);
            })
            break;
    }

    console.log("Game Status: " + statusMessage);
    $("#statusBoard").text(statusMessage)
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
        var userHits = user.hitShips;
        var opponent = data.players.filter(function(player) {return player.id != id;})[0];
        var opponentName = "opponent";
        var opponentSalvos = [];
        var opponentHits = [];
        turn = user.salvos.length + 1;
        var opponentTurn = opponent.salvos.length + 1;

        if(shipNumber > 4) {disablePlacingShips();}

        if(opponent !== undefined) {
            opponentSalvos = opponent.salvos;
            opponentName = opponent.player.username;
            opponentHits = opponent.hitShips;
        }

        printShips(ships);
        printViewData(username, opponentName);

        if(turn == opponentTurn) {
            printElement(userSalvos, "salvo", "salvo");
            printElement(opponentSalvos, "ship", "hiddenSalvo");
            printElement(opponentHits, "ship", "hit");
            printElement(userHits, "salvo", "hit");
            printPlacedShips(opponentHits, "#shipList");
            printPlacedShips(userHits, "#opponentShipList");
            displayGameStatus(data.status);
        }
    })
        .fail(function( jqXHR, textStatus ) {
        console.log("Request Failed");
    });
}

function printPlacedShips(ships, element) {
    $(element).html("");
    ships.forEach(function(ship) {
        $(element).append($("<tr></tr>")
                          .append($("<td></td>").html("<img src=\"assets/images/battleship.png\">"))
                          .append($("<td></td>").text(ship.locations.length))
                          .append($("<td></td>").text(ship.left))
                          .append($("<td></td>").text(ship.sunk)));
    })
}

function printElement(element, grid, css) {
    element.forEach(function(item, i){
        var loc = item.locations;
        loc.forEach(function(location) {
            $("#" + grid + "Grid" + location).addClass(css);
            $("#" + grid + "Grid" + location).text(item.turn);        
        })
    })
}

function printShips(ships) {
    ships.forEach(function(ship){
        var loc = ship.locations;
        var orientation = "V";


        if(loc[0].slice(0,1) == loc[1].slice(0,1)) { orientation = "H"}

        loc.forEach(function(location, i) {

            switch(i) {
                case 0:
                    $("#shipGrid" + location).addClass("shipStart" + orientation);
                    break;
                case ship.locations.length - 1:
                    $("#shipGrid" + location).addClass("shipEnd" + orientation);
                    break;
                default:
                    $("#shipGrid" + location).addClass("shipCenter" + orientation);
                    break;
            }

        })
    })
}

function printViewData(user, opponent) {
    $("#user").html(user);
    $("#opponent").html(opponent);
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

    event.preventDefault();
    var shipCategory = ships[$("#shipType").val()].category;
    var locations = getPreselectedLocations("#shipGrid");

    $.post({
        url: "api/game_view/" + partId +"/ships", 
        data: JSON.stringify({ category: shipCategory, locations: locations}),
        contentType: "application/json"
    })
        .done(function (response, status, jqXHR) {
        getViewData();
        console.log(response);
    })
        .fail(function (jqXHR, status, httpError) {
        console.log("Failed: " + status + " " + httpError);
    })
}

function placeSalvos(event) {

    event.preventDefault();
    var locations = getPreselectedLocations("#salvoGrid");

    $.post({
        url: "api/game_view/" + partId +"/salvos", 
        data: JSON.stringify({ turn: turn, locations: locations}),
        contentType: "application/json"
    })
        .done(function (response, status, jqXHR) {
        getViewData();
        $(".salvoGridCell").removeClass("preSelected");
        console.log(response);
    })
        .fail(function (jqXHR, status, httpError) {
        console.log("Failed: " + status + " " + httpError);
    })
}