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

import javafx.fxml.*;
import javafx.scene.control.*;
import model.*;
import org.opencv.core.*;
import org.opencv.imgproc.*;
import org.springframework.beans.factory.annotation.*;

public class ImageFilterController{

    @FXML
    private CheckBox firstFilter;

    @Autowired
    private MessageModel model;



    @FXML
    public void bilateralFilter(){
        if(firstFilter.isSelected()){
            Mat src = model.getImage();
            Mat dst = new Mat();
            Imgproc.bilateralFilter(src, dst, 15, 80, 80, Core.BORDER_DEFAULT);
            model.setImage(dst);
        }
    }
}
