//Nhung Luu

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;

public class WeatherMain extends Application{

    static Connection conn;
    static Statement stmt;
    static ResultSet rs;
    static String host = "jdbc:mysql://" + Credentials.getUrl() + ":" + Credentials.getPort() + "/misc";

    Scene scene1;
    Scene scene2;
    Scene scene3;
    Scene scene4;
    Button start;
    Button submit;
    Button quit;
    Button again;

    String state;
    String city;
    String explain;

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {
        //this set up the first scene where introductions are made
        primaryStage.setTitle("Weather Underground Middle Man");

        Text welcome = new Text("Welcome to the middle man to Weather Underground!\n");
        welcome.setFont(Font.font(null, FontWeight.BOLD, 35));
        welcome.setFill(Color.ORANGERED);

        Text intro = new Text("We are here to make sure that you're up to date with the weather anywhere in the US.\n");
        intro.setStyle("-fx-font-size: 30");
        intro.setFill(Color.WHITESMOKE);
        intro.setWrappingWidth(750);

        start = new Button("Let's begin!");
        start.setStyle("-fx-font-size: 30; -fx-background-color: orangered; -fx-text-fill: white");
        start.setOnAction(event -> {
            setScene2(primaryStage);
        });

        Image logo = new Image(new FileInputStream("wundergroundLogo_4c_horz.png"));
        ImageView logoView = new ImageView(logo);

        VBox boxPane1 = new VBox(10);
        boxPane1.setStyle("-fx-background-color: black");
        boxPane1.setAlignment(Pos.CENTER);
        boxPane1.getChildren().addAll(welcome, intro, start, logoView);
        scene1 = new Scene(boxPane1, 1000, 970);
        primaryStage.setScene(scene1);
        primaryStage.show();
    }

    //the second scene is for the user to input the city and state to search
    public void setScene2(Stage primaryStage) {
        Text ques1 = new Text("Please enter the state abbreviation:");
        ques1.setFont(Font.font(null, FontWeight.NORMAL, 30));
        ques1.setFill(Color.WHITESMOKE);
        TextField ques1Answer = new TextField();
        ques1Answer.setMaxSize(50,50);

        Text ques2 = new Text("Please enter the city with no abbreviation:");
        ques2.setFont(Font.font(null, FontWeight.NORMAL, 30));
        ques2.setFill(Color.WHITESMOKE);
        TextField ques2Answer = new TextField();
        ques2Answer.setMaxSize(500,500);

        submit = new Button("Search");
        submit.setStyle("-fx-background-color: lightblue; -fx-text-fill: white; -fx-font-size: 25");
        submit.setOnAction(event2 -> {
            state = ques1Answer.getText().trim().toUpperCase();
            city = ques2Answer.getText().trim();
            if(!stateLookup(state)){
                explain = "OH NO!!!  We can't find " + state + " in the US!";
                setScene3(primaryStage);
            }
            else if(!cityLookup(city, state)){
                explain = "OH NO!!!  " + city + " does not exist in " + state + "!";
                setScene3(primaryStage);
            }
            else{
                setScene4(primaryStage);
            }
        });

        quit = new Button("Quit");
        quit.setStyle("-fx-background-color: green; -fx-text-fill: red; -fx-font-size: 25");
        quit.setOnAction(event2 -> primaryStage.close());

        Image logo = null;
        try {
            logo = new Image(new FileInputStream("wundergroundLogo_4c_horz.png"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ImageView logoView = new ImageView(logo);

        GridPane pane = new GridPane();
        pane.setStyle("-fx-background-color: black");
        pane.setAlignment(Pos.CENTER);
        pane.setVgap(10);
        pane.setHgap(10);
        pane.setPadding(new Insets(20,20,20,20));
        pane.add(ques1,0,0);
        pane.add(ques1Answer,1,0);
        pane.add(ques2,0,1);
        pane.add(ques2Answer,1,1);
        pane.add(submit,1,2);
        pane.add(quit,0,2);
        pane.add(logoView,0,3);
        scene2 = new Scene(pane, 1000,970);
        primaryStage.setScene(scene2);
    }

    //the third scene only appear when there is an error of unknown city or state in the search
    public void setScene3(Stage primaryStage){
        Text reason = new Text(explain);
        reason.setFont(Font.font(null, FontWeight.BOLD, 25));
        reason.setFill(Color.CRIMSON);

        again = new Button("Try again.");
        again.setStyle("-fx-background-color: lightblue; -fx-text-fill: white; -fx-font-size: 20");
        quit = new Button("Quit");
        quit.setStyle("-fx-background-color: green; -fx-text-fill: red; -fx-font-size: 20");
        again.setOnAction(event3 -> primaryStage.setScene(scene2));
        quit.setOnAction(event3 -> primaryStage.close());

        Image logo = null;
        try {
            logo = new Image(new FileInputStream("wundergroundLogo_4c_horz.png"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ImageView logoView = new ImageView(logo);

        VBox boxPane3 = new VBox(10);
        boxPane3.setStyle("-fx-background-color: black");
        boxPane3.setPadding(new Insets(20,20,20,20));
        boxPane3.setAlignment(Pos.CENTER);
        boxPane3.getChildren().addAll(reason, again, quit, logoView);
        scene3 = new Scene(boxPane3, 1000, 970);
        primaryStage.setScene(scene3);
    }

    //the fourth scene is for displaying the weather of the location searched
    public void setScene4(Stage primaryStage){
        Weather today = new Weather();
        weatherLookup(today, city, state);

        Text result = new Text("The weather of " + city + ", " + state + ":\n");
        result.setFont(Font.font(null, FontWeight.BOLD, 30));
        result.setFill(Color.BLUEVIOLET);
        Text result2 = new Text("Today, on " + today.getTime() + ", the weather is " + today.getWeather() + "\n" +
            "The temperature is " + today.getTemperature() + "\n" +
            "However, it really feels like " + today.getFeelLikeTemp() + "\n" +
            "The humidity is " + today.getHumidity() + "\n" +
            "The wind is " + today.getWind() + "\n" +
            "The chance of precipitation is " + today.getPrecip() + "\n" +
            "The dew point is " + today.getDewPointF() + " F (" + today.getDewPointC() + " C)\n\n");
        result2.setFill(Color.WHITESMOKE);

        again = new Button("Search another location");
        again.setStyle("-fx-background-color: lightblue; -fx-text-fill: white; -fx-font-size: 25");
        again.setOnAction(event4 -> setScene2(primaryStage));

        quit = new Button("Quit");
        quit.setStyle("-fx-background-color: green; -fx-text-fill: red; -fx-font-size: 25");
        quit.setOnAction(event -> primaryStage.close());

        Image logo = null;
        try {
            logo = new Image(new FileInputStream("wundergroundLogo_4c_horz.png"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ImageView logoView = new ImageView(logo);

        GridPane pane = new GridPane();
        pane.setStyle("-fx-background-color: black");
        pane.setAlignment(Pos.CENTER);
        pane.setVgap(10);
        pane.setHgap(10);
        pane.setPadding(new Insets(20,20,20,20));
        pane.add(result, 0,0);
        pane.add(result2,0,1);
        pane.add(again,1,2);
        pane.add(quit,0,2);
        pane.add(logoView,0,3);
        scene4 = new Scene(pane, 1000, 970);
        primaryStage.setScene(scene4);
    }

    //function to look up the states in the database to ensure the searched state is within the US
    public static boolean stateLookup(String state){
        boolean result = false;
        String query = "SELECT state FROM " + Credentials.getTable();
        try {
            conn = DriverManager.getConnection(host, Credentials.getUser(), Credentials.getPassword());
            stmt = conn.createStatement();
            stmt.execute(query);
            rs = stmt.getResultSet();
            while(rs.next()){
                String aState = rs.getString("state");
                if(state.compareTo(aState) == 0)result = true;
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    //function to ensure that the city searched is within the state
    public static boolean cityLookup(String city, String state){
        boolean result = false;
        String query = "SELECT city FROM " + Credentials.getTable() + " WHERE state LIKE '" + state.toUpperCase() + "'" ;
        try {
            conn = DriverManager.getConnection(host, Credentials.getUser(), Credentials.getPassword());
            stmt = conn.createStatement();
            stmt.execute(query);
            rs = stmt.getResultSet();
            while(rs.next()){
                String aCity = rs.getString("city");
                if(city.toUpperCase().compareTo(aCity) == 0) result = true;
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    //function to scrape from weather underground for the weather forecast
    public static void weatherLookup(Weather today, String city, String state){
        if(state.contains(" "))state = state.replace(" ", "_");
        String url = "http://api.wunderground.com/api/" + Credentials.getApiKey() + "/conditions/q/" + state + "/" + city + ".xml";
        try {
            Document doc = Jsoup.connect(url).get();
            today.setDewPointC(doc.select("dewpoint_c").text());
            today.setDewPointF(doc.select("dewpoint_f").text());
            today.setFeelLikeTemp(doc.select("feelslike_string").text());
            today.setHumidity(doc.select("relative_humidity").text());
            today.setPrecip(doc.select("precip_today_string").text());
            today.setTemperature(doc.select("temperature_String").text());
            today.setTime(doc.select("local_time_rfc822").text());
            today.setWeather(doc.select("weather").text());
            today.setWind(doc.select("wind_string").text());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
