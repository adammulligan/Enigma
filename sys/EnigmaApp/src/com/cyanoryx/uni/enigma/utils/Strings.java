package com.cyanoryx.uni.enigma.utils;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;


public class Strings {

    public static Locale[] SUPPORTED_LOCALES = {Locale.ENGLISH};

    private static ResourceBundle resourceBundle;
    private static MessageFormat formatter;

    public static String translate(String messageName) {
        return resourceBundle.getString(messageName);
    }


    public static Locale getCurrentLocale() {
        return resourceBundle.getLocale();
    }


    public static void initialise() {
      //  Locale locale = Locale.ENGLISH;
       // String localePreference = Preferences.get(Preferences.ApplicationOptions.LOCALE);
        //if (localePreference != null) {
         //   locale = new Locale(localePreference);
        //}
        loadBundle(Locale.ENGLISH);
    }

    
    public static void loadBundle(Locale locale) {
        resourceBundle = ResourceBundle.getBundle("Enigma", locale);
        formatter = new MessageFormat("");
        formatter.setLocale(locale);
    }

}