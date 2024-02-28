import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;

public class Mario {
    private static final int WIN_WIDTH = 286;
    private static final int WIN_HEIGHT = 286;

    private static JFrame frame;

    private static BufferedImage background;
    private static int xPos;

    public static void main(String[] args) {
        setup();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIN_WIDTH, WIN_HEIGHT);
        frame.setVisible(true);

        Thread t1 = new Thread(new Mario().new draw());
        t1.start();
    }

    private static void setup() {
        frame = new JFrame("Mario");
        xPos = 0;

        String image = "";
        try {
            image = "background";
            background = ImageIO.read(new File("images/World_1.png"));
        } catch (IOException e) {
            System.out.println(image + " not found.");
        }
    }

    private class draw implements Runnable {
        public void run() {
            while (true) {
                drawBackground();
                //draw mario
                //draw enemies
                delay(1);
            }
        }
    }

    private void drawBackground() {
        Graphics g = frame.getGraphics();
        Graphics2D g2d = (Graphics2D) g;


        g2d.drawImage(background, xPos, 30, null);
    }

    private void delay(int n) {
        try {
            Thread.sleep(n);
        } catch (InterruptedException e) {
            System.out.println("Error");
        }
    }
}