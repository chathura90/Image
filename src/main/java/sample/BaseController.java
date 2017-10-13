package sample;

import config.*;
import gui.*;
import javafx.beans.value.*;
import javafx.embed.swing.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import model.*;
import net.sourceforge.tess4j.*;
import org.opencv.core.*;
import org.opencv.imgcodecs.*;
import org.opencv.imgproc.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;

import javax.imageio.*;
import java.awt.image.*;
import java.io.*;

public class BaseController extends Presentation {

    private static final Logger logger = LoggerFactory.getLogger(BaseController.class);

    @FXML
    private Button button;
    @FXML
    private Button resetButton;
    @FXML
    private Button readButton;
    @FXML
    private ImageView imageView;
    @FXML
    private ImageView resultImageView;
    @FXML
    private CheckMenuItem greyscale;
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
    private Slider sobelXValue;
    @FXML
    private Slider sobelYValue;
    @FXML
    private Slider scharrXValue;
    @FXML
    private Slider scharrYValue;


    @FXML
    private AnchorPane filterSetting;
    @FXML
    private FilterController filterSettingController;
    @FXML
    private AnchorPane fillingSetting;
    @FXML
    private FillingController fillingSettingController;

    private File file;
    private Mat image;
    private Mat resultMat;
    private Mat unedited;
    int thresMin, thresMax;

    @Autowired
    private MessageModel model;

    public BaseController(ScreensConfig config) {
        super(config);
    }

    @FXML
    public void uploadButtonClickAction(ActionEvent e) throws IOException {
        FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser);
        file = fileChooser.showOpenDialog(button.getScene().getWindow());
        uploadInitialProcessing();

    }

    private void uploadInitialProcessing() throws IOException {

        filterSettingController.setModel(model);
        fillingSettingController.setModel(model);
        try {
            BufferedImage bufferedImage = ImageIO.read(file);
            Image fxImage = SwingFXUtils.toFXImage(bufferedImage, null);
            resultImageView.setImage(fxImage);
            imageView.setImage(fxImage);
            resultMat = Imgcodecs.imread(file.getAbsolutePath());
            unedited = resultMat.clone();
            image = resultMat.clone();
            model.setImage(resultMat);
        } catch (Exception e) {
            logger.debug("Exception occurred while uploading image : {}", e);
        }

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
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        if (greyscale.isSelected()) {
            Mat frame = model.getImage().clone();
            Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
            resultMat = frame;
            resultImageView.setImage(convertMatToImage(frame));
            model.setImage(frame);
            greyscale.setDisable(true);
        }
    }


    @FXML
    public void openThresholdWindow() throws IOException {
        config.loadPopupThreshold();
    }

    @FXML
    public void openComparingWindow() throws IOException {
        config.loadPopupComparing();
    }

    @FXML
    public void openSaveDialog(){
        config.loadSaveImage();
    }


    private Image convertMatToImage(Mat mat) {
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".png", mat, buffer);
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    }

    @FXML
    public void resetImage() {
        model.setImage(image);
        resultMat = image.clone();
        unedited = image.clone();
        if (greyscale.isSelected()) {
            greyscale.setDisable(false);
            greyscale.setSelected(false);
        }

    }

    @FXML
    void initialize() {

        model.addObserver((o, arg) -> {
            resultMat = model.getImage();
            resultImageView.setImage(convertMatToImage(resultMat));
        });
        configureThresholdSlider();
    }

    @FXML
    public void automaticCannyEdgeDetection() {

        if (automaticDetection.isSelected()) {
            Mat frame = model.getImage().clone();
            thresholdMax.setDisable(true);
            thresholdMin.setDisable(true);
            textThresholdMax.setDisable(true);
            textThresholdMin.setDisable(true);
            double thresholdValue = Imgproc.threshold(frame, frame, 0, 255, Imgproc.THRESH_BINARY | Imgproc
                    .THRESH_OTSU);
            Imgproc.Canny(frame, frame, thresholdValue * 0.5, thresholdValue);
            unedited = model.getImage().clone();
            model.setImage(frame);
        } else {
            thresholdMax.setDisable(false);
            thresholdMin.setDisable(false);
            textThresholdMax.setDisable(false);
            textThresholdMin.setDisable(false);
            resultMat = unedited.clone();
            model.setImage(unedited);
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

        sobelXValue.disableProperty().setValue(false);
        sobelXValue.setMin(0);
        sobelXValue.setMax(1);
        sobelXValue.setValue(0);
        sobelXValue.setMajorTickUnit(1);
        sobelXValue.setShowTickMarks(true);
        sobelXValue.setShowTickLabels(true);
        sobelXValue.valueProperty().addListener(sobelEdgeChangeListener);

        sobelYValue.disableProperty().setValue(false);
        sobelYValue.setMin(0);
        sobelYValue.setMax(1);
        sobelYValue.setValue(1);
        sobelYValue.setMajorTickUnit(1);
        sobelYValue.setShowTickMarks(true);
        sobelYValue.setShowTickLabels(true);
        sobelYValue.valueProperty().addListener(sobelEdgeChangeListener);

        scharrXValue.disableProperty().setValue(false);
        scharrXValue.setMin(0);
        scharrXValue.setMax(1);
        scharrXValue.setValue(0);
        scharrXValue.setMajorTickUnit(1);
        scharrXValue.setShowTickMarks(true);
        scharrXValue.setShowTickLabels(true);
        scharrXValue.valueProperty().addListener(scharrEdgeChangeListener);

        scharrYValue.disableProperty().setValue(false);
        scharrYValue.setMin(0);
        scharrYValue.setMax(1);
        scharrYValue.setValue(1);
        scharrYValue.setMajorTickUnit(1);
        scharrYValue.setShowTickMarks(true);
        scharrYValue.setShowTickLabels(true);
        scharrYValue.valueProperty().addListener(scharrEdgeChangeListener);
    }

    ChangeListener<Number> sliderChangeListener = (observable, oldValue, newValue) -> manualCannyEdgeDetection();
    ChangeListener<Number> sobelEdgeChangeListener = (observable, oldValue, newValue) -> sobelEdgeDetection();
    ChangeListener<Number> scharrEdgeChangeListener = (observable, oldValue, newValue) -> scharrEdgeDetection();

    public void manualCannyEdgeDetection() {
        Mat toChange = resultMat.clone();
        thresMin = thresholdMin.valueProperty().intValue();
        thresMax = thresholdMax.valueProperty().intValue();

        textThresholdMin.setText(String.valueOf(thresMin));
        textThresholdMax.setText(String.valueOf(thresMax));
        Imgproc.Canny(toChange, toChange, thresMin, thresMax);
        resultImageView.setImage(convertMatToImage(toChange));
    }

    public void sobelEdgeDetection(){

        Mat dst = resultMat.clone();
        int xValue = sobelXValue.valueProperty().intValue();
        int yValue = sobelYValue.valueProperty().intValue();

        Imgproc.Sobel(dst, dst, -1, xValue, yValue);
        resultImageView.setImage(convertMatToImage(dst));
    }

    public void scharrEdgeDetection(){

        Mat dst = resultMat.clone();
        int xValue = scharrXValue.valueProperty().intValue();
        int yValue = scharrYValue.valueProperty().intValue();

        Imgproc.Scharr(dst, dst, Imgproc.CV_SCHARR, xValue, yValue);
        resultImageView.setImage(convertMatToImage(dst));
    }

    @FXML
    public void closeStage(){
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void readText(){
//        File imageFile = new File("eurotext.tif");
        ITesseract instance = new Tesseract();  // JNA Interface Mapping
        instance.setDatapath("/usr/share/tesseract/tessdata");
        instance.setLanguage("eng");
        try {

            String result = instance.doOCR(file);
            System.out.println("----------------------------------"+result);
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }
    }
}
