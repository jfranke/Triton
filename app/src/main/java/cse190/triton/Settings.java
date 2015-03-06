package cse190.triton;

public class Settings {

    static int numPlayers = 2;
    static String aiMoney = "0";
    static String aiMoney2 = "0";
    static String aiMoney3 = "0";
    static String playerMoney = "0";
    static String userID = "";
    static String aiName = "Minh";
    static String aiName2 = "John";
    static String aiName3 = "Albert";

    public static int getNumPlayers() {
        return numPlayers;
    }

    public static void setNumPlayers(int x) {
        numPlayers = x;
    }

    public static String getUserID() {return userID;}

    public static void setUserID(String id) {
        userID = id;
    }

    public static String getMoney(String id) {
        if(id.equals(aiName)) {
            return aiMoney;
        }

        else if(id.equals(aiName2)) {
            return aiMoney2;
        }

        else if(id.equals(aiName3)) {
            return aiMoney3;
        }

        else {
            return playerMoney;
        }
    }

    public static int getIntMoney(String id) {
        if(id.equals(aiName)) {
            return Integer.parseInt(aiMoney);
        }
        else if(id.equals(aiName2)) {
            return Integer.parseInt(aiMoney2);
        }
        else if(id.equals(aiName3)) {
            return Integer.parseInt(aiMoney3);
        }

        else {
            return Integer.parseInt(playerMoney);
        }
    }

    public static void setStartingMoney(String y) {
        aiMoney = "100000";
        aiMoney2 = "100000";
        aiMoney3 = "100000";
        playerMoney = "5000";
    }

    public static String subMoney(String id, int amount) {
        if (id.equals(aiName)) {
            aiMoney = String.valueOf(Integer.parseInt(aiMoney) - amount);
            return aiMoney;
        }

        else if (id.equals(aiName2)) {
            aiMoney2 = String.valueOf(Integer.parseInt(aiMoney2) - amount);
            return aiMoney2;
        }
        else if (id.equals(aiName3)) {
            aiMoney3 = String.valueOf(Integer.parseInt(aiMoney3) - amount);
            return aiMoney3;
        }
        else {
            playerMoney = String.valueOf(Integer.parseInt(playerMoney) - amount);
            return playerMoney;
        }
    }

    public static String addMoney(String id, int amount) {
        if (id.equals(aiName)) {
            aiMoney = String.valueOf(Integer.parseInt(aiMoney) + amount);
            return aiMoney;
        }

        if (id.equals(aiName2)) {
            aiMoney2 = String.valueOf(Integer.parseInt(aiMoney2) + amount);
            return aiMoney2;
        }

        if (id.equals(aiName3)) {
            aiMoney3 = String.valueOf(Integer.parseInt(aiMoney3) + amount);
            return aiMoney3;
        }

        else {
            playerMoney = String.valueOf(Integer.parseInt(playerMoney) + amount);
            return playerMoney;

        }
    }


    public static void resetMonies() {
        aiMoney = "100000";
        aiMoney2 = "100000";
        aiMoney3 = "100000";
        playerMoney = "5000";
    }
}
