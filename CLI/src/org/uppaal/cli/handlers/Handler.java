package org.uppaal.cli.handlers;

/**
* interface providing the declaration of all public methods for a handler
*/

public interface Handler {
public static enum HandlerCode {
EDITOR,
SIMULATOR,
VERIFIER
}
}