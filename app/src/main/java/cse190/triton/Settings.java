package cse190.triton;

public class Settings {

    static int numPlayers = 0;
    static String aiMoney = "100";
    static String playerMoney = "100";

    public static int getNumPlayers() {
        return numPlayers;
    }

    public static void setNumPlayers(int x) {
        numPlayers = x;
    }

    public static String getMoney(String id) {
        if(id.equals("ai")) {
            return aiMoney;
        }

        else {
            return playerMoney;
        }
    }

    public static void setStartingMoney(String y) {
        aiMoney = y;
        playerMoney = y;
    }

    public static String subMoney(String id, int amount) {
        if (id.equals("ai")) {
            aiMoney = String.valueOf(Integer.parseInt(aiMoney) - amount);
            return aiMoney;
        }

        else {
            playerMoney = String.valueOf(Integer.parseInt(playerMoney) - amount);
            return playerMoney;
        }
    }

    public static String addMoney(String id, int amount) {
        if (id.equals("ai")) {
            aiMoney = String.valueOf(Integer.parseInt(aiMoney) + amount);
            return aiMoney;
        }

        else {
            playerMoney = String.valueOf(Integer.parseInt(playerMoney) + amount);
            return playerMoney;
        }
    }
}
