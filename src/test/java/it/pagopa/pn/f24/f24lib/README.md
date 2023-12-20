Questo sviluppo è stato implementato per valutare la correttezza dell'integrazione del microservizio pn-f24 con le funzionalità esposte dalla libreria F24lib.
Il sistema consente di consumare una serie di file JSON contenenti metadati di F24, verificandone l'accuratezza secondo i criteri stabiliti nel microservizio pn-f24 e sfruttando le classi esposte dalla libreria F24Lib, e segnalando eventuali errori nei campi.
Successivamente, se in seguito alla verifica dei metadati non vengono riscontrati errori, si procede con la generazione di un modello F24 in formato PDF.
A seguire, viene effettuata l'estrazione dei dati dal PDF generato, al fine di verificare l'assenza di incongruenze.

Il sistema legge ed analizza il nome del file del metadato json e prova ad estrarre delle keyword (parole chiave che sottointendono delle condizioni da verificare, separate da "_") appartenenti ad un set predefinito descritto in seguito, per generare delle asserzioni.
Ma è anche possibile utilizzare dei casi di test che nel nome del file non prevedano le suddette keyword. In quel caso il test verifica che il metadato sia valido, senza entrare nel dettaglio della tipologia di errore riscontrato.

## TestCase con keywords
1) Se l'obiettivo è verificare uno o più errori di validazione nel dettaglio, è possibile inserire come nome del file da testare una o più keyword. In questa modalità per superare il test con successo è necessario che le condizioni relative alle keyword siano verificate.
Queste sono le keyword accettate:

* VALID  Con questa keyword ci si aspetta che l'esito della validazione sia positivo.
* INVALID              Con questa keyword ci si aspetta che l'esito della validazione sia negativo.
* INVALID-APPLY_COST   Con questa keyword ci si aspetta che l'esito della validazione sia negativo. Nello specifico si cerca un errore di validazione relativo all'incongruenza del flag applyCost fornito come input del test e la presenza/assenza del suddetto flag nel JSON
* INVALID-PARSING      Con questa keyword ci si aspetta che l'esito della validazione sia negativo. Nello specifico si cerca un errore di sintassi nel JSON, riscontrato dal validatore sintattico di pn-f24.
* INVALID-METADATA     Con questa keyword ci si aspetta che l'esito della validazione sia negativo. Nello specifico si cerca un errore di sintassi nel JSON, riscontrato dal validatore sintattico della libreria pnf24Lib.
* INVALID-MULTITYPE    Con questa keyword ci si aspetta che l'esito della validazione sia negativo. Nello specifico si cerca un errore che riporta presenza di più tipi di metadato all'interno del metadato (es standard + simplified).

## TestCase senza keywords
2) La seconda casistica consente di consumare un file json che non presenta delle keyword nel nome. In questa modalità il test tornerà esito positivo se nessun errore viene riscontrato, altrimenti il test fallirà e verrà mostrato l'issueList popolata con eventuali errori trovati.

Per aggiungere casi di test basta inserire nel metodo di test nella classe F24LibIt nell'annotation @CsvSource una nuova riga con due parametri separati da ",": nome del file JSON e flag che descrive se il metadato contiene o meno l'applyCost.

NB: i file json devono essere posizionati sotto la folder _src/test/resources/metadata._