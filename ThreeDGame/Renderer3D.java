package ThreeDGame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Renderer3D {
    private Raycaster raycaster;
    private BufferedImage screenBuffer;
    private double[] depthBuffer;

    public Renderer3D(TextureManager textureManager) {
        this.screenBuffer = new BufferedImage(TextureManager.WIDTH, TextureManager.HEIGHT, BufferedImage.TYPE_INT_ARGB);
        this.depthBuffer = new double[TextureManager.WIDTH];
        this.raycaster = new Raycaster(textureManager, depthBuffer);
    }

    public void render(int[][] map, ArrayList<Sprite> sprites, ArrayList<Enemy> enemies, ArrayList<Particle> particles, Weapon weapon) {
        Graphics g = screenBuffer.getGraphics();
        raycaster.draw3DScene(g, map, screenBuffer);
        drawAllObjects((Graphics2D) g, sprites, enemies);
        drawParticles((Graphics2D) g, particles);
        weapon.draw((Graphics2D) g);
        g.dispose();
    }

    public BufferedImage getScreenBuffer() {
        return screenBuffer;
    }

    private void drawSprite(Sprite s, Graphics2D g2d) {
        double spriteX = s.x - Camera.x;
        double spriteY = s.y - Camera.y;

        double rotX = -Math.sin(Camera.angle) * spriteX + Math.cos(Camera.angle) * spriteY;
        double rotY = Math.cos(Camera.angle) * spriteX + Math.sin(Camera.angle) * spriteY;

        if (rotY <= 0.1)
            return;

        double spriteScreenX = TextureManager.WIDTH * 0.5 * (1.0 + (rotX / rotY) / Math.tan(Camera.fov * 0.5));
        int spriteHeight = (int) ((TextureManager.HEIGHT / rotY) * s.scale);
        double aspectRatio = (double) s.texture.getWidth() / (double) s.texture.getHeight();
        int spriteWidth = (int) (spriteHeight * aspectRatio);

        int drawEndY = TextureManager.HEIGHT / 2 + spriteHeight / 2 + (int) (spriteHeight * s.yOffset);
        int drawStartY = drawEndY - spriteHeight;
        int drawStartX = (int) (spriteScreenX - spriteWidth / 2);
        int drawEndX = (int) (spriteScreenX + spriteWidth / 2);

        for (int stripe = drawStartX; stripe < drawEndX; stripe++) {
            if (stripe >= 0 && stripe < TextureManager.WIDTH) {
                if (rotY < depthBuffer[stripe]) {
                    int texX = (int) (((stripe - drawStartX) * s.texture.getWidth()) / (double) spriteWidth);
                    if (texX >= 0 && texX < s.texture.getWidth()) {
                        g2d.drawImage(
                                s.texture,
                                stripe, drawStartY, stripe + 1, drawEndY,
                                texX, 0, texX + 1, s.texture.getHeight(),
                                null);
                    }
                }
            }
        }
    }

    private void drawEnemy(Enemy e, Graphics2D g2d) {
        double spriteX = e.x - Camera.x;
        double spriteY = e.y - Camera.y;

        double rotX = -Math.sin(Camera.angle) * spriteX + Math.cos(Camera.angle) * spriteY;
        double rotY = Math.cos(Camera.angle) * spriteX + Math.sin(Camera.angle) * spriteY;

        if (rotY <= 0.1)
            return;

        double spriteScreenX = TextureManager.WIDTH * 0.5 * (1.0 + (rotX / rotY) / Math.tan(Camera.fov * 0.5));
        int spriteHeight = (int) ((TextureManager.HEIGHT / rotY) * e.scale);
        double aspectRatio = (double) e.texture.getWidth() / (double) e.texture.getHeight();
        int spriteWidth = (int) (spriteHeight * aspectRatio);

        int drawEndY = TextureManager.HEIGHT / 2 + spriteHeight / 2 + (int) (spriteHeight * e.yOffset);
        int drawStartY = drawEndY - spriteHeight;
        int drawStartX = (int) (spriteScreenX - spriteWidth / 2);
        int drawEndX = (int) (spriteScreenX + spriteWidth / 2);

        for (int stripe = drawStartX; stripe < drawEndX; stripe++) {
            if (stripe >= 0 && stripe < TextureManager.WIDTH) {
                if (rotY < depthBuffer[stripe]) {
                    int texX = (int) (((stripe - drawStartX) * e.texture.getWidth()) / (double) spriteWidth);
                    if (texX >= 0 && texX < e.texture.getWidth()) {
                        g2d.drawImage(
                                e.texture,
                                stripe, drawStartY, stripe + 1, drawEndY,
                                texX, 0, texX + 1, e.texture.getHeight(),
                                null);
                    }
                }
            }
        }
    }

    private void drawAllObjects(Graphics2D g2d, ArrayList<Sprite> sprites, ArrayList<Enemy> enemies) {
        class Item {
            double distance;
            boolean isEnemy;
            Sprite sprite;
            Enemy enemy;
        }

        ArrayList<Item> list = new ArrayList<>();

        for (Sprite s : sprites) {
            Item i = new Item();
            i.distance = Math.sqrt(
                    (Camera.x - s.x) * (Camera.x - s.x) +
                            (Camera.y - s.y) * (Camera.y - s.y));
            i.sprite = s;
            i.isEnemy = false;
            list.add(i);
        }

        for (Enemy e : enemies) {
            Item i = new Item();
            double dx = Camera.x - e.x;
            double dy = Camera.y - e.y;
            i.distance = dx * dx + dy * dy;
            i.enemy = e;
            i.isEnemy = true;
            list.add(i);
        }

        list.sort((a, b) -> Double.compare(b.distance, a.distance));

        for (Item i : list) {
            if (i.isEnemy) {
                drawEnemy(i.enemy, g2d);
            } else {
                drawSprite(i.sprite, g2d);
            }
        }
    }

    

    private void drawParticles(Graphics2D g2d, ArrayList<Particle> particles) {
        for (Particle p : particles) {
            g2d.setColor(new Color(255, 255, 0, (int) (255 * p.life)));
            double dx = p.x - Camera.x;
            double dy = p.y - Camera.y;

            double rotX = -Math.sin(Camera.angle) * dx + Math.cos(Camera.angle) * dy;
            double rotY = Math.cos(Camera.angle) * dx + Math.sin(Camera.angle) * dy;

            if (rotY <= 0)
                continue;

            int screenX = (int) (TextureManager.WIDTH / 2 + (rotX / rotY) * TextureManager.WIDTH);
            int screenY = (int) (TextureManager.HEIGHT / 2 + p.z / rotY * 200);
            int size = (int) (25 / rotY);

            g2d.fillOval(screenX, screenY, size, size);
        }
    }
}