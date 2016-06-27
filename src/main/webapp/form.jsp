<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>

  <head>
    <title>Deck Maker</title>
    <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons" />
    <link rel="stylesheet" href="https://code.getmdl.io/1.1.3/material.indigo-pink.min.css" />
    <link rel="stylesheet" href="http://fonts.googleapis.com/css?family=Roboto:300,400,500,700" type="text/css">
    <link rel="stylesheet" href="<c:url value="/resources/form.css" />" />
    <script defer src="https://code.getmdl.io/1.1.3/material.min.js"></script>
  </head>

  <body>

    <div class="mdl-layout mdl-js-layout mdl-layout--fixed-header">
      <header class="mdl-layout__header deckMakerHeader">
        <div class="mdl-layout__header-row">
          <h4>Deck Maker: Import Data</h4>
        </div>
      </header>

      <main class="mdl-layout__content">

        <div class="mdl-grid">
          <div class="mdl-cell mdl-cell--6-col formBody">
            <form method="post">

              <h6>Deck Name</h6>
              <p>An effective deck name explains who the deck is for and where it's most useful.</p>
              <div class="mdl-textfield mdl-js-textfield">
                <input class="mdl-textfield__input" type="text" id="deckName" name="deckName">
                <label class="mdl-textfield__label" for="deckName">Deck Name</label>
              </div>

              <h6>Publisher</h6>
              <p>Your organisation and/or your name.</p>
              <div class="mdl-textfield mdl-js-textfield">
                <input class="mdl-textfield__input" type="text" id="publisher" name="publisher">
                <label class="mdl-textfield__label" for="publisher">Publisher</label>
              </div>

              <h6>Deck ID</h6>
              <p>The unique identifier for the deck.</p>
              <div class="mdl-textfield mdl-js-textfield">
                <input class="mdl-textfield__input" type="text" id="deckId" name="deckId">
                <label class="mdl-textfield__label" for="deckId">Deck ID</label>
              </div>

              <h6>Document ID</h6>
              <p>[explanation]</p>
              <div class="mdl-textfield mdl-js-textfield">
                <input class="mdl-textfield__input" type="text" id="docId" name="docId">
                <label class="mdl-textfield__label" for="docId">Document ID</label>
              </div>

              <h6>Audio Directory ID</h6>
              <p>[explanation]</p>
              <div class="mdl-textfield mdl-js-textfield">
                <input class="mdl-textfield__input" type="text" id="audioDirId" name="audioDirId">
                <label class="mdl-textfield__label" for="audioDirId">Audio Directory ID</label>
              </div>

              <h6>License URL</h6>
              <div class="mdl-textfield mdl-js-textfield">
                <input class="mdl-textfield__input" type="text" id="licenseUrl" name="licenseUrl">
                <label class="mdl-textfield__label" for="licenseUrl">License URL</label>
              </div>

              <h6>Deck Locking</h6>
              <div class="mdl-textfield mdl-js-textfield">
                <input class="mdl-checkbox__input" type="checkbox" id="locked" name="locked">
                <label class="mdl-checkbox__label" for="locked">Lock this deck so it cannot be modified</label>
              </div>

              <br />

              <button class="mdl-button mdl-js-button mdl-button--raised submitButton" type="submit">
                Submit
              </button>

            </form>
          </div>
        </div>
      </main>
    </div>

  </body>
</html>
