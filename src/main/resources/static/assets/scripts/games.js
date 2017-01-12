loadGames();
$("#loginPrompt").click(function() {$("#login").removeClass("hidden")});
$("#registerPrompt").click(function() {$("#register").removeClass("hidden")});
$("#loginForm").submit(login);
$("#registerForm").submit(register);

function login(event) {
    event.preventDefault();
    var username = $("#userLogIn").val();
    var password = $("#passwordLogIn").val();

    $.post("/login", { username: username, password: password })
        .done(function() {window.location.href = "/manager.html"})
        .fail(function() {console.log("Log In failed.")});
}

function register(event) {
    event.preventDefault();
    var username = $("#userRegister").val();
    var password = $("#passwordRegister").val();

    $.post("/api/players", { username: username, password: password })
        .done(function() {
        $("#register").addClass("hidden");
        alert("Sign Up successful!");
    })
        .fail(function() {console.log("Sign Up failed.");});
}

function loadGames() {
    $.get("api/games")
        .done(function(gameInfo) {
        var user = gameInfo.player;
        var games = gameInfo.games;
        printGameTags(user, games);
    })
        .fail(function( jqXHR, textStatus ) {
        console.log( "Failed: " + textStatus );
    });
}

//PRINTGAMETAGS METHOD REFACTOR PENDING
function printGameTags(user, games) {
    games.map(function(game) {
        var header = "Game " + game.id;
        var date = getDate(game);
        var participants = getParticipants(game);
        var name1 = participants[0].player.username;
        var name2 = "/.../"
        var avatar1 = "assets/images/pirate_user.png";
        var avatar2 = getEmptyAvatar();
        var url = getGameUrl(user, game);

        if(participants.length == 2) {
            name2 = participants[1].player.username;
            avatar2 = "assets/images/pirate_user.png";
        }

        var player1 = createPlayerProfile(name1.slice(0,14), avatar1);
        var player2 = createPlayerProfile(name2.slice(0,14), avatar2);

        var gameHTML = $("<a></a>").attr("href", url).addClass("gameBlock")
        .append($("<h2></h2>").text(header))
        .append($("<div></div>").addClass("gameParticipants")
                .append($("<div></div>").html(player1)).addClass("user")
                .append($("<p></p>").text("vs"))
                .append($("<div></div>").html(player2)).addClass("user"))
        .append($("<p></p>").text(date).addClass("date"));

        if(participants.length < 2 && (user != "guest" && user != name1)) {
            gameHTML.append($("<button></button>")
                            .attr("onclick","return joinGame(this);")
                            .attr("value","joinGame" + game.id)
                            .text("Join"));
        }

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

function getGameUrl(user, game) {
    var url = "#"
    game.participants.forEach(function(participant) {
        if(user == participant.player.username){url = "/game_view.html?part=" + participant.id};
    });
    return url;
}

function createPlayerProfile(name, avatar) {
    var player = "<img src=" + avatar + " alt=\"user\">" + "<p>" + name + "</p>";
    return player;
}