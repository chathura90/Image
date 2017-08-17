/*
 *   (C) Copyright 1996-2017 hSenid Software International (Pvt) Limited.
 *   All Rights Reserved.
 *
 *   These materials are unpublished, proprietary, confidential source code of
 *   hSenid Software International (Pvt) Limited and constitute a TRADE SECRET
 *   of hSenid Software International (Pvt) Limited.
 *
 *   hSenid Software International (Pvt) Limited retains all title to and intellectual
 *   property rights in these materials.
 *
 */
package model;

import java.util.*;

public class LanguageModel extends Observable {
    private ResourceBundle bundle;
    private Language lang;

    public  LanguageModel(){
        setBundle(Language.EN);
    }

    public ResourceBundle getBundle() {
        return bundle;
    }

    public void setBundle(Language lang) {
        if (lang == null || lang.equals(this.bundle)) return;
        setLang(lang);
        bundle = ResourceBundle.getBundle("lang", new Locale(lang.getValue(), lang.toString()));
        setChanged();
        notifyObservers();
    }

    public Language getLang() {
        return lang;
    }

    public void setLang(Language lang) {
        this.lang = lang;
    }

    public enum Language{
        EN("en");

        private String value;

        Language(String s){
            value = s;
        }

        public String getValue(){
            return value;
        }
    }
}
