import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;

public class Mario {
    private static final int WIN_WIDTH = 286;
    private static final int WIN_HEIGHT = 286;
    private static final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;

    private static JFrame frame;

    private static BufferedImage background;
    private static float xPos;

    private static boolean upPressed;
    private static boolean downPressed;
    private static boolean leftPressed;
    private static boolean rightPressed;
    private static boolean spacePressed;

    public static void main(String[] args) {
        setup();

        JPanel myPanel = new JPanel();

        bindKey(myPanel, "UP");
        bindKey(myPanel, "DOWN");
        bindKey(myPanel, "LEFT");
        bindKey(myPanel, "RIGHT");
        bindKey(myPanel, "SPACE");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIN_WIDTH, WIN_HEIGHT);
        frame.setVisible(true);
        frame.getContentPane().add(myPanel, "South");

        Thread t1 = new Thread(new Mario().new draw());
        t1.start();
    }

    private static void setup() {
        frame = new JFrame("Mario");
        xPos = 0;
        upPressed = false;
        downPressed = false;
        leftPressed = false;
        rightPressed = false;
        spacePressed = false;

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

        if (rightPressed && xPos > -3180) xPos -= 0.8f;
//        if (leftPressed && xPos < 0) xPos += 0.8f;

        g2d.drawImage(background, (int)xPos, 30, null);
    }

    private static void bindKey(JPanel myPanel, String input) {
        myPanel.getInputMap(IFW).put(KeyStroke.getKeyStroke("pressed " + input), input + " pressed");
        myPanel.getActionMap().put(input + " pressed", new Mario.KeyPressed(input));

        myPanel.getInputMap(IFW).put(KeyStroke.getKeyStroke("released " + input), input + " released");
        myPanel.getActionMap().put(input + " released", new Mario.KeyReleased(input));
    }

    private static class KeyPressed extends AbstractAction {
        private String action;

        public KeyPressed() { action = ""; }
        public KeyPressed(String input) { action = input; }

        public void actionPerformed(ActionEvent e) {
            if (action.equals("UP") || action.equals("W")) upPressed = true;
            if (action.equals("DOWN") || action.equals("S")) downPressed = true;
            if (action.equals("LEFT") || action.equals("A")) leftPressed = true;
            if (action.equals("RIGHT") || action.equals("D")) rightPressed = true;
            if (action.equals("SPACE")) spacePressed = true;
        }
    }

    private static class KeyReleased extends AbstractAction {
        private String action;

        public KeyReleased() { action = ""; }
        public KeyReleased(String input) { action = input; }

        public void actionPerformed(ActionEvent e) {
            if (action.equals("UP")) upPressed = false;
            if (action.equals("DOWN")) downPressed = false;
            if (action.equals("LEFT")) leftPressed = false;
            if (action.equals("RIGHT")) rightPressed = false;
            if (action.equals("SPACE")) spacePressed = false;
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