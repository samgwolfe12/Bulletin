package bulletin;


import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.text.*;
import javafx.scene.image.*;
import javafx.scene.*;
import javafx.event.*;
import javafx.stage.*;
import javafx.application.*;
import javafx.geometry.*;
import javafx.animation.*;
import javafx.util.*;
//import com.google.gson.JsonParser;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonArray;
import java.net.URL;
import java.io.InputStreamReader;
import java.lang.Runnable;
import java.util.Random;
import java.util.Arrays;
import java.net.URLEncoder;

/** 
 * Represents an iTunes GalleryApp!
 */
public class GalleryApp extends Application {

    /** Private variable */
    private Stage stage;
    private Scene scene;
    private GridPane tile = new GridPane();;
    private VBox bigBox;
    private HBox biggerBox;
    private HBox menu;
    private HBox toolbar;
    private TextField search;
    private Button update;
    private int count;
    private int numResults;
    private String input;
    private String input2;
    private String[] stored = new String[20];
    private String[] array;
    private ProgressBar bar = new ProgressBar();
    private InputStreamReader reader = null;
    
    /** Default height and width for Images */
    private static final int DEF_HEIGHT = 100;
    private static final int DEF_WIDTH = 100;

    /** @inheritdoc */
    @Override
    public void start(Stage stage) {
	//call create methods and add them to bigBox
	this.stage = stage;
	createMenu();
	createToolbar();
	createProgressBar();
	bigBox = new VBox();
        bigBox.getChildren().addAll(menu,toolbar,tile,bar);

	biggerBox = new HBox();
	biggerBox.getChildren().add(bigBox);
        scene = new Scene(biggerBox);
        stage.setMaxWidth(640);
        stage.setMaxHeight(480);
        stage.setTitle("GalleryApp!");
        stage.setScene(scene);
        stage.sizeToScene();
	stage.setMaximized(true); 
	stage.show();
    }
    /**
     * Method to create the menu bar with exit item 
     */    
    public void createMenu(){
	MenuButton menuButton = new MenuButton("File");
	MenuItem exit = new MenuItem("Exit");
	//create event to exit when button is clicked
	EventHandler<ActionEvent> exiting=
            event ->  System.exit(0);
	exit.setOnAction(exiting);
	MenuButton menuButton2 = new MenuButton("Help");
	MenuButton menuButton3 = new MenuButton("Theme");
	//call createTheme to built new menu
	createTheme(menuButton3);
	//call createHelp method to build new menu
	createHelp(menuButton2);
	menuButton.getItems().addAll(exit);
     	menu = new HBox();
     	menu.getChildren().addAll(menuButton,menuButton3,menuButton2);
    }

    /**
     * Method to create the extra credit help menu.
     * The help menu has a about item.
     */
   public void createHelp(MenuButton menuButton2){
	MenuItem about = new MenuItem("About");
        VBox samBox = new VBox();
 	Image samPic =new Image("https://pbs.twimg.com/profile_images/"+
				"1114024663418580993/TsVZiMqp_400x400.jpg");
	ImageView samView = new ImageView(samPic);
	Text name = new Text("Sam Wolfe");
	Text email = new Text("sgw73466@uga.edu");
	Text vers = new Text("Version 678314");
	samBox.getChildren().addAll(samView, name, email, vers);
	//when clicked, menuitem opens new window
        EventHandler<ActionEvent> helping =
            event -> {
            Scene scene = new Scene(samBox);
            Stage stage = new Stage();
            stage.setTitle("About Sam Wolfe:");
	    stage.sizeToScene();
	    stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.show();
        };
        about.setOnAction(helping);
	menuButton2.getItems().addAll(about);
   }
    /**
     * Method to create add a basic and edgy theme
     */
    public void createTheme(MenuButton menuButton3){
	MenuItem theme = new MenuItem("Edgy");
	MenuItem basic = new MenuItem("Basic");
	theme.setOnAction(new EventHandler<ActionEvent>() {
		@Override public void handle(ActionEvent e) {//call css file
		    scene.getStylesheets().add("file:src/main/java/cs1302/gallery/stylesheet.css");
		    stage.setScene(scene);
		    stage.show();
		}
	    });
	basic.setOnAction(new EventHandler<ActionEvent>() {
		@Override public void handle(ActionEvent e) {
		    scene.getStylesheets().clear();//set to basic theme
		    stage.setScene(scene);
		    stage.show();
		}
	    });
	menuButton3.getItems().addAll(theme,basic);
    }

    /** 
     * Method to build the toolbar with pause, search query, and update.
     * createToolbar() sets up the timeline used for pause/play button
     */
    public void createToolbar(){
	Button pauseButt = new Button("pause");
	EventHandler<ActionEvent> handle = event -> update(tile,array); 
	KeyFrame keyFrame = new KeyFrame(Duration.seconds(2), handle);//updates the tile every 2 sec
	Timeline timeline = new Timeline(); //sets up timeline that stops when pause is pressed
	timeline.setCycleCount(Timeline.INDEFINITE);
	timeline.getKeyFrames().add(keyFrame);
	timeline.play();
	pauseButt.setOnAction(e -> play(timeline,pauseButt));
	Label query = new Label("Search Query");
	search = new TextField("rock");
	Button update = new Button("Update Images");
	input2 = "rock";//default search query text
        update.setOnAction(e -> {
		Runnable r = () -> {
		    pUpdate(0);
		    //convertUrl(search,tile);
		};
		Thread t = new Thread(r);
		t.setDaemon(true);
		t.start();
	    });
	//convertUrl(search,tile);//converts url to string    
	toolbar = new HBox();
	toolbar.setPadding(new Insets(10, 12, 10, 12));
	toolbar.setSpacing(10);
	toolbar.setPrefWidth(500);
	toolbar.getChildren().addAll(pauseButt,query,search,update);
    }
    /** 
     * Method to create the progress bar
     */
    public void createProgressBar(){
	HBox progress = new HBox();
	progress.getChildren().add(bar);
    }

    /** 
     * Method to begin converting the url to string and test if it is valid
     * @param input the text from the textfield 
     */
    public void urlTests(String input){
	try{ //try catch statements to make sure url is valid
	    input = URLEncoder.encode(input,"UTF-8");
        }catch(Exception e){
            System.out.println("Invalid url");
        }
	String link = "https://itunes.apple.com/search?term=";
        String sUrl = link + input + "&entity=album";
        URL url = null;
	try{
	    url = new URL(sUrl);
	}catch (Exception e) {
	    System.out.println("Invalid url");
	}
	try{
	    reader = new InputStreamReader(url.openStream());
	}catch(Exception e){
	    System.out.println("Invalid url");
	}
    }
    /**
     * Method to convert the url into a string
     * @param searchBar the textfield in the toolbar
     * @param tile the GridPane of images
     */
    /*
    public void convertUrl(TextField searchBar,GridPane tile){
	input = search.getText();
	urlTests(input);	
	JsonParser jp = new JsonParser();
	JsonElement je = jp.parse(reader);
	JsonObject root = je.getAsJsonObject();                      // root of response
	JsonArray results = root.getAsJsonArray("results");          // "results" array
        numResults = results.size();                                 // "results" array size
	if(numResults < 20){//if less than 20 show error
	    input = input2;
	    TextField tfield = new TextField(input);
	    Platform.runLater(() -> {
		    Alert alert = new Alert(AlertType.ERROR, "Error");
		    alert.setContentText("Less than 20");
		    alert.show();
		});
	    convertUrl(tfield,tile);
	    loadImage(array);
	}
	else{//if more than 20 call makeArray
	    input2 = input;
	    makeArray(results);
	}
	}*/

    /** 
     * Method to create an array of distinct string urls.
     * @param results the JsonArray from convertUrl
     */
    /*
    public void makeArray(JsonArray results){
	array = new String[numResults];
	for (int i = 0; i < numResults; i++) {                       
	    JsonObject result = results.get(i).getAsJsonObject();    // object i in array
	    JsonElement artworkUrl100 = result.get("artworkUrl100"); // artworkUrl100 member
	    if (artworkUrl100 != null) {                             // member might not exist
		String artUrl = artworkUrl100.getAsString();        // get member as string
		array[i] = artUrl;                                //art array
	    } 
	} //make distinct
	array = Arrays.stream(array).filter(e -> e !=null).distinct().toArray(String[]::new);
	loadImage(array);
	} */

    /**
     * Method to add images to the GridPane
     * @param array the array of picture
     */
    public void loadImage(String[] array){
	int row = -1;//add images based on row and col
	int col = 0;
	double inc =0;
	for(int i = 0;i < 20;i++){
	    if(i % 5 ==0){
		row++;
		col=0;
	    }
	    Image newImg = new Image(array[i] ,DEF_HEIGHT, DEF_WIDTH, false, false);
	    ImageView imgView = new ImageView(newImg);
	    pFix(imgView,col,row);
	    inc += .05;
	    pUpdate(inc);
	    stored[i]=array[i];
	    count++;
	    col++;
	}
    }
    
    /**
     * Method to update the pictues randomly every 2 sec
     * @param tile the GridPane of pictures
     * @param array the array of urls
     */
    public void update(GridPane tile, String[] array){
	Random r = new Random();
	Random c = new Random();
	int row = r.nextInt(4);
	int col= c.nextInt(5);
	int repeats = -1;
	String art= null;
	while(repeats != 0){
	    repeats=0;
	    if(count >= numResults){
		count =0;
	    }
	    art= array[count];
	    for(int i=0; i < 20; i++){
		if(stored[i].equals(art)){
		    repeats++;
		}
	    }
	    if(repeats ==0){//if no repeats add image
		stored[(5*row)+col] = art;	
		Image newImg = new Image(art ,DEF_HEIGHT, DEF_WIDTH, false, false);
		ImageView imgView = new ImageView(newImg);
		tile.add(imgView,col,row);
	    }
	    else{
		count++;
	    }
	}
    }

    /**
     * Method to play or pause the timeline depending on the text
     * @param timeline the Timeline object
     * @param pauseButt the play/pause button
     */
    public void play(Timeline timeline, Button pauseButt){
	if(pauseButt.getText().equals("pause")){//what to do if paused
	    pauseButt.setText("play");
	    timeline.pause();
	}
	else if(pauseButt.getText().equals("play")){//what to do if playing
	    pauseButt.setText("pause");
	    timeline.play();
	}
    }

    /** 
     * Method to update progress bar
     */ 
    private void pUpdate(double inc){
	Platform.runLater(()-> 	bar.setProgress(inc));
    }

    /**
     * Method to add the images to the gridpane
     */
    private void pFix(ImageView images,int col,int row){
	Platform.runLater(()->   tile.add(images,col,row));
    }
      

} // GalleryApp
