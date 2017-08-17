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

import config.*;
import gui.*;
import javafx.fxml.*;
import javafx.scene.image.*;
import model.*;
import org.opencv.core.*;
import org.opencv.imgcodecs.*;
import org.springframework.beans.factory.annotation.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class ThresholdController extends Modal implements Initializable{

    @FXML
    private ImageView thresholdImageView;


    @Autowired
    private MessageModel model;

    public ThresholdController(ScreensConfig config) {
        super(config);
    }


    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  <tt>null</tt> if the location is not known.
     * @param resources The resources used to localize the root object, or <tt>null</tt> if
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        thresholdImageView.setImage(convertMatToImage(model.getImage()));
    }

    private Image convertMatToImage(Mat mat){
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".png", mat, buffer);
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    }
}

