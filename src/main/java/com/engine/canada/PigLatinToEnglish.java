package com.engine.canada;

import java.io.IOException;

public class PigLatinToEnglish {
    public static String toEnglish (String str)
    {
        String result = "";

        try {
            result=GoogleTranslate.translate("en",str);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
