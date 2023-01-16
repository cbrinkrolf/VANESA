package biologicalElements;

public enum NodeStateChanged {
/** Node is unchanged.*/
UNCHANGED,
/** Node was included in coarsing operation.*/
COARSED,
/** Node was flatted.*/
FLATTENED,
/** Node was deleted.*/
DELETED,
/** Node was created.*/
CREATED,
/** The connection to the whole graph (connecting edges, border and/or environment) has been modified.*/
CONNECTIONMODIFIED,
/** Node was added to coarse node. */
ADDED
}
