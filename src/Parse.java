import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.Locale;
import java.util.PriorityQueue;

public class Parse {
    static {
        File evalFolder = new File("evaluations");
        evalFolder.mkdirs();
    }



    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Startet jetzt\n");

        count(true);
//        nameStartsWith("Riot");
    }

    public static void count(boolean seperateServers) throws IOException {
        if(seperateServers) {
            System.out.println("EUW:\t" + Fetch.players().filter(player -> player.server.equals("EUW")).count());
            System.out.println("NA:\t\t" + Fetch.players().filter(player -> player.server.equals("NA")).count());
            System.out.println("EUNE:\t" + Fetch.players().filter(player -> player.server.equals("EUNE")).count());
            System.out.println("Total:\t" + Fetch.players().count());
        } else {
            System.out.println(Fetch.players().count());
        }
    }

    public static void nameStartsWith(String prefix) throws IOException {
        final String finalPrefix = prefix.toLowerCase();

        File thisEvalFile = new File("evaluations/nameStartsWith_" + finalPrefix);

        Fetch.players()
                .filter(p -> p.name.toLowerCase().startsWith(finalPrefix))
                .sorted()
                .forEachOrdered(p -> store(p, thisEvalFile));
    }

    private static void store(Player player, File saveTo) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(saveTo, true))) {
            writer.append(player.toString()).append(System.lineSeparator());
        } catch (IOException e) {
            throw new RuntimeException("smth went wrong in store()");
        }
    }

}
