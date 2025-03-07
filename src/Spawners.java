import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;
 
public class Spawners extends Thread {
 
    private MapView mv;
    protected ImageIcon image, scaledIcon;
    private Random rand;
    private List<JLabel> planets;
    protected volatile boolean planetRun, allowBullets;
    protected int lives = 3;
    public HashMap<JLabel, Boolean> jlabels = new HashMap<>();

    public Spawners(MapView mv)
    {
        this.mv = mv;
        setImage("src/planetToDestroy.png");
        rand = new Random();
        planets = new ArrayList<>();
        planetRun = true;
        allowBullets = true;
    }

    public void run()
    {
        int minTime = 1000;
        int maxTime = 2000;
        int randomPLanetSpawnTime = rand.nextInt(maxTime - minTime + 1) + minTime;

        while (planetRun)
        {
            try
            {
                spawnPlanet();
                Thread.sleep(randomPLanetSpawnTime);
            }
            catch (Exception e)
            {
                System.out.println(e);
            }
        }
    }

    public void spawnPlanet() {
        Thread planetThread = new Thread(() -> {
            int minPosition = 150;
            int maxPosition = 600;
            int randomPosition = rand.nextInt(maxPosition - minPosition + 1) + minPosition;
            JLabel planet = new JLabel();
            planet.setBounds(randomPosition, -50, 90, 90);
            planet.setBackground(Color.BLACK);
            planet.setIcon(scaledIcon);
            planets.add(planet);
            mv.gamePanel.add(planet);

            int id = jlabels.size();
            jlabels.put(planet, false);
            while (planet.getY() < 810 && !jlabels.get(planet) && planetRun ) {
                try {
                    planet.setLocation(planet.getX(), planet.getY() + 5);
                    Thread.sleep(50);

                    // Check collision with ship
                    if (mv.detectCollisionWithShip(mv.ship.getX(), mv.ship.getY(), planet)) {
                        System.out.println(planet.getX() + " x ");
                        System.out.println("Collision with ship");
                        kaboom(mv.ship);
                        removePlanet(planet);
                        planets.remove(planet);
                        jlabels.replace(planet, true);
                        mv.resetPoints();
                        gameOver();
                        break;
                    }
                } catch (InterruptedException e) {
                    System.out.println("Planet thread interrupted");
                    planetRun = false;
                } catch (Exception e) {
                    System.out.println("Error in the spawners class: " + e);
                }
            }

            if(!jlabels.get(planet)) {
                lives = lives - 1;
                looseHP();
                planets.remove(planet);
                removePlanet(planet);
            }

            jlabels.remove(planet);
            System.out.println("planet removed from hashMap");
            mv.gamePanel.revalidate();
            mv.gamePanel.repaint();
        });

        planetThread.start();
    }

    private void setImage(String fileName)
    {
        image = new ImageIcon(fileName);
        Image img = image.getImage();
        Image scaledImage = img.getScaledInstance(90, 90, Image.SCALE_SMOOTH);
        scaledIcon = new ImageIcon(scaledImage);
    }

    public void shoot() {
        Thread shootingThread = new Thread(() -> {
            JLabel shootingLabel = new JLabel();
            shootingLabel.setBounds(mv.ship.getX() + 24, mv.ship.getY() + 15, 20, 20);
            shootingLabel.setBackground(Color.YELLOW);
            shootingLabel.setOpaque(true);
            mv.gamePanel.add(shootingLabel);
            mv.gamePanel.revalidate();
            mv.gamePanel.repaint();
            boolean hit;

            if(allowBullets) {playSound("src\\pew.wav");}

            try
            {
                hit = false;
                while (shootingLabel.getY() > -10 && !hit && allowBullets) {
                    shootingLabel.setLocation(shootingLabel.getX(), shootingLabel.getY() - 5);

                    // Check collision with any planet
                    if (planets != null) {
                        for (JLabel planet : planets) {
                            if (mv.detectCollisionWithBullets(shootingLabel.getX(), shootingLabel.getY(), planet)) {
                                System.out.println(planet.getX() + " x");
                                System.out.println("Collision");
                                kaboom(planet);
                                mv.gamePanel.remove(shootingLabel);
                                mv.gamePanel.remove(planet);
                                jlabels.replace(planet, true);
                                planets.remove(planet);
                                mv.addPoints();
                                hit = true;
                                mv.sgopa = 1;
                                break;
                            }
                        }
                    }

                    lives = lives - 1;

                    Thread.sleep(5);
                }
                mv.gamePanel.remove(shootingLabel);
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
            }

            mv.gamePanel.remove(shootingLabel);
            mv.gamePanel.revalidate();
            mv.gamePanel.repaint();
        });

        shootingThread.start();
    }

    public static void playSound(String fileName) {
        try {
            File sound = new File(fileName);
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(sound));
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void kaboom(JLabel planet)
    {
        try
        {
            playSound("src/explosion_old.wav");
            ImageIcon image2 = new ImageIcon("src/planetBoom1.png");
            Image img2 = image2.getImage();
            Image scaledImage = img2.getScaledInstance(90, 90, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon2 = new ImageIcon(scaledImage);
            planet.setIcon(scaledIcon2);

            Thread.sleep(50);

            ImageIcon image3 = new ImageIcon("src/planetBoom1.png");
            Image img3 = image3.getImage();
            Image scaledImage2 = img3.getScaledInstance(90, 90, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon3 = new ImageIcon(scaledImage2);
            planet.setIcon(scaledIcon3);
            Thread.sleep(50);

            planet.setLocation(planet.getX(), planet.getY());
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public synchronized void stopAllPlanets() {
        Iterator<JLabel> iterator = planets.iterator();
        while (iterator.hasNext()) {
            JLabel planet = iterator.next();
            removePlanet(planet);
            iterator.remove();  // Safe removal during iteration
        }
    }

    private void removePlanet(JLabel planet)
    {
        SwingUtilities.invokeLater(() ->{
            mv.gamePanel.remove(planet);
            mv.gamePanel.revalidate();
            mv.gamePanel.repaint();
        });
    }



    protected void gameOver() {
        System.out.println("entered game over");
        synchronized (this) {
            if (!planetRun) return;
            planetRun = false;
            allowBullets = false;
            stopAllPlanets();
            playSound("src/gameovr.wav");
            JOptionPane.showMessageDialog(null, "Game Over!");
            mv.sgopa = 1;
            mv.startButton.setText("Play Again!");
            mv.startButton.setEnabled(true);
            mv.points = 0;
            mv.gamePanel.revalidate();
            mv.gamePanel.repaint();
        }

    }


    protected void looseHP()
    {
        if(lives == 2)
        {
            playSound("src/bruh.wav");
            mv.setImage("src/Black-Heart-3.png", mv.heart3, 50, 50);
            System.out.println("lost hp");
        }

        else if (lives == 1)
        {
            playSound("src/bruh.wav");
            mv.setImage("src/Black-Heart-3.png", mv.heart2, 50, 50);

        }

        else if(lives == 0)
        {
            playSound("src/bruh.wav");
            mv.setImage("src/Black-Heart-3.png", mv.heart1, 50, 50);
            gameOver();
        }

    }

    private synchronized void decrementLives() {
        if (lives > 0) {
            lives--;
            System.out.println("Removed planet");
            System.out.println("lives left: " + lives);
            looseHP();
        }
        if (lives <= 0) {
            mv.sgopa = 1;
            mv.resetPoints();
            mv.startButton.setText("Play Again");
            mv.startButton.setEnabled(true);
            gameOver();
        }
    }

}
