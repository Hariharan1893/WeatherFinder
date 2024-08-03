package com.apicalls.weatherfinder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class WeatherFinder {

	public static void main(String[] args) {

		try {
			Scanner scanner = new Scanner(System.in);
			System.out.print("Enter City Name: ");
			String city = scanner.nextLine();

			// Get location data
			JSONObject cityLocation = (JSONObject) getLocationData(city);
			double latitude = (double) cityLocation.get("latitude");
			double longitude = (double) cityLocation.get("longitude");

			System.out.println();
			System.out.println("City: " + city);
			System.out.println("Latitude: " + latitude);
			System.out.println("Longitude: " + longitude);

			// display weather data
			displayWeatherData(latitude, longitude);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static JSONObject getLocationData(String city) {
		city = city.replaceAll(" ", "+");

		String apiurl = "https://geocoding-api.open-meteo.com/v1/search?name=" + city
				+ "&count=1&language=en&format=json";

		try {

			// fetching connection response
			HttpURLConnection conn = fetchApiResponse(apiurl);

			// check for response status
			if (conn.getResponseCode() != 200) {
				System.out.println("Error: Could not connect to API");
				return null;
			}

			// Read the response and convert to String type
			String jsonResponse = readApiResponse(conn);

			// Parse the string into a JSON Object
			JSONParser parser = new JSONParser();
			JSONObject resultsJsonObj = (JSONObject) parser.parse(jsonResponse);

			// Retrieve Location Data
			JSONArray locationData = (JSONArray) resultsJsonObj.get("results");
			return (JSONObject) locationData.get(0);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static void displayWeatherData(double latitude, double longitude) {
		try {

			String apiurl = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude + "&longitude=" + longitude
					+ "&current=temperature_2m,relative_humidity_2m,wind_speed_10m";

			HttpURLConnection apiConnection = fetchApiResponse(apiurl);

			if (apiConnection.getResponseCode() != 200) {
				System.out.println("Error: Could not connect to API");
				return;
			}

			String jsonResponse = readApiResponse(apiConnection);

			JSONParser parser = new JSONParser();
			JSONObject jsonObject = (JSONObject) parser.parse(jsonResponse);

			JSONObject currentWeatherJson = (JSONObject) jsonObject.get("current");

			double temperature = (double) currentWeatherJson.get("temperature_2m");
			System.out.println("Current Temperature (C): " + temperature);

			long relativeHumidity = (long) currentWeatherJson.get("relative_humidity_2m");
			System.out.println("Relative Humidity: " + relativeHumidity);

			double windSpeed = (double) currentWeatherJson.get("wind_speed_10m");
			System.out.println("Wind Speed: " + windSpeed);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String readApiResponse(HttpURLConnection apiConnection) {
		try {
			StringBuilder resultJson = new StringBuilder();
			Scanner scanner = new Scanner(apiConnection.getInputStream());

			while (scanner.hasNext()) {
				resultJson.append(scanner.nextLine());
			}

			return resultJson.toString();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private static HttpURLConnection fetchApiResponse(String urlString) {
		try {
			// attempt to create connection
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			// set request method to get
			conn.setRequestMethod("GET");

			return conn;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}