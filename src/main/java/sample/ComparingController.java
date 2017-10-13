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
import javafx.embed.swing.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.stage.*;
import org.opencv.core.*;
import org.opencv.imgcodecs.*;
import org.opencv.imgproc.*;
import org.slf4j.*;

import javax.imageio.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class ComparingController extends Modal implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(ComparingController.class);

    @FXML
    private Button sourceUpload;
    @FXML
    private Button templateUpload;
    @FXML
    private Button compare;
    @FXML
    private Button shape;
    @FXML
    private ImageView sourceImage;
    @FXML
    private ImageView templateImage;

    private File fileSource;
    private File fileTemplate;

    private Mat sourceMat;
    private Mat templateMat;
    private  Mat result = new Mat();

    private int match_method= 0;

    public ComparingController(ScreensConfig config) {
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

    }

    @FXML
    public void sourceUploadButtonClickAction(ActionEvent e) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileSource = fileChooser.showOpenDialog(sourceUpload.getScene().getWindow());
        sourceUploadInitialProcessing(fileSource);

    }

    private void sourceUploadInitialProcessing(File file) throws IOException {

        try {
            BufferedImage bufferedImage = ImageIO.read(file);
            Image fxImage = SwingFXUtils.toFXImage(bufferedImage, null);
            sourceImage.setImage(fxImage);
            sourceMat = Imgcodecs.imread(file.getAbsolutePath());
        } catch (Exception e) {
            logger.debug("Exception occurred while uploading image : {}", e);
        }

    }

    @FXML
    public void templateUploadButtonClickAction(ActionEvent e) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileTemplate = fileChooser.showOpenDialog(templateUpload.getScene().getWindow());
        templateUploadInitialProcessing(fileTemplate);

    }

    private void templateUploadInitialProcessing(File file) throws IOException {

        try {
            BufferedImage bufferedImage = ImageIO.read(file);
            Image fxImage = SwingFXUtils.toFXImage(bufferedImage, null);
            templateImage.setImage(fxImage);
            templateMat = Imgcodecs.imread(file.getAbsolutePath());
        } catch (Exception e) {
            logger.debug("Exception occurred while uploading image : {}", e);
        }

    }

    @FXML
    private void matchingMethod(){

        Mat img_display = sourceMat.clone();

        /// Create the result matrix
        int result_cols =  sourceMat.cols() - templateMat.cols() + 1;
        int result_rows = sourceMat.rows() - templateMat.rows() + 1;

        result.create( result_rows, result_cols, CvType.CV_32FC1 );

        /// Do the Matching and Normalize
        Imgproc.matchTemplate(sourceMat, templateMat, result, match_method );
       // Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());
        Imgproc.threshold(result, result, 0.5, 1.0, Imgproc.THRESH_TOZERO);
        /// Localizing the best match with minMaxLoc
        Point matchLoc;

        Core.MinMaxLocResult minMaxLoc =  Core.minMaxLoc( result, new Mat());

        /// For SQDIFF and SQDIFF_NORMED, the best matches are lower values. For all the other methods, the higher the better
        if( match_method  == Imgproc.TM_SQDIFF || match_method == Imgproc.TM_SQDIFF_NORMED )
        { matchLoc = minMaxLoc.minLoc; }
        else
        { matchLoc = minMaxLoc.maxLoc; }

        double threashhold = 0.40;
        if (minMaxLoc.maxVal > threashhold) {
            System.out.println("------------------------------");
            Imgproc.rectangle(sourceMat, matchLoc, new Point(matchLoc.x + templateMat.cols(),
                    matchLoc.y + templateMat.rows()), new Scalar(0, 255, 0));
        }

        /// Show me what you got
        //Imgproc.rectangle( img_display, matchLoc, new Point( matchLoc.x + templateMat.cols() , matchLoc.y + templateMat.rows() ), new Scalar(255,0,0) );
        //Imgproc.rectangle(result, matchLoc, new Point(matchLoc.x + templateMat.cols(), matchLoc.y + templateMat.rows()), new Scalar(0,255,0));

        sourceImage.setImage(convertMatToImage(sourceMat));
    }

    private Image convertMatToImage(Mat mat) {
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".png", mat, buffer);
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    }


    private MatOfPoint drawContour(){
      /*  ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();

        Mat src = sourceMat.clone();
        Imgproc.cvtColor(src, src ,Imgproc.COLOR_BGR2GRAY);

        Imgproc.findContours(src, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        System.out.println(contours.size() + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

        Mat source = new Mat();
        for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++)
        {
            Imgproc.drawContours(src, contours, contourIdx, new Scalar(0,0,255), -1);
        }

        templateImage.setImage(convertMatToImage(src));*/

       /* Mat image = templateMat;
        Mat gray = new Mat();
        Mat hierarchy = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);
        double thresholdValue = Imgproc.threshold(gray, gray, 0, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
        Imgproc.Canny(gray, gray, thresholdValue * 0.5, thresholdValue);
        /// Find contours
        ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        Imgproc.findContours(gray, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        /// Draw contours
        Mat drawing = Mat.zeros( gray.size(), CvType.CV_8UC3 );
        for( int i = 0; i< contours.size(); i++ )
        {
           Imgproc.drawContours(gray, contours, i, new Scalar(0,0,255), -1);
        }

        templateImage.setImage(convertMatToImage(gray));*/

        //1.Read image file & clone.

        Mat src_mat ;
        Mat gray_mat = new Mat();
        Mat canny_mat = new Mat();
        Mat contour_mat;
        Mat bounding_mat;
        ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();

        src_mat = templateMat;
        contour_mat = src_mat.clone();
        bounding_mat = src_mat.clone();

        //2. Convert to gray image and apply canny edge detection
        Imgproc.cvtColor(src_mat, gray_mat, Imgproc.COLOR_RGB2GRAY);
        double thresholdValue = Imgproc.threshold(gray_mat, gray_mat, 0, 255, Imgproc.THRESH_BINARY | Imgproc
                .THRESH_OTSU);
        Imgproc.Canny(gray_mat, canny_mat, thresholdValue * 0.5, thresholdValue);
        //3. Find & process the contours
        //3.1 find contours on the edge image.
        Imgproc.findContours(canny_mat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        //3.2 draw contours & property value on the source image.
        double area, length;
        Imgproc.drawContours(contour_mat, contours, -1, new Scalar(0,255,0), -1);  //draw contours on the image

        templateImage.setImage(convertMatToImage(contour_mat));

       /* for(int i = 0; i < contours.size(); ++i)
        {
            MatOfPoint2f center;
            float radius;

            area = Imgproc.contourArea(contours.get(i));
            length = Imgproc.arcLength(contours[i], true);
            Imgproc.minEnclosingCircle(contours[i], center, radius);

            //draw contour property value at the contour center.
            char buffer[64] = {0};
            sprintf(buffer, "Area: %.2lf", area);
            putText(contour_mat, buffer, center, FONT_HERSHEY_SIMPLEX, .6, Scalar(0), 1);

            sprintf(buffer, "Length: %.2lf", length);
            putText(contour_mat, buffer, Point(center.x,center.y+20), FONT_HERSHEY_SIMPLEX, .6, Scalar(0), 1);

        }*/

      /*  //3.3 find bounding of each contour, and draw it on the source image.
        for(int i = 0; i < contours.size(); ++i)
        {
            Point2f points[4];
            Point2f center;
            float radius;
            Rect rect;
            RotatedRect rotate_rect;

            //compute the bounding rect, rotated bounding rect, minum enclosing circle.
            rect = boundingRect(contours[i]);
            rotate_rect = minAreaRect(contours[i]);
            minEnclosingCircle(contours[i], center, radius);

            rotate_rect.points(points);

            vector< vector< Point> > polylines;
            polylines.resize(1);
            for(int j = 0; j < 4; ++j)
                polylines[0].push_back(points[j]);

            //draw them on the bounding image.
            cv::rectangle(bounding_mat, rect, Scalar(0,0,255), 2);
            cv::polylines(bounding_mat, polylines, true, Scalar(0, 255, 0), 2);
            cv::circle(bounding_mat, center, radius, Scalar(255, 0, 0), 2);

        }*/

        return contours.get(1);
    }

    @FXML
    private void shapeDetection(){
        MatOfPoint templateContour =  drawContour();


        Mat src_mat ;
        Mat gray_mat = new Mat();
        Mat canny_mat = new Mat();
        Mat contour_mat;
        Mat bounding_mat;
        ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();

        src_mat = sourceMat;
        contour_mat = src_mat.clone();
        bounding_mat = src_mat.clone();

        //2. Convert to gray image and apply canny edge detection
        Imgproc.cvtColor(src_mat, gray_mat, Imgproc.COLOR_RGB2GRAY);
        double thresholdValue = Imgproc.threshold(gray_mat, gray_mat, 0, 255, Imgproc.THRESH_BINARY | Imgproc
                .THRESH_OTSU);
        Imgproc.Canny(gray_mat, canny_mat, thresholdValue * 0.5, thresholdValue);
        //3. Find & process the contours
        //3.1 find contours on the edge image.
        Imgproc.findContours(canny_mat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        //3.2 draw contours & property value on the source image.
        double area, length;
          //draw contours on the image

        System.out.println("--------------"+ contours.size());
        for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++)
        {
           double ret = Imgproc.matchShapes(contours.get(contourIdx),templateContour,Imgproc.CV_CONTOURS_MATCH_I1,0);

           if(ret < 0.0001){

               System.out.println("------"+ contourIdx + "-----------"+ ret );

               Rect rect;

               rect = Imgproc.boundingRect(contours.get(contourIdx));

               Imgproc.drawContours(contour_mat, contours, -1, new Scalar(0,255,0), -1);
               //Imgproc.rectangle(bounding_mat, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0,0,255), 3);
           }
        }

        sourceImage.setImage(convertMatToImage(contour_mat));
    }

}


