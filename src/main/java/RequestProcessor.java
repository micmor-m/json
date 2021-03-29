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
    // Create an object with the 3 keys-values pair from params
    public static Map<String, List<String>> getQueryParams(String url) {
        Map<String, List<String>> params = new HashMap<String, List<String>>();
        String[] urlParts = url.split(" ");
        if (urlParts.length > 1) {
            String query = urlParts[0] ;
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
                    value = pair[1];
                }
                List<String> values = params.get(key);

                if (values == null) {
                    values = new ArrayList<String>();
                    params.put(key, values);
                }
                values.add(value);
            }
        }
        return params;
    }

    public void run() {

        // get url parameters from in
        String url = null;
        try {
            url = in.readLine();
            // Discard first 5 characters for the route
            String urlRoute = url.substring(5);

            if (!url.substring(0,5).toUpperCase().equals("GET /")){
                System.out.println("Client required wrong route");
            } else {
                Map<String, List<String>> params = this.getQueryParams(urlRoute);
                if ((String.valueOf(params.get("leftOperand").get(0)) == "") || (String.valueOf(params.get("rightOperand").get(0)) == "") || (params.get("operation").get(0).equals("undefined"))) {
                    System.out.println("At least one of the required params was empty");
                } else {
                    if (isNumeric(String.valueOf(params.get("leftOperand").get(0))) && isNumeric(String.valueOf(params.get("rightOperand").get(0)))) {
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
                        jsonObject.put("Expression", leftNum + " " + operation + " " + rightNum);
                        jsonObject.put("Result", result);
                    } else {
                        System.out.println("At least one of the required params was not parsable");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


//end of your code
        String response = msgToClient + jsonObject.toString();
        try {
            os.write(response.getBytes());
            os.flush();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }}
