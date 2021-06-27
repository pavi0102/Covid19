package com.company;

import org.json.JSONArray;
import org.json.JSONObject;
import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    static BufferedReader reader;
    static String line;
    static StringBuffer responseContent = new StringBuffer();
    private static HttpsURLConnection con;

    public static void main (String[] args) {
        String s1;
        String s2;
        Scanner sc = new Scanner(System.in);
        String st = "https://api.covid19api.com/country/";
        System.out.println("Enter the country else press enter");
        String s0 = sc.nextLine();
        System.out.println("enter the start date in format yyyy-mm-dd");
        String d1 = sc.nextLine();
        System.out.println("enter the end date in format yyyy-mm-dd");
        String d2 = sc.nextLine();
        if (s0.isEmpty()) {
                s1 = "south-africa";
                writeToFile(s1, d1, d2);
                System.out.println("Writing "+s1);
                s2 = "canada";
                writeToFile(s2, d1, d2);
                System.out.println("Writing "+s2);
        } else {
            s1 = s0;
            writeToFile(s1,d1,d2);
        }
    }
    public static void writeToFile(String s, String d, String en){
        String ad = "https://api.covid19api.com/country/"+s+"?from="+d+"T00:00:00Z&to="+en+"T00:00:00Z";
        try {
            URL url = new URL(ad);
            con = (HttpsURLConnection) url.openConnection();
            //set request
            con.setRequestMethod("GET");
            con.setConnectTimeout(10000);
            con.setReadTimeout(1000000);
            int status = con.getResponseCode();
            if(status>299){
                reader = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                while((line=reader.readLine())!=null){
                 responseContent.append(line);
                }
                reader.close();
            }
            else{
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                while((line=reader.readLine())!=null){
                    responseContent.append(line);
                }
                reader.close();
            }
            //copy data to csv file
            List<FileData> data= new ArrayList<FileData>();
            File csvFile = new File("CovidData.csv");
            PrintWriter out = new PrintWriter(csvFile);
            data.addAll(parse(responseContent.toString()));
            out.println("Date, Country, Confirmed, Deaths, Recovered, Active ");
            for (FileData f : data){
                out.printf("%s, %s, %d, %d, %d, %d\n",f.getDate(),f.getCountry(),f.getConfirmed(),f.getDeaths(),f.getRecovered(),f.getActive());
            }
            out.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            con.disconnect();
        }
    }
    // parsing the json data
    public static List<FileData> parse(String responseBody){
        JSONArray ar = new JSONArray(responseBody);
        List<FileData> lst= new ArrayList<FileData>();
        FileData fl = new FileData();
        for (int i=0; i<ar.length();i++){
            JSONObject ob = ar.getJSONObject(i);
            String dt = ob.getString("Date");
            String country = ob.getString("Country");
            int confirmed = ob.getInt("Confirmed");
            int deaths = ob.getInt("Deaths");
            int recovered = ob.getInt("Recovered");
            int active = ob.getInt("Active");
            FileData fd = new FileData(dt,country,confirmed,deaths,recovered,active);
            lst.add(fd);
        }
        return lst;
    }
    }


