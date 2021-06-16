public class Player implements Comparable<Player> {
    public String name;
    public String tier;
    public int lp;
    public int level;
    public int winrate;
    public String server;

    /**
     * @param name    %s
     * @param tier    %s
     * @param lp      %d LP
     * @param level   %d
     * @param winrate %d%%
     * @param server  %s
     */
    public Player(String name, String tier, String lp, String level, String winrate, String server) {
        this.name = name;
        this.tier = tier;
        this.lp = Integer.parseInt(lp.replaceAll(",| LP", ""));
        this.level = Integer.parseInt(level);
        this.winrate = Integer.parseInt(winrate.replace("%", ""));
        this.server = server;
    }

    public Player(String allAttributes) {
        var splitted = allAttributes.split(",");
        name = splitted[0];
        tier = splitted[1];
        lp = Integer.parseInt(splitted[2].replaceAll(",| LP", ""));
        level = Integer.parseInt(splitted[3]);
        winrate = Integer.parseInt(splitted[4].replace("%", ""));
        server = splitted[5];
    }

    private int eloToNumber() {
        return switch (tier) {
            case "Iron 4" -> 0;
            case "Iron 3" -> 1;
            case "Iron 2" -> 2;
            case "Iron 1" -> 3;
            case "Bronze 4" -> 4;
            case "Bronze 3" -> 5;
            case "Bronze 2" -> 6;
            case "Bronze 1" -> 7;
            case "Silver 4" -> 8;
            case "Silver 3" -> 9;
            case "Silver 2" -> 10;
            case "Silver 1" -> 11;
            case "Gold 4" -> 12;
            case "Gold 3" -> 13;
            case "Gold 2" -> 14;
            case "Gold 1" -> 15;
            case "Platinum 4" -> 16;
            case "Platinum 3" -> 17;
            case "Platinum 2" -> 18;
            case "Platinum 1" -> 19;
            case "Diamond 4" -> 20;
            case "Diamond 3" -> 21;
            case "Diamond 2" -> 22;
            case "Diamond 1" -> 23;
            case "Master" -> 24;
            case "Grandmaster" -> 25;
            case "Challenger" -> 26;
            default -> -1;
        };
    }

    public String toString() {
        return name + "," + tier + "," + lp + "," + level + "," + winrate + "," + server;
    }

    @Override
    public int compareTo(Player o) {
        //order is reversed as to make it easier in the parser (aka have the highest ones at the top)
        return Integer.compare(o.eloToNumber(), eloToNumber());
    }
}
