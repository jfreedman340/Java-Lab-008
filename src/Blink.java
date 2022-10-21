import com.google.gson.Gson;
import com.google.gson.JsonArray;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;
import java.util.concurrent.TimeUnit;

public class Blink {
    private ZContext zctx;
    private ZMQ.Socket zsocket;
    private Gson gson;
    private String connStr;
    private final String topic = "GPIO";

    private static final int[] OFF = {0, 0, 0};

    public Blink(String protocol, String host, int port) {
        zctx = new ZContext();
        zsocket = zctx.createSocket(SocketType.PUB);
        this.connStr = String.format("%s://%s:%d", protocol, "*", port);
        zsocket.bind(connStr);
        this.gson = new Gson();
    }

    public void send(int[] color) throws InterruptedException {
        JsonArray ja = gson.toJsonTree(color).getAsJsonArray();
        String message = topic + " " + ja.toString();
        System.out.println(message);
        zsocket.send(message);
    }

    public void blinkN(int[] color, int times, int miliseconds) throws  InterruptedException{
        for(int i=0; i<times; i++) {
            send(color);
            TimeUnit.MILLISECONDS.sleep(miliseconds);
            send(Blink.OFF);
            TimeUnit.MILLISECONDS.sleep(miliseconds);
        }
    }

    public void close() throws InterruptedException {
        TimeUnit.SECONDS.sleep(2); // Allow the socket a chance to flush.
        this.zsocket.close();
        this.zctx.close();
    }

    public static void main(String[] args) {
        LEDClient blink = new LEDClient("tcp", "192.168.86.250", 5001);
        try {
            int[] blue = {51,204,255};
            int[] red = {255, 0 , 0};
            int[] yellow = {255, 204, 0};
            int[] green = {0, 153, 0};
            int[] purple = {102, 0, 153};
            int[] orange = {255, 102, 0};
            int[] grey = {153, 153, 153};
            int[] brown = {102, 51, 0};

            blink.blinkN(blue, 1, 1000);
            blink.blinkN(red, 2, 500);
            blink.blinkN(yellow, 3, 250);
            blink.blinkN(green, 4, 125);
            blink.blinkN(yellow, 5, 120);
            blink.blinkN(red, 6, 115);
            blink.blinkN(blue, 7, 110);

            blink.blinkN(brown, 8, 100);
            blink.blinkN(purple, 9, 90);
            blink.blinkN(orange, 10, 80);
            blink.blinkN(grey, 11, 70);

            blink.close();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}