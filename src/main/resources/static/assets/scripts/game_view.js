createGrid();
getViewData();

function createGrid(){
    var rows = 10;
    var cols = 10;
    var rowHeaders = ["", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J"]

    for(var i = 0; i <= rows ; i++){
        var row = document.createElement("tr");
        $("#grid").append(row);

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
            col.setAttribute("id", "" + rowHeaders[i] + j);
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
        printShips(data.ships);
        printViewData(data.players, id);
    })
        .fail(function( jqXHR, textStatus ) {
        console.log("Request Failed");
    });
}

function printShips(ships) {
    ships.forEach(function(ship){
        var loc = ship.locations;
            loc.forEach(function(location) {
                $("#" + location).addClass("shipCell");
            })
    })
}

function printViewData(players, id) {
    var user = players.filter(function(user) {return user.id == id});
    var opponent = players.filter(function(user) {return user.id != id});
    $("#user").html(user[0].player.username);
    $("#opponent").html(opponent[0].player.username);
}