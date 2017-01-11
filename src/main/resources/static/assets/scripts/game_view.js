createGrid("topGrid");
createGrid("bottomGrid");
getViewData();
$("#toGames").click(function() {window.location.href = "/manager.html"});

function createGrid(element){
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
        var user = data.players.filter(function(player) {return player.id == id;})[0];
        var username = user.player.username;
        var userSalvos = user.salvos;
        var opponent = data.players.filter(function(player) {return player.id != id;})[0];
        var opponentName = "opponent";
        var opponentSalvos = [];
        
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
        for(j = 0; j< 10; j++) {
            var cell = "#topGrid" + rowHeaders[j] + i
            if($(cell).hasClass("ship") && $(cell).hasClass("hiddenSalvo")) {
                $(cell).removeClass("hiddenSalvo");
                $(cell).addClass("salvo");
            };
        }
    }
}