package ThreeDGame;

import java.awt.image.BufferedImage;

public class Sprite {
    public BufferedImage texture;
    public double x;
    public double y;
    public double distance;
    public double scale = 0.5;
    public double yOffset = 0.5;

    public boolean exploding = false;
    public int explosionFrame = 0;
    public int explosionTimer = 0;

    public Sprite(double x, double y, BufferedImage texture) {
        this.texture = texture;
        this.x = x;
        this.y = y;
    }
}
