package ThreeDGame;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class TextureManager {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    public BufferedImage wallTexture;
    public BufferedImage floorTexture;
    public BufferedImage ceilingTexture;
    public BufferedImage barrelTexture;
    public BufferedImage barrelTexture3;
    public BufferedImage barrelTexture4;
    public BufferedImage barrelTexture5;
    public BufferedImage pistolIdle;
    public BufferedImage pistolFire1;
    public BufferedImage pistolFire2;
    public BufferedImage shotgunIdle;
    public BufferedImage shotgunFire2;
    public BufferedImage shotgunFire3;
    public BufferedImage shotgunFire4;
    public BufferedImage shotgunFire5;
    public BufferedImage shotgunFire6;
    public BufferedImage zombie1Texture;
    public BufferedImage zombie2Texture;
    public BufferedImage zombie3Texture;
    public BufferedImage zombie4Texture;
    public BufferedImage zombieDie1Texture;
    public BufferedImage zombieDie2Texture;
    public BufferedImage zombieDie3Texture;
    public BufferedImage zombieDie4Texture;
    public BufferedImage zombieDie5Texture;
    public BufferedImage zombieHurtFrontTexture;

    public int floorTextureWidth;
    public int floorTextureHeight;
    public int textureWidth;
    public int textureHeight;

    public TextureManager() {
        loadTextures();
    }

    private BufferedImage toCompatibleImage(BufferedImage image) {
        BufferedImage compatibleImage = new BufferedImage(
                image.getWidth(),
                image.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2d = compatibleImage.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
        return compatibleImage;
    }

    private void loadTextures() {
        try {
            SoundManager.load("dspistol.wav");
            SoundManager.load("explosion.wav");
            SoundManager.load("dsshotgn.wav");
            SoundManager.load("dspopain.wav");
            SoundManager.load("dspodth1.wav");
            SoundManager.load("dspodth2.wav");
            SoundManager.load("dspodth3.wav");

            java.net.URL imgUrl = Game.class.getResource("wallTexture.png");
            java.net.URL floorUrl = Game.class.getResource("floorTexture.png");
            java.net.URL ceilingUrl = Game.class.getResource("ceilingTexture.png");
            java.net.URL barrelUrl = Game.class.getResource("barrel2.png");
            java.net.URL barrel3Url = Game.class.getResource("barrel3.png");
            java.net.URL barrel4Url = Game.class.getResource("barrel4.png");
            java.net.URL barrel5Url = Game.class.getResource("barrel5.png");
            java.net.URL weaponUrl = Game.class.getResource("pistol.png");
            java.net.URL weaponFire1Url = Game.class.getResource("pistolFire1.png");
            java.net.URL weaponFire2Url = Game.class.getResource("pistolFire2.png");
            java.net.URL shotgunIdleUrl = Game.class.getResource("shotgun1.png");
            java.net.URL shotgun2Url = Game.class.getResource("shotgun2.png");
            java.net.URL shotgun3Url = Game.class.getResource("shotgun3.png");
            java.net.URL shotgun4Url = Game.class.getResource("shotgun4.png");
            java.net.URL shotgun5Url = Game.class.getResource("shotgun5.png");
            java.net.URL shotgun6Url = Game.class.getResource("shotgun6.png");
            java.net.URL zombie1Url = Game.class.getResource("Zombie1.png");
            java.net.URL zombie2Url = Game.class.getResource("Zombie2.png");
            java.net.URL zombie3Url = Game.class.getResource("Zombie3.png");
            java.net.URL zombie4Url = Game.class.getResource("Zombie4.png");
            java.net.URL zombieDie1Url = Game.class.getResource("ZombieDie1.png");
            java.net.URL zombieDie2Url = Game.class.getResource("ZombieDie2.png");
            java.net.URL zombieDie3Url = Game.class.getResource("ZombieDie3.png");
            java.net.URL zombieDie4Url = Game.class.getResource("ZombieDie4.png");
            java.net.URL zombieDie5Url = Game.class.getResource("ZombieDie5.png");
            java.net.URL zombieHurtFrontUrl = Game.class.getResource("ZombieHurtFront.png");

            if (imgUrl == null || floorUrl == null || ceilingUrl == null || barrelUrl == null || weaponFire1Url == null
                    || weaponFire2Url == null) {
                throw new IOException("File not found inside the package directory!");
            }

            wallTexture = toCompatibleImage(javax.imageio.ImageIO.read(imgUrl));
            textureWidth = wallTexture.getWidth();
            textureHeight = wallTexture.getHeight();
            floorTexture = toCompatibleImage(javax.imageio.ImageIO.read(floorUrl));
            floorTextureWidth = floorTexture.getWidth();
            floorTextureHeight = floorTexture.getHeight();
            ceilingTexture = toCompatibleImage(javax.imageio.ImageIO.read(ceilingUrl));
            barrelTexture = toCompatibleImage(javax.imageio.ImageIO.read(barrelUrl));
            barrelTexture3 = toCompatibleImage(javax.imageio.ImageIO.read(barrel3Url));
            barrelTexture4 = toCompatibleImage(javax.imageio.ImageIO.read(barrel4Url));
            barrelTexture5 = toCompatibleImage(javax.imageio.ImageIO.read(barrel5Url));
            pistolIdle = toCompatibleImage(javax.imageio.ImageIO.read(weaponUrl));
            pistolFire1 = toCompatibleImage(javax.imageio.ImageIO.read(weaponFire1Url));
            pistolFire2 = toCompatibleImage(javax.imageio.ImageIO.read(weaponFire2Url));
            shotgunIdle = toCompatibleImage(javax.imageio.ImageIO.read(shotgunIdleUrl));
            shotgunFire2 = toCompatibleImage(javax.imageio.ImageIO.read(shotgun2Url));
            shotgunFire3 = toCompatibleImage(javax.imageio.ImageIO.read(shotgun3Url));
            shotgunFire4 = toCompatibleImage(javax.imageio.ImageIO.read(shotgun4Url));
            shotgunFire5 = toCompatibleImage(javax.imageio.ImageIO.read(shotgun5Url));
            shotgunFire6 = toCompatibleImage(javax.imageio.ImageIO.read(shotgun6Url));
            zombie1Texture = toCompatibleImage(javax.imageio.ImageIO.read(zombie1Url));
            zombie2Texture = toCompatibleImage(javax.imageio.ImageIO.read(zombie2Url));
            zombie3Texture = toCompatibleImage(javax.imageio.ImageIO.read(zombie3Url));
            zombie4Texture = toCompatibleImage(javax.imageio.ImageIO.read(zombie4Url));
            zombieDie1Texture = toCompatibleImage(javax.imageio.ImageIO.read(zombieDie1Url));
            zombieDie2Texture = toCompatibleImage(javax.imageio.ImageIO.read(zombieDie2Url));
            zombieDie3Texture = toCompatibleImage(javax.imageio.ImageIO.read(zombieDie3Url));
            zombieDie4Texture = toCompatibleImage(javax.imageio.ImageIO.read(zombieDie4Url));
            zombieDie5Texture = toCompatibleImage(javax.imageio.ImageIO.read(zombieDie5Url));
            zombieHurtFrontTexture = toCompatibleImage(javax.imageio.ImageIO.read(zombieHurtFrontUrl));
        } catch (IOException e) {
            System.out.println(
                    "Error: Could not locate textures inside the source package.");
            e.printStackTrace();
        }
    }
}
