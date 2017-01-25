#TXC Maker User Guide

##Who should use TXC Maker?

TXC Maker is a tool for anyone who is interested in creating a translation deck for the [Translation Cards](https://github.com/translation-cards/translation-card) app.

##What should I have ready before using TXC Maker?
You should have two documents ready before using the TXC Maker:
####1. Copy [this excel template](https://docs.google.com/spreadsheets/d/1AZoI5z_BPuJevtmclW0RCMFE4cJnKFvN1nlkpjdZL_k/edit?usp=sharing) and fill out a line for every translation you'd like to make. 
- Under the Language heading you should fill out the iso code for the language you'd like to use not the language itself. Not sure what the iso code is for your language? [Look here](https://en.wikipedia.org/wiki/List_of_ISO_639-2_codes). For example if the language for my translation is spanish, I would fill out 'es' under the language column for that translation. 
- The label column should hold the english phrase and the translation column should hold the phrase in the language you filled out under language
- Finally, put the name of the audio file you recorded holding that translation under the filename column
- [Here](https://docs.google.com/spreadsheets/d/1ug7jEJB3a0zE3pNdNkE1MIAXS5-3ldBS35hhO-HgYto/edit?usp=sharing) you can view an example of a filled out sheet that is in use for the default deck in the application. 
- Make sure you save your spreadsheet or import it into google drive

####2. Create a folder in your Google drive and put all your audio files holding the recorded translations for your deck in it.

##Using TXC Maker
Navigate to http://translation-cards-dev.appspot.com/#!/DeckImport to get to the deck import page. 
- Input the name of your deck, and the publisher name (organization name or author of deck). 
- Under the document ID input the last part of the URL containing your deck spreadsheet in google drive. For example if the URL with your spreadsheet looks like this https://drive.google.com/drive/u/1/folders/0BzScrGiXtpD_dnBRZ1BOWms5dWM. You just need to input '0BzScrGiXtpD_dnBRZ1BOWms5dWM' under document ID.
- Similarly in the audio directory input the last part of the Google Drive URL containing all your recorded audio translation files. Use the example for document ID to as a reference for what part of the URL to enter.
- Choose your own license and decide whether you want to lock the deck (which means users will not be allowed to add, change, or remove translations from your deck).
- Finally submit and wait for the deck maker to compose your deck into a TXC and catch any possible mistakes on the following screens
- If any errors are caught, they must be resolved before you can publish the deck.
