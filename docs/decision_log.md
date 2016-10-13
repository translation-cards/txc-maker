# Decision Log

## Architectural Redesign, Octover 2016
While working on a validation story, [Pat Dale](https://github.com/PatrickDale) and [David Suckstorff](https://github.com/davsucks)
realized that completing the story would be very difficult given the design on hand.

#### The Previous Design

The main domain objects were **Card**s, **Language**s, and **Deck**s. Each class has the following responsibilities:

```java
public class Deck {
    public String deck_label;
    public String publisher;
    public String iso_code;
    public String language_label;
    public String id;
    public long timestamp;
    public String license_url;
    public boolean locked;

    public List<Language> languages;
    public List<Error> errors;
    public transient List<Error> parseErrors;
    private transient Map<String, Language> languageLookup;
}

public class Language {
    public String iso_code;
    public String language_label;
    public List<Card> cards;
}

public class Card {
    public String card_label;
    public String dest_audio;
    public String dest_txt;
    public List<Error> errors;
}
```

The important take away here is that each `Deck` has a list of `Language`s which in turn have a list of `Card`s.
This design makes sense when looking at the preview page of the app. Each deck has tabs for each language it translates into,
and each of those language tabs will display the language's cards.

The validation we needed to do involved checking each card for a specific source phrase to make sure that at least one of the
cards have an audio file associated with it. The preview page visualizes the deck as a series of columns in a table (each language is a column),
with each "row" in the column being an individual card. What the validation requires us to do is take a complete horizontal
slice across the entire table for a specific row. To put it more visually a deck looks similar to this on the preview page:

| Source Phrase  | Arabic        | Spanish      |
| -------------- | ------------- | ------------ |
| Hello          | مرحبا         | Hola         |
| Goodbye        | وداعا         | Adiós        |
| How are you?   | كيف حالك      | ¿Cómo estás? |

Our validation requires that for each source phrase, at least one card associated with that phrase must have an audio file.
With the design at hand, given a specific source phrase, there was no easy way to view each language's card for that phrase.
It seemed that something was missing from this design.

Pat and David sat down and started from scratch discussing what we thought that missing component was and begun reimagining the design
of the app. What they landed on looks like this:

#### The Redesign

```java
public class Deck {
    private String sourceLanguage;
    private String deckLabel;
    private String author;
    private long timestamp;
    private boolean locked;
    private String licenseUrl;
    private String readme;
    private List<Error> parsingErrors;
    private List<Translation> translations;
    private List<String> destinationLanguages;
    private String id;
}

public class Translation {
    private List<Card> cards;
    private String sourcePhrase;
}

public class Card {
    private String sourcePhrase;
    private String destinationAudioFilename;
    private String destinationPhrase;
    private List<Error> errors;
    private Language destinationLanguage;
}
```

Note: `Language` was left unchanged

The biggest change here is that there is now a class called `Translation` which
provides the logical connection between each language's card of a specific
source phrase. Since the commonality between each of the `Card`s in a `Translation`
is their source phrase, we have that field also living in the `Translation`.

We obviously don't want this redesign to impact the format of the .txc file
that is created when anyone publishes their deck and it gets handed off to the
Android app. Therefore right before we create the .txc file we have implemented
some classes to go through transforming from this design to the design discussed
above.
