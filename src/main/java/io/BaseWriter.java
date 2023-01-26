package io;

import java.io.*;

public abstract class BaseWriter<T> {
    private boolean hasErrors;
    private final File file;

    public BaseWriter(File file) {
        this.file = file;
    }

    protected void setHasErrors() {
        hasErrors = true;
    }

    public boolean hasErrors() {
        return hasErrors;
    }

    public final void write(T value) {
        // Defer overwriting the output file by first writing safely into memory and if all
        // succeeded we transfer the data to file output stream.
        ByteArrayOutputStream memoryOutputStream = new ByteArrayOutputStream();
        try {
            internalWrite(memoryOutputStream, value);
        } catch (Exception e) {
            hasErrors = true;
        }
        if (!hasErrors) {
            try (OutputStream outputStream = new FileOutputStream(file)) {
                outputStream.write(memoryOutputStream.toByteArray());
            } catch (IOException e) {
                hasErrors = true;
            }
        }
    }

    protected abstract void internalWrite(OutputStream outputStream, T value) throws Exception;
}
