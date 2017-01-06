loadGames();

function loadGames() {
    $.get("api/games")
        .done(function(gameInfo) {
        printGameTags(gameInfo.games);
        printCurrentUserInfo(gameInfo.player);
    })
        .fail(function( jqXHR, textStatus ) {
        console.log( "Failed: " + textStatus );
    });
}

//REFACTOR PENDING
function printGameTags(games) {
    games.map(function(game) {
        var header = "Game " + game.id;
        var date = getDate(game);
        var participants = getParticipants(game);
        var name1 = participants[0].player.username.slice(0,14);
        var name2 = getEmptyPlayer();
        var avatar1 = "assets/images/pirate_user.png";
        var avatar2 = getEmptyAvatar();

        if(participants.length == 2) {
            name2 = participants[1].player.username.slice(0,14);
            avatar2 = "assets/images/pirate_user.png";
        }

        var player1 = createPlayerProfile(name1, avatar1);
        var player2 = createPlayerProfile(name2, avatar2);

        var gameHTML = $("<div></div>").addClass("gameBlock")
        .append($("<h2></h2>").text(header))
        .append($("<div></div>").addClass("gameParticipants")
                .append($("<div></div>").html(player1)).addClass("user")
                .append($("<p></p>").text("vs"))
                .append($("<div></div>").html(player2)).addClass("user"))
        .append($("<p></p>").text(date).addClass("date"))

        $("#games").append(gameHTML);
    });
}

function getDate(game) {
    return moment(game.created).format("lll");
}

function getParticipants(game) {
    var participants = [];
    game.participants.forEach(function(participant) {
        participants.push(participant);
    });
    return participants;
}

function getEmptyAvatar() {
    return "assets/images/empty_user.png";
}

function createPlayerProfile(name, avatar) {
    var player = "<img src=" + avatar + " alt=\"user\">" + "<p>" + name + "</p>";
    return player;
}