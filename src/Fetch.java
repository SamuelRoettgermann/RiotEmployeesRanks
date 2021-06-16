import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Random;
import java.util.stream.Stream;

public class Fetch {

    public static final String firstLine = "Name,Tier,LP,Level,Winrate,Server";

    private static final File STORE_FILE = new File("data/players.csv");
    private static final File PATH = new File("data");

    static {
        PATH.mkdirs();
    }

    public static Stream<Player> players() throws IOException {
        PATH.mkdirs(); //just in case, idk all the interactions
        STORE_FILE.createNewFile();

        return Files.lines(STORE_FILE.toPath())
                .map(Player::new);
    }

    private static volatile int jobs_done = 0;
    static final int BAR_LENGTH = 60;
    static final int FETCH_TIMES = 1471;

    public static void main(String[] args) throws IOException, InterruptedException {
        final long time0 = System.currentTimeMillis();

        Thread t = new Thread(() -> {
            for (; ; ) {
                progressBar(jobs_done, FETCH_TIMES, (System.currentTimeMillis() - time0) / 1000d, BAR_LENGTH);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        t.setDaemon(true);
        t.start();

        fetchServer("OCE", time0);
        t.interrupt();

        //effectively same code. too lazy to prevent duplicated code

//        final long time1 = System.currentTimeMillis();
//
//        Thread t2 = new Thread(() -> {
//            for (; ; ) {
//                progressBar(jobs_done, FETCH_TIMES, (System.currentTimeMillis() - time1) / 1000d, BAR_LENGTH);
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    break;
//                }
//            }
//        });
//        t2.setDaemon(true);
//        t2.start();
//
//        fetchServer("JP", time0);
//        t2.interrupt();
    }

    private static void fetchServer(final String server, final long time0) throws InterruptedException {
        final Random rand = new Random();

        jobs_done = 0; //reset jobs done in case this is not the first server
        System.out.print("\r" + " ".repeat(600) + "\r");  //just in case there was a bar printed

        System.out.println(server + ":"); //print out what we're fetching now

        for (; jobs_done < FETCH_TIMES; jobs_done++) {
            readHTML(jobs_done + 1, server);
            Thread.sleep(rand.nextInt(500)); //we sleep here as to not kill the op.gg servers
        }

        progressBar(FETCH_TIMES, FETCH_TIMES, (System.currentTimeMillis() - time0) / 1000d, BAR_LENGTH);

        System.out.print(System.lineSeparator() + System.lineSeparator());

    }

    private static void readHTML(int page, String server) {

        for (; ; ) {
            try {
                Document doc = Jsoup.connect("https://" + server.toLowerCase() + ".op.gg/ranking/ladder/page=" + page)
                        .userAgent("com.corex.fetchevenmorepagessorryforyourservers")
                        .timeout(10_000)
                        .get();

                Elements summoners = doc.select(".ranking-table__row");

                summoners.stream().map(element -> {
                    final String idGeneral = ".ranking-table__cell--";
                    String name = element.select(idGeneral + "summoner").get(0).child(0).child(1).text();
                    String rank = element.select(idGeneral + "tier").get(0).text();
                    String lp = element.select(idGeneral + "lp").get(0).text();
                    String level = element.select(idGeneral + "level").get(0).text();
                    String winrate = element.select(".winratio__text").get(0).text();

                    return new Player(name, rank, lp, level, winrate, server.toUpperCase());
                }).forEachOrdered(Fetch::store);

                break;
            } catch (IOException e) {
                continue;
            }
        }
    }

    private static void store(Player player) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(STORE_FILE, true))) {
            writer.append(player.toString()).append(System.lineSeparator());
        } catch (IOException e) {
            throw new RuntimeException("smth went wrong in store()");
        }
    }


    /**
     * @param jobsDone  The jobs that are already done
     * @param jobsToDo  The amount of jobs that need to be done (needs to be known in advance)
     * @param time      The time that has already passed
     * @param barLength Length of the bar in characters
     */
    private static void progressBar(int jobsDone, int jobsToDo, double time, int barLength) {
        double percentDone = (double) jobsDone / (double) jobsToDo;
        int l = (int) (percentDone * barLength);
        System.out.printf("[%s%s%s] %2.1f%% %d/%d %.1fs\r",
                "=".repeat(l == 0 ? l : l - 1), l == 0 ? "" : ">", " ".repeat(barLength - l),
                percentDone * 100, jobsDone, jobsToDo, time);
    }
}
