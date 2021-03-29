import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class RequestProcessor implements Runnable {
    private Socket socket = null;
    private OutputStream os = null;
    private BufferedReader in = null;
    private DataInputStream dis = null;
    private String msgToClient = "HTTP/1.1 200 OK\n"
            + "Server: HTTP server/0.1\n"
            + "Access-Control-Allow-Origin: *\n\n";
    private JSONObject jsonObject = new JSONObject();
    public RequestProcessor(Socket Socket) {
        super();
        try {
            socket = Socket;
            in = new BufferedReader(new
                    InputStreamReader(socket.getInputStream()));
            os = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void run() {
        String url;
        String trimmedUrl;
        try {
            // get url parameters from in
            url = in.readLine();
            // Create an object with the 3 keys and values from params
            Map<String, List<String>> params = new HashMap<String, List<String>>();
            String[] urlParts = url.split(" ");
            if (urlParts.length > 1) {
                // remove "/?" at the beginning of the url parameters
                trimmedUrl = urlParts[1].substring(2);
                String query = trimmedUrl ;
                // separate the key and value pair and put into the object
                for (String param : query.split("&")) {
                    String[] pair = param.split("=");
                    String key = null;
                    try {
                        key = URLDecoder.decode(pair[0], "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    String value = "";
                    if (pair.length > 1) {
                        try {
                            value = URLDecoder.decode(pair[1], "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    List<String> values = params.get(key);

                    if (values == null) {
                        values = new ArrayList<String>();
                        params.put(key, values);
                    }
                    values.add(value);
                }
                System.out.println(params);
            }

            float leftNum = Float.parseFloat(String.valueOf(params.get("leftOperand").get(0)));
            float rightNum = Float.parseFloat(String.valueOf(params.get("rightOperand").get(0)));
            String operation = String.valueOf(params.get("operation").get(0));
            float result = 0;

            if (operation.equals("+")) {
                result = leftNum + rightNum;
            } else if (operation.equals("-")) {
                result = leftNum - rightNum;
            } else if (operation.equals("*")) {
                result = leftNum * rightNum;
            } else if (operation.equals("/")) {
                result = leftNum / rightNum;
            } else if (operation.equals("%25")) {
                operation = "%";
                result = leftNum % rightNum;
            }

            // mock-up JSON object to send to client
            System.out.println(result);
            jsonObject.put("Expression", leftNum + " " + operation + " " + rightNum);
            jsonObject.put("Result", result);
        } catch (IOException e) {
            e.printStackTrace();
        }

//end of your code
        String response = msgToClient + jsonObject.toString();
        try {
            os.write(response.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }}