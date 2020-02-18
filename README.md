# Anreichern von GPX-Tracks
Komoot ist ein sehr gutes Tool zum Erstellen von Strecken; leider können keine separaten Wegpunkte im zum  Download angebotenen GPX-Track angelegt werden. Diese Lücke soll dieses Tool schließen.

Touren werden in einem Google Docs Sheet gepflegt; Vorlage hier: https://docs.google.com/spreadsheets/d/1trwAN0YqZUmUHpih8J-7N2u5NIDqv_lOZZ2vf2n-C7I/

Anschließend muss nach folgender Anleitung eine credentials.json erstellt und in src/main/resources gespeichert werden: https://developers.google.com/docs/api/quickstart/java

Jetzt noch die Konstanten in TrackCreator anpassen - fertig! (COOKIE ist der Inhalt des Cookies, der beim Download an Komoot gesendet wird.)

Nun kann die Anwendung z.B. mit ```gradle run``` gestartet werden.