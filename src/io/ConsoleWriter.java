package io;

import interfaces.Writer;

public class ConsoleWriter implements Writer {

    public void writeLine(String text) {
        System.out.println(text);
    }

    public void write(String text) {
        System.out.print(text);
    }

    public void writeEmptyLine() {
        System.out.println();
    }
}
