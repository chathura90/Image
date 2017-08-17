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
    private CheckBox convertHSV;
    @FXML
    private Slider hueSliderMin;
    @FXML
    private Slider saturationSliderMin;
    @FXML
    private Slider valueSliderMin;
    @FXML
    private Slider hueSliderMax;
    @FXML
    private Slider saturationSliderMax;
    @FXML
    private Slider valueSliderMax;

    double adjHueMin, adjSaturationMin, adjValueMin, adjHueMax, adjSaturationMax, adjValueMax;
    private File file;
    private Image image;
    private Mat resultMat;

    public BaseController(ScreensConfig config) {
        super(config);
    }

    @Autowired
    private MessageModel model;

    @FXML
    public void uploadButtonClickAction(ActionEvent e) throws IOException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
                convertHSV.setSelected(false);
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
        } else {
            readAndSetImage();
        }
    }

    @FXML
    public void convertHSV(ActionEvent e){
          if(convertHSV.isSelected()){
              configureHSVSlider();
              configureSaturationSlider();
              configureValueSlider();

          } else {
              hueSliderMin.disableProperty().setValue(true);
              saturationSliderMin.disableProperty().setValue(true);
              valueSliderMin.disableProperty().setValue(true);
              hueSliderMax.disableProperty().setValue(true);
              saturationSliderMax.disableProperty().setValue(true);
              valueSliderMax.disableProperty().setValue(true);
          }
    }

    private void configureHSVSlider() {
        hueSliderMin.disableProperty().setValue(false);
        hueSliderMin.setPrefWidth(300);
        hueSliderMin.setMin(0);
        hueSliderMin.setMax(255);
        hueSliderMin.setMajorTickUnit(25);
        hueSliderMin.setShowTickMarks(true);
        hueSliderMin.setShowTickLabels(true);
        hueSliderMin.setValue(0);
        hueSliderMin.valueProperty().addListener(sliderChangeListener);

        hueSliderMax.disableProperty().setValue(false);
        hueSliderMax.setPrefWidth(300);
        hueSliderMax.setMin(0);
        hueSliderMax.setMax(255);
        hueSliderMax.setMajorTickUnit(25);
        hueSliderMax.setShowTickMarks(true);
        hueSliderMax.setShowTickLabels(true);
        hueSliderMax.setValue(255);
        hueSliderMax.valueProperty().addListener(sliderChangeListener);
    }

    private void configureSaturationSlider() {
        saturationSliderMin.disableProperty().setValue(false);
        saturationSliderMin.setPrefWidth(300);
        saturationSliderMin.setMin(0);
        saturationSliderMin.setMax(255);
        saturationSliderMin.setMajorTickUnit(25);
        saturationSliderMin.setShowTickMarks(true);
        saturationSliderMin.setShowTickLabels(true);
        saturationSliderMin.setValue(0);
        saturationSliderMin.valueProperty().addListener(sliderChangeListener);

        saturationSliderMax.disableProperty().setValue(false);
        saturationSliderMax.setPrefWidth(300);
        saturationSliderMax.setMin(0);
        saturationSliderMax.setMax(255);
        saturationSliderMax.setMajorTickUnit(25);
        saturationSliderMax.setShowTickMarks(true);
        saturationSliderMax.setShowTickLabels(true);
        saturationSliderMax.setValue(255);
        saturationSliderMax.valueProperty().addListener(sliderChangeListener);
    }

    private void configureValueSlider() {
        valueSliderMin.disableProperty().setValue(false);
        valueSliderMin.setPrefWidth(300);
        valueSliderMin.setMin(0);
        valueSliderMin.setMax(255);
        valueSliderMin.setMajorTickUnit(25);
        valueSliderMin.setShowTickMarks(true);
        valueSliderMin.setShowTickLabels(true);
        valueSliderMin.setValue(0);
        valueSliderMin.valueProperty().addListener(sliderChangeListener);

        valueSliderMax.disableProperty().setValue(false);
        valueSliderMax.setPrefWidth(300);
        valueSliderMax.setMin(0);
        valueSliderMax.setMax(255);
        valueSliderMax.setMajorTickUnit(25);
        valueSliderMax.setShowTickMarks(true);
        valueSliderMax.setShowTickLabels(true);
        valueSliderMax.setValue(255);
        valueSliderMax.valueProperty().addListener(sliderChangeListener);
    }
    ChangeListener<Number> sliderChangeListener = (observable, oldValue, newValue) -> updateImageHSV();

    private void updateImageHSV(){

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        adjHueMin = hueSliderMin.valueProperty().doubleValue();
        adjSaturationMin = saturationSliderMin.valueProperty().doubleValue();
        adjValueMin = valueSliderMin.valueProperty().doubleValue();

        adjHueMax = hueSliderMax.valueProperty().doubleValue();
        adjSaturationMax = saturationSliderMax.valueProperty().doubleValue();
        adjValueMax = valueSliderMax.valueProperty().doubleValue();

        Mat img = Imgcodecs.imread(file.getAbsolutePath());
        Mat hsv = img.clone();
        Imgproc.cvtColor(img, hsv, Imgproc.COLOR_BGR2HSV);
        Core.inRange(hsv, new Scalar(adjHueMin,adjSaturationMin,adjValueMin), new Scalar(adjHueMax,adjSaturationMax,adjValueMax), hsv);

        resultMat = hsv;

        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".png", hsv, buffer);
        resultImageView.setImage(new Image(new ByteArrayInputStream(buffer.toArray())));
    }

    @FXML
    public void openThresholdWindow() throws IOException {
         config.loadPopup();
    }

   @FXML
   public  void cannyEdgeDetectionImage(){
       if(edgeDetection.isSelected()) {
           Imgproc.Canny(resultMat, resultMat, 60, 60 * 3);
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

}
