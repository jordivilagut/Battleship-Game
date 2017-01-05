loadGames();
loadPlayers();

function loadGames() {
    $.get("api/games")
        .done(function(gameInfo) {
            var games = gameInfo.games;
            showGameDetail(games);
    })
        .fail(function( jqXHR, textStatus ) {
            console.log( "Failed: " + textStatus );
    });
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

function showGameDetail(games) {
    games.map(function(game, i) {
        var date = getDate(game);
        var li = $("<li></li>").append($("<h2></h2>").text(date));

        game.participants.map(function(participant, i) {
            var player = participant.player.username;
            var total = participant.player.total;
            var won = participant.player.won;
            var lost = participant.player.lost;
            var tied = participant.player.tied;
            li.append($("<p></p>").text(player));
            li.append($("<table></table>")
                .append($("<tr></tr>")
                    .append($("<th></th>")
                    .text("Total"))
                    .append($("<th></th>")
                    .text("W"))
                    .append($("<th></th>")
                    .text("L"))
                    .append($("<th></th>")
                    .text("T")))
                .append($("<tr></tr>")
                    .append($("<td></td>")
                    .text(total))
                    .append($("<td></td>")
                    .text(won))
                    .append($("<td></td>")
                    .text(lost))
                    .append($("<td></td>")
                    .text(tied))));
        })
        $("#gamesList").append(li);
    });
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