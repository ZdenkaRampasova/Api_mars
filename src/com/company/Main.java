package com.company;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        dejPetObrazkuMarzu();
        zkontrolujJestliJsouVsechnyOdkazyPouzeVideaEasyVerze();
        zkontrolujJestliJsouVsechnyOdkazyPouzeVidea();
    }

    public static void dejPetObrazkuMarzu() {
        URL adresaAPI = null;
        try {
            // alternativní dotaz:
            //adresaAPI = new URL("https://images-api.nasa.gov/search?q=surface&keywords=mars&year_start=2018&year_end=2018&location=mars");
            adresaAPI = new URL("https://images-api.nasa.gov/search?q=surface&keywords=mars&year_start=2018&year_end=2018");
        } catch (MalformedURLException e) {
            System.out.println("Nedaří se vytvořit adresa.");
            e.printStackTrace();
        }

        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) adresaAPI.openConnection();
        } catch (IOException e) {
            System.out.println("Nedaří se otevřít spojení, možná chyba firewallu.");
            e.printStackTrace();
        }

        try {
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(2000);
        } catch (ProtocolException e) {
            System.out.println("Nepodařilo se nastavit metodu GET.");
            e.printStackTrace();
        }

        try {
            connection.connect();
        } catch (IOException e) {
            System.out.println("Nepodařilo se připojit na server.");
            e.printStackTrace();
        }

        int kodOdpovedi = 0;
        try {
            kodOdpovedi = connection.getResponseCode();
        } catch (IOException e) {
            System.out.println("Nepodařilo se získat odpoveď od serveru.");
            e.printStackTrace();
        }

        if (kodOdpovedi != 200) {
            throw new RuntimeException("Nedokážu se připojit na server. Kˇod odpovědi: " + kodOdpovedi);
        } else {
            String odpovedJakoText = new String();

            Scanner scanner = null;
            try {
                scanner = new Scanner(adresaAPI.openStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (scanner.hasNext()) {
                odpovedJakoText += scanner.nextLine();
            }

//            System.out.println("\nData ve formátu JSON jako text: \n");
//            System.out.println(odpovedJakoText);
            scanner.close();


            JSONParser parser = new JSONParser();

            try {
                JSONObject jsonObjectZOdpovedi = (JSONObject) parser.parse(odpovedJakoText);

                JSONObject jsonCollectionZOdpovedi = (JSONObject) jsonObjectZOdpovedi.get("collection");

                JSONArray jsonItemsZPoleZOdpovedi = (JSONArray) jsonCollectionZOdpovedi.get("items");

                if (jsonItemsZPoleZOdpovedi.size() < 5) {
                    System.out.println("Nemáme ani 5 výsledků...");
                }

                for (int i = 0; i < jsonItemsZPoleZOdpovedi.size() && i < 5; i++) {
                    JSONObject item_links = (JSONObject) jsonItemsZPoleZOdpovedi.get(i);
                    JSONArray item_link_href = (JSONArray) item_links.get("links");
                    JSONObject item_link_hrefs = (JSONObject) item_link_href.get(0);

                    String odkaz = item_link_hrefs.get("href").toString();

                    System.out.println("Fotka Marsu zde: " + odkaz);
                }
            } catch (ParseException e) {
                System.out.println("Nepovedlo se zparzovat odpověď.");
                e.printStackTrace();
            }
        }
    }

    public static Boolean zkontrolujJestliJsouVsechnyOdkazyPouzeVideaEasyVerze() {
        System.out.println("\nRučním testem jsme zjistily, že vybrané odkazy nejsou pouze videa. Obsahují i kolekce náhledových obrázků, titulky, atp.\n");
        return false;
    }

    public static void zkontrolujJestliJsouVsechnyOdkazyPouzeVidea() {
        URL adresaAPI = null;
        try {
            adresaAPI = new URL("https://images-api.nasa.gov/search?keywords=mars&year_start=2018&year_end=2018&media_type=video");
        } catch (MalformedURLException e) {
            System.out.println("Nedaří se vytvořit adresa.");
            e.printStackTrace();
        }

        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) adresaAPI.openConnection();
        } catch (IOException e) {
            System.out.println("Nedaří se otevřít spojení, možná chyba firewallu.");
            e.printStackTrace();
        }

        try {
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(2000);
        } catch (ProtocolException e) {
            System.out.println("Nepodařilo se nastavit metodu GET.");
            e.printStackTrace();
        }

        try {
            connection.connect();
        } catch (IOException e) {
            System.out.println("Nepodařilo se připojit na server.");
            e.printStackTrace();
        }

        int kodOdpovedi = 0;
        try {
            kodOdpovedi = connection.getResponseCode();
        } catch (IOException e) {
            System.out.println("Nepodařilo se získat odpověď od serveru.");
            e.printStackTrace();
        }

        if (kodOdpovedi != 200) {
            throw new RuntimeException("Nedokážu se připojit na server. Kód odpovědi: " + kodOdpovedi);
        } else {
            String odpovedJakoText = new String();

            Scanner scanner = null;
            try {
                scanner = new Scanner(adresaAPI.openStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (scanner.hasNext()) {
                odpovedJakoText += scanner.nextLine();
            }
            scanner.close();

            JSONParser parser = new JSONParser();

            try {
                JSONObject jsonObjectZOdpovedi = (JSONObject) parser.parse(odpovedJakoText);
                JSONObject jsonCollectionZOdpovedi = (JSONObject) jsonObjectZOdpovedi.get("collection");
                JSONArray jsonItemsZPoleZOdpovedi = (JSONArray) jsonCollectionZOdpovedi.get("items");

                if (jsonItemsZPoleZOdpovedi.size() <= 0) {
                    System.out.println("Nedostali jsme ani jeden výsledek z API.");
                } else {
                    Set<String> nalezeneKoncovky = new HashSet<String>();

                    for (int i = 0; i < jsonItemsZPoleZOdpovedi.size() && i < 10; i++) {
                        JSONObject item_links = (JSONObject) jsonItemsZPoleZOdpovedi.get(i);
                        String item_href_na_kolekci = item_links.get("href").toString();

                        //https://images-assets.nasa.gov/video/NHQ_2018_0511_A Copter Companion for the Mars 2020 Rover on This Week @NASA – May 11, 2018/collection.json
                        String zakladUrl = item_href_na_kolekci.substring(0, 31);
                        String parametry = item_href_na_kolekci.substring(31);

                        URL urlNaKolekci = new URL(zakladUrl + URLEncoder.encode(parametry, StandardCharsets.UTF_8));

                        connection.disconnect();
                        connection = (HttpURLConnection) urlNaKolekci.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(2000);
                        connection.connect();

                        kodOdpovedi = connection.getResponseCode();
                        if (kodOdpovedi != 200) {
                            throw new RuntimeException("Nedokážu se připojit na server pro kolekci videa... Kód odpovědi: " + kodOdpovedi);
                        } else {
                            Scanner scanner2 = null;
                            try {
                                scanner2 = new Scanner(urlNaKolekci.openStream());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            String odpovedSKolekciVideiJakoText = new String();
                            while (scanner2.hasNext()) {
                                odpovedSKolekciVideiJakoText += scanner2.nextLine();
                            }

                            scanner2.close();

                            JSONArray jsonPoleZOdpovediKolekceVidei = (JSONArray) parser.parse(odpovedSKolekciVideiJakoText);
                            for (int j = 0; j < jsonPoleZOdpovediKolekceVidei.size(); j++) {
                                String odkaz = jsonPoleZOdpovediKolekceVidei.get(j).toString();
                                if (!odkaz.endsWith(".mp4")) {
                                    nalezeneKoncovky.add(odkaz.substring(odkaz.length() - 4, odkaz.length()));
                                }
                            }
                        }
                    }
                    if (nalezeneKoncovky.size() > 0) {
                        System.out.println("NASA nám kromě videa (.mp4) poskytla i soubory typu: ");
                        Iterator<String> it = nalezeneKoncovky.iterator();
                        while (it.hasNext()) {
                            System.out.println(it.next());
                        }
                    } else {
                        System.out.println("NASA nám poskytla pouze videa (.mp4).");
                    }
                }

            } catch (Exception e) {
                System.out.println("Nedopadlo to dobře...");
                e.printStackTrace();
            }
        }
    }
}