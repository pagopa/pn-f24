Questo sviluppo è stato implementato per valutare la correttezza dell'integrazione del microservizio pn-f24 con le funzionalità esposte dalla libreria F24lib.
Questo strumento consente di consumare una serie di file JSON contenenti metadati di F24, verificandone l'accuratezza e segnalando eventuali errori nei campi.
Successivamente, se in seguito alla verifica dei metadati non vengono riscontrati errori, il sistema procede con la generazione di un modello F24 in formato PDF.
A seguire,viene effettuata l'estrazione dei dati dal PDF generato, al fine di verificare l'assenza di incongruenze.

Il sistema estrapola uno o più token a partire dal nome del file JSON. Per token si intende una keyword, 

Per lo sviluppo dei test i integrazione con la libreria di f24 sono state implementate due modalita di utilizzo:



1) se l'utente si vuole verificare un determinato errore di validazione, può rinominare il file da testare con una delle estensisoni riportate in seguito. In questa casistica il test cercherà per 
un errore di validazione specifico e il test risultera passato se l'errore viene riscontrato.
il nome del json deve contenere le seguenti keyword per andare ad eseguire dei controlli in fase di test:

* _VALID  Con questa keyword ci si aspetta che l'esito della validazione sia positivo.
* _INVALID              Con questa keyword ci si aspetta che l'esito della validazione sia negativo.
* _INVALID-APPLY_COST   Con questa keyword ci si aspetta che l'esito della validazione sia negativo. Nello specifico si cerca un errore di validazione relativo all'incongruenza del flag applyCost fornito come input del test e la presenza/assenza del suddetto flag nel JSON
* _INVALID-PARSING      Con questa keyword ci si aspetta che l'esito della validazione sia negativo. Nello specifico si cerca un errore di sintassi nel JSON, riscontrato dal validatore sintattico di pn-f24.
* _INVALID-METADATA     Con questa keyword ci si aspetta che l'esito della validazione sia negativo. Nello specifico si cerca un errore di sintassi nel JSON, riscontrato dal validatore sintattico della libreria pnf24Lib.
* _INVALID-MULTITYPE    Con questa keyword ci si aspetta che l'esito della validazione sia negativo. Nello specifico si cerca un errore che riporta presenza di più tipi di metadato all'interno del metadato (es standard + simplified).

2) La seconda casistica consente di inserire un file json con una denominazione non specifica, in questa modalità il test tornerà esito positivo se nessun errore viene riscontrato, altrimenti il test fallirà e verrà mostrato l'issueList popolata con l'errore trovato.

In caso i json proposti superino le validazioni, viene generato il modello f24 a partire dal metadato fornito, in seguito viene effettuato un parsing dei dati impostati sul pdf per ricercare eventuali incongruenze con i dati contenuti nel metadato JSON.
due casi di test, una prevede l'estrazione di ntoken da un file json che verranno utilizzati 

Il metodo di test parametrizzato libTest accetta una lista CSV composta da due parametri: nome del file JSON e flag che descrive se il metadato contiene o meno l'applyCost.

_i file json devono essere posizionati sotto la folder src/test/resources/metadata._
La classe prevede due casistiche di test: