import java.awt.*;
import java.nio.channels.SelectableChannel;
import java.util.Vector;
import java.util.Random;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import javax.swing.*;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class SarahMario {
    // global variables for the game
    private static Boolean endgame;

    private static BufferedImage background;

    private static Vector<Vector<Vector<ImageObject>>> walls;

    private static BufferedImage mario;
//    private static BufferedImage[] link;
//    private static BufferedImage leftHeartOutline;
//    private static BufferedImage rightHeartOutline;
//    private static BufferedImage leftHeart;
//    private static BufferedImage rightHeart;
    private static Vector<BufferedImage> goomba;
    private static Vector<ImageObject> goombas;
    private static Vector<ImageObject> bubblebossEnemies;

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
    private static double p1velocityX;
    private static double p1velocityY;

    private static double goombawidth;
    private static double goombaheight;

    private static int level;

    private static Long audiolifetime;
    private static Long lastAudioStart;
    private static Clip clip;

    private static Long dropLifeLifetime;
    private static Long lastDropLife;

    private static int XOFFSET;
    private static int YOFFSET;
    private static int WINWIDTH;
    private static int WINHEIGHT;
    private static int backgroundX;

    private static double pi;
    private static double quarterPi;
    private static double halfPi;
    private static double threequartersPi;
    private static double fivequartersPi;
    private static double threehavlesPi;
    private static double sevenquartersPi;
    private static double twoPi;

    private static JFrame appFrame;
    private static String backgroundState;

    private static Boolean availableToDropLife;

    private static final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;

    public SarahMario() {
        setup();
    }

    public static void setup() {
        // TODO: get rid of print stmt later
        System.out.println("Made it to setup");
        appFrame = new JFrame("Mario World 1");
        XOFFSET = 0;
        YOFFSET = 30;
        WINWIDTH = 338;
        WINHEIGHT = 251; //271
        backgroundX = 0;
        pi = 3.1459265358979;
        quarterPi = 0.25 * pi;
        halfPi = 0.5 * pi;
        threequartersPi = 0.75 * pi;
        fivequartersPi = 1.25 * pi;
        threehavlesPi = 1.5 * pi;
        sevenquartersPi = 1.75 * pi;
        twoPi = 2.0 * pi;
        endgame = false;
        p1width = 20; // 18.5
        p1height = 20; // 25;
        p1originalX = (double)XOFFSET + ((double) WINWIDTH / 2.0) - (p1width / 2.0);
        p1originalY = (double)YOFFSET + ((double)WINHEIGHT / 2.0) - (p1height / 2.0);
        goombawidth = 20;
        goombaheight = 20;
        level = 3;
        audiolifetime = 78000L; // 78 seconds for KI.WAV, was new Long(78000)
        dropLifeLifetime = 1000L; // 1 second

        try {
            background = ImageIO.read(new File("images\\World_1.png"));
            mario = ImageIO.read(new File("images\\Mario.png"));
            // Link's images
//            link = new BufferedImage[]{ImageIO.read(new File("images\\Orange0.png")), ImageIO.read(new File("images\\Orange1.png")),
//                    ImageIO.read(new File("images\\Orange2.png")), ImageIO.read(new File("images\\Orange3.png")),
//                    ImageIO.read(new File("images\\Orange4.png")), ImageIO.read(new File("images\\Orange5.png")),
//                    ImageIO.read(new File("images\\Orange6.png")), ImageIO.read(new File("images\\Orange7.png"))};

            // setting up the Koholint Island walls and their collisions
            walls = new Vector<Vector<Vector<ImageObject>>>(); // diff version of ImageObj than Asteroids
            for (int i = 0; i < background.getHeight(); i++) {
                Vector<Vector<ImageObject>> temp = new Vector<Vector<ImageObject>>();
                for (int j = 0; j < background.getWidth(); j++) {
                    Vector<ImageObject> tempWalls = new Vector<ImageObject>();
                    temp.addElement(tempWalls);
                }
                walls.add(temp);
            }

            for (int i = 0; i < walls.size(); i++) {
                for (int j = 0; j < walls.elementAt(i).size(); j++) {
                    if (i == 0 && j == 0) {
                        walls.elementAt(i).elementAt(j).addElement(new ImageObject(0, 241, 338, 10, 0.0));
                        //338x271 window size
//                        walls.elementAt(i).elementAt(j).addElement(new ImageObject(0, 0, 100, 400, 0.0));
//                        walls.elementAt(i).elementAt(j).addElement(new ImageObject(0, 0, 400, 75, 0.0));
//                        walls.elementAt(i).elementAt(j).addElement(new ImageObject(0, 260, 400, 100, 0.0));
//                        walls.elementAt(i).elementAt(j).addElement(new ImageObject(270, 220, 400, 400, 0.0));
//                        walls.elementAt(i).elementAt(j).addElement(new ImageObject(100, 75, 25, 100, 0.0));
//                        walls.elementAt(i).elementAt(j).addElement(new ImageObject(270, 0, 100, 180, 0.0));
//                        walls.elementAt(i).elementAt(j).addElement(new ImageObject(240, 75, 100, 100, 0.0));
//                        walls.elementAt(i).elementAt(j).addElement(new ImageObject(200, 130, 100, 50, 0.0));
//                        walls.elementAt(i).elementAt(j).addElement(new ImageObject(100, 130, 60, 50, 0.0));
                    }
                }
            }

//            player = ImageIO.read(new File("images\\Orange0.png"));

            // BluePig Enemy's images
            goombas = new Vector<ImageObject>();
            goomba = new Vector<BufferedImage>();
            goomba.addElement(ImageIO.read(new File("images/Goomba.png")));
            double goombaX = (double)XOFFSET + ((double) WINWIDTH / 2.0) - (p1width / 2.0);
            double goombaY = (double)YOFFSET + ((double)WINHEIGHT / 2.0) - (p1height / 2.0);
            ImageObject goomba1 = new ImageObject(goombaX, goombaY, goombawidth, goombaheight, 0.0);

            // BubbleBoss Enemies
//            bubblebossEnemies = new Vector<ImageObject>();

            // Health images
//            leftHeartOutline = ImageIO.read(new File("images\\Small_Heart_LeftHalf.png"));
//            rightHeartOutline = ImageIO.read(new File("images\\Small_Heart_RightHalf.png"));
//            leftHeart = ImageIO.read(new File("images\\Small_Heart_ActLeftOutline.png"));
//            rightHeart = ImageIO.read(new File("images\\Small_Heart_RightOutline.png"));

        } catch (IOException ioe) { }

    }

    private static class Animate implements Runnable {
        public void run() {
            while (endgame == false) {
                backgroundDraw();
                enemyDraw();
//                enemiesDraw();
                playerDraw();
//                healthDraw();

                try {
                    Thread.sleep(32);
                } catch (InterruptedException e) { }
            }
        }
    }

    private static void enemyDraw() {
//        System.out.println("Drew enemies");
        Graphics g = appFrame.getGraphics();
        Graphics2D g2D = (Graphics2D) g;

        for (int i = 0; i < goombas.size(); i++) {
            goombas.elementAt(i).setMaxFrames(10);
            g2D.drawImage(goomba.elementAt(0), 180, YOFFSET, null);
        }


    }

    private static class AudioLooper implements Runnable {
        public void run() {
            while (endgame == false) {
                Long currTime = Long.valueOf(System.currentTimeMillis()); // was new Long(System.currentTimeMillis()
                if (currTime - lastAudioStart > audiolifetime) {
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
//            AudioInputStream ais = AudioSystem.getAudioInputStream(new File("audio\\TitleKI.wav").getAbsoluteFile());
            AudioInputStream ais = AudioSystem.getAudioInputStream(new File("audio/Super Mario NES Bkgd Music.wav"));
            clip = AudioSystem.getClip();
            clip.open(ais);
            clip.start();
            lastAudioStart = System.currentTimeMillis();
            audiolifetime = Long.valueOf(78000); // was new Long(78000)
        } catch (Exception e) {
            // NOP
        }
    }

    private static class PlayerMover implements Runnable {
        private double velocitystepX;
        private double velocitystepY;

        public PlayerMover() {
            velocitystepX = 2;
            velocitystepY = 5;
        }

        public void run() {
            while (endgame == false) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) { }

                if (upPressed || downPressed || leftPressed || rightPressed) {
                    p1velocityY = 12;
                    //                    p1velocityY = velocitystepY;
                    p1velocityX = velocitystepX;
                    if (p1velocityY > 12) {
                        p1velocityY = 12;
                    }
                    if (p1velocityX > 5) {
                        p1velocityX = 5;
                    }
                    if (upPressed) {
                        if (leftPressed) {
                            p1.setInternalAngle(fivequartersPi);
                        } else if (rightPressed) {
                            p1.setInternalAngle(5.49779);
                        } else {
                            p1.setInternalAngle(threehavlesPi);
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
                    p1velocityX = 0.0;
                    p1velocityY = 0.0;
                    p1.setInternalAngle(threehavlesPi);
                }

                p1.updateBounce();
                // the 0.25 is the gravity
                // if statement is to stop player from getting pinned to the ground
               if (p1.getX() > 250 && rightPressed) {
                   p1velocityX = 0;
               } else if (p1.getX() < 88 && leftPressed) {
                    p1velocityX = 0;
               }
                if (p1.getY() < 220) {
                    p1.move(p1velocityX * Math.cos(p1.getInternalAngle()), p1velocityY * Math.sin(p1.getInternalAngle()) + 0.75);
                } else {
                    p1.move(p1velocityX * Math.cos(p1.getInternalAngle()), p1velocityY * Math.sin(p1.getInternalAngle()));
                }
//                int wrap = p1.screenWrap(XOFFSET, XOFFSET + WINWIDTH, YOFFSET, YOFFSET + WINHEIGHT);
////                backgroundState = bgWrap(backgroundState, wrap);
//                if (wrap != 0) {
////                    clearEnemies();
//                    generateEnemies(backgroundState);
//                }
            }
        }
    }

    private static void clearEnemies() {
        goombas.clear();
        bubblebossEnemies.clear();
    }

    private static void generateEnemies(String backgroundState) {
        if (backgroundState.substring(0, 6).equals("KI0809")) {
            goombas.addElement(new ImageObject(20, 90, 33, 33, 0.0));
            goombas.addElement(new ImageObject(250, 230, 33, 33, 0.0));
        }

        for (int i = 0; i < goombas.size(); i++) {
            goombas.elementAt(i).setMaxFrames(25);
        }
    }

    private static class EnemyMover implements Runnable {
        private double goombavelocitystep;
        private double goombavelocity;

        public EnemyMover() {
            goombavelocitystep = 2;
        }

        public void run() {
            Random randomNumbers = new Random(LocalTime.now().getNano());
            while (endgame == false) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    // NOP
                }

                // TODO
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
                            goombas.elementAt(i).setInternalAngle(threehavlesPi);
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

//                    for (int i = 0; i < bubblebossEnemies.size(); i++) {
//
//                    }
                } catch (java.lang.NullPointerException jlnpe) {
                    // NOP
                }
            }
        }
    }

//    private static class HealthTracker implements Runnable {
//        public void run() {
//            while (endgame == false) {
//                Long currTime = Long.valueOf(System.currentTimeMillis()); // was new Long(System.currentTimeMillis())
//                if (availableToDropLife && p1.getDropLife() > 0) {
//                    int newLife = p1.getLife() - p1.getDropLife();
//                    p1.setDropLife(0);
//                    availableToDropLife = false;
//
//                    lastDropLife = System.currentTimeMillis();
//                    p1.setLife(newLife);
//
//                    try {
//                        AudioInputStream ais = AudioSystem.getAudioInputStream(new File("hurt.wav").getAbsoluteFile());
//                        Clip hurtclip = AudioSystem.getClip();
//                        hurtclip.open(ais);
//                        hurtclip.start();
//                    } catch (Exception e) { }
//                } else {
//                    if (currTime - lastDropLife > dropLifeLifetime) {
//                        availableToDropLife = true;
//                    }
//                }
//            }
//        }
//    }

    public static class Gravity implements Runnable {
        public void run() {
            while (endgame == false) {
                // gravity is set in player mover right now
//                p1.move(p1.getX(), p1.getY() + 0.5);
//                System.out.println("X: " + p1.getX() + " Y: " + p1.getY());
//                p1.move(Math.cos(p1.getInternalAngle()) - 0.1, Math.sin(p1.getInternalAngle()) - 0.1);
            }
        }
    }

    private static class CollisionChecker implements Runnable {
        public void run() {
            // Random randomNumbers = new Random(LocalTime.now().getNano());
            while (endgame == false) {

                // check player and enemies against walls
                checkMoversAgainstWalls(walls.elementAt(0).elementAt(0));
//                if (backgroundState.substring(0, 6).equals("KI0000")) {
//                    checkMoversAgainstWalls(walls.elementAt(0).elementAt(0));
//                }

                // check player against enemies
                for (int i = 0; i < goombas.size(); i++) {
                    if (GameLevel.collisionOccurs(p1, goombas.elementAt(i))) {
                        // System.out.println("Still Colliding: " + i + ", " + System.currentTimeMllis());
                        p1.setBounce(true);
                        goombas.elementAt(i).setBounce(true);
                        if (availableToDropLife) {
                            p1.setDropLife(1);
                        }
                    }
                }

                // TODO: check enemies against walls
                // TODO: check player against deep water or pits
                // TODO: check player against enemy arrows
                // TODO: check enemies against player weapons
            }
        }

        // pg 126
        private static void checkMoversAgainstWalls(Vector<ImageObject> wallsInput) {
            for (int i = 0; i < wallsInput.size(); i++) {
                if (GameLevel.collisionOccurs(p1, wallsInput.elementAt(i))) {
                    p1.setBounce(true);
                }
                for (int j = 0; j < goombas.size(); j++) {
                    if (GameLevel.collisionOccurs(goombas.elementAt(j), wallsInput.elementAt(i))) {
                        goombas.elementAt(j).setBounce(true);
                    }
                }
            }
        }
    }

    // dist is a distance between the two objects at the bottom of objInner.
    private static void lockrotateObjAroundObjbottom(ImageObject objOuter, ImageObject objInner, double dist) {
        objOuter.moveto(objInner.getX() + (dist + objInner.getWidth() / 2.0) * Math.cos(-objInner.getAngle() + pi/2.0)
                        + objOuter.getWidth() / 2.0,
                objInner.getY() + (dist + objInner.getHeight() / 2.0) * Math.sin(-objInner.getAngle() + pi/2.0) +
                        objOuter.getHeight() / 2.0);
        objOuter.setAngle(objInner.getAngle());
    }

    // dist is a distance between the two objectys at the top of the inner object
    private static void lockrotateObjAroundObjtop(ImageObject objOuter, ImageObject objInner, double dist) {
        objOuter.moveto(objInner.getX() + objOuter.getWidth() + (objInner.getWidth() / 2.0 + (dist +
                        objInner.getWidth() / 2.0) * Math.cos(objInner.getAngle() + pi/2.0)) / 2.0,
                objInner.getY() - objOuter.getHeight() + (dist + objInner.getHeight() / 2.0) *
                        Math.sin(objInner.getAngle() / 2.0));
        objOuter.setAngle(objInner.getAngle());
    }

    private static AffineTransformOp rotateImageObject(ImageObject obj) {
        AffineTransform at = AffineTransform.getRotateInstance(-obj.getInternalAngle(),
                obj.getWidth() / 2.0, obj.getHeight() / 2.0);
        AffineTransformOp atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        return atop;
    }

    private static AffineTransformOp spinImageObject(ImageObject obj) {
        AffineTransform at = AffineTransform.getRotateInstance(-obj.getInternalAngle(), obj.getWidth() / 2.0,
                obj.getHeight() / 2.0);
        AffineTransformOp atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        return atop;
    }

    private static void backgroundDraw() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2D = (Graphics2D) g;
        g2D.drawImage(background, XOFFSET, YOFFSET, null);
        System.out.println(XOFFSET);
        // scrolling mechanism
        if (p1.getX() > 250 && rightPressed) {
            XOFFSET -= 10;
//            g2D.drawImage(background, XOFFSET + 100, YOFFSET, null);
        } else if (p1.getX() < 88 && leftPressed) {
            XOFFSET += 10;
        }

    }

    private static void playerDraw() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2D = (Graphics2D) g;
        p1.setMaxFrames(10);

        if (upPressed || downPressed || leftPressed || rightPressed) {
            if (upPressed == true) {
                if (p1.getCurrentFrame() < 5) {
//                    g2D.drawImage(rotateImageObject(p1).filter(link[0], null),
//                    g2D.drawImage(link[0],
//                            (int) (p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
                    g2D.drawImage(mario, (int) (p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
                } else if (p1.getCurrentFrame() > 5) {
//                    g2D.drawImage(rotateImageObject(p1).filter(link[1], null),
//                    g2D.drawImage(link[1],
//                            (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
                    g2D.drawImage(mario,
                            (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
                }
                p1.updateCurrentFrame();
            }
            if (downPressed == true) {
                if (p1.getCurrentFrame() < 5) {
//                    g2D.drawImage(rotateImageObject(p1).filter(link[2], null),
//                    g2D.drawImage(link[2],
//                            (int)(p1.getX() + 0.5), (int) (p1.getY() + 0.5), null);
                    g2D.drawImage(mario,
                            (int)(p1.getX() + 0.5), (int) (p1.getY() + 0.5), null);
                } else if (p1.getCurrentFrame() > 5) {
//                    g2D.drawImage(rotateImageObject(p1).filter(link[3], null),
//                    g2D.drawImage(link[3],
//                            (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
                    g2D.drawImage(mario,
                            (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
                }
                p1.updateCurrentFrame();
            }
            if (leftPressed == true) {
                if (p1.getCurrentFrame() < 5) {

//                    g2D.drawImage(rotateImageObject(p1).filter(link[4], null),
//                    g2D.drawImage(link[4],
//                            (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
                    g2D.drawImage(mario,
                            (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
                } else if (p1.getCurrentFrame() > 5) {
//                    g2D.drawImage(rotateImageObject(p1).filter(link[5], null),
//                    g2D.drawImage(link[5],
//                            (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
                    g2D.drawImage(mario,
                            (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
                }
                p1.updateCurrentFrame();
            }
            if (rightPressed == true) {
                if (p1.getCurrentFrame() < 5) {
//                    g2D.drawImage(rotateImageObject(p1).filter(link[6], null),
//                    g2D.drawImage(link[6],
//                            (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
                    g2D.drawImage(mario,
                            (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
                } else if (p1.getCurrentFrame() > 5) {
//                    g2D.drawImage(rotateImageObject(p1).filter(link[7], null),
//                    g2D.drawImage(link[7],
//                            (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
                    g2D.drawImage(mario,
                            (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
                }
                p1.updateCurrentFrame();
            }
        } else {
            if (Math.abs(lastPressed - 90.0) < 1.0) {
//                g2D.drawImage(rotateImageObject(p1).filter(link[0], null),
//                g2D.drawImage(link[0],
//                        (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
                g2D.drawImage(mario,
                        (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
            }
            if (Math.abs(lastPressed - 270.0) < 1.0) {
//                g2D.drawImage(rotateImageObject(p1).filter(link[2], null),
//                g2D.drawImage(link[2],
//                        (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
                g2D.drawImage(mario,
                        (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
            }
            if (Math.abs(lastPressed - 0.0) < 1.0) {
//                g2D.drawImage(rotateImageObject(p1).filter(link[6], null),
//                g2D.drawImage(link[6],
//                        (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
                g2D.drawImage(mario,
                        (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
            }
            if (Math.abs(lastPressed - 180.0) < 1.0) {
//                g2D.drawImage(rotateImageObject(p1).filter(link[4], null),
//                g2D.drawImage(link[4],
//                        (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
                g2D.drawImage(mario,
                        (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
            }
        }

        // g2D.drawImage(rotateImageObject(p1).filter(player, null), (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
    }

//    private static void healthDraw() {
//        Graphics g = appFrame.getGraphics();
//        Graphics2D g2D = (Graphics2D) g;
//
//        int leftscale = 10;
//        int leftoffset = 10;
//        int rightoffset = 9;
//        int interioroffset = 2;
//        int halfinterioroffset = 1;
//        for (int i = 0; i < p1.getMaxLife(); i++) {
//            if (i % 2 == 0) {
//                g2D.drawImage(leftHeartOutline, leftscale * i + leftoffset + XOFFSET, YOFFSET, null);
////                g2D.drawImage(rotateImageObject(p1).filter(leftHeartOutline, null),
////                        leftscale * i + leftoffset + XOFFSET, YOFFSET, null);
//            } else {
//                g2D.drawImage(rightHeartOutline, leftscale * i + rightoffset + XOFFSET, YOFFSET, null);
////                g2D.drawImage(rotateImageObject(p1).filter(rightHeartOutline, null),
////                        leftscale * i + rightoffset + XOFFSET, YOFFSET, null);
//            }
//        }
//
//        for (int i = 0; i < p1.getLife(); i++) {
//            if (i % 2 == 0) {
//                g2D.drawImage(leftHeart, leftscale * i + leftoffset + interioroffset + XOFFSET,
//                        interioroffset + YOFFSET, null);
////                g2D.drawImage(rotateImageObject(p1).filter(leftHeart, null),
////                        leftscale * i + leftoffset + interioroffset + XOFFSET, interioroffset + YOFFSET, null);
//            } else {
//                g2D.drawImage(rightHeart, leftscale * i + leftoffset - halfinterioroffset + XOFFSET,
//                        interioroffset + YOFFSET, null);
////                g2D.drawImage(rotateImageObject(p1).filter(rightHeart, null),
////                        leftscale * i + leftoffset - halfinterioroffset + XOFFSET, interioroffset + YOFFSET, null);
//            }
//        }
//    }

    private static void enemiesDraw() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2D = (Graphics2D) g;

        for (int i = 0; i < goombas.size(); i++) {
            if (Math.abs(goombas.elementAt(i).getInternalAngle() - 0.0) < 1.0) {
                if (goombas.elementAt(i).getCurrentFrame() < goombas.elementAt(i).getMaxFrames() / 2) {
                    g2D.drawImage(rotateImageObject(goombas.elementAt(i)).filter(goomba.elementAt(6), null),
                            (int)(goombas.elementAt(i).getX() + 0.5),
                            (int)(goombas.elementAt(i).getY() + 0.5), null);
                } else {
                    g2D.drawImage(rotateImageObject(goombas.elementAt(i)).filter(goomba.elementAt(7), null),
                            (int)(goombas.elementAt(i).getX() + 0.5),
                            (int)(goombas.elementAt(i).getY() + 0.5), null);
                }
                goombas.elementAt(i).updateCurrentFrame();;
            }
            if(Math.abs(goombas.elementAt(i).getInternalAngle() - pi) < 1.0) {
                if (goombas.elementAt(i).getCurrentFrame() < goombas.elementAt(i).getMaxFrames() / 2) {
                    g2D.drawImage(rotateImageObject(goombas.elementAt(i)).filter(goomba.elementAt(4), null),
                            (int)(goombas.elementAt(i).getX() + 0.5),
                            (int)(goombas.elementAt(i).getY() + 0.5), null);
                } else {
                    g2D.drawImage(rotateImageObject(goombas.elementAt(i)).filter(goomba.elementAt(5), null),
                            (int)(goombas.elementAt(i).getX() + 0.5),
                            (int)(goombas.elementAt(i).getY() + 0.5), null);
                }
                goombas.elementAt(i).updateCurrentFrame();
            }
            if (Math.abs(goombas.elementAt(i).getInternalAngle() - halfPi) < 1.0) {
                if (goombas.elementAt(i).getCurrentFrame() < goombas.elementAt(i).getMaxFrames() / 2) {
                    g2D.drawImage(rotateImageObject(goombas.elementAt(i)).filter(goomba.elementAt(2), null),
                            (int)(goombas.elementAt(i).getX() + 0.5),
                            (int)(goombas.elementAt(i).getY() + 0.5), null);
                } else {
                    g2D.drawImage(rotateImageObject(goombas.elementAt(i)).filter(goomba.elementAt(3), null),
                            (int)(goombas.elementAt(i).getX() + 0.5),
                            (int)(goombas.elementAt(i).getY() + 0.5), null);
                }
                goombas.elementAt(i).updateCurrentFrame();
            }
            if (Math.abs(goombas.elementAt(i).getInternalAngle() - threehavlesPi) < 1.0) {
                if (goombas.elementAt(i).getCurrentFrame() < goombas.elementAt(i).getMaxFrames() / 2) {
                    g2D.drawImage(rotateImageObject(goombas.elementAt(i)).filter(goomba.elementAt(0), null),
                            (int)(goombas.elementAt(i).getX() + 0.5),
                            (int)(goombas.elementAt(i).getY() + 0.5), null);
                } else {
                    g2D.drawImage(rotateImageObject(goombas.elementAt(i)).filter(goomba.elementAt(1), null),
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

                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                upPressed = false;

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
        public void actionPerformed(ActionEvent e) {
            endgame = true;
        }
    }

    private static class StartGame implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            endgame = true;
            upPressed = false;
            downPressed = false;
            leftPressed = false;
            rightPressed = false;
            lastPressed = 90.0;
            backgroundState = "KI0000";
            availableToDropLife = true;

            try {
                clearEnemies();
                generateEnemies(backgroundState);
            } catch (java.lang.NullPointerException jlnpe) { }

            p1 = new ImageObject(p1originalX, p1originalY, p1width, p1height, 0.0);
            p1velocityX = 0.0;
            p1velocityY = 0.0;
            p1.setInternalAngle(threehavlesPi); // 270 degrees, in radians
            p1.setMaxFrames(2);
            p1.setlastposx(p1originalX);
            p1.setlastposy(p1originalY);
            p1.setLife(6);
            p1.setMaxLife(6);

            try {
                Thread.sleep(50);
            } catch (InterruptedException ie) { }

            lastAudioStart = System.currentTimeMillis();
            playAudio(backgroundState);
            endgame = false;
            lastDropLife = System.currentTimeMillis();
            Thread t1 = new Thread(new Animate());
            Thread t2 = new Thread(new PlayerMover());
            Thread t3 = new Thread(new CollisionChecker());
            Thread t4 = new Thread(new AudioLooper());
            Thread t5 = new Thread(new EnemyMover());
//            Thread t6 = new Thread(new HealthTracker());
            Thread t7 = new Thread(new Gravity());
            t1.start();
            t2.start();
            t3.start();
            t4.start();
            t5.start();
//            t6.start();
            t7.start();
        }
    }

    private static class GameLevel implements ActionListener {
        public int decodeLevel(String input) {
            int ret = 3;
            if (input.equals("One")) {
                ret = 1;
            } else if (input.equals("Two")) {
                ret = 2;
            } else if (input.equals("Three")) {
                ret = 3;
            } else if (input.equals("Four")) {
                ret = 4;
            } else if (input.equals("Five")) {
                ret = 5;
            } else if (input.equals("Six")) {
                ret = 6;
            } else if (input.equals("Seven")) {
                ret = 7;
            } else if (input.equals("Eight")) {
                ret = 8;
            } else if (input.equals("Nine")) {
                ret = 9;
            } else if (input.equals("Ten")) {
                ret = 10;
            }
            return ret;
        }

        public void actionPerformed(ActionEvent e) {
            JComboBox cb = (JComboBox) e.getSource();
            String textLevel = (String) cb.getSelectedItem();
            level = decodeLevel(textLevel);
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

        /**
         String[] levels = { "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten" };
         JComboBox<String> levelMenu = new JComboBox<String>(levels);
         levelMenu.setSelectedIndex(2);
         levelMenu.addActionListener(new GameLevel());
         myPanel.add(levelMenu);
         */

        JButton quitButton = new JButton("Select");
        quitButton.addActionListener(new QuitGame());
        myPanel.add(quitButton);

        JButton newGameButton = new JButton("Start");
        newGameButton.addActionListener(new StartGame());
        myPanel.add(newGameButton);

        bindKey(myPanel, "UP");
        bindKey(myPanel, "DOWN");
        bindKey(myPanel, "LEFT");
        bindKey(myPanel, "RIGHT");

        appFrame.getContentPane().add(myPanel, "South");
        appFrame.setVisible(true);
    }
}