# txc-maker Deck API

The Deck API should support the following user flow:

1. User puts audio files and a supported CSV file in their Google Drive.
2. User submits a form on the txc-maker website with the Deck metadata and the locations of the files in their Google Drive.
3. User previews their deck on the txc-maker website, and is able to see warnings and error messages where applicable
4. User finalizes their deck on the txc-maker website, and is able to retrieve the .txc file from their Google Drive.

At the time of writing, the txc-maker server provides these endpoints but responds with fake information. Integration with Google services is an ongoing effort.

## TODO

* define format of errors/warnings
* determine Unsuccessful responses for POST, PUT of /api/decks/{id}

## Endpoints

### /api/decks

* **GET** : Retrieve all decks currently stored on the server
  * Response:
    * Array of decks as JSON
* **POST** : Import a deck
  * Request Body:
    * Deck as JSON
  * Successful Response:
    * Status 201
    * Headers:
      * Location -> ".../api/decks/{id}"
    * Body :
      ```json
      {
        "errors": [],
        "warnings": ["warning"],
        "id": 10
      }
      ```
  * Unsuccessful Response:
    * Status 400
    * Body :
      ```json
      {
        "errors": ["error"],
        "warnings": ["warning1", "warning2"],
        "id": -1
      }
      ```

### /api/decks/{id}

This endpoint can always respond with a status 404 and an empty body when the id doesn't match anything.

* **GET** : Retrieve deck by id
  * Successful Response:
    * Status 200
    * Body:
      * Deck as JSON
* **PUT** : Update deck by id
  * Request Body:
    * Deck as JSON
* **POST** : Publish deck
