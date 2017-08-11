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

import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.image.*;
import javafx.stage.*;
import util.*;

public class ThresholdController {

    @FXML
    private ImageView thresholdImageView;


    final EventHandler<WindowEvent> shownHandler = new EventHandler<WindowEvent>() {
        @Override
        public void handle(WindowEvent event) {
            setInitImage();
            System.out.println("-----------------------");
        }
    };

    public void setInitImage(){
     thresholdImageView.setImage(Context.getInstance().getResultImage());
    }

    public Image getProcessImage(){
        return thresholdImageView.getImage();
    }
}

