package org.rsp.interaction;

import org.rsp.exception.TerminationException;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;

abstract class AbstractDialogInteractor {

    protected BufferedReader wrapReader(Reader reader) {
        if (reader instanceof BufferedReader br) {
            return br;
        } else {
            return new BufferedReader(reader);
        }
    }

    protected PrintWriter wrapWriter(Writer writer) {
        if (writer instanceof PrintWriter pw) {
            return pw;
        } else {
            return new PrintWriter(writer, true);
        }
    }

    protected void checkForExit(String name) throws TerminationException {
        if (name.equals("!exit")) {
            throw new TerminationException();
        }
    }
}
