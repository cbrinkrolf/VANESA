package biologicalElements;

public enum NodeStateChanged {
/** Node is unchanged.*/
UNCHANGED,
/** Node was included in coarsing operation.*/
COARSED,
/** Node was flatted.*/
FLATTED,
/** Node was deleted.*/
DELETED,
/** Node was creates.*/
CREATED,
/** The connection to the whole graph (connecting edges, border and/or environment) has been modified.*/
CONNECTIONMODIFIED,
}
