package dev.dogetime.ui.watch

/**
 * Presses play for the user, so the stream is requested without a tap. Runs in every frame of
 * the player page; frames without a player do nothing.
 *
 * The vidsrc.sh embed nests a landing page (`#player.landing`) whose play button only creates
 * the real player's iframe (`#player_frame`). That player autoplays and requests the stream as
 * soon as it starts, so pressing the landing button is enough. The press is retried until the
 * iframe appears, because the button can exist before the page has attached its click handler.
 * Other sources start playing through their URL instead, and have no such button.
 */
internal val AUTO_PLAY_JS = """
(function () {
  if (window.__dogetimeAutoPlay) return;
  window.__dogetimeAutoPlay = true;
  var tries = 0;
  var timer = setInterval(function () {
    if (++tries > 60 || document.getElementById('player_frame')) return clearInterval(timer);
    if (document.readyState === 'loading') return;
    var button = document.querySelector('#player.landing #bigPlay');
    if (!button || button.style.display === 'none') return;
    console.log('dogetime: pressing play');
    button.click();
  }, 500);
})();
""".trimIndent()
