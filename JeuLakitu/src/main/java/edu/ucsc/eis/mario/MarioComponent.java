package edu.ucsc.eis.mario;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.*;
import java.io.Serializable;
import java.util.Random;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;

import edu.ucsc.eis.mario.events.*;
import edu.ucsc.eis.mario.repairs.RepairEvent;
import edu.ucsc.eis.mario.repairs.RepairHandler;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.conf.ClockTypeOption;
import org.drools.runtime.rule.FactHandle;

import com.mojang.sonar.FakeSoundEngine;
import com.mojang.sonar.SonarSoundEngine;

import edu.ucsc.eis.mario.rules.KnowledgeFactory;
import edu.ucsc.eis.mario.rules.LakituFrameLauncher;
import edu.ucsc.eis.mario.sprites.*;

import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;


public class MarioComponent extends JComponent implements Runnable, KeyListener, FocusListener
{
    private static final long serialVersionUID = 739318775993206607L;
    public static final int TICKS_PER_SECOND = 24;

    private boolean running = false;
    private int width, height;
    private GraphicsConfiguration graphicsConfiguration;
    private Scene scene;
    private SonarSoundEngine sound;
    @SuppressWarnings("unused")
	private boolean focused = false;
    private boolean useScale2x = false;
    private MapScene mapScene;
    int delay = 0;
    public static StatefulKnowledgeSession ksession;
    FactHandle levelHandle;
    public static boolean rulesEnabled = true;
    private LakituFrameLauncher parent;
    private int ticks = 0;

    private Scale2x scale2x = new Scale2x(320, 240);

    public static MessageProducer producer;
    public static Session session;
    public static MessageConsumer consumer;

    private static Logger logger = Logger.getLogger("edu.ucsc.eis.mario");

    public MarioComponent(int width, int height) {
    	this(width, height, null);
    }

    public MarioComponent(int width, int height, LakituFrameLauncher parent)
    {
        this.setFocusable(true);
        this.setEnabled(true);
        this.width = width;
        this.height = height;
        this.parent = parent;

        Dimension size = new Dimension(width, height);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);

        try
        {
            sound = new SonarSoundEngine(64);
        }
        catch (LineUnavailableException e)
        {
            e.printStackTrace();
            sound = new FakeSoundEngine();
        }

        setFocusable(true);
    }

    private void toggleKey(int keyCode, boolean isPressed)
    {
        if (keyCode == KeyEvent.VK_LEFT)
        {
            scene.toggleKey(Mario.KEY_LEFT, isPressed);
        }
        if (keyCode == KeyEvent.VK_RIGHT)
        {
            scene.toggleKey(Mario.KEY_RIGHT, isPressed);
        }
        if (keyCode == KeyEvent.VK_DOWN)
        {
            scene.toggleKey(Mario.KEY_DOWN, isPressed);
        }
        if (keyCode == KeyEvent.VK_UP)
        {
            scene.toggleKey(Mario.KEY_UP, isPressed);
        }
        if (keyCode == KeyEvent.VK_A)
        {
            scene.toggleKey(Mario.KEY_SPEED, isPressed);
        }
        if (keyCode == KeyEvent.VK_S)
        {
            scene.toggleKey(Mario.KEY_JUMP, isPressed);
        }
        if (isPressed && keyCode == KeyEvent.VK_F1)
        {
            useScale2x = !useScale2x;
        }
    }

//    public void paint(Graphics g)
//    {
//    }
//
//    public void update(Graphics g)
//    {
//    }

    public void paintComponent(Graphics g) {
    	super.paintComponent(g);
    }

    public void start()
    {
        if (!running)
        {
            running = true;
            new Thread(this, "Game Thread").start();
        }
    }

    public void stop()
    {
        Art.stopMusic();
        running = false;
    }

    public void run()
    {
        graphicsConfiguration = getGraphicsConfiguration();

        mapScene = new MapScene(graphicsConfiguration, this, new Random().nextLong());
        scene = mapScene;
        scene.setSound(sound);

        Art.init(graphicsConfiguration, sound);

        VolatileImage image = createVolatileImage(320, 240);
        Graphics g = getGraphics();
        Graphics og = image.getGraphics();

        int renderedFrames = 0;

        double time = System.nanoTime() / 1000000000.0;
        double now = time;
        long tm = System.currentTimeMillis();
        long lTick = tm;

        addKeyListener(this);
        addFocusListener(this);
        initKnowledgeSession();

        try {
            ActiveMQConnectionFactory factory =
                    new ActiveMQConnectionFactory("tcp://localhost:61616");
            //factory.setUseAsyncSend(true);
            Connection connection = factory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue("mariotest");
            producer = session.createProducer(destination);
            consumer = session.createConsumer(destination);
        } catch (Exception e) {
            System.err.println("Couldn't connect to broker");
            //System.exit(1);
        }

        ksession.setGlobal("producer", producer);
        ksession.setGlobal("session", session);
        ksession.setGlobal("logger", logger);

        toTitle();
        adjustFPS();

        FactHandle marioFact = null;
        RepairHandler repairHandler = new RepairHandler();

//        FactHandle triggerFact = ksession.insert("Trigger");

        while (running)
        {
            scene.tick();

        	if (scene instanceof LevelScene) {
                Message message;
                boolean gotMessage = false;

                ticks++;

                if (ticks % 24 == 0) {
                    MarioComponent.insertFact(new MarioPosition(((LevelScene)scene).mario));
                }

//                ksession.update(triggerFact, "Trigger");
//                ksession.startProcess("Mario");
//                ksession.fireAllRules();

                try {
                    //while ((message = consumer.receive()) != null) {
        		    while ((message = consumer.receiveNoWait()) != null) {
                        logger.debug("Got message " + message);

                        if (message instanceof TextMessage) { break; } //continue
                        gotMessage = true;

                        ObjectMessage objectMessage = (ObjectMessage) message;
                        Object payload = objectMessage.getObject();

                        if (payload instanceof MarioEvent || payload instanceof Level) {
                            logger.info("Inserting event " + payload);
                            ksession.insert(payload);
                            if (rulesEnabled && gotMessage) {
                                ksession.startProcess("Mario");
                                ksession.fireAllRules();
                            }
                        }
                        else if (payload instanceof RepairEvent) {
                            logger.info("Handling repair " + payload);
                            RepairEvent rp = (RepairEvent) payload;
                            repairHandler.setMario(((LevelScene) scene).mario);
                            repairHandler.execute((RepairEvent) payload);
                        }
                        else {
                            logger.warn("Can't find type of message " + objectMessage);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
        	}

            float alpha = (float) (System.currentTimeMillis() - lTick);
            sound.clientTick(alpha);

            @SuppressWarnings("unused")
			int x = (int) (Math.sin(now) * 16 + 160);
            @SuppressWarnings("unused")
			int y = (int) (Math.cos(now) * 16 + 120);

            //og.setColor(Color.WHITE);
            og.fillRect(0, 0, 320, 240);

            alpha = 0;
            scene.render(og, alpha);

            if (!this.hasFocus() && lTick/4%2==0)
            {
                String msg = "CLICK TO PLAY";

                drawString(og, msg, 160 - msg.length() * 4 + 1, 110 + 1, 0);
                drawString(og, msg, 160 - msg.length() * 4, 110, 7);
            }
            og.setColor(Color.BLACK);
            /*          drawString(og, "FPS: " + fps, 5, 5, 0);
             drawString(og, "FPS: " + fps, 4, 4, 7);*/

            if (width != 320 || height != 240)
            {
                if (useScale2x)
                {
                    g.drawImage(scale2x.scale(image), 0, 0, null);
                }
                else
                {
                    g.drawImage(image, 0, 0, 640, 480, null);
                }
            }
            else
            {
                g.drawImage(image, 0, 0, null);
            }

            if (delay > 0)
                try {
                    tm += delay;
                    Thread.sleep(Math.max(0, tm - System.currentTimeMillis()));
                } catch (InterruptedException e) {
                    break;
                }

            renderedFrames++;
        }

        Art.stopMusic();
    }

    public void initKnowledgeSession() {
        if (ksession != null) { ksession.dispose(); }
        
		try {
			// load up the knowledge base
			KnowledgeBase kbase = KnowledgeFactory.newKnowledgeBase("Mario.drl", "Mario.rf");
			ksession = kbase.newStatefulKnowledgeSession();
			//KnowledgeRuntimeLogger knowledgeLogger = KnowledgeRuntimeLoggerFactory.newConsoleLogger(ksession);
			KnowledgeSessionConfiguration config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
			config.setOption( ClockTypeOption.get("realtime") );

		} catch (Exception e) {
            System.err.println("Couldn't parse rules " + e);
			e.printStackTrace();
			//System.exit(2);
		}

        ksession.setGlobal("producer", producer);
        ksession.setGlobal("session", session);
        ksession.setGlobal("logger", logger);
	}

	private void drawString(Graphics g, String text, int x, int y, int c)
    {
        char[] ch = text.toCharArray();
        for (int i = 0; i < ch.length; i++)
        {
            g.drawImage(Art.font[ch[i] - 32][c], x + i * 8, y, null);
        }
    }

    public void keyPressed(KeyEvent arg0)
    {
        toggleKey(arg0.getKeyCode(), true);
    }

    public void keyReleased(KeyEvent arg0)
    {
        toggleKey(arg0.getKeyCode(), false);
    }

    public void startLevel(long seed, int difficulty, int type)
    {
        scene = new LevelScene(graphicsConfiguration, this, seed, difficulty, type);
        LevelScene ls = (LevelScene) scene;
        scene.setSound(sound);
        scene.init();
        if (parent != null) {parent.setMario(ls.mario);}
        MarioComponent.insertFact(new LevelGenerated(ls.mario, ls.level));
    }

    public void levelFailed()
    {
        MarioComponent.insertFact(new LevelOver());
        scene = mapScene;
        mapScene.startMusic();
        Mario.lives--;
        ksession.insert(new NewLife());
        if (Mario.lives == 0)
        {
            lose();
        }
    }

    public void keyTyped(KeyEvent arg0)
    {
    }

    public void focusGained(FocusEvent arg0)
    {
        focused = true;
    }

    public void focusLost(FocusEvent arg0)
    {
        focused = false;
    }

    public void levelWon()
    {
        MarioComponent.insertFact(new LevelOver());
        scene = mapScene;
        mapScene.startMusic();
        mapScene.levelWon();
    }

    public void win()
    {
        scene = new WinScene(this);
        scene.setSound(sound);
        scene.init();
    }

    public void toTitle()
    {
        Mario.resetStatic();
        scene = new TitleScene(this, graphicsConfiguration);
        scene.setSound(sound);
        scene.init();
    }

    public void lose()
    {
        scene = new LoseScene(this);
        scene.setSound(sound);
        scene.init();
    }

    public void startGame()
    {
        scene = mapScene;
        mapScene.startMusic();
        mapScene.init();
   }

    public void adjustFPS() {
        int fps = 24;
        delay = (fps > 0) ? (fps >= 100) ? 0 : (1000 / fps) : 100;
        logger.debug("Delay: " + delay);
    }

    public static void insertFact(Object fact) {
    	if (ksession != null && fact != null && rulesEnabled) {

            if (session == null) {
                logger.warn("Session is null, throwing away " + fact);
            } else {
                try {
                    logger.info("Sending fact " + fact);
                    producer.send(session.createObjectMessage((Serializable) fact));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    	}
    }

    public static long getClockTime() {
    	long time = System.currentTimeMillis();
    	if (ksession != null) {
    		time = ksession.getSessionClock().getCurrentTime();
    	}

    	return time;
    }
}
