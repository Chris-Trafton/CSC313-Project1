import java.util.Vector;
import java.util.Random;

import java.time.LocalTime;
import javax.swing.*;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.Graphics;
import java.awt.Graphics2D;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class SarahMario {
    // global variables for the game
    private static Boolean endgame;

    private static BufferedImage background;

    private static BufferedImage mario;
    private static Vector<BufferedImage> goombaImages;
    private static Vector<ImageObject> goombas;

    private static Boolean upPressed;
    private static Boolean downPressed;
    private static Boolean leftPressed;
    private static Boolean rightPressed;
    private static double lastPressed;

    private static ImageObject p1;
    private static double p1width;
    private static double p1height;
    private static double p1originalX;
    private static double p1originalY;
    private static double p1velocity;

    private static String backgroundState;
    private static Long audiolifetime;
    private static Long lastAudioStart;
    private static Clip clip;

    private static int XOFFSET;
    private static int YOFFSET;
    private static int WINWIDTH;
    private static int WINHEIGHT;

    private static double pi;
    private static double quarterPi;
    private static double halfPi;
    private static double threequartersPi;
    private static double fivequartersPi;
    private static double threehalvesPi;
    private static double sevenquartersPi;
    private static double twoPi;

    private static JFrame appFrame;

    private static final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;

    public SarahMario() { setup(); }

    public static void setup() {
        appFrame = new JFrame("Super Mario NES");
        XOFFSET = 20;
        YOFFSET = 30;
        WINWIDTH = 338;
        WINHEIGHT = 271;
        pi = 3.1459265358979;
        quarterPi = 0.25 * pi;
        halfPi = 0.5 * pi;
        threequartersPi = 0.75 * pi;
        fivequartersPi = 1.25 * pi;
        threehalvesPi = 1.5 * pi;
        sevenquartersPi = 1.75 * pi;
        twoPi = 2.0 * pi;
        endgame = false;
        p1width = 23;
        p1height = 30;
        p1originalX = (double)XOFFSET + ((double) WINWIDTH / 4.0) - (p1width / 2.0);
        p1originalY = (double) YOFFSET + ((double) WINHEIGHT / 2.0) - (p1height / 2.0);
        backgroundState = "";
        audiolifetime = 78000L;

        try {
            mario = ImageIO.read(new File("images/Mario.png"));

            // goomba images
            goombas = new Vector<ImageObject>();
            goombaImages = new Vector<BufferedImage>();
            goombaImages.addElement(ImageIO.read(new File("images/Mario.png")));

        } catch (IOException ioe) {

        }
    }

    private static class Animate implements Runnable {
        public void run() {
            while(endgame == false) {
                backgroundDraw();
                enemiesDraw();
                playerDraw();
//                healthDraw();

                try {
                    Thread.sleep(32);
                } catch (InterruptedException e) {}
            }
        }
    }

    private static class AudioLooper implements Runnable {
        public void run() {
            while(endgame == false) {
                Long currTime = Long.valueOf(System.currentTimeMillis());
                if (currTime - lastAudioStart > audiolifetime) {
                    // TODO: get filename of the audio
                    playAudio(backgroundState);
                }
            }
        }
    }

    private static void playAudio(String backgroundState) {
        try {
            clip.stop();
        } catch (Exception e) {
            // NOP
        }

        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(new File(backgroundState).getAbsoluteFile());
            clip = AudioSystem.getClip();
            clip.open(ais);
            clip.start();
            lastAudioStart = System.currentTimeMillis();
            audiolifetime = Long.valueOf(78000);
        } catch (Exception e) {
            // NOP
        }
    }

    private static class PlayerMover implements Runnable {
        private double velocitystep;

        public PlayerMover() { velocitystep = 1; }

        public void run() {
            while(endgame == false) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {}

                if (upPressed || downPressed || leftPressed || rightPressed) {
                    p1velocity = velocitystep;

                    if (upPressed) {
                        if (leftPressed) {
                            p1.setInternalAngle(fivequartersPi);
                        } else if (rightPressed) {
                            p1.setInternalAngle(5.49779);
                        } else {
                            p1.setInternalAngle(threehalvesPi);
                        }
                    }
                    if (downPressed) {
                        if (leftPressed) {
                            p1.setInternalAngle(2.35619);
                        } else if (rightPressed) {
                            p1.setInternalAngle(quarterPi);
                        } else {
                            p1.setInternalAngle(halfPi);
                        }
                    }
                    if (leftPressed) {
                        if (upPressed) {
                            p1.setInternalAngle(fivequartersPi);
                        } else if (downPressed) {
                            p1.setInternalAngle(threequartersPi);
                        } else {
                            p1.setInternalAngle(pi);
                        }
                    }
                    if (rightPressed) {
                        if (upPressed) {
                            p1.setInternalAngle(5.49779);
                        } else if (downPressed) {
                            p1.setInternalAngle(quarterPi);
                        } else {
                            p1.setInternalAngle(0.0);
                        }
                    }
                } else {
                    p1velocity = 0.0;
                    p1.setInternalAngle(threehalvesPi);
                }

                p1.updateBounce();
                p1.move(p1velocity * Math.cos(p1.getInternalAngle()), p1velocity * Math.sin(p1.getInternalAngle()));
                int wrap = p1.screenWrap(XOFFSET, XOFFSET + WINWIDTH, YOFFSET, YOFFSET + WINHEIGHT);
//                backgroundState = bgWrap(backgroundState, wrap);
                if (wrap != 0) {
//                    clearEnemies();
                    generateEnemies(backgroundState);
                }
            }
        }
    }

    private static void clearEnemies() {
        goombas.clear();
    }

    private static void generateEnemies(String backgroundState) {
        goombas.addElement(new ImageObject(20, 90, 33, 33, 0.0));
        goombas.addElement(new ImageObject(250, 230, 33, 33, 0.0));

        for (int i = 0; i < goombas.size(); i++) {
            goombas.elementAt(i).setMaxFrames(25);
        }
    }

    private static class EnemyMover implements Runnable {
        private double goombavelocitystep;
        private double goombavelocity;

        public EnemyMover() { goombavelocity = 2; }

        public void run() {
            Random randomNumbers = new Random(LocalTime.now().getNano());
            while(endgame == false) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    // N0P
                }

                try {
                    for (int i = 0; i < goombas.size(); i++) {
                        int state = randomNumbers.nextInt(1000);
                        if (state < 5) {
                            goombavelocity = goombavelocitystep;
                            goombas.elementAt(i).setInternalAngle(0);
                        } else if (state < 10) {
                            goombavelocity = goombavelocitystep;
                            goombas.elementAt(i).setInternalAngle(halfPi);
                        } else if (state < 15) {
                            goombavelocity = goombavelocitystep;
                            goombas.elementAt(i).setInternalAngle(pi);
                        } else if (state < 20) {
                            goombavelocity = goombavelocitystep;
                            goombas.elementAt(i).setInternalAngle(threehalvesPi);
                        } else if (state < 250) {
                            goombavelocity = goombavelocitystep;
                        } else {
                            goombavelocity = 0;
                        }

                        goombas.elementAt(i).updateBounce();
                        goombas.elementAt(i).move(goombavelocity *
                                        Math.cos(goombas.elementAt(i).getInternalAngle()),
                                goombavelocity * Math.sin(goombas.elementAt(i).getInternalAngle()));
                    }
                } catch (java.lang.NullPointerException jlnpe) {
                    // NOP
                }
            }
        }
    }

    private static class CollisionChecker implements Runnable {
        public void run() {
            while (endgame == false) {
                // code to check if a collision occurs
            }
        }

        private static void checkMoversAgainstWalls(Vector<ImageObject> wallsInput) {
            for (int i = 0; i < wallsInput.size(); i++) {
                if (SarahMario.GameLevel.collisionOccurs(p1, wallsInput.elementAt(i))) {
                    p1.setBounce(true);
                }
                for (int j = 0; j < goombas.size(); j++) {
                    if (SarahMario.GameLevel.collisionOccurs(goombas.elementAt(j), wallsInput.elementAt(j))) {
                        goombas.elementAt(j).setBounce(true);
                    }
                }
            }
        }
    }

    private static AffineTransformOp rotateImageObject(ImageObject obj) {
        AffineTransform at = AffineTransform.getRotateInstance(-obj.getInternalAngle(),
                obj.getWidth() / 2.0, obj.getHeight() / 2.0);
        AffineTransformOp atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        return atop;
    }

    private static void backgroundDraw() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2D = (Graphics2D) g;

        g2D.drawImage(background, XOFFSET, YOFFSET, null);
    }

    private static void playerDraw() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2D = (Graphics2D) g;
        p1.setMaxFrames(10);

        if (upPressed || downPressed || leftPressed || rightPressed) {
            if (upPressed == true) {
                if (p1.getCurrentFrame() < 5) {
//                    g2D.drawImage(rotateImageObject(p1).filter(link[0], null),
                    g2D.drawImage(mario,
                            (int) (p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
                } else if (p1.getCurrentFrame() > 5) {
//                    g2D.drawImage(rotateImageObject(p1).filter(link[1], null),
                    g2D.drawImage(mario,
                            (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
                }
                p1.updateCurrentFrame();
            }
            if (downPressed == true) {
                if (p1.getCurrentFrame() < 5) {
//                    g2D.drawImage(rotateImageObject(p1).filter(link[2], null),
                    g2D.drawImage(mario,
                            (int)(p1.getX() + 0.5), (int) (p1.getY() + 0.5), null);
                } else if (p1.getCurrentFrame() > 5) {
//                    g2D.drawImage(rotateImageObject(p1).filter(link[3], null),
                    g2D.drawImage(mario,
                            (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
                }
                p1.updateCurrentFrame();
            }
            if (leftPressed == true) {
                if (p1.getCurrentFrame() < 5) {

//                    g2D.drawImage(rotateImageObject(p1).filter(link[4], null),
                    g2D.drawImage(mario,
                            (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
                } else if (p1.getCurrentFrame() > 5) {
//                    g2D.drawImage(rotateImageObject(p1).filter(link[5], null),
                    g2D.drawImage(mario,
                            (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
                }
                p1.updateCurrentFrame();
            }
            if (rightPressed == true) {
                if (p1.getCurrentFrame() < 5) {
//                    g2D.drawImage(rotateImageObject(p1).filter(link[6], null),
                    g2D.drawImage(mario,
                            (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
                } else if (p1.getCurrentFrame() > 5) {
//                    g2D.drawImage(rotateImageObject(p1).filter(link[7], null),
                    g2D.drawImage(mario,
                            (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
                }
                p1.updateCurrentFrame();
            }
        } else {
            if (Math.abs(lastPressed - 90.0) < 1.0) {
//                g2D.drawImage(rotateImageObject(p1).filter(link[0], null),
                g2D.drawImage(mario,
                        (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
            }
            if (Math.abs(lastPressed - 270.0) < 1.0) {
//                g2D.drawImage(rotateImageObject(p1).filter(link[2], null),
                g2D.drawImage(mario,
                        (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
            }
            if (Math.abs(lastPressed - 0.0) < 1.0) {
//                g2D.drawImage(rotateImageObject(p1).filter(link[6], null),
                g2D.drawImage(mario,
                        (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
            }
            if (Math.abs(lastPressed - 180.0) < 1.0) {
//                g2D.drawImage(rotateImageObject(p1).filter(link[4], null),
                g2D.drawImage(mario,
                        (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
            }
        }

        // g2D.drawImage(rotateImageObject(p1).filter(player, null), (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
    }

    private static void enemiesDraw() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2D = (Graphics2D) g;

        for (int i = 0; i < goombas.size(); i++) {
            if (Math.abs(goombas.elementAt(i).getInternalAngle() - 0.0) < 1.0) {
                if (goombas.elementAt(i).getCurrentFrame() < goombas.elementAt(i).getMaxFrames() / 2) {
                    g2D.drawImage(rotateImageObject(goombas.elementAt(i)).filter(goombaImages.elementAt(6), null),
                            (int)(goombas.elementAt(i).getX() + 0.5),
                            (int)(goombas.elementAt(i).getY() + 0.5), null);
                } else {
                    g2D.drawImage(rotateImageObject(goombas.elementAt(i)).filter(goombaImages.elementAt(7), null),
                            (int)(goombas.elementAt(i).getX() + 0.5),
                            (int)(goombas.elementAt(i).getY() + 0.5), null);
                }
                goombas.elementAt(i).updateCurrentFrame();;
            }
            if(Math.abs(goombas.elementAt(i).getInternalAngle() - pi) < 1.0) {
                if (goombas.elementAt(i).getCurrentFrame() < goombas.elementAt(i).getMaxFrames() / 2) {
                    g2D.drawImage(rotateImageObject(goombas.elementAt(i)).filter(goombaImages.elementAt(4), null),
                            (int)(goombas.elementAt(i).getX() + 0.5),
                            (int)(goombas.elementAt(i).getY() + 0.5), null);
                } else {
                    g2D.drawImage(rotateImageObject(goombas.elementAt(i)).filter(goombaImages.elementAt(5), null),
                            (int)(goombas.elementAt(i).getX() + 0.5),
                            (int)(goombas.elementAt(i).getY() + 0.5), null);
                }
                goombas.elementAt(i).updateCurrentFrame();
            }
            if (Math.abs(goombas.elementAt(i).getInternalAngle() - halfPi) < 1.0) {
                if (goombas.elementAt(i).getCurrentFrame() < goombas.elementAt(i).getMaxFrames() / 2) {
                    g2D.drawImage(rotateImageObject(goombas.elementAt(i)).filter(goombaImages.elementAt(2), null),
                            (int)(goombas.elementAt(i).getX() + 0.5),
                            (int)(goombas.elementAt(i).getY() + 0.5), null);
                } else {
                    g2D.drawImage(rotateImageObject(goombas.elementAt(i)).filter(goombaImages.elementAt(3), null),
                            (int)(goombas.elementAt(i).getX() + 0.5),
                            (int)(goombas.elementAt(i).getY() + 0.5), null);
                }
                goombas.elementAt(i).updateCurrentFrame();
            }
            if (Math.abs(goombas.elementAt(i).getInternalAngle() - threehalvesPi) < 1.0) {
                if (goombas.elementAt(i).getCurrentFrame() < goombas.elementAt(i).getMaxFrames() / 2) {
                    g2D.drawImage(rotateImageObject(goombas.elementAt(i)).filter(goombaImages.elementAt(0), null),
                            (int)(goombas.elementAt(i).getX() + 0.5),
                            (int)(goombas.elementAt(i).getY() + 0.5), null);
                } else {
                    g2D.drawImage(rotateImageObject(goombas.elementAt(i)).filter(goombaImages.elementAt(1), null),
                            (int)(goombas.elementAt(i).getX() + 0.5),
                            (int)(goombas.elementAt(i).getY() + 0.5), null);
                }
                goombas.elementAt(i).updateCurrentFrame();
            }
        }
    }

    private static class KeyPressed extends AbstractAction {
        private String action;

        public KeyPressed() { action = ""; }
        public KeyPressed(String input) { action = input; }

        public void actionPerformed(ActionEvent e) {
            if (action.equals("UP")) {
                upPressed = true;
                lastPressed = 90.0;
            }
            if (action.equals("DOWN")) {
                downPressed = true;
                lastPressed = 270.0;
            }
            if (action.equals("LEFT")) {
                leftPressed = true;
                lastPressed = 180.0;
            }
            if (action.equals("RIGHT")) {
                rightPressed = true;
                lastPressed = 0.0;
            }
        }
    }

    private static class KeyReleased extends AbstractAction {
        private String action;

        public KeyReleased() { action = ""; }
        public KeyReleased(String input) { action = input; }

        public void actionPerformed(ActionEvent e) {
            if(action.equals("UP")) {
                upPressed = false;
            }
            if (action.equals("DOWN")) {
                downPressed = false;
            }
            if (action.equals("LEFT")) {
                leftPressed = false;
            }
            if (action.equals("RIGHT")) {
                rightPressed = false;
            }
        }
    }

    private static class QuitGame implements ActionListener {
        public void actionPerformed(ActionEvent e) { endgame = true; }
    }

    private static class StartGame implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            endgame = true;
            upPressed = false;
            downPressed = false;
            leftPressed = false;
            rightPressed = false;
            lastPressed = 90.0;
            backgroundState = ""; // TODO: add in background image for this

            try {
                clearEnemies();
                generateEnemies(backgroundState);
            } catch (java.lang.NullPointerException jlnpe) {}

            p1 = new ImageObject(p1originalX, p1originalY, p1width, p1height, 0.0);
            p1velocity = 0.0;
            p1.setInternalAngle(threehalvesPi);
            p1.setMaxFrames(2);
            p1.setlastposx(p1originalX);
            p1.setlastposy(p1originalY);

            try {
                Thread.sleep(50);
            } catch(InterruptedException ie) {}

            lastAudioStart = System.currentTimeMillis();
            playAudio(backgroundState);
            endgame = false;
            Thread t1 = new Thread(new Animate());
            Thread t2 = new Thread(new PlayerMover());
            Thread t3 = new Thread(new CollisionChecker());
            Thread t4 = new Thread(new AudioLooper());
            Thread t5 = new Thread(new EnemyMover());
            t1.start();
            t2.start();
            t3.start();
            t4.start();
            t5.start();
        }
    }

    private static class GameLevel implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JComboBox cb = (JComboBox) e.getSource();
            String textLevel = (String) cb.getSelectedItem();
        }

        private static Boolean isInside(double p1x, double p1y, double p2x1, double p2y1, double p2x2, double p2y2) {
            Boolean ret = false;
            if (p1x > p2x1 && p1x < p2x2) {
                if (p1y > p2y1 && p1y < p2y2) {
                    ret = true;
                }
                if (p1y > p2y2 && p1y < p2y1) {
                    ret = true;
                }
            }
            if (p1x > p2x2 && p1x < p2x1) {
                if (p1y > p2y1 && p1y < p2y2) {
                    ret = true;
                }
                if (p1y > p2y2 && p1y < p2y1) {
                    ret = true;
                }
            }
            return ret;
        }

        private static Boolean collisionOccursCoordinates(double p1x1, double p1y1, double p1x2, double p1y2,
                                                          double p2x1, double p2y1, double p2x2, double p2y2) {
            Boolean ret = false;
            if (isInside(p1x1, p1y1, p2x1, p2y1, p2x2, p2y2) == true) {
                ret = true;
            }
            if (isInside(p1x1, p1y2, p2x1, p2y1, p2x2, p2y2) == true) {
                ret = true;
            }
            if (isInside(p1x2, p1y1, p2x1, p2y1, p2x2, p2y2) == true) {
                ret = true;
            }
            if (isInside(p1x2, p1y2, p2x1, p2y1, p2x2, p2y2) == true) {
                ret = true;
            }
            if (isInside(p2x1, p2y1, p1x1, p1y1, p1x2, p1y2) == true) {
                ret = true;
            }
            if (isInside(p2x1, p2y2, p1x1, p1y1, p1x2, p1y2) == true) {
                ret = true;
            }
            if (isInside(p2x2, p2y1, p1x1, p1y1, p1x2, p1y2) == true) {
                ret = true;
            }
            if (isInside(p2x2, p2y2, p1x1, p1y1, p1x2, p1y2) == true) {
                ret = true;
            }
            return ret;
        }

        private static Boolean collisionOccurs(ImageObject obj1, ImageObject obj2) {
            Boolean ret = false;
            if (collisionOccursCoordinates(obj1.getX(), obj1.getY(), obj1.getX() + obj1.getWidth(),
                    obj1.getY() + obj1.getHeight(), obj2.getX(), obj2.getY(), obj2.getX() + obj2.getWidth(),
                    obj2.getY() + obj2.getHeight()) == true) {
                ret = true;
            }
            return ret;
        }
    }


    private static class ImageObject {
        // vars of ImageObject
        private double x;
        private double y;
        private double lastposx;
        private double lastposy;
        private double xwidth;
        private double yheight;
        private double angle; // in Radians
        private double internalangle; // in Radians
        private Vector<Double> coords;
        private Vector<Double> triangles;
        private double comX;
        private double comY;

        private int maxFrames;
        private int currentFrame;

        private int life;
        private int maxLife;
        private int dropLife;

        private Boolean bounce;

        public ImageObject() {
            maxFrames = 1;
            currentFrame = 0;
            bounce = false;
            life = 1;
            maxLife = 1;
            dropLife = 0;
        }

        // pgs 139-146
        public ImageObject(double xinput, double yinput, double xwidthinput, double yheightinput, double angleinput) {
            this();
            x = xinput;
            y = yinput;
            lastposx = x;
            lastposy = y;
            xwidth = xwidthinput;
            yheight = yheightinput;
            angle = angleinput;
            internalangle = 0.0;
            coords = new Vector<Double>();
        }

        public double getX() { return x; }
        public double getY() { return y; }
        public double getlastposx() { return lastposx; }
        public double getlastposy() { return lastposy; }
        public void setlastposx (double input) { lastposx = input; }
        public void setlastposy (double input) { lastposy = input; }
        public double getWidth() { return xwidth; }
        public double getHeight() { return yheight; }
        public double getAngle() { return angle; }
        public double getInternalAngle() { return internalangle; }
        public void setAngle(double angleinput) { angle = angleinput; }
        public void setInternalAngle(double internalangleinput) { internalangle = internalangleinput; }
        public Vector<Double> getCoords() { return coords; }
        public void setCoords(Vector<Double> coordsinput) {
            coords = coordsinput;
            generateTriangles();
            // printTriangles();
        }
        public int getMaxFrames() { return maxFrames; }
        public void setMaxFrames(int input) { maxFrames = input; }
        public int getCurrentFrame() { return currentFrame; }
        public void setCurrentFrame(int input) { currentFrame = input; }
        public Boolean getBounce() { return bounce; }
        public void setBounce(Boolean input) { bounce = input; }
        public int getLife() { return life; }
        public void setLife(int input) { life = input; }
        public int getMaxLife() { return maxLife; }
        public void setMaxLife(int input) { maxLife = input; }
        public int getDropLife() { return dropLife; }
        public void setDropLife(int input) { dropLife = input; }

        // pg 143
        public void updateBounce() {
            if (getBounce()) {
                moveto(getlastposx(), getlastposy());
            } else {
                setlastposx(getX());
                setlastposy(getY());
            }
            setBounce(false);
        }

        // pg 143
        public void updateCurrentFrame() {
            currentFrame = (currentFrame + 1) % maxFrames;
        }

        // pg 143
        public void generateTriangles() {
            triangles = new Vector<Double>();
            // format: (0, 1), (2, 3), (4, 5) is the (x, y) coords of a triangle

            // get center point of all coordinates
            comX = getComX();
            comY = getComY();

            for (int i = 0; i < coords.size(); i = i + 2) {
                triangles.addElement(coords.elementAt(i));
                triangles.addElement(coords.elementAt(i + 1));

                triangles.addElement(coords.elementAt((i+2) % coords.size()));
                triangles.addElement(coords.elementAt((i+3) % coords.size()));

                triangles.addElement(comX);
                triangles.addElement(comY);
            }
        }

        // pg 143-144
        public void printTriangles() {
            for (int i = 0; i < triangles.size(); i = i + 6) {
                System.out.println("p0x: " + triangles.elementAt(i) + ", p0y: " + triangles.elementAt(i+1));
                System.out.println("p1x: " + triangles.elementAt(i+2) + ", p1y: " + triangles.elementAt(i+3)
                        + triangles.elementAt(i+3));
                System.out.println("p2x: " + triangles.elementAt(i+4) + ", p2y: " + triangles.elementAt(i+5));
            }
        }

        // pg 144
        public double getComX() {
            double ret = 0;
            if (coords.size() > 0) {
                for (int i = 0; i < coords.size(); i = i + 2) {
                    ret = ret + coords.elementAt(i);
                }
                ret = ret / (coords.size() / 2.0);
            }
            return ret;
        }

        // pg 144
        public double getComY() {
            double ret = 0;
            if (coords.size() > 0) {
                for (int i = 1; i < coords.size(); i = i + 2) {
                    ret = ret + coords.elementAt(i);
                }
                ret = ret / (coords.size() / 2.0);
            }
            return ret;
        }

        // pg 144
        public void move(double xinput, double yinput) {
            x = x + xinput;
            y = y + yinput;
        }

        // pg 144-145
        public void moveto(double xinput, double yinput) {
            x = xinput;
            y = yinput;
        }

        // pg 145
        public int screenWrap(double leftEdge, double rightEdge, double topEdge, double bottomEdge) {
            int ret = 0;
            if (x > rightEdge) {
                moveto(leftEdge, getY());
                ret = 1;
            }
            if (x < leftEdge) {
                moveto(rightEdge, getY());
                ret = 2;
            }
            if (y > bottomEdge) {
                moveto(getX(), topEdge);
                ret = 3;
            }
            if (y < topEdge) {
                moveto(getX(), bottomEdge);
                ret = 4;
            }

            return ret;
        }

        // pg 145
        public void rotate(double angleinput) {
            angle = angle + angleinput;
            while (angle > twoPi) {
                angle = angle - twoPi;
            }

            while (angle < 0) {
                angle = angle + twoPi;
            }
        }

        // pg 145-146
        public void spin(double internalangleinput) {
            internalangle = internalangle + internalangleinput;
            while (internalangle > twoPi) {
                internalangle = internalangle - twoPi;
            }

            while (internalangle < 0) {
                internalangle = internalangle + twoPi;
            }
        }

    }

    private static void bindKey(JPanel myPanel, String input) {
        myPanel.getInputMap(IFW).put(KeyStroke.getKeyStroke("pressed " + input), input + " pressed");
        myPanel.getActionMap().put(input + " pressed", new KeyPressed(input));

        myPanel.getInputMap(IFW).put(KeyStroke.getKeyStroke("released " + input), input + " released");
        myPanel.getActionMap().put(input + " released", new KeyReleased(input));
    }

    public static void main(String[] args) {
        setup();
        appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        appFrame.setSize(WINWIDTH + 1, WINHEIGHT + 85);

        JPanel myPanel = new JPanel();

        JButton quitButton = new JButton("Quit");
        quitButton.addActionListener(new QuitGame());
        myPanel.add(quitButton);

        JButton startGameButton = new JButton("Start");
        startGameButton.addActionListener(new StartGame());
        myPanel.add(startGameButton);

        bindKey(myPanel, "UP");
        bindKey(myPanel, "DOWN");
        bindKey(myPanel, "LEFT");
        bindKey(myPanel, "RIGHT");

        appFrame.getContentPane().add(myPanel, "South");
        appFrame.setVisible(true);
    }

}
