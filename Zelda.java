// Zelda.java Copyright (C) 2020 Ben Sanders
//import java.lang.invoke.DelegatingMethodHandle$Holder;
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

import java.awt.Graphics;
import java.awt.Graphics2D;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;


public class Zelda {
    // global variables for the game
    private static Boolean endgame;

    private static BufferedImage[][] backgroundKI;
    private static BufferedImage[][] backgroundTC;
//    private static Vector<Vector<BufferedImage>> backgroundTC;

    private static Vector<Vector<Vector<ImageObject>>> wallsKI;
    private static Vector<Vector<Vector<ImageObject>>> wallsTC;

    private static int xdimKI;
    private static int ydimKI;
    private static int xdimTC;
    private static int ydimTC;

    private static BufferedImage player;
//    private static Vector<BufferedImage> link;
    private static BufferedImage[] link;
    private static BufferedImage leftHeartOutline;
    private static BufferedImage rightHeartOutline;
    private static BufferedImage leftHeart;
    private static BufferedImage rightHeart;
    private static Vector<BufferedImage> bluepigEnemy;
    private static Vector<ImageObject> bluepigEnemies;
    private static Vector<ImageObject> bubblebossEnemies;

    private static ImageObject doorKItoTC;
    private static ImageObject doorTCtoKI;

    private static Boolean upPressed;
    private static Boolean downPressed;
    private static Boolean leftPressed;
    private static Boolean rightPressed;
    private static Boolean aPressed;
    private static Boolean xPressed;
    private static double lastPressed;

    private static ImageObject p1;
    private static double p1width;
    private static double p1height;
    private static double p1originalX;
    private static double p1originalY;
    private static double p1velocity;

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

    public Zelda() {
        setup();
    }

    // pgs 109-115
    public static void setup() {
        // TODO: get rid of print stmt later
        System.out.println("Made it to setup");
        appFrame = new JFrame("The Legend of Zelda: Link's Awakening");
        XOFFSET = 0;
        YOFFSET = 30;
        WINWIDTH = 338;
        WINHEIGHT = 271;
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
        level = 3;
        audiolifetime = 78000L; // 78 seconds for KI.WAV, was new Long(78000)
        dropLifeLifetime = 1000L; // 1 second

        try {
            // setting up the Koholint Island images
            xdimKI = 3;
            ydimKI = 2;
            // this 2D vector keeps all the diff images of KI
            backgroundKI = new BufferedImage[xdimKI][ydimKI];
            backgroundKI[0][0] = ImageIO.read(new File("images\\KI0000.png"));
            backgroundKI[1][0] = ImageIO.read(new File("images\\KI0100.png"));
            backgroundKI[2][0] = ImageIO.read(new File("images\\KI0200.png"));
            backgroundKI[0][1] = ImageIO.read(new File("images\\KI0001.png"));
            backgroundKI[1][1] = ImageIO.read(new File("images\\KI0101.png"));

            // Link's images
            link = new BufferedImage[]{ImageIO.read(new File("images\\Orange0.png")), ImageIO.read(new File("images\\Orange1.png")),
                    ImageIO.read(new File("images\\Orange2.png")), ImageIO.read(new File("images\\Orange3.png")),
                    ImageIO.read(new File("images\\Orange4.png")), ImageIO.read(new File("images\\Orange5.png")),
                    ImageIO.read(new File("images\\Orange6.png")), ImageIO.read(new File("images\\Orange7.png"))};

            // setting up the Koholint Island walls and their collisions
            wallsKI = new Vector<Vector<Vector<ImageObject>>>(); // diff version of ImageObj than Asteroids
            for (int i = 0; i < ydimKI; i++) {
                Vector<Vector<ImageObject>> temp = new Vector<Vector<ImageObject>>();
                for (int j = 0; j < xdimKI; j++) {
                    Vector<ImageObject> tempWalls = new Vector<ImageObject>();
                    temp.addElement(tempWalls);
                }
                wallsKI.add(temp);
            }

            for (int i = 0; i < wallsKI.size(); i++) {
                for (int j = 0; j < wallsKI.elementAt(i).size(); j++) {
                    if (i == 0 && j == 0) {
                        //338x271 window size
                        wallsKI.elementAt(i).elementAt(j).addElement(new ImageObject(0, 0, 100, 400, 0.0));
                        wallsKI.elementAt(i).elementAt(j).addElement(new ImageObject(0, 0, 400, 75, 0.0));
                        wallsKI.elementAt(i).elementAt(j).addElement(new ImageObject(0, 260, 400, 100, 0.0));
                        wallsKI.elementAt(i).elementAt(j).addElement(new ImageObject(270, 220, 400, 400, 0.0));
                        wallsKI.elementAt(i).elementAt(j).addElement(new ImageObject(100, 75, 25, 100, 0.0));
                        wallsKI.elementAt(i).elementAt(j).addElement(new ImageObject(270, 0, 100, 180, 0.0));
                        wallsKI.elementAt(i).elementAt(j).addElement(new ImageObject(240, 75, 100, 100, 0.0));
                        wallsKI.elementAt(i).elementAt(j).addElement(new ImageObject(200, 130, 100, 50, 0.0));
                        wallsKI.elementAt(i).elementAt(j).addElement(new ImageObject(100, 130, 60, 50, 0.0));
                    }
                }
            }

            // setting up the Tail Cave images
            xdimTC = 3;
            ydimTC = 2;
            backgroundTC = new BufferedImage[xdimTC][ydimTC];
            backgroundTC[0][0] = ImageIO.read(new File("images\\TC0101.png"));
            backgroundTC[1][0] = ImageIO.read(new File("images\\TC0102.png"));
            backgroundTC[2][0] = ImageIO.read(new File("images\\TC0103.png"));
            backgroundTC[0][1] = ImageIO.read(new File("images\\TC0203.png"));
            backgroundTC[1][1] = ImageIO.read(new File("images\\TC0303.png"));

            // TODO: get rid of later
            System.out.println("initialized TC background images");
//            xdimTC = 9; // 7; // TODO: need to be able to just use 7 and 6, not 9 and 8
//            ydimTC = 8; // 6;
//            backgroundTC = new Vector<Vector<BufferedImage>>();
//
//            for (int i = 0; i < ydimTC; i++) {
//                Vector<BufferedImage> temp = new Vector<BufferedImage>();
//                for (int j = 0; j < xdimTC; j++) {
//                    BufferedImage tempImg = ImageIO.read(new File("blank.png"));
//                    temp.addElement(tempImg);
//                }
//                backgroundTC.addElement(temp);
//            }

//            for (int i = 0; i < backgroundTC.size(); i++) {
//                for (int j = 0; j < backgroundTC.elementAt(i).size(); j++) {
//                    if ( (j == 0 && i == 2) || (j == 0 && i == 3) || (j == 0 && i == 4) || (j == 1 && i == 1) ||
//                            (j == 1 && i == 3) || (j == 1 && i == 5) || (j == 2 && i == 1) || (j == 2 && i == 2) ||
//                            (j == 2 && i == 3) || (j == 2 && i == 4) || (j == 2 && i == 5) || (j == 2 && i == 6) ||
//                            (j == 3 && i == 1) || (j == 3 && i == 2) || (j == 3 && i == 3) || (j == 3 && i == 4) ||
//                            (j == 3 && i == 5) || (j == 4 && i == 2) || (j == 4 && i == 3) || (j == 4 && i == 4) ||
//                            (j == 5 && i == 2) || (j == 5 && i == 3) || (j == 6 && i == 0) || (j == 6 && i == 1) ||
//                            (j == 6 && i == 2) || (j == 6 && i == 3)) {
//                        String filename = "images\\TC";
//                        if (j < 10) {
//                            filename = filename + "0";
//                        }
//                        filename = filename + j;
//                        if (i < 10) {
//                            filename = filename + "0";
//                        }
//                        filename = filename + i + ".png";
////                        System.out.println(filename);
//                        backgroundTC.elementAt(i).set(j, ImageIO.read(new File(filename)));
//                    }
//                }
//            }
//
            // setting up the Tail Cave walls
            wallsTC = new Vector<Vector<Vector<ImageObject>>>();
            for (int i = 0; i < ydimTC; i++) {
                Vector<Vector<ImageObject>> temp = new Vector<Vector<ImageObject>>();
                for (int j = 0; j < xdimTC; j++) {
                    Vector<ImageObject> tempWalls = new Vector<ImageObject>();
                    temp.addElement(tempWalls);
                }
                wallsTC.add(temp);
            }

            for (int i = 0; i < wallsTC.size(); i++) {
                for (int j = 0; j < wallsTC.elementAt(i).size(); j++) {
                    if (i == 0 && j == 0) {
                        //338x271 window size
                        wallsTC.elementAt(i).elementAt(j).addElement(new ImageObject(0, 0, 100, 400, 0.0));
                        wallsTC.elementAt(i).elementAt(j).addElement(new ImageObject(0, 0, 400, 75, 0.0));
                        wallsTC.elementAt(i).elementAt(j).addElement(new ImageObject(0, 260, 400, 100, 0.0));
                        wallsTC.elementAt(i).elementAt(j).addElement(new ImageObject(270, 220, 400, 400, 0.0));
                        wallsTC.elementAt(i).elementAt(j).addElement(new ImageObject(100, 75, 25, 100, 0.0));
                        wallsTC.elementAt(i).elementAt(j).addElement(new ImageObject(270, 0, 100, 180, 0.0));
                        wallsTC.elementAt(i).elementAt(j).addElement(new ImageObject(240, 75, 100, 100, 0.0));
                        wallsTC.elementAt(i).elementAt(j).addElement(new ImageObject(200, 130, 100, 50, 0.0));
                        wallsTC.elementAt(i).elementAt(j).addElement(new ImageObject(100, 130, 60, 50, 0.0));
                    }
                }
            }

            player = ImageIO.read(new File("images\\Orange0.png"));

            // BluePig Enemy's images
            bluepigEnemies = new Vector<ImageObject>();
            bluepigEnemy = new Vector<BufferedImage>();
            bluepigEnemy.addElement(ImageIO.read(new File("images\\Pig_Up_1.png")));
            bluepigEnemy.addElement(ImageIO.read(new File("images\\Pig_Up_1.png")));
            bluepigEnemy.addElement(ImageIO.read(new File("images\\Pig_Down_1.png")));
            bluepigEnemy.addElement(ImageIO.read(new File("images\\Pig_Down_2.png")));
            bluepigEnemy.addElement(ImageIO.read(new File("images\\Pig_Left_1.png")));
            bluepigEnemy.addElement(ImageIO.read(new File("images\\Pig_Left_2.png")));
            bluepigEnemy.addElement(ImageIO.read(new File("images\\Pig_Right_1.png")));
            bluepigEnemy.addElement(ImageIO.read(new File("images\\Pig_Right_2.png")));

            // BubbleBoss Enemies
            bubblebossEnemies = new Vector<ImageObject>();

            // Health images
//             leftHeartOutline = ImageIO.read(new File("images\\Heart_Outline_Left.png"));
//             rightHeartOutline = ImageIO.read(new File("images\\Heart_Outline_Right.png"));
//             leftHeart = ImageIO.read(new File("images\\Heart_Left.png"));
//             rightHeart = ImageIO.read(new File("images\\Heart_Right.png"));
            leftHeartOutline = ImageIO.read(new File("images\\Small_Heart_LeftHalf.png"));
            rightHeartOutline = ImageIO.read(new File("images\\Small_Heart_RightHalf.png"));
            leftHeart = ImageIO.read(new File("images\\Small_Heart_ActLeftOutline.png"));
            rightHeart = ImageIO.read(new File("images\\Small_Heart_RightOutline.png"));

        } catch (IOException ioe) { }

    }

    // pg 115
    private static class Animate implements Runnable {
        public void run() {
            while (endgame == false) {
                backgroundDraw();
//                enemiesDraw();
                playerDraw();
                healthDraw();

                try {
                    Thread.sleep(32);
                } catch (InterruptedException e) { }
            }
        }
    }

    // pgs 115-116
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

    // pgs 116-117
    private static void playAudio(String backgroundState) {
        try {
            clip.stop();
        } catch (Exception e) {
            // NOP
        }

        try {
            if (backgroundState.substring(0, 2).equals("KI")) {
                AudioInputStream ais = AudioSystem.getAudioInputStream(new File("audio\\TitleKI.wav").getAbsoluteFile());
                clip = AudioSystem.getClip();
                clip.open(ais);
                clip.start();
                lastAudioStart = System.currentTimeMillis();
                audiolifetime = Long.valueOf(78000); // was new Long(78000)
            } else if (backgroundState.substring(0, 2).equals("TC")) {
                AudioInputStream ais = AudioSystem.getAudioInputStream(new File("audio\\TC.wav").getAbsoluteFile());
                clip = AudioSystem.getClip();
                clip.open(ais);
                clip.start();
                lastAudioStart = System.currentTimeMillis();
                audiolifetime = Long.valueOf(219000); // was new Long(191000)
            }
        } catch (Exception e) {
            // NOP
        }
    }

    // pgs 117-119
    private static String bgWrap(String input, int wrap) {
        String ret = input;
        if (wrap == 0) {
            // NOP
        } else if (wrap == 1) { // right
            // these change the background for each "map"
            int xcoord = Integer.parseInt(input.substring(2, 4));
            int ycoord = Integer.parseInt(input.substring(4, 6));

            xcoord = xcoord + 1;

            if (xcoord < 10) {
                ret = input.substring(0, 2) + "0" + xcoord;
            } else {
                ret = input.substring(0, 2) + xcoord;
            }

            if (ycoord < 10) {
                ret = ret + "0" + ycoord;
            } else {
                ret = ret + ycoord;
            }
        } else if (wrap == 2) { // left
            int xcoord = Integer.parseInt(input.substring(2, 4));
            int ycoord = Integer.parseInt(input.substring(4, 6));

            xcoord = xcoord - 1;

            if (xcoord < 10) {
                ret = input.substring(0, 2) + "0" + xcoord;
            } else {
                ret = input.substring(0, 2) + xcoord;
            }

            if (ycoord < 10) {
                ret = ret + "0" + ycoord;
            } else {
                ret = ret + ycoord;
            }
        } else if (wrap == 3) { // down
            int xcoord = Integer.parseInt(input.substring(2, 4));
            int ycoord = Integer.parseInt(input.substring(4, 6));

            ycoord = ycoord + 1;

            if (xcoord < 10) {
                ret = input.substring(0, 2) + "0" + xcoord;
            } else {
                ret = input.substring(0, 2) + xcoord;
            }

            if (ycoord < 10) {
                ret = ret + "0" + ycoord;
            } else {
                ret = ret + ycoord;
            }
        } else if (wrap == 4) { // up
            int xcoord = Integer.parseInt(input.substring(2, 4));
            int ycoord = Integer.parseInt(input.substring(4, 6));

            ycoord = ycoord - 1;

            if (xcoord < 10) {
                ret = input.substring(0, 2) + "0" + xcoord;
            } else {
                ret = input.substring(0, 2) + xcoord;
            }

            if (ycoord < 10) {
                ret = ret + "0" + ycoord;
            } else {
                ret = ret + ycoord;
            }
        }
        return ret;
    }

    // pgs 119-
    private static class PlayerMover implements Runnable {
        private double velocitystep;

        public PlayerMover() {
            velocitystep = 1;
        }

        public void run() {
            while (endgame == false) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) { }

                if (upPressed || downPressed || leftPressed || rightPressed) {
                    p1velocity = velocitystep;
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
                    p1velocity = 0.0;
                    p1.setInternalAngle(threehavlesPi);
                }

                p1.updateBounce();
                p1.move(p1velocity * Math.cos(p1.getInternalAngle()), p1velocity * Math.sin(p1.getInternalAngle()));
                int wrap = p1.screenWrap(XOFFSET, XOFFSET + WINWIDTH, YOFFSET, YOFFSET + WINHEIGHT);
                backgroundState = bgWrap(backgroundState, wrap);
                if (wrap != 0) {
//                    clearEnemies();
                    generateEnemies(backgroundState);
                }
            }
        }

    }

    // pg 121
    private static void clearEnemies() {
        bluepigEnemies.clear();
        bubblebossEnemies.clear();
    }

    // pgs 121-122
    private static void generateEnemies(String backgroundState) {
        if (backgroundState.substring(0, 6).equals("KI0809")) {
            bluepigEnemies.addElement(new ImageObject(20, 90, 33, 33, 0.0));
            bluepigEnemies.addElement(new ImageObject(250, 230, 33, 33, 0.0));
        }

        for (int i = 0; i < bluepigEnemies.size(); i++) {
            bluepigEnemies.elementAt(i).setMaxFrames(25);
        }
    }

    // pgs 122-123
    private static class EnemyMover implements Runnable {
        private double bluepigvelocitystep;
        private double bluepigvelocity;

        public EnemyMover() {
            bluepigvelocitystep = 2;
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
                    for (int i = 0; i < bluepigEnemies.size(); i++) {
                        int state = randomNumbers.nextInt(1000);
                        if (state < 5) {
                            bluepigvelocity = bluepigvelocitystep;
                            bluepigEnemies.elementAt(i).setInternalAngle(0);
                        } else if (state < 10) {
                            bluepigvelocity = bluepigvelocitystep;
                            bluepigEnemies.elementAt(i).setInternalAngle(halfPi);
                        } else if (state < 15) {
                            bluepigvelocity = bluepigvelocitystep;
                            bluepigEnemies.elementAt(i).setInternalAngle(pi);
                        } else if (state < 20) {
                            bluepigvelocity = bluepigvelocitystep;
                            bluepigEnemies.elementAt(i).setInternalAngle(threehavlesPi);
                        } else if (state < 250) {
                            bluepigvelocity = bluepigvelocitystep;
                        } else {
                            bluepigvelocity = 0;
                        }

                        bluepigEnemies.elementAt(i).updateBounce();
                        bluepigEnemies.elementAt(i).move(bluepigvelocity *
                                        Math.cos(bluepigEnemies.elementAt(i).getInternalAngle()),
                                bluepigvelocity * Math.sin(bluepigEnemies.elementAt(i).getInternalAngle()));
                    }

                    for (int i = 0; i < bubblebossEnemies.size(); i++) {

                    }
                } catch (java.lang.NullPointerException jlnpe) {
                    // NOP
                }
            }
        }
    }

    // pgs 123-124
    private static class HealthTracker implements Runnable {
        public void run() {
            while (endgame == false) {
                Long currTime = Long.valueOf(System.currentTimeMillis()); // was new Long(System.currentTimeMillis())
                if (availableToDropLife && p1.getDropLife() > 0) {
                    int newLife = p1.getLife() - p1.getDropLife();
                    p1.setDropLife(0);
                    availableToDropLife = false;

                    lastDropLife = System.currentTimeMillis();
                    p1.setLife(newLife);

                    try {
                        AudioInputStream ais = AudioSystem.getAudioInputStream(new File("hurt.wav").getAbsoluteFile());
                        Clip hurtclip = AudioSystem.getClip();
                        hurtclip.open(ais);
                        hurtclip.start();
                    } catch (Exception e) { }
                } else {
                    if (currTime - lastDropLife > dropLifeLifetime) {
                        availableToDropLife = true;
                    }
                }
            }
        }
    }

    // pgs 124-125
    private static class CollisionChecker implements Runnable {
        public void run() {
            // Random randomNumbers = new Random(LocalTime.now().getNano());
            while (endgame == false) {
                // check player against doors in given scenes
//                if (backgroundState.substring(0, 6).equals("KI0511")) {
//                    if (Zelda.GameLevel.collisionOccurs(p1, doorKItoTC)) {
//                        p1.moveto(p1originalX, p1originalY);4
//                        backgroundState = "TC0305";
//                        clip.stop();
//                        playAudio(backgroundState);
//                    }
//                } else if (backgroundState.substring(0, 6).equals("TC0305")) {
//                    if (Zelda.GameLevel.collisionOccurs(p1, doorTCtoKI)) {
//                        p1.moveto(p1originalX, p1originalY);
//                        backgroundState = "KI0511";
//                        clip.stop();
//                        playAudio(backgroundState);
//                    }
//                }

                // check player against doors in given scenes
                if (backgroundState.substring(0, 6).equals("KI0101")) {
                    if (Zelda.GameLevel.collisionOccurs(p1, doorKItoTC)) {
                        p1.moveto(p1originalX, p1originalY);
                        backgroundState = "TC0101";
                        clip.stop();
                        playAudio(backgroundState);
                    }
                } else if (backgroundState.substring(0, 6).equals("TC0101")) {
                    if (Zelda.GameLevel.collisionOccurs(p1, doorTCtoKI)) {
                        p1.moveto(p1originalX, p1originalY);
                        backgroundState = "KI0101";
                        clip.stop();
                        playAudio(backgroundState);
                    }
                }

                // check player and enemies against walls
                if (backgroundState.substring(0, 6).equals("KI0000")) {
                    checkMoversAgainstWalls(wallsKI.elementAt(0).elementAt(0));
                }

                // check player against enemies
//                for (int i = 0; i < bluepigEnemies.size(); i++) {
//                    if (Zelda.GameLevel.collisionOccurs(p1, bluepigEnemies.elementAt(i))) {
//                        // System.out.println("Still Colliding: " + i + ", " + System.currentTimeMllis());
//                        p1.setBounce(true);
//                        bluepigEnemies.elementAt(i).setBounce(true);
//                        if (availableToDropLife) {
//                            p1.setDropLife(1);
//                        }
//                    }
//                }

                // TODO: check enemies against walls
                // TODO: check player against deep water or pits
                // TODO: check player against enemy arrows
                // TODO: check enemies against player weapons
            }
        }

        // pg 126
        private static void checkMoversAgainstWalls(Vector<ImageObject> wallsInput) {
            for (int i = 0; i < wallsInput.size(); i++) {
                if (Zelda.GameLevel.collisionOccurs(p1, wallsInput.elementAt(i))) {
                    p1.setBounce(true);
                }
                for (int j = 0; j < bluepigEnemies.size(); j++) {
                    if (Zelda.GameLevel.collisionOccurs(bluepigEnemies.elementAt(j), wallsInput.elementAt(i))) {
                        bluepigEnemies.elementAt(j).setBounce(true);
                    }
                }
            }
        }
    }

    // pg 126
    // TODO: make one lockrotate function which takes as input objInner, objOuter, and point relative to objInner's x,y
    //  that objOuter must rotate around.
    // dist is a distance between the two objects at the bottom of objInner.
    private static void lockrotateObjAroundObjbottom(ImageObject objOuter, ImageObject objInner, double dist) {
        objOuter.moveto(objInner.getX() + (dist + objInner.getWidth() / 2.0) * Math.cos(-objInner.getAngle() + pi/2.0)
                        + objOuter.getWidth() / 2.0,
                objInner.getY() + (dist + objInner.getHeight() / 2.0) * Math.sin(-objInner.getAngle() + pi/2.0) +
                        objOuter.getHeight() / 2.0);
        objOuter.setAngle(objInner.getAngle());
    }

    // pgs 126-127
    // dist is a distance between the two objectys at the top of the inner object
    private static void lockrotateObjAroundObjtop(ImageObject objOuter, ImageObject objInner, double dist) {
        objOuter.moveto(objInner.getX() + objOuter.getWidth() + (objInner.getWidth() / 2.0 + (dist +
                        objInner.getWidth() / 2.0) * Math.cos(objInner.getAngle() + pi/2.0)) / 2.0,
                objInner.getY() - objOuter.getHeight() + (dist + objInner.getHeight() / 2.0) *
                        Math.sin(objInner.getAngle() / 2.0));
        objOuter.setAngle(objInner.getAngle());
    }

    // pg 127
    private static AffineTransformOp rotateImageObject(ImageObject obj) {
        AffineTransform at = AffineTransform.getRotateInstance(-obj.getInternalAngle(),
                obj.getWidth() / 2.0, obj.getHeight() / 2.0);
        AffineTransformOp atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        return atop;
    }

    // pg 127
    private static AffineTransformOp spinImageObject(ImageObject obj) {
        AffineTransform at = AffineTransform.getRotateInstance(-obj.getInternalAngle(), obj.getWidth() / 2.0,
                obj.getHeight() / 2.0);
        AffineTransformOp atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        return atop;
    }

    // pg 127-128
    private static void backgroundDraw() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2D = (Graphics2D) g;

        if (backgroundState.substring(0, 2).equals("KI")) {
            int i = Integer.parseInt(backgroundState.substring(4, 6));
            int j = Integer.parseInt(backgroundState.substring(2, 4));
            if (i < 3) {
                if (j < 2) {
                    g2D.drawImage(backgroundKI[j][i], XOFFSET, YOFFSET, null);
                }
            }
        }

        if (backgroundState.substring(0, 2).equals("TC")) {
            int i = Integer.parseInt(backgroundState.substring(4, 6)) - 1;
            int j = Integer.parseInt(backgroundState.substring(2, 4)) - 1;
            if (i < 3) {
                if (j < 2) {
                    g2D.drawImage(backgroundTC[j][i], XOFFSET, YOFFSET, null);
                }
            }
//            if (i < backgroundTC.length) {
//                if (j < backgroundTC[i].length) {
//                    g2D.drawImage(backgroundTC[i][j], XOFFSET, YOFFSET, null);
//                }
//            }
        }
    }


    // pg 128-130
    private static void playerDraw() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2D = (Graphics2D) g;
        p1.setMaxFrames(10);

        if (upPressed || downPressed || leftPressed || rightPressed) {
            if (upPressed == true) {
                if (p1.getCurrentFrame() < 5) {
//                    g2D.drawImage(rotateImageObject(p1).filter(link[0], null),
                    g2D.drawImage(link[0],
                            (int) (p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
                } else if (p1.getCurrentFrame() > 5) {
//                    g2D.drawImage(rotateImageObject(p1).filter(link[1], null),
                    g2D.drawImage(link[1],
                            (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
                }
                p1.updateCurrentFrame();
            }
            if (downPressed == true) {
                if (p1.getCurrentFrame() < 5) {
//                    g2D.drawImage(rotateImageObject(p1).filter(link[2], null),
                    g2D.drawImage(link[2],
                            (int)(p1.getX() + 0.5), (int) (p1.getY() + 0.5), null);
                } else if (p1.getCurrentFrame() > 5) {
//                    g2D.drawImage(rotateImageObject(p1).filter(link[3], null),
                    g2D.drawImage(link[3],
                            (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
                }
                p1.updateCurrentFrame();
            }
            if (leftPressed == true) {
                if (p1.getCurrentFrame() < 5) {

//                    g2D.drawImage(rotateImageObject(p1).filter(link[4], null),
                    g2D.drawImage(link[4],
                            (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
                } else if (p1.getCurrentFrame() > 5) {
//                    g2D.drawImage(rotateImageObject(p1).filter(link[5], null),
                    g2D.drawImage(link[5],
                            (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
                }
                p1.updateCurrentFrame();
            }
            if (rightPressed == true) {
                if (p1.getCurrentFrame() < 5) {
//                    g2D.drawImage(rotateImageObject(p1).filter(link[6], null),
                    g2D.drawImage(link[6],
                            (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
                } else if (p1.getCurrentFrame() > 5) {
//                    g2D.drawImage(rotateImageObject(p1).filter(link[7], null),
                    g2D.drawImage(link[7],
                            (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
                }
                p1.updateCurrentFrame();
            }
        } else {
            if (Math.abs(lastPressed - 90.0) < 1.0) {
//                g2D.drawImage(rotateImageObject(p1).filter(link[0], null),
                g2D.drawImage(link[0],
                        (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
            }
            if (Math.abs(lastPressed - 270.0) < 1.0) {
//                g2D.drawImage(rotateImageObject(p1).filter(link[2], null),
                g2D.drawImage(link[2],
                        (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
            }
            if (Math.abs(lastPressed - 0.0) < 1.0) {
//                g2D.drawImage(rotateImageObject(p1).filter(link[6], null),
                g2D.drawImage(link[6],
                        (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
            }
            if (Math.abs(lastPressed - 180.0) < 1.0) {
//                g2D.drawImage(rotateImageObject(p1).filter(link[4], null),
                g2D.drawImage(link[4],
                        (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
            }
        }

        // g2D.drawImage(rotateImageObject(p1).filter(player, null), (int)(p1.getX() + 0.5), (int)(p1.getY() + 0.5), null);
    }

    // pgs 130-131
    private static void healthDraw() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2D = (Graphics2D) g;

        int leftscale = 10;
        int leftoffset = 10;
        int rightoffset = 9;
        int interioroffset = 2;
        int halfinterioroffset = 1;
        for (int i = 0; i < p1.getMaxLife(); i++) {
            if (i % 2 == 0) {
                g2D.drawImage(leftHeartOutline, leftscale * i + leftoffset + XOFFSET, YOFFSET, null);
//                g2D.drawImage(rotateImageObject(p1).filter(leftHeartOutline, null),
//                        leftscale * i + leftoffset + XOFFSET, YOFFSET, null);
            } else {
                g2D.drawImage(rightHeartOutline, leftscale * i + rightoffset + XOFFSET, YOFFSET, null);
//                g2D.drawImage(rotateImageObject(p1).filter(rightHeartOutline, null),
//                        leftscale * i + rightoffset + XOFFSET, YOFFSET, null);
            }
        }

        for (int i = 0; i < p1.getLife(); i++) {
            if (i % 2 == 0) {
                g2D.drawImage(leftHeart, leftscale * i + leftoffset + interioroffset + XOFFSET,
                        interioroffset + YOFFSET, null);
//                g2D.drawImage(rotateImageObject(p1).filter(leftHeart, null),
//                        leftscale * i + leftoffset + interioroffset + XOFFSET, interioroffset + YOFFSET, null);
            } else {
                g2D.drawImage(rightHeart, leftscale * i + leftoffset - halfinterioroffset + XOFFSET,
                        interioroffset + YOFFSET, null);
//                g2D.drawImage(rotateImageObject(p1).filter(rightHeart, null),
//                        leftscale * i + leftoffset - halfinterioroffset + XOFFSET, interioroffset + YOFFSET, null);
            }
        }
    }

    // pgs 131-133
    private static void enemiesDraw() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2D = (Graphics2D) g;

        for (int i = 0; i < bluepigEnemies.size(); i++) {
            if (Math.abs(bluepigEnemies.elementAt(i).getInternalAngle() - 0.0) < 1.0) {
                if (bluepigEnemies.elementAt(i).getCurrentFrame() < bluepigEnemies.elementAt(i).getMaxFrames() / 2) {
                    g2D.drawImage(rotateImageObject(bluepigEnemies.elementAt(i)).filter(bluepigEnemy.elementAt(6), null),
                            (int)(bluepigEnemies.elementAt(i).getX() + 0.5),
                            (int)(bluepigEnemies.elementAt(i).getY() + 0.5), null);
                } else {
                    g2D.drawImage(rotateImageObject(bluepigEnemies.elementAt(i)).filter(bluepigEnemy.elementAt(7), null),
                            (int)(bluepigEnemies.elementAt(i).getX() + 0.5),
                            (int)(bluepigEnemies.elementAt(i).getY() + 0.5), null);
                }
                bluepigEnemies.elementAt(i).updateCurrentFrame();;
            }
            if(Math.abs(bluepigEnemies.elementAt(i).getInternalAngle() - pi) < 1.0) {
                if (bluepigEnemies.elementAt(i).getCurrentFrame() < bluepigEnemies.elementAt(i).getMaxFrames() / 2) {
                    g2D.drawImage(rotateImageObject(bluepigEnemies.elementAt(i)).filter(bluepigEnemy.elementAt(4), null),
                            (int)(bluepigEnemies.elementAt(i).getX() + 0.5),
                            (int)(bluepigEnemies.elementAt(i).getY() + 0.5), null);
                } else {
                    g2D.drawImage(rotateImageObject(bluepigEnemies.elementAt(i)).filter(bluepigEnemy.elementAt(5), null),
                            (int)(bluepigEnemies.elementAt(i).getX() + 0.5),
                            (int)(bluepigEnemies.elementAt(i).getY() + 0.5), null);
                }
                bluepigEnemies.elementAt(i).updateCurrentFrame();
            }
            if (Math.abs(bluepigEnemies.elementAt(i).getInternalAngle() - halfPi) < 1.0) {
                if (bluepigEnemies.elementAt(i).getCurrentFrame() < bluepigEnemies.elementAt(i).getMaxFrames() / 2) {
                    g2D.drawImage(rotateImageObject(bluepigEnemies.elementAt(i)).filter(bluepigEnemy.elementAt(2), null),
                            (int)(bluepigEnemies.elementAt(i).getX() + 0.5),
                            (int)(bluepigEnemies.elementAt(i).getY() + 0.5), null);
                } else {
                    g2D.drawImage(rotateImageObject(bluepigEnemies.elementAt(i)).filter(bluepigEnemy.elementAt(3), null),
                            (int)(bluepigEnemies.elementAt(i).getX() + 0.5),
                            (int)(bluepigEnemies.elementAt(i).getY() + 0.5), null);
                }
                bluepigEnemies.elementAt(i).updateCurrentFrame();
            }
            if (Math.abs(bluepigEnemies.elementAt(i).getInternalAngle() - threehavlesPi) < 1.0) {
                if (bluepigEnemies.elementAt(i).getCurrentFrame() < bluepigEnemies.elementAt(i).getMaxFrames() / 2) {
                    g2D.drawImage(rotateImageObject(bluepigEnemies.elementAt(i)).filter(bluepigEnemy.elementAt(0), null),
                            (int)(bluepigEnemies.elementAt(i).getX() + 0.5),
                            (int)(bluepigEnemies.elementAt(i).getY() + 0.5), null);
                } else {
                    g2D.drawImage(rotateImageObject(bluepigEnemies.elementAt(i)).filter(bluepigEnemy.elementAt(1), null),
                            (int)(bluepigEnemies.elementAt(i).getX() + 0.5),
                            (int)(bluepigEnemies.elementAt(i).getY() + 0.5), null);
                }
                bluepigEnemies.elementAt(i).updateCurrentFrame();
            }
        }
    }

    // pgs 133-134
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
            if (action.equals("A")) {
                aPressed = true;
            }
            if (action.equals("X")) {
                xPressed = true;
            }
        }
    }

    // pgs 134-135
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
            if (action.equals("A")) {
                aPressed = false;
            }
            if (action.equals("X")) {
                xPressed = false;
            }
        }
    }

    // pg 135
    private static class QuitGame implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            endgame = true;
        }
    }

    // pgs 135-136
    private static class StartGame implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            endgame = true;
            upPressed = false;
            downPressed = false;
            leftPressed = false;
            rightPressed = false;
            aPressed = false;
            xPressed = false;
            lastPressed = 90.0;
            backgroundState = "KI0000";
            availableToDropLife = true;

            try {
//                clearEnemies();
//                generateEnemies(backgroundState);
            } catch (java.lang.NullPointerException jlnpe) { }

            p1 = new ImageObject(p1originalX, p1originalY, p1width, p1height, 0.0);
            p1velocity = 0.0;
            p1.setInternalAngle(threehavlesPi); // 270 degrees, in radians
            p1.setMaxFrames(2);
            p1.setlastposx(p1originalX);
            p1.setlastposy(p1originalY);
            p1.setLife(6);
            p1.setMaxLife(6);
            doorKItoTC = new ImageObject(200, 55, 35, 35, 0.0);
            doorTCtoKI = new ImageObject(200, WINHEIGHT + YOFFSET, 35, 35, 0.0);

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
//            Thread t5 = new Thread(new EnemyMover());
            Thread t6 = new Thread(new HealthTracker());
            t1.start();
            t2.start();
            t3.start();
            t4.start();
//            t5.start();
            t6.start();
        }
    }

    // pgs 136-139
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

    // pgs 139-146
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

    // pg 146
    private static void bindKey(JPanel myPanel, String input) {
        myPanel.getInputMap(IFW).put(KeyStroke.getKeyStroke("pressed " + input), input + " pressed");
        myPanel.getActionMap().put(input + " pressed", new KeyPressed(input));

        myPanel.getInputMap(IFW).put(KeyStroke.getKeyStroke("released " + input), input + " released");
        myPanel.getActionMap().put(input + " released", new KeyReleased(input));
    }

    // pgs 147-
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
        bindKey(myPanel, "F");

        appFrame.getContentPane().add(myPanel, "South");
        appFrame.setVisible(true);
    }


}
