package io;

import org.apache.log4j.Logger;

import java.io.*;

public abstract class BaseReader<T> {
    private final Logger logger = Logger.getRootLogger();
    private boolean hasErrors;
    private InputStream inputStream;

    public BaseReader(final File file) {
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            hasErrors = true;
        }
    }

    public BaseReader(final InputStream inputStream) {
        this.inputStream = inputStream;
    }

    protected void setHasErrors() {
        hasErrors = true;
    }

    public boolean hasErrors() {
        return hasErrors;
    }

    public final T read() {
        try {
            return internalRead(inputStream);
        } catch (IOException e) {
            logger.error("Failed to read file", e);
            hasErrors = true;
            return null;
        }
    }

    protected abstract T internalRead(final InputStream inputStream) throws IOException;
}
