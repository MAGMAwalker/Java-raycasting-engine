package ThreeDGame;

public class Particle {
    double x, y, z = 0;
    double dx, dy, dz = 0; // Remove vy, use dz for height
    double gravity = 0.02;
    double life;

    public Particle(double x, double y){
        this.x = x;
        this.y = y;
        
        double angle = Math.random() * Math.PI * 2;
        double speed = Math.random() * 0.1;

        dx = Math.cos(angle) * speed;
        dy = Math.sin(angle) * speed;

        // JUMP: Start with a negative upward speed (-0.05)
        dz = -0.05 - (Math.random() * 0.05); 

        life = 1.0;
    }

    public void update(){
        x += dx;
        y += dy;

        z += dz;
        dz += gravity; // Gravity pulls it back down

        dx *= 0.9;
        dy *= 0.9;

        life -= 0.05; // Made it smaller so you can see the jump before it dies
    }

    public boolean isDead(){
        return life <= 0;
    }
}
