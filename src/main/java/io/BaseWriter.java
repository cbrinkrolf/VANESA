package io;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseWriter<T> {
    private final List<String> errors = new ArrayList<>();
    private final File file;

    public BaseWriter(File file) {
        this.file = file;
    }

    protected void addError(String message) {
        errors.add(message);
    }

    public boolean hasErrors() {
        return errors.size() > 0;
    }

    public final void write(T value) {
        errors.clear();
        // Defer overwriting the output file by first writing safely into memory and if all
        // succeeded we transfer the data to file output stream.
        ByteArrayOutputStream memoryOutputStream = new ByteArrayOutputStream();
        try {
            internalWrite(memoryOutputStream, value);
        } catch (Exception e) {
            addError(e.getMessage());
        }
        if (!hasErrors()) {
            try (OutputStream outputStream = new FileOutputStream(file)) {
                outputStream.write(memoryOutputStream.toByteArray());
            } catch (IOException e) {
                addError(e.getMessage());
            }
        }
    }

    protected abstract void internalWrite(OutputStream outputStream, T value) throws Exception;

    public String getErrors() {
        return String.join("\n", errors);
    }

    public String getFileName() {
        return file.getName();
    }
}
