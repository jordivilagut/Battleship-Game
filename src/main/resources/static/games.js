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
    data.forEach(function(game) {
        var date = getDate(game);
        var li = $("<li></li>")
            .append($("<h2></h2>")
            .text(date));
        for(var i = 0; i < game.participants.length; i++) {
            var player = game.participants[i].player.username;
            li.append($("<p></p>").text(player));
        }
        $("#gamesList").append(li);
    });
 }

 function getDate(game) {
 return moment(game.created).format("lll");
 }