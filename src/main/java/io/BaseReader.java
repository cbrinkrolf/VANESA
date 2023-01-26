package io;

import java.io.*;

public abstract class BaseReader<T> {
    private boolean hasErrors;
    private InputStream inputStream;

    public BaseReader(File file) {
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            hasErrors = true;
        }
    }

    public BaseReader(InputStream inputStream) {
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
            hasErrors = true;
            return null;
        }
    }

    protected abstract T internalRead(InputStream inputStream) throws IOException;
}
