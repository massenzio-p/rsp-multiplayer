package org.rsp.network;

import org.rsp.exception.TerminationException;

import java.io.*;

public class PlayerConnection implements Connection {

    private final BufferedReader reader;
    private final PrintWriter writer;

    public PlayerConnection(Reader reader, Writer writer) {
        this.reader = wrapReader(reader);
        this.writer = wrapWriter(writer);
    }

    @Override
    public String receiveMessage() throws IOException {
        return reader.readLine();
    }

    @Override
    public void sendMessage(String msg) {
        this.writer.println(msg);
    }

    private PrintWriter wrapWriter(Writer writer) {
        if (writer instanceof PrintWriter pw) {
            return pw;
        } else {
            return new PrintWriter(writer, true);
        }
    }

    private BufferedReader wrapReader(Reader reader) {
        if (reader instanceof BufferedReader br) {
            return br;
        } else {
            return new BufferedReader(reader);
        }
    }

    @Override
    public boolean readyToRead() throws IOException {
        return reader.ready();
    }

    @Override
    public void close() throws IOException {
        this.reader.close();
        this.writer.close();
    }
}
