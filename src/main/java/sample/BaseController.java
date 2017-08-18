package sample;

import config.*;
import gui.*;
import javafx.beans.value.*;
import javafx.embed.swing.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.stage.*;
import model.*;
import org.opencv.core.*;
import org.opencv.imgcodecs.*;
import org.opencv.imgproc.*;
import org.springframework.beans.factory.annotation.*;

import javax.imageio.*;
import java.awt.image.*;
import java.io.*;

public class BaseController extends Presentation {

    @FXML
    private Button button;
    @FXML
    private Button resetButton;
    @FXML
    private Button saveButton;
    @FXML
    private ImageView imageView;
    @FXML
    private ImageView resultImageView;
    @FXML
    private CheckMenuItem greyscale;
    @FXML
    private CheckMenuItem edgeDetection;
    @FXML
    private Slider thresholdMax;
    @FXML
    private Slider thresholdMin;
    @FXML
    private CheckBox automaticDetection;
    @FXML
    private TextField textThresholdMin;
    @FXML
    private TextField textThresholdMax;

    @FXML
    private CheckBox firstFilter;

    private File file;
    private Image image;
    private Mat resultMat;
    private Mat unedited;
    int thresMin, thresMax;
    public BaseController(ScreensConfig config) {
        super(config);
    }

    @Autowired
    private MessageModel model;

    @FXML
    public void uploadButtonClickAction(ActionEvent e) throws IOException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
                FileChooser fileChooser = new FileChooser();
                configureFileChooser(fileChooser);
                file = fileChooser.showOpenDialog(button.getScene().getWindow());
                try {
                    readAndSetImage();
                    if(image != null){
                        imageView.setImage(image);
                        resultMat = Imgcodecs.imread(file.getAbsolutePath());
                        model.setImage(resultMat);
                    }
                } catch (IOException ex) {
                    System.out.println(file);
                }
    }

    private void readAndSetImage() throws IOException {

        BufferedImage bufferedImage = ImageIO.read(file);
        image = SwingFXUtils.toFXImage(bufferedImage, null);
        resultImageView.setImage(image);

    }

    private static void configureFileChooser(
            final FileChooser fileChooser) {
        fileChooser.setTitle("View Pictures");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Images", "*.*"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );
    }

    @FXML
    public void convertGrey(ActionEvent e) throws IOException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        if(greyscale.isSelected()) {
            Mat frame = Imgcodecs.imread(file.getAbsolutePath());
            Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
            resultMat = frame;

            MatOfByte buffer = new MatOfByte();
            Imgcodecs.imencode(".png", frame, buffer);
            resultImageView.setImage(new Image(new ByteArrayInputStream(buffer.toArray())));
            model.setImage(frame);
        } else {
            readAndSetImage();
        }
    }


    @FXML
    public void openThresholdWindow() throws IOException {
         config.loadPopup();
    }

   @FXML
   public  void cannyEdgeDetectionImage(){
       if(edgeDetection.isSelected()) {
           double thresholdValue = Imgproc.threshold(resultMat,resultMat,0,255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
           Imgproc.Canny(resultMat, resultMat, thresholdValue * 0.5, thresholdValue);
       }

       resultImageView.setImage(convertMatToImage(resultMat));
   }

     private Image convertMatToImage(Mat mat){
         MatOfByte buffer = new MatOfByte();
         Imgcodecs.imencode(".png", mat, buffer);
         return new Image(new ByteArrayInputStream(buffer.toArray()));
     }

    @FXML
    public void resetImage(){
        resultImageView.setImage(imageView.getImage());
        if(greyscale.isSelected()){
            greyscale.setSelected(false);
        }

        if(edgeDetection.isSelected()){
            greyscale.setSelected(false);
        }
    }

    @FXML
    void initialize() {

        model.addObserver((o, arg) -> {
            resultMat =  model.getImage();
            resultImageView.setImage(convertMatToImage(resultMat));
        });
        configureThresholdSlider();
    }

    @FXML
    public void automaticCannyEdgeDetection(){

        if(automaticDetection.isSelected()) {
            unedited = resultMat.clone();
            thresholdMax.setDisable(true);
            thresholdMin.setDisable(true);
            textThresholdMax.setDisable(true);
            textThresholdMin.setDisable(true);
            double thresholdValue = Imgproc.threshold(resultMat,resultMat,0,255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
            Imgproc.Canny(resultMat, resultMat, thresholdValue * 0.5, thresholdValue);
            resultImageView.setImage(convertMatToImage(resultMat));
        }else {
            thresholdMax.setDisable(false);
            thresholdMin.setDisable(false);
            textThresholdMax.setDisable(false);
            textThresholdMin.setDisable(false);
            resultMat = unedited.clone();
            resultImageView.setImage(convertMatToImage(unedited));
        }

    }

    private void configureThresholdSlider() {
        thresholdMin.disableProperty().setValue(false);
        thresholdMin.setMin(0);
        thresholdMin.setMax(255);
        thresholdMin.setValue(0);
        thresholdMin.valueProperty().addListener(sliderChangeListener);

        thresholdMax.disableProperty().setValue(false);
        thresholdMax.setMin(0);
        thresholdMax.setMax(255);
        thresholdMax.setValue(255);
        thresholdMax.valueProperty().addListener(sliderChangeListener);
    }

    ChangeListener<Number> sliderChangeListener = (observable, oldValue, newValue) -> manualCannyEdgeDetection();

    public void manualCannyEdgeDetection(){
        Mat toChange = resultMat.clone();
        thresMin = thresholdMin.valueProperty().intValue();
        thresMax = thresholdMax.valueProperty().intValue();

        textThresholdMin.setText(String.valueOf(thresMin));
        textThresholdMax.setText(String.valueOf(thresMax));
        Imgproc.Canny(toChange, toChange, thresMin, thresMax);
        resultImageView.setImage(convertMatToImage(toChange));
    }


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
