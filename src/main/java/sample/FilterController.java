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

import javafx.beans.value.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import model.*;
import org.opencv.core.*;
import org.opencv.imgproc.*;

public class FilterController {

    @FXML
    private Slider bilateralFilterValue;
    @FXML
    private Slider bilateralSigmaValue;
    @FXML
    private Slider boxFilterValue;
    @FXML
    private Slider filter2DValue;

    private MessageModel model;
    private Mat src;

    @FXML
    public void initialize(){
        configSliders();

    }

    private void bilateralFilter(){
        if(src == null){
            src = model.getImage();
        }

            int filterValue = bilateralFilterValue.valueProperty().intValue();
            int sigmaValue = bilateralSigmaValue.valueProperty().intValue();
            Mat dst = new Mat();
            Imgproc.bilateralFilter(src, dst, filterValue, sigmaValue, sigmaValue, Core.BORDER_DEFAULT);
            model.setImage(dst);
    }

    void configSliders(){
        bilateralFilterValue.setMin(1);
        bilateralFilterValue.setMax(15);
        bilateralFilterValue.setValue(1);
        bilateralFilterValue.valueProperty().addListener(bilateralSliderChangeListener);

        bilateralSigmaValue.setMin(0);
        bilateralSigmaValue.setMax(255);
        bilateralSigmaValue.setValue(0);
        bilateralSigmaValue.valueProperty().addListener(bilateralSliderChangeListener);

        boxFilterValue.setMin(0);
        boxFilterValue.setMax(255);
        boxFilterValue.setValue(0);
        boxFilterValue.valueProperty().addListener(boxSliderChangeListener);

        filter2DValue.setMin(0);
        filter2DValue.setMax(255);
        filter2DValue.setValue(0);
        filter2DValue.valueProperty().addListener(filter2DSliderChangeListener);
    }

    ChangeListener<Number> bilateralSliderChangeListener = (observable, oldValue, newValue) -> bilateralFilter();
    ChangeListener<Number> boxSliderChangeListener = (observable, oldValue, newValue) -> boxFilter();
    ChangeListener<Number> filter2DSliderChangeListener = (observable, oldValue, newValue) -> filter2DFilter();

    public void setModel(MessageModel model) {
        this.model = model;
    }

    private void boxFilter(){

        if(src == null){
            src = model.getImage();
        }

        int boxValue = boxFilterValue.valueProperty().intValue();

        Mat frame = new Mat();
        Size size = new Size(boxValue, boxValue);
        Point point = new Point(-1, -1);
        Imgproc.boxFilter(src, frame, -1, size, point, true, Core.BORDER_DEFAULT);
        model.setImage(frame);
    }

    private void filter2DFilter(){
         if(src == null){
            src = model.getImage();
        }
        int filterValue = filter2DValue.valueProperty().intValue();
        Mat frame = new Mat();
        Mat kernel = Mat.ones(filterValue,filterValue, CvType.CV_32F);

        for(int i = 0; i<kernel.rows(); i++) {
            for(int j = 0; j<kernel.cols(); j++) {
                double[] m = kernel.get(i, j);

                for(int k = 1; k<m.length; k++) {
                    m[k] = m[k]/(2 * 2);
                }
                kernel.put(i,j, m);
            }
        }
        Imgproc.filter2D(src, frame, -1, kernel);
    }
}
