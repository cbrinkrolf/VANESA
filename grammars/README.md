# Grammars

The VANESA formula grammar is using ANTLR which is split into the code generator and
the runtime library. To generate the necessary java libraries from the grammar download
the ANTLR complete jar (antlr-[...]-complete.jar) from
[https://www.antlr.org/download.html](https://www.antlr.org/download.html) and place it
into this folder.

To generate the java classes from the grammar execute the following command:

> java -jar antlr-[...]-complete.jar VanesaFormula.g4 -visitor -no-listener -package prettyFormula

Afterwards, copy the generated `*.java` files into the `src/prettyFormula/` package folder.
