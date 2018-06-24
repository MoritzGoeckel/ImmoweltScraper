import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class Downloader {
    public static void main(String[] args) throws IOException {
        URL[] urls = {
                new URL("https://www.immowelt.de/liste/stuttgart/haeuser/kaufen?sort=relevanz&cp=1"),
                new URL("https://www.immowelt.de/liste/stuttgart/haeuser/kaufen?sort=relevanz&cp=2"),
                new URL("https://www.immowelt.de/liste/stuttgart/haeuser/kaufen?sort=relevanz&cp=3"),
                new URL("https://www.immowelt.de/liste/stuttgart/haeuser/kaufen?sort=relevanz&cp=4"),
                new URL("https://www.immowelt.de/liste/stuttgart/haeuser/kaufen?sort=relevanz&cp=5"),
                new URL("https://www.immowelt.de/liste/stuttgart/haeuser/kaufen?sort=relevanz&cp=6")
        };

        List<Hashtable<String, String>> homes = new ArrayList<>();

        for(URL url : urls) {
            String content = download(url);

            Document doc = Jsoup.parse(content);
            Elements listItems = doc.select(".listitem");
            for (Element listItem : listItems) {
                String description = listItem.select(".ellipsis").text();
                String location = listItem.select(".listlocation").text();

                Hashtable<String, String> facts = new Hashtable<>();
                facts.put("Description", description);
                facts.put("Location", location);

                //System.out.println(description + ", " + location);

                for(Element hardFact : listItem.select(".hardfact")){
                    //System.out.println(hardFact.text());

                    String[] words = hardFact.text().split(" ");
                    String identifier = words[0];
                    String value = "NAN";
                    String unit = "NONE";

                    int i = 1;
                    for(;i < words.length; i++) {
                        char firstChar = words[i].toCharArray()[0];
                        if (firstChar <= '9' && firstChar >= '0') {
                            value = words[i];
                            break;
                        }
                    }

                    if(i < words.length - 1)
                        unit = words[words.length - 1];

                    facts.put(identifier, value);

                    //System.out.println("Identifier=" + identifier + " value=" + value + " unit="+unit);
                }

                homes.add(facts);
            }
        }

        //Output
        String delimiterTab = "\t";
        String delimiterLine = "\r\n";

        StringBuilder output = new StringBuilder();

        for(String key : homes.get(0).keySet())
            output.append(key).append(delimiterTab);
        output.append(delimiterLine);

        for(Dictionary<String, String> home : homes){
            for(String key : homes.get(0).keySet())
                output.append(home.get(key)).append(delimiterTab);
            output.append(delimiterLine);
        }

        System.out.println(output.toString());
    }

    public static String download(URL url){
        InputStream is = null;
        BufferedReader br;

        StringBuilder output = new StringBuilder();

        try {
            is = url.openStream();  // throws an IOException
            br = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = br.readLine()) != null) {
                output.append(line + "\n");
            }
        } catch (MalformedURLException mue) {
            mue.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException ioe) {
                // nothing to see here
            }
        }

        return output.toString();
    }
}
