package ThreeDGame;

import java.awt.image.BufferedImage;

public class Enemy {
    double x, y;
    int health = 100;
    BufferedImage texture;
    BufferedImage[] walkFrames;
    BufferedImage[] dieFrames;
    BufferedImage[] shootFrames;
    public double distance;
    public double scale = 0.7;
    public double yOffset = 0.3;
    public int animationTimer = 0;
    public int animationFrame = 0;

    boolean dying = false;
    boolean dead = false;
    boolean shooting = false;
    boolean moving = true;
    boolean hurting = false;

    public Enemy(double x, double y, BufferedImage texture) {
        this.x = x;
        this.y = y;
        this.texture = texture;
    }

    public void update() {
        animationTimer++;

        BufferedImage[] frames;
        if (dying) {
            if (animationTimer >= 5) {
                animationTimer = 0;
                animationFrame++;
            }
            if (animationFrame >= dieFrames.length) {
                dead = true;
                animationFrame = dieFrames.length - 1;
                return;
            }
            texture = dieFrames[Math.min(animationFrame, dieFrames.length - 1)];
            return;
        }

        if (shooting) {
            frames = shootFrames;
        } else if (moving) {
            frames = walkFrames;
        } else {
            texture = walkFrames[0]; // idle = first frame
            return;
        }

        if (animationTimer >= 6) { // speed of animation
            animationTimer = 0;
            animationFrame = (animationFrame + 1) % frames.length;
            texture = frames[animationFrame];
        }
    }
}
