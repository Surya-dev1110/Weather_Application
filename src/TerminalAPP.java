import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class TerminalAPP
{
    public static void main(String[] args)
    {
        try {
            Scanner sc = new Scanner(System.in);
            String city;
            do {
                //Getting input from user
                System.out.println("==================================");
                System.out.println("Enter City (Say No to Quit ) : ");
                city = sc.nextLine();

                if (city.equalsIgnoreCase("No"))
                    break;

                //Get location data
                JSONObject location = (JSONObject) getLocatioinData(city);

                double latitude = (double) location.get("latitude");
                double longitude = (double) location.get("longitude");

                displayWeatherData(latitude,longitude);

            }while (!city.equalsIgnoreCase("No"));

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private static JSONObject getLocatioinData(String city)
    {
        city = city.replaceAll(" ","+");

        String urlstring = "https://geocoding-api.open-meteo.com/v1/search?name="+city+"&count=1&language=en&format=json";
        try{

            //1. Fetch API response
            HttpURLConnection apiConn = fetchApiResponce(urlstring);

            //check for response status
            //200 - means connection was successful
            if(apiConn.getResponseCode()!=200){
                System.err.println("Error : Could not connect API");
                return null;
            }

            //2. Read API response and convert store string type
            String jsonResponse = readApiResponse(apiConn);

            //3. Parse the string into JSON object
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(jsonResponse);

            //4. Retrieve Location data
            JSONArray locationData = (JSONArray) resultJsonObj.get("results");
            return (JSONObject) locationData.get(0);

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private static String readApiResponse(HttpURLConnection apiConn)
    {
        try {
            //create StringBuilder to store resulting json data
            StringBuilder resultJson = new StringBuilder();

            //create a scanner to read inputstream of url connection
            Scanner sc = new Scanner(apiConn.getInputStream());

            //loop through each line in the response append it in stringBuilder
            while (sc.hasNext()){
                resultJson.append(sc.nextLine());
            }
            sc.close();

            //return jsondata as string
            return resultJson.toString();

        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    private static HttpURLConnection fetchApiResponce(String urlstring)
    {
        try{
            //attempt to create connection
            URL url = new URL(urlstring);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            return conn;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static void displayWeatherData(double latitude, double longitude)
    {
        try{
            //1. Fetch Api Response
            String url = "https://api.open-meteo.com/v1/forecast?latitude="+latitude+"&longitude="+longitude+"&current=temperature_2m,relative_humidity_2m,is_day,weather_code,wind_speed_10m";

            HttpURLConnection apiConn = fetchApiResponce(url);

            //check for response status
            //200 - means connection was successfull
            if(apiConn.getResponseCode()!=200){
                System.err.println("Error : Could not connect API");
                return;
            }

            //2. Read API response and convert store string type
            String jsonResponse = readApiResponse(apiConn);

            //3. Parse the string into JSON object
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(jsonResponse);
            JSONObject currentWeatherJson = (JSONObject) resultJsonObj.get("current");

            //4. Store the data into their corresponding data type
            String time = (String) currentWeatherJson.get("time");
            System.out.println("Current Time : "+time);

            double temperature = (double) currentWeatherJson.get("temperature_2m");
            System.out.println("Current Temperature : "+temperature);

            long relativeHumidity = (long) currentWeatherJson.get("relative_humidity_2m");
            System.out.println("Relative Humidity : "+relativeHumidity);

            double windspeed = (double) currentWeatherJson.get("wind_speed_10m");
            System.out.println("Wind Speed : "+windspeed);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
