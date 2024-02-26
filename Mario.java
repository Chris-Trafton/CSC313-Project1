import javax.swing.*;
import java.awt.*;
import java.awt.image.*;

public class Mario {
    private static final int WIN_WIDTH = 800;
    private static final int WIN_HEIGHT = 800;

    private static JFrame frame;

    public static void main(String[] args) {
        setup();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIN_WIDTH, WIN_HEIGHT);
        frame.setVisible(true);

//        Thread t1 = new Thread(new Mario().new drawBackground());
//        t1.start();
    }

    private static void setup() {
        frame = new JFrame("Mario");
    }

    private class draw implements Runnable {
        public void run() {
            while (true) {
                //draw background
                //draw mario
                //draw enemies
                delay(100);
            }
        }
    }

    private class drawBackground implements Runnable {
        public void run() {

        }
    }

    private void delay(int n) {
        try {
            Thread.sleep(n);
        } catch (InterruptedException e) {
            System.out.println("Error");
        }
    }
}