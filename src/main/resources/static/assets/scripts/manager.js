loadPlayers();
loadUser();
$("#logout").click(logout);
$("#newGame").click(createGame);

function logout(event) {
    event.preventDefault();
    var username = $("#userLogIn").val();
    var password = $("#passwordLogIn").val();

    $.post("/logout")
        .done(function() {window.location.href = "/index.html"})
        .fail(function() {console.log("Log Out failed.")});
}

function loadPlayers() {
    $.get("api/players")
        .done(function(players) {
        showLeaderBoard(players);
    })
        .fail(function( jqXHR, textStatus ) {
        console.log( "Failed: " + textStatus );
    });
}

function loadUser() {
    $.get("api/games")
        .done(function(gameInfo) {
        var user = gameInfo.player;
        printCurrentUserInfo(user);
    })
        .fail(function( jqXHR, textStatus ) {
        console.log( "Failed: " + textStatus );
    });
}

function printCurrentUserInfo(user) {
    var user = createPlayerProfile(user, "assets/images/pirate_user.png");
    $("#userInfo").append(user);
}

function showLeaderBoard(players) {
    players.sort(compare).map(function(player, i) {
        var username = player.username;
        var row = $("<tr></tr>")
        .append($("<td></td>")
                .text(player.username))
        .append($("<td></td>")
                .text(player.total))
        .append($("<td></td>")
                .text(player.won))
        .append($("<td></td>")
                .text(player.lost))
        .append($("<td></td>")
                .text(player.tied));
        $("#leaderBoard").append(row);
    });
}

function compare(p1,p2) {
    return p2.total - p1.total;
}

function getDate(game) {
    return moment(game.created).format("lll");
}

function createGame() {
    var timeStamp = new Date();
    $.post("/api/games", { timeStamp: timeStamp})
        .done(function(data) {window.location.href = "/game_view.html?part=" + data.participation;})
        .fail(function(data) {console.log("Game creation failed.");});
}

function joinGame(el) {
    var game = el.value.slice(8);
    var timeStamp = new Date();
    $.post("/api/games/" + game + "/players", { timeStamp: timeStamp})
        .done(function(data) {window.location.href = "/game_view.html?part=" + data.participation;})
        .fail(function(data) {console.log("Couldn't join game: " + JSON.stringify(data));});
}