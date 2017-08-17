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
package sample;

import model.*;

public class LanguageController {

    private LanguageModel model;

    public LanguageController(LanguageModel model){
        this.model = model;
        toEnglish();
    }

    public void toEnglish(){
        model.setBundle(LanguageModel.Language.EN);
    }
    public LanguageModel.Language getLanguage(){
        return model.getLang();
    }
}
