package com.engine.canada;

import java.io.IOException;

public class EnglishToPigLatin {

    public static String toPigLatin (String str)
    {
        String result = "";

        try {
            result=GoogleTranslate.translate("vi",str);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
