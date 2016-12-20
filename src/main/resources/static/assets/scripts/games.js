loadData();

function loadData() {
    $.get("api/games")
        .done(function(data) {
        wrapGameInHTML(data);
    })
        .fail(function( jqXHR, textStatus ) {
        showOutput( "Failed: " + textStatus );
    });
}

function wrapGameInHTML(data) {
    data.map(function(game, i) {
        var date = getDate(game);
        var li = $("<li></li>").append($("<h2></h2>").text(date));

        game.participants.map(function(participant, i) {
            var player = participant.player.username;
            li.append($("<p></p>").text(player));
        })

        $("#gamesList").append(li);
    });
}

function getDate(game) {
    return moment(game.created).format("lll");
}