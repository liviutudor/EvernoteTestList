EvernoteTestList
================

Code to test retrieving evernote notes and interpret them as a list.

Simply connects to an Evernote sandbox account (http://sandbox.evernote.com), retrieve a specified note and interprets the XHTML inside as a list, which is to say it looks for the `<li>` elements.

In order to run this, you need to create a property files called `.evernotetestlist` in your home directory with a single line in it containing the developer token from Evernote:

        devtoken=....token here

Then simply run the app.

Please note that even if the `<li>` elements are in separate lists, they will all be treated as part of a single list.
