package ThreeDGame;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Weapon {
    private TextureManager textureManager;
    private int weaponState = 0;
    private boolean firing = false;
    private int currentGun = 0;
    private BufferedImage currentWeapon;

    public Weapon(TextureManager textureManager) {
        this.textureManager = textureManager;
    }

    public void update() {
        if (weaponState > 0) {
            weaponState++;
            if (currentGun == 0) {
                if (weaponState > 12) {
                    weaponState = 0;
                    firing = false;
                }
            } else {
                if (weaponState > 35) {
                    weaponState = 0;
                    firing = false;
                }
            }
        }
    }

    public void draw(Graphics2D g2d) {
        int scale = 4;

        if (currentGun == 0) {
            if (weaponState == 0)
                currentWeapon = textureManager.pistolIdle;
            else if (weaponState <= 5)
                currentWeapon = textureManager.pistolFire1;
            else
                currentWeapon = textureManager.pistolFire2;
        } else {
            if (weaponState == 0)
                currentWeapon = textureManager.shotgunIdle;
            else if (weaponState <= 5)
                currentWeapon = textureManager.shotgunFire2;
            else if (weaponState <= 10)
                currentWeapon = textureManager.shotgunFire3;
            else if (weaponState <= 15)
                currentWeapon = textureManager.shotgunFire4;
            else if (weaponState <= 20)
                currentWeapon = textureManager.shotgunFire5;
            else if (weaponState <= 25)
                currentWeapon = textureManager.shotgunFire6;
            else if (weaponState <= 30)
                currentWeapon = textureManager.shotgunFire5;
            else if (weaponState <= 35)
                currentWeapon = textureManager.shotgunFire4;
        }

        int weaponWidth = currentWeapon.getWidth() * scale;
        int weaponHeight = currentWeapon.getHeight() * scale;

        int x = (TextureManager.WIDTH - weaponWidth) / 2;
        int y = TextureManager.HEIGHT - weaponHeight;
        g2d.drawImage(currentWeapon, x, y, weaponWidth, weaponHeight, null);
    }

    public void startFiring() {
        if (!firing) {
            weaponState = 1;
            firing = true;
        }
    }

    public void switchWeapon(int key) {
        currentGun = key;
        weaponState = 0;
    }

    public boolean isFiring() {
        return firing;
    }

    public int getCurrentGun() {
        return currentGun;
    }
}
