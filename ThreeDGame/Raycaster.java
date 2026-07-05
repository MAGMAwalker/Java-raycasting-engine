package ThreeDGame;

import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;

public class Raycaster {
    private TextureManager textureManager;
    private double[] depthBuffer;

    public Raycaster(TextureManager textureManager, double[] depthBuffer) {
        this.textureManager = textureManager;
        this.depthBuffer = depthBuffer;
    }  

    private void renderFloorAndCeiling(BufferedImage screenBuffer) {
        // Render the floor and ceiling one horizontal row at a time,
        // starting from the horizon and moving toward the bottom.
        for (int y = TextureManager.HEIGHT / 2; y < TextureManager.HEIGHT; y++) {
            // Calculate the perpendicular distance from the camera
            // to the current floor row.
            int p = y - TextureManager.HEIGHT / 2;
            if (p == 0)
                p = 1;
            double straightDist = (double) TextureManager.HEIGHT / (2.0 * p);
            // Compute the left and right viewing rays for this row.
            // Every floor pixel on this row lies somewhere between them.
            double rayDirX0 = Math.cos(Camera.angle - Camera.fov / 2);
            double rayDirY0 = Math.sin(Camera.angle - Camera.fov / 2);
            double rayDirX1 = Math.cos(Camera.angle + Camera.fov / 2);
            double rayDirY1 = Math.sin(Camera.angle + Camera.fov / 2);
            // Calculate how much the floor position changes
            // as we move one pixel across the screen.
            double rowStepX = straightDist * (rayDirX1 - rayDirX0) / TextureManager.WIDTH;
            double rowStepY = straightDist * (rayDirY1 - rayDirY0) / TextureManager.WIDTH;
            // Determine the world position of the first floor pixel on the left side of the screen.
            double floorX = Camera.x + straightDist * rayDirX0;
            double floorY = Camera.y + straightDist * rayDirY0;

            // Draw every pixel across the current floor row.
            for (int x = 0; x < TextureManager.WIDTH; x++) {
                // Find the map cell containing the current floor position.
                int cellX = (int) floorX;
                int cellY = (int) floorY;
                // Extract the fractional position inside the map cell.
                // These values are used to sample the floor texture.
                double fractX = floorX - cellX;
                double fractY = floorY - cellY;

                // Convert the fractional coordinates into texture coordinates.
                int tx = (int) (textureManager.floorTextureWidth * fractX) % textureManager.floorTextureWidth;
                int ty = (int) (textureManager.floorTextureHeight * fractY) % textureManager.floorTextureHeight;
                if (tx < 0)
                    tx += textureManager.floorTextureWidth;
                if (ty < 0)
                    ty += textureManager.floorTextureHeight;

                // Draw the floor pixel.
                if (textureManager.floorTexture != null) {
                    screenBuffer.setRGB(x, y, textureManager.floorTexture.getRGB(tx, ty));
                }

                // Draw the mirrored ceiling pixel using the same texture coordinates.
                if (textureManager.ceilingTexture != null) {
                    screenBuffer.setRGB(x, TextureManager.HEIGHT - y - 1, textureManager.ceilingTexture.getRGB(tx, ty));
                }

                // Move to the next floor position for the next screen pixel.
                floorX += rowStepX;
                floorY += rowStepY;
            }
        }
    }

    protected void draw3DScene(Graphics g, int[][] map, BufferedImage screenBuffer) {
        Graphics2D g2d = (Graphics2D) g;

        // Draw the textured floor and ceiling first.
        renderFloorAndCeiling(screenBuffer);

        int mapWidth = map[0].length;
        int mapHeight = map.length;

        // Main raycasting loop, cast one ray for each pixel of the screen.
        for (int i = 0; i < TextureManager.WIDTH; i++) {

            //-----------------------------------------------------------
            // Calculate the ray direction for this screen column.
            //-----------------------------------------------------------

            double rayAngle = (Camera.angle - Camera.fov / 2) + ((double) i / (double) TextureManager.WIDTH) * Camera.fov;
            double rayX = Math.cos(rayAngle);
            double rayY = Math.sin(rayAngle);

            //-----------------------------------------------------------
            // Initialize the ray's start position inside the map.
            //-----------------------------------------------------------

            int mapX = (int) Camera.x;
            int mapY = (int) Camera.y;

            //-----------------------------------------------------------
            // Calculate how far the ray travels to cross one grid cell
            // along the X and Y axes.
            //-----------------------------------------------------------

            double deltaDistX = (rayX == 0) ? Double.MAX_VALUE : Math.abs(1 / rayX);
            double deltaDistY = (rayY == 0) ? Double.MAX_VALUE : Math.abs(1 / rayY);

            //-----------------------------------------------------------
            // Determine the ray's step direction and initial distance to
            // the first vertical and horizontal grid boundaries.
            //-----------------------------------------------------------

            double sideDistX;
            double sideDistY;
            int stepX;
            int stepY;

            if (rayX < 0) {
                stepX = -1;
                sideDistX = (Camera.x - mapX) * deltaDistX;
            } else {
                stepX = 1;
                sideDistX = (mapX + 1.0 - Camera.x) * deltaDistX;
            }
            if (rayY < 0) {
                stepY = -1;
                sideDistY = (Camera.y - mapY) * deltaDistY;
            } else {
                stepY = 1;
                sideDistY = (mapY + 1.0 - Camera.y) * deltaDistY;
            }

            boolean hitWall = false;
            int side = 0;
            double maxDepth = 16.0;
            double distanceToWall = 0;

            //-----------------------------------------------------------
            // Perform DDA (Digital Differential Analysis).
            // Step through the grid until a wall is hit.
            //-----------------------------------------------------------

            while (!hitWall && distanceToWall < maxDepth) {
                if (sideDistX < sideDistY) {
                    distanceToWall = sideDistX;
                    sideDistX += deltaDistX;
                    mapX += stepX;
                    side = 0;
                } else {
                    distanceToWall = sideDistY;
                    sideDistY += deltaDistY;
                    mapY += stepY;
                    side = 1;
                }

                if (mapX < 0 || mapX >= mapWidth || mapY < 0 || mapY >= mapHeight) {
                    break;
                }

                if (map[mapY][mapX] > 0) {
                    hitWall = true;
                }
            }

            //-----------------------------------------------------------
            // A wall is hit. Compute where the ray hit the wall.
            // Remove fisheye distortion, and determine the wall slice.
            //-----------------------------------------------------------

            if (hitWall) {  
                // Calculate the exact position where the ray hit the wall.
                // This will later be used to determine the correct texture column. 
                double wallXCoord;
                if (side == 0) {
                    wallXCoord = Camera.y + distanceToWall * rayY;
                } else {
                    wallXCoord = Camera.x + distanceToWall * rayX;
                }
                wallXCoord -= Math.floor(wallXCoord);
                // Correct the distance to remove the fisheye effect and
                // store it in the depth buffer for sprite rendering.
                distanceToWall = distanceToWall * Math.cos(rayAngle - Camera.angle);
                if (distanceToWall < 0.1)
                    distanceToWall = 0.1;
                depthBuffer[i] = distanceToWall;
                // Calculate the height of the wall slice on screen and
                // determine which texture column should be sampled.
                int wallHeight = (int) (TextureManager.HEIGHT / distanceToWall);
                int ceiling = TextureManager.HEIGHT / 2 - wallHeight / 2;
                int floor = TextureManager.HEIGHT / 2 + wallHeight / 2;
                // Determine which vertical column of the wall texture should be sampled.
                int texX = (int) (wallXCoord * (double) textureManager.textureWidth);
                if (side == 0 && rayX > 0)
                    texX = textureManager.textureWidth - texX - 1;
                if (side == 1 && rayY < 0)
                    texX = textureManager.textureWidth - texX - 1;
                texX = Math.max(0, Math.min(textureManager.textureWidth - 1, texX));
                //Draw one vertical textured wall slice.
                if (textureManager.wallTexture != null) {
                    g2d.drawImage(
                            textureManager.wallTexture,
                            i, ceiling, i + 1, floor,
                            texX, 0, texX + 1, textureManager.textureHeight,
                            null);
                } else {
                    g2d.setColor(Color.RED);
                    g2d.drawLine(i, ceiling, i, floor);
                }
            }
        }
    }
}
