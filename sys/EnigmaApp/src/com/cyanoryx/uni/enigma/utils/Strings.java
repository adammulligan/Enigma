package com.cyanoryx.uni.enigma.utils;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;


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
    	Preferences prefs = new AppPrefs().getPrefs();
        Locale locale = new Locale(prefs.get("locale", "en"));
        loadBundle(locale);
    }

    
    public static void loadBundle(Locale locale) {
        resourceBundle = ResourceBundle.getBundle("Enigma", locale);
        formatter = new MessageFormat("");
        formatter.setLocale(locale);
    }

}