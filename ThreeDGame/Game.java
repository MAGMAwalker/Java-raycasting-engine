package ThreeDGame;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Robot;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.awt.image.BufferedImage;

public class Game extends JPanel implements Runnable {
    private boolean isRobotCentering = false;
    private Thread gameThread;
    private boolean running = false;
    private int[][] map = {
            { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
            { 1, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
    };
    private boolean keyZ = false;
    private boolean keyQ = false;
    private boolean keyD = false;
    private boolean keyS = false;
    private ArrayList<Sprite> sprites = new ArrayList<>();
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private ArrayList<Particle> particles = new ArrayList<>();
    private Robot robot;

    private TextureManager textureManager;
    private Weapon weapon;
    private Renderer3D renderer3D;

    public Game() {
        this.setPreferredSize(new Dimension(TextureManager.WIDTH, TextureManager.HEIGHT));
        this.setBackground(java.awt.Color.BLACK);
        this.setFocusable(true);

        this.textureManager = new TextureManager();
        this.weapon = new Weapon(textureManager);
        this.renderer3D = new Renderer3D(textureManager);

        initializeGameObjects();
        setupInputHandlers();
    }

    private void initializeGameObjects() {
        sprites.add(new Sprite(3.5, 3.5, textureManager.barrelTexture));
        sprites.add(new Sprite(5.0, 2.5, textureManager.barrelTexture));
        enemies.add(new Enemy(6.5, 6.5, textureManager.zombie1Texture));
        enemies.add(new Enemy(8.5, 8.5, textureManager.zombie1Texture));
        enemies.add(new Enemy(1.5, 1.5, textureManager.zombie1Texture));

        for (Enemy e : enemies) {
            e.walkFrames = new BufferedImage[] {
                    textureManager.zombie1Texture,
                    textureManager.zombie2Texture,
                    textureManager.zombie3Texture,
                    textureManager.zombie4Texture,
            };
        }
        for (Enemy e : enemies) {
            e.dieFrames = new BufferedImage[] {
                    textureManager.zombieDie1Texture,
                    textureManager.zombieDie2Texture,
                    textureManager.zombieDie3Texture,
                    textureManager.zombieDie4Texture,
                    textureManager.zombieDie5Texture,
            };
        }
    }

    private void setupInputHandlers() {
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_Z:
                        keyZ = true;
                        break;
                    case KeyEvent.VK_Q:
                        keyQ = true;
                        break;
                    case KeyEvent.VK_D:
                        keyD = true;
                        break;
                    case KeyEvent.VK_S:
                        keyS = true;
                        break;
                    case KeyEvent.VK_1:
                        weapon.switchWeapon(0);
                        break;
                    case KeyEvent.VK_2:
                        weapon.switchWeapon(1);
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_Z:
                        keyZ = false;
                        break;
                    case KeyEvent.VK_Q:
                        keyQ = false;
                        break;
                    case KeyEvent.VK_D:
                        keyD = false;
                        break;
                    case KeyEvent.VK_S:
                        keyS = false;
                        break;
                }
            }
        });

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!weapon.isFiring()) {
                    if (weapon.getCurrentGun() == 0) {
                        SoundManager.playSound("dspistol.wav");
                        firePistol(Camera.angle);
                    } else if (weapon.getCurrentGun() == 1) {
                        SoundManager.playSound("dsshotgn.wav");
                        fireShotgun();
                    }
                    weapon.startFiring();
                }
            }
        });

        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        java.awt.Cursor blankCursor = java.awt.Toolkit.getDefaultToolkit().createCustomCursor(cursorImg,
                new java.awt.Point(0, 0), "blank cursor");
        this.setCursor(blankCursor);

        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (isRobotCentering) {
                    isRobotCentering = false;
                    return;
                }

                int centerX = getWidth() / 2;
                int deltaX = e.getX() - centerX;
                double mouseSensitivity = 0.0025;
                Camera.angle += deltaX * mouseSensitivity;

                try {
                    isRobotCentering = true;
                    robot = new Robot();
                    java.awt.Point screenPos = new java.awt.Point(centerX, getHeight() / 2);
                    javax.swing.SwingUtilities.convertPointToScreen(screenPos, e.getComponent());
                    robot.mouseMove(screenPos.x, screenPos.y);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public synchronized void start() {
        if (running)
            return;
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        while (running) {
            update();
            render();
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void update() {
        Camera.angle = (Camera.angle + Math.PI * 2) % (Math.PI * 2);

        double moveX = 0;
        double moveY = 0;

        if (keyZ) {
            moveX += Math.cos(Camera.angle);
            moveY += Math.sin(Camera.angle);
        }
        if (keyS) {
            moveX -= Math.cos(Camera.angle);
            moveY -= Math.sin(Camera.angle);
        }
        if (keyQ) {
            moveX += Math.cos(Camera.angle - Math.PI / 2);
            moveY += Math.sin(Camera.angle - Math.PI / 2);
        }
        if (keyD) {
            moveX += Math.cos(Camera.angle + Math.PI / 2);
            moveY += Math.sin(Camera.angle + Math.PI / 2);
        }

        double baseSpeed = 0.05;
        double magnitude = Math.sqrt(moveX * moveX + moveY * moveY);

        if (magnitude > 0) {
            moveX = (moveX / magnitude) * baseSpeed;
            moveY = (moveY / magnitude) * baseSpeed;
        }

        double targetX = Camera.x + moveX;
        double targetY = Camera.y + moveY;
        double buffer = 0.2;
        double checkX = targetX + (moveX > 0 ? buffer : -buffer);
        double checkY = targetY + (moveY > 0 ? buffer : -buffer);

        int mapWidth = map[0].length;
        int mapHeight = map.length;

        if (checkX >= 0 && checkX < mapWidth && (int) targetY >= 0 && (int) targetY < mapHeight) {
            if (map[(int) targetY][(int) checkX] == 0) {
                Camera.x = targetX;
            }
        }

        if ((int) targetX >= 0 && (int) targetX < mapWidth && checkY >= 0 && checkY < mapHeight) {
            if (map[(int) checkY][(int) targetX] == 0) {
                Camera.y = targetY;
            }
        }

        for (int i = 0; i < particles.size(); i++) {
            Particle p = particles.get(i);
            p.update();
            if (p.isDead()) {
                particles.remove(i);
                i--;
            }
        }

        weapon.update();

        for (int i = 0; i < sprites.size(); i++) {
            Sprite s = sprites.get(i);
            if (s.exploding) {
                s.explosionTimer++;
                if (s.explosionTimer >= 5) {
                    s.explosionTimer = 0;
                    s.explosionFrame++;
                }

                switch (s.explosionFrame) {
                    case 0:
                        s.texture = textureManager.barrelTexture3;
                        break;
                    case 1:
                        s.texture = textureManager.barrelTexture4;
                        break;
                    case 2:
                        s.texture = textureManager.barrelTexture5;
                        break;
                    default:
                        break;
                }
                if (s.explosionFrame > 3) {
                    sprites.remove(s);
                    i--;
                }
            }
        }

        for (Enemy e : enemies) {
            e.update();
        }
    }

    private void render() {
        renderer3D.render(map, sprites, enemies, particles, weapon);
        repaint();
    }

    private void firePistol(double angle) {
        double rayX = Math.cos(angle);
        double rayY = Math.sin(angle);
        double bulletX = Camera.x;
        double bulletY = Camera.y;
        for (double d = 0; d < 20; d += 0.05) {
            bulletX = Camera.x + rayX * d;
            bulletY = Camera.y + rayY * d;

            int mapX = (int) bulletX;
            int mapY = (int) bulletY;

            if (mapX < 0 || mapX >= map[0].length || mapY < 0 || mapY >= map.length)
                break;

            for (Enemy e : enemies) {
                double dx = e.x - bulletX;
                double dy = e.y - bulletY;

                double dist = Math.sqrt(dx * dx + dy * dy);
                if (dist < 0.5 && !e.dead) {
                    e.health -= 34;
                    if (e.health <= 0 && !e.dying) {
                        e.dying = true;
                        e.animationTimer = 0;
                        e.animationFrame = 0;
                        double r = Math.random();
                        if (r < 0.33) {
                            SoundManager.playSound("dspodth1.wav");
                        } else if (r < 0.66) {
                            SoundManager.playSound("dspodth2.wav");
                        } else {
                            SoundManager.playSound("dspodth3.wav");
                        }
                    } else {
                        e.texture = textureManager.zombieHurtFrontTexture;
                        SoundManager.playSound("dspopain.wav");
                    }
                    return;
                }
            }

            if (map[mapY][mapX] > 0) {
                for (int i = 0; i < 8; i++)
                    particles.add(new Particle(bulletX, bulletY));
                break;
            }

            spawnWallParticles(bulletX, bulletY);
        }
    }

    private void fireShotgun() {
        int pellets = 8;

        for (int i = 0; i < pellets; i++) {
            double spread = (Math.random() - 0.5) * 0.15;
            firePistol(Camera.angle + spread);
        }
    }

    private void spawnWallParticles(double x, double y) {
        for (int i = 0; i < sprites.size(); i++) {
            Sprite s = sprites.get(i);
            double dx = x - s.x;
            double dy = y - s.y;
            double dist = Math.sqrt(dx * dx + dy * dy);
            if (dist < 0.3) {
                SoundManager.playSound("explosion.wav");
                s.exploding = true;
                s.explosionFrame = 0;
                s.explosionTimer = 0;
                return;
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(renderer3D.getScreenBuffer(), 0, 0, this);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Scratch 3D Java Engine");
        Game engine = new Game();
        frame.add(engine);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        engine.start();
    }
}
