/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.util;

import java.text.ParseException;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.function.Predicate;

public class SeparatedKeysParser {
    private Predicate<Character> isSeparator;

    public SeparatedKeysParser(Predicate<Character> isSeparator) {
        this.isSeparator = isSeparator;
    }

    public String[] parseKeys(String keysString) throws ParseException {
        char c;
        StringCharacterIterator sci = new StringCharacterIterator(keysString);
        StringBuilder keyBuilder = new StringBuilder(64);
        ArrayList<String> keysBuilder = new ArrayList<String>();
        Predicate<Character> isSeparator = this.isSeparator;
        boolean findSeparator = false;
        while ((c = sci.current()) != '\uffff') {
            if (keyBuilder.length() != 0 || c != ' ') {
                if (isSeparator.test(Character.valueOf(c))) {
                    if (!findSeparator) {
                        keysBuilder.add(keyBuilder.toString());
                        keyBuilder.setLength(0);
                    }
                    findSeparator = false;
                } else {
                    if (findSeparator) {
                        this.throwError(c, sci.getIndex(), keysString);
                    }
                    if (c == '\'') {
                        if (keyBuilder.length() != 0) {
                            this.throwError('\'', sci.getIndex(), keysString);
                        }
                        sci.next();
                        keysBuilder.add(this.parseKeyUntilChar(keyBuilder, sci, t -> t.charValue() == '\'', keysString));
                        keyBuilder.setLength(0);
                        findSeparator = true;
                    } else {
                        keyBuilder.append(c);
                    }
                }
            }
            sci.next();
        }
        if (keyBuilder.length() > 0) {
            keysBuilder.add(keyBuilder.toString());
        }
        return keysBuilder.toArray(new String[0]);
    }

    private String parseKeyUntilChar(StringBuilder keyBuilder, StringCharacterIterator sci, Predicate<Character> isEnd, String keysString) throws ParseException {
        char c;
        keyBuilder.setLength(0);
        while ((c = sci.current()) != '\uffff') {
            if (c == '\\') {
                keyBuilder.append(sci.next());
            } else {
                if (isEnd.test(Character.valueOf(c))) break;
                keyBuilder.append(c);
            }
            sci.next();
        }
        if (!isEnd.test(Character.valueOf(c))) {
            this.throwError(c, sci.getIndex(), keysString);
        }
        return keyBuilder.toString();
    }

    private void throwError(char unexpected, int position, String keysString) throws ParseException {
        throw new ParseException(String.format("Unexpected \"%s\" at position %d in \"%s\"!", Character.valueOf(unexpected), position, keysString), position);
    }
}

