package me.acul.coolpay;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Krist {

    private static char hexToBase36(int in) {

        for (int i = 6; i <= 251; i += 7) {

            if (in <= i) {

                if (i <= 69) {

                    return (char) ('0' + (i - 6) / 7);

                }

                return (char) ('a' + ((i - 76) / 7));

            }

        }

        return 'e';

    }

    public static TransactionResult transact(String password, String to, int amount) {

        JSONObject body = new JSONObject();
        body.put("privatekey", password);
        body.put("to", to);
        body.put("amount", amount);
        String returnData = post("http://krist.ceriat.net/transactions/", body);
        JSONObject json = (JSONObject) new JSONTokener(returnData).nextValue();
        TransactionResult success = new TransactionResult();
        success.ok = json.getBoolean("ok");
        if(!success.ok) {
            success.error = json.getString("error");
        }
        return success;
    }

    static int getBalance(String address) {

        try {

            URI uri = new URI("http://krist.ceriat.net/addresses/" + address);
            JSONTokener tokener = new JSONTokener(uri.toURL().openStream());
            JSONObject root = new JSONObject(tokener);

            if (root.getBoolean("ok")) {

                return root.getJSONObject("address").getInt("balance");

            } else {

                return 0;

            }

        } catch (URISyntaxException | IOException e) {

            e.printStackTrace();

        }

        return 0;

    }

    public static String makeV2Address(String password) {

        String[] chars = {"", "", "", "", "", "", "", "", ""};
        String prefix = "k";
        String hash = sha256(sha256(password));

        for (int i = 0; i <= 8; i++) {

            chars[i] = hash.substring(0, 2);
            hash = sha256(sha256(hash));

        }

        for (int i = 0; i <= 8; ) {

            int index = Integer.parseInt(hash.substring(2 * i, 2 + (2 * i)), 16) % 9;

            if (chars[index].equals("")) {

                hash = sha256(hash);

            } else {

                prefix = prefix + hexToBase36(Integer.parseInt(chars[index], 16));
                chars[index] = "";
                i++;

            }

        }

        return prefix;

    }

    private static String sha256(String in) {

        MessageDigest md;

        try {

            md = MessageDigest.getInstance("SHA-256");
            md.update(in.getBytes("UTF-8"));
            byte[] digest = md.digest();
            return String.format("%064x", new java.math.BigInteger(1, digest));

        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {

            e.printStackTrace();

        }

        return null;

    }

    //Credit to Lignumm
    //https://github.com/Lignumm/JKrist

    private static HttpURLConnection getConnection(String url) {

        try {

            URL lurl = new URL(url);
            HttpURLConnection conn;

            switch (lurl.getProtocol().toLowerCase()) {

                case "https":
                case "http":
                    conn = (HttpURLConnection) lurl.openConnection();
                    conn.setReadTimeout(4000);
                    break;

                default:
                    throw new IllegalArgumentException("URL protocol must be HTTP/HTTPS");

            }

            return conn;

        } catch (IOException e) {

            e.printStackTrace();

        }

        return null;
    }

    private static String post(@SuppressWarnings("SameParameterValue") String url, JSONObject body) {

        HttpURLConnection conn = getConnection(url);

        try {

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            String jsonBody = body.toString(0);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Content-Length", String.valueOf(jsonBody.length()));

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            bw.write(jsonBody);
            bw.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String response = "";

            String line;
            while ((line = br.readLine()) != null) {
                response += line + "\n";
            }

            br.close();
            return response;

        } catch (IOException e) {

            e.printStackTrace();

        }

        return null;

    }

    //TODO: Remove when not use, uncomment when used.
// --Commented out by Inspection START (30.07.16, 13:48):
//    public static String get(String url) {
//        HttpURLConnection conn = getConnection(url);
//
//        try {
//            conn.setRequestMethod("GET");
//
//            InputStream is = conn.getInputStream();
//            BufferedReader br = new BufferedReader(new InputStreamReader(is));
//            String response = "";
//
//            String line;
//            while ((line = br.readLine()) != null) {
//                response += line + "\n";
//            }
//
//            br.close();
//            return response;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
// --Commented out by Inspection STOP (30.07.16, 13:48)
}
