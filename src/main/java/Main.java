import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Objects;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        final String URL = "https://www.leagueoflegends.com/en-us/champions/";
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("accept", "application/json")
                .uri(URI.create(URL))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String content = response.body();
//        System.out.println(content);

        ArrayList<Champion> ChampionsList = generateChampsList(content, "style__Text-sc-n3ovyt-3 kThhiV");
        System.out.println(ChampionsList);
    }

    public static ArrayList<Champion> generateChampsList(String content, String value) throws IOException, InterruptedException {
        ArrayList<Champion> champsList = new ArrayList<>();

        //--------------------------------findClass------------------------------//
        int index = 0;
        while (index < content.length()) {

            boolean same = true;
            if (String.valueOf(content.charAt(index)).equals("c")) {
                String[] comparedString = new String[5];
                String[] wordInClass = {"c", "l", "a", "s", "s"};

                if (content.length() - index >= wordInClass.length) {

                    for (int i = index; i < index + wordInClass.length; i++) {
                        comparedString[i - index] = String.valueOf(content.charAt(i));

                        int nullPos = 0;
                        while (comparedString[nullPos] != null) {
                            if (nullPos + 1 < comparedString.length) {
                                nullPos++;
                            } else {
                                break;
                            }
                        }

                        for (int comparedIndex = 0; comparedIndex < nullPos; comparedIndex++) {
                            if (!Objects.equals(comparedString[comparedIndex], wordInClass[comparedIndex])) {
                                same = false;
                                break;
                            }
                        }
                    }
                }   else {
                    same = false;
                }
//-----------------------------------------------------getCLassName-----------------------------------------------------
                // if class
                // info now:
                // 1. index
                //iterate from end class (index + wordInClass.length) -> = sign -> any char not " " -> syntax error -->
                if (same) {
                    int endClass = index + wordInClass.length;
                    int equalsCounter = 0;
                    while (String.valueOf(content.charAt(endClass)).equals(" ") || String.valueOf(content.charAt(endClass)).equals("=")) {
                        if (String.valueOf(content.charAt(endClass)).equals("=")) {
                            equalsCounter++;
                        }
                        endClass++;
                    }

                    ArrayList<String> stringList = new ArrayList<>();
                    if (equalsCounter == 1) {
                        if (String.valueOf(content.charAt(endClass)).equals("\"")) {
                            while (true) {
                                String s = String.valueOf(content.charAt(endClass + 1));
                                if (s.equals("\"")) break;
                                stringList.add(s);
                                endClass++;
                            }
                        }

                        // Convert array className --> String
                        StringBuilder bufferClassName = new StringBuilder();
                        for (String s : stringList) {
                            bufferClassName.append(s);
                        }
                        String className = bufferClassName.toString();
//--------------------------------------------------getDataFromClass---------------------------------------------------

                        ArrayList<String> val = new ArrayList<>();
                        boolean started = false;
                        if (className.equals(value)) {
                            int ending = index;
                            while (!String.valueOf(content.charAt(ending)).equals("<")) {
                                if (started) {
                                    val.add(String.valueOf(content.charAt(ending)));
                                }
                                if (String.valueOf(content.charAt(ending)).equals(">")) {
                                    started = true;
                                }
                                if (ending == content.length()-1) {
                                    break;
                                }
                                ending++;
                            }

                            // convert arrayValue --> String
                            StringBuilder bufferValue = new StringBuilder();
                            for (String s : val) {
                                bufferValue.append(s);
                            }
                            String answer = bufferValue.toString();
                            String specMod = answer.replace("&#x27;", "-");
                            String ModifyName = specMod.replace(" ", "-");

                            String champsURL = "https://www.leagueoflegends.com/en-us/champions/" + ModifyName;
                            HttpClient client = HttpClient.newHttpClient();

                            HttpRequest request = HttpRequest.newBuilder()
                                    .GET()
                                    .header("accept", "application/json")
                                    .uri(URI.create(champsURL))
                                    .build();

                            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                            String charInfo = response.body();

                            String name = getValOfTag(charInfo,"data-testid", "overview:title");
                            String subtitle = getValOfTag(charInfo,"data-testid", "overview:subtitle");

                            Champion champion = new Champion(name, subtitle);
                            champsList.add(champion);
//                            Dr.Mundo & Nunu & Renata

                        }
                    }
                }
            }
            index++;
        }
        return champsList;
    }

    public static String getValOfTag(String content, String tag, String value) {
        //--------------------------------findClass------------------------------//
        int index = 0;
        while (index < content.length()) {

            boolean same = true;
            if (String.valueOf(content.charAt(index)).equals(String.valueOf(tag.charAt(0)))) {
                String[] comparedString = new String[tag.length()];
                String[] wordInClass = new String[tag.length()];

                for (int i = 0; i < tag.length(); i++) {
                    wordInClass[i] = String.valueOf(tag.charAt(i));
                }

                if (content.length() - index >= wordInClass.length) {

                    for (int i = index; i < index + wordInClass.length; i++) {
                        comparedString[i - index] = String.valueOf(content.charAt(i));

                        int nullPos = 0;
                        while (comparedString[nullPos] != null) {
                            if (nullPos + 1 < comparedString.length) {
                                nullPos++;
                            } else {
                                break;
                            }
                        }

                        for (int comparedIndex = 0; comparedIndex < nullPos; comparedIndex++) {
                            if (!Objects.equals(comparedString[comparedIndex], wordInClass[comparedIndex])) {
                                same = false;
                                break;
                            }
                        }
                    }
                }   else {
                    same = false;
                }
//-----------------------------------------------------getCLassName-----------------------------------------------------
                // if class
                // info now:
                // 1. index
                //iterate from end class (index + wordInClass.length) -> = sign -> any char not " " -> syntax error -->
                if (same) {
                    int endClass = index + wordInClass.length;
                    int equalsCounter = 0;
                    while (String.valueOf(content.charAt(endClass)).equals(" ") || String.valueOf(content.charAt(endClass)).equals("=")) {
                        if (String.valueOf(content.charAt(endClass)).equals("=")) {
                            equalsCounter++;
                        }
                        endClass++;
                    }

                    ArrayList<String> stringList = new ArrayList<>();
                    if (equalsCounter == 1) {
                        if (String.valueOf(content.charAt(endClass)).equals("\"")) {
                            while (true) {
                                String s = String.valueOf(content.charAt(endClass + 1));
                                if (s.equals("\"")) break;
                                stringList.add(s);
                                endClass++;
                            }
                        }

                        // Convert array className --> String
                        StringBuilder bufferClassName = new StringBuilder();
                        for (String s : stringList) {
                            bufferClassName.append(s);
                        }
                        String className = bufferClassName.toString();
//--------------------------------------------------getValueFromClass---------------------------------------------------

                        ArrayList<String> val = new ArrayList<>();
                        boolean started = false;
                        if (className.equals(value)) {
                            int ending = index;
                            while (!String.valueOf(content.charAt(ending)).equals("<")) {
                                if (started) {
                                    val.add(String.valueOf(content.charAt(ending)));
                                }
                                if (String.valueOf(content.charAt(ending)).equals(">")) {
                                    started = true;
                                }
                                if (ending == content.length()-1) {
                                    break;
                                }
                                ending++;
                            }

                            // convert arrayValue --> String
                            StringBuilder bufferValue = new StringBuilder();
                            for (String s : val) {
                                bufferValue.append(s);
                            }
                            return bufferValue.toString();
                        }
                    }
                }
            }
            index++;
        }
        return "value unfounded";
    }
}