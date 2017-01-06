loadPlayers();

function loadPlayers() {
    $.get("api/players")
        .done(function(players) {
        showLeaderBoard(players);
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

function getEmptyPlayer() {
    return "<button>Join!</button>";
}
