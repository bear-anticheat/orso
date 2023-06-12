package com.bear.orso.common.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Color {

    public String translate(String message) {
        return translateAlternateColorCodes('&', message);
    }

    public String translateAlternateColorCodes(final char altColorChar, final String textToTranslate) {
        final char[] b = textToTranslate.toCharArray();

        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i+1]) > -1) {
                b[i] = '§';
                b[i+1] = Character.toLowerCase(b[i+1]);
            }
        }

        return new String(b);
    }
}
