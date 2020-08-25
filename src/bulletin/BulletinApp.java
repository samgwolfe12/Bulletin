package bulletin;

import com.google.gson.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.*;
import javafx.scene.image.*;
import javafx.scene.*;
import javafx.event.*;
import javafx.stage.*;
import javafx.application.*;
import javafx.animation.*;
import javafx.util.*;
import java.net.URL;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.Arrays;


/**
 * Displays pins from a Pinterest board
 */
public class BulletinApp extends Application {

	/** Private variables */
	private Stage stage;
	private Scene scene;
	private GridPane tile = new GridPane();;
	private VBox container;
	private HBox menu;
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
		this.stage = stage;
		createMenu();						//createMenu()->createTheme(),createHelp()
		convertUrl();						//convertUrl()->urlTests()
		//createProgressBar();
		container = new VBox();
		container.setPrefWidth(500);		
		container.setPrefHeight(540);
		container.getChildren().addAll(menu,tile);
		scene = new Scene(container);
		scene.getStylesheets().add("file:src/resources/spots.css");  //the original "theme" is set to Tie Dye
		stage.setMaxWidth(640);
		stage.setMaxHeight(480);
		stage.setTitle("My Bulletin");
		stage.setScene(scene);
		stage.sizeToScene();
		stage.setMaximized(true);
		stage.show();
	}

	/**
	 * Method to create the menu bar with exit item
	 */
	public void createMenu() {
		MenuButton menuButton = new MenuButton("File");
		MenuItem exit = new MenuItem("Exit");
		EventHandler<ActionEvent> exiting = event -> System.exit(0); // create event to exit when button is clicked
		exit.setOnAction(exiting);
		MenuButton menuButton2 = new MenuButton("Help");
		MenuButton menuButton3 = new MenuButton("Theme");
		createTheme(menuButton3);									// call createTheme to built scroll menu button
		createInfo(menuButton2);									// call createInfo method to build new menu 
		menuButton.getItems().addAll(exit);
		
		Button pauseButton = new Button("Pause");
		EventHandler<ActionEvent> handle = event -> update(tile, array);
		KeyFrame keyFrame = new KeyFrame(Duration.seconds(2), handle);  // updates the tile every 2 sec
		Timeline timeline = new Timeline(); 							// sets up timeline that stops when pause is pressed
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.getKeyFrames().add(keyFrame);
		timeline.play();
		pauseButton.setOnAction(e -> play(timeline, pauseButton));
		pauseButton.setTranslateX(250);
		
		menu = new HBox();
		menu.getChildren().addAll(menuButton, menuButton3, menuButton2, pauseButton);
	}

	/**
	 * Method to create an info pop-up with a bio and contact information.
	 */
	public void createInfo(MenuButton menuButton2) {
		MenuItem about = new MenuItem("About");
		VBox samBox = new VBox();
		Image samPic = new Image("https://pbs.twimg.com/profile_images/" + "1114024663418580993/TsVZiMqp_400x400.jpg");
		ImageView samView = new ImageView(samPic);
		Text name = new Text("Sam Wolfe");
		Text email = new Text("sgw73466@uga.edu");
		Text vers = new Text("Version 678314");
		samBox.getChildren().addAll(samView, name, email, vers);
		EventHandler<ActionEvent> helping = event -> {    			// when clicked, menu item opens new window
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
	public void createTheme(MenuButton menuButton3) {
		MenuItem theme = new MenuItem("Space");
		MenuItem basic = new MenuItem("Tie Dye");
		MenuItem spots = new MenuItem("Spots");
		theme.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {					// call css file
				scene.getStylesheets().add("file:src/resources/space.css");
				stage.setScene(scene);
				stage.show();
			}
		});
		spots.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {					// call css file
				scene.getStylesheets().add("file:src/resources/spots.css");
				stage.setScene(scene);
				stage.show();
			}
		});
		basic.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				scene.getStylesheets().add("file:src/resources/stylesheet.css");
				stage.setScene(scene);
				stage.show();
			}
		});
		menuButton3.getItems().addAll(basic,theme,spots);
	}

	
	
	/**
	 * Method to create the progress bar
	 */
	/*
	public void createProgressBar() {
		HBox progress = new HBox();
		progress.getChildren().add(bar);
	}*/

	/**
	 * Method to update the pictures randomly every 2 sec
	 * 
	 * @param tile  the GridPane of pictures
	 * @param array the array of urls
	 */
	public void update(GridPane tile, String[] array) {
		Random r = new Random();
		Random c = new Random();
		int row = r.nextInt(4);
		int col = c.nextInt(5);
		int repeats = -1;
		String art = null;
		while (repeats != 0) {
			repeats = 0;
			if (count >= numResults) {
				count = 0;
			}
			art = array[count];
			for (int i = 0; i < 20; i++) {
				if (stored[i].equals(art)) {
					repeats++;
				}
			}
			if (repeats == 0) {// if no repeats add image
				stored[(5 * row) + col] = art;
				Image newImg = new Image(art, DEF_HEIGHT, DEF_WIDTH, false, false);
				ImageView imgView = new ImageView(newImg);
				tile.add(imgView, col, row);
			} else {
				count++;
			}
		}
	}

	/**
	 * Method to play or pause the timeline depending on the text
	 * 
	 * @param timeline  the Timeline object
	 * @param pauseButt the play/pause button
	 */
	public void play(Timeline timeline, Button pauseButt) {
		if (pauseButt.getText().equals("pause")) {// what to do if paused
			pauseButt.setText("Play");
			timeline.pause();
		} else if (pauseButt.getText().equals("Play")) {// what to do if playing
			pauseButt.setText("Pause");
			timeline.play();
		}
	}
	
	
	/**
	 * Method to convert the url into a string
	 * 
	 * @param searchBar the textfield in the toolbar
	 * @param tile      the GridPane of images
	 */

	public void convertUrl() {
		urlTests();
		JsonParser jp = new JsonParser();
		JsonElement je = jp.parse(reader);
		JsonObject root = je.getAsJsonObject();			 // root of response
		root = root.getAsJsonObject("data");
		JsonArray results = root.getAsJsonArray("pins"); // "results" array
		numResults = results.size(); 					 // "results" array size
		if (numResults < 20) {							 // if less than 20 show error
			input = input2;
			TextField tfield = new TextField("filler");
			Platform.runLater(() -> {
				Alert alert = new Alert(AlertType.ERROR, "Error");
				alert.setContentText("Less than 20");
				alert.show();
			});
			convertUrl();
			loadImage(array);
		} else {// if more than 20 call makeArray
			input2 = input;
			makeArray(results);
		}
	}
	
	/**
	 * Method to begin converting the url to string and test if it is valid
	 * 
	 * @param input the text from the textfield
	 */
	
	public void urlTests() {
		String sUrl = "https://api.pinterest.com/v3/pidgets/boards/sam6072/Bulletin/pins/";
		URL url = null;
		try {
			url = new URL(sUrl);
		} catch (Exception e) {
			System.out.println("Invalid url");
		}
		try {
			reader = new InputStreamReader(url.openStream());
		} catch (Exception e) {
			System.out.println("Invalid url");
		}
		
	}
	

	/**
	 * Method to create an array of distinct string urls.
	 * 
	 * @param results the JsonArray from convertUrl
	 */

	public void makeArray(JsonArray results) {
		array = new String[numResults];
		for (int i = 0; i < numResults; i++) {
			JsonObject result = results.get(i).getAsJsonObject(); // object i in array
			result  = result.getAsJsonObject("images");
			result = result.getAsJsonObject("237x");
			JsonElement imageUrl = result.get("url"); 
			if (imageUrl != null) { // member might not exist
				String artUrl = imageUrl.getAsString(); // get member as string
				array[i] = artUrl; // art array
			}
		} // make distinct
		array = Arrays.stream(array).filter(e -> e != null).distinct().toArray(String[]::new);
		loadImage(array);
	}

	/**
	 * Method to add images to the GridPane
	 * 
	 * @param array the array of picture
	 */
	public void loadImage(String[] array) {
		int row = -1;// add images based on row and col
		int col = 0;
		//double inc = 0;
		for (int i = 0; i < 20; i++) {
			if (i % 5 == 0) {
				row++;
				col = 0;
			}
			Image newImg = new Image(array[i], DEF_HEIGHT, DEF_WIDTH, false, false);
			ImageView imgView = new ImageView(newImg);
			pFix(imgView, col, row);
			//inc += .05;
			//pUpdate(inc);
			stored[i] = array[i];
			count++;
			col++;
		}
	}


	/**
	 * Method to update progress bar
	 */
	/*
	private void pUpdate(double inc) {
		Platform.runLater(() -> bar.setProgress(inc));
	}*/

	/**
	 * Method to add the images to the gridpane
	 */
	private void pFix(ImageView images, int col, int row) {
		Platform.runLater(() -> tile.add(images, col, row));
	}

} // GalleryApp
