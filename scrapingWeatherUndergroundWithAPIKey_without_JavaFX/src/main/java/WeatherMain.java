import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class WeatherMain {

    static Connection conn;
    static Statement stmt;
    static ResultSet rs;
    static String host = "jdbc:mysql://" + Credentials.getUrl() + ":" + Credentials.getPort() + "/misc";

    public static void main(String[] args) {
        menu();
    }

    public static void menu(){
        Weather today = new Weather();
        String state = "st";
        String city = "c";
        char quit = 'N';
        while(quit != 'Y'){
            try {
                conn = DriverManager.getConnection(host, Credentials.getUser(), Credentials.getPassword());
                stmt = conn.createStatement();
                System.out.println("Please enter the state abbreviation:");
                Scanner stateInput = new Scanner(System.in);
                state = stateInput.next().toUpperCase().trim();
                if(!stateLookup(state)){
                    System.out.println("State does not exist in the US");
                    menu();
                }
                System.out.println("Please enter the city:");
                System.out.println("If more than one word, include the '_' in between.");
                Scanner cityInput = new Scanner(System.in);
                city = cityInput.next().toUpperCase().trim();
                if(!cityLookup(city, state)){
                    System.out.println("City does not exist in " + state);
                    menu();
                }
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            System.out.println("Looking up the weather in " + city + ", " + state);
            printWeather(today, city, state);
            System.out.println("Do you want to quit?");
            System.out.println("Enter Y for yes.");
            System.out.println("Enter N for no.");
            Scanner input  = new Scanner(System.in);
            quit = input.next().toUpperCase().trim().charAt(0);
            if(quit == 'Y')System.out.println("Good bye!");
        }
    }

    public static boolean stateLookup(String state){
        boolean result = false;
        String query = "SELECT state FROM " + Credentials.getTable();
        ArrayList myState = new ArrayList();
        try {
            stmt.execute(query);
            rs = stmt.getResultSet();
            while(rs.next()){
                String aState = rs.getString("state");
                myState.add(aState);
            }
            for(int i=0;i<myState.size();i++){
                if(state.compareTo(String.valueOf(myState.get(i))) == 0)result = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean cityLookup(String city, String state){
        boolean result = false;
        if(city.contains("_")){
            city = city.replace("_", " ");
        }
        String query = "SELECT city FROM " + Credentials.getTable();
        ArrayList myCity = new ArrayList();
        try {
            stmt.execute(query);
            rs = stmt.getResultSet();
            while(rs.next()){
                String aCity = rs.getString("city");
                myCity.add(aCity);
            }
            for(int i=0;i<myCity.size();i++){
                if(city.compareTo(String.valueOf(myCity.get(i))) == 0)result = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void weatherLookup(Weather today, String city, String state){
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

    public static void printWeather(Weather today, String city, String state){
        weatherLookup(today, city, state);
        System.out.println("The weather of " + today.getTime() + ":");
        System.out.println("Today, the weather is " + today.getWeather());
        System.out.println("The temperature is " + today.getTemperature());
        System.out.println("However, it really feels like " + today.getFeelLikeTemp());
        System.out.println("The humidity is " + today.getHumidity());
        System.out.println("The wind is " + today.getWind());
        System.out.println("The chance of precipitation is " + today.getPrecip());
        System.out.println("The dew point is " + today.getDewPointF() + " F (" + today.getDewPointC() + " C)");
    }
}
