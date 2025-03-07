import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.Thread;



public class MapView extends Thread implements KeyListener
{

    private JFrame frame;
    protected JPanel gamePanel, menuPanel;
    private ImageIcon image;
    public JLabel ship, heart1, heart2, heart3, pointLabel;
    private Spawners spawn;
    protected JButton startButton, exitButton;
    protected int sgopa, points;


    public MapView()
    {
        startUp();
        spawn = new Spawners(this);
        spawn.allowBullets = false;
        spawn.planetRun = false;
        sgopa = 0;
        points = 0;

    }


    public void run()
    {
        while (true)
        {

        }
    }



    private void startUp()
    {

        frame = new JFrame("Testing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200,800);
        frame.setLayout(null);
        frame.setVisible(true);
        frame.addKeyListener(this);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.addKeyListener(this);

        gamePanel = new JPanel();
        gamePanel.setBackground(Color.black);
        gamePanel.setBounds(0,0,800,800);
        gamePanel.setLayout(null);
        frame.add(gamePanel);

        setUpRocketShip();

        setUpMenu();
    }


    @Override
    public void keyTyped(KeyEvent e)
    {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A:
                ship.setLocation(ship.getX() - 10, ship.getY());
                break;

            case KeyEvent.VK_D:
                ship.setLocation(ship.getX() + 10, ship.getY());
                break;

            case KeyEvent.VK_F:
                spawn.shoot();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {

    }



    private void setUpRocketShip()
    {

        image = new ImageIcon("src/Rocket_Ship.png");
        Image img = image.getImage();
        Image scaledImage = img.getScaledInstance(70, 90, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);


        ship = new JLabel();
        ship.setBounds(350,580,150,150);
        ship.setBackground(Color.BLACK);
        ship.setIcon(scaledIcon);
        gamePanel.add(ship);

        gamePanel.revalidate();
        gamePanel.repaint();
    }

    public void setUpPoint()
    {
        pointLabel = new JLabel("Points: " + points);
        pointLabel.setBounds(130, 100, 300,150);
        pointLabel.setFont(new Font("Calibri", Font.CENTER_BASELINE, 30));
        menuPanel.add(pointLabel);

        menuPanel.revalidate();
        menuPanel.repaint();

    }

    public void addPoints()
    {
        points = points + 10;
        pointLabel.setText("Points: " + points);
    }

    public void resetPoints()
    {
        points = 0;
        pointLabel.setText("Points: " + points);
    }

    private void setUpMenu()
    {
        menuPanel = new JPanel();
        menuPanel.setBackground(Color.GRAY);
        menuPanel.setLayout(null);
        menuPanel.setBounds(810,0,365,800);
        menuPanel.setLayout(null);
        frame.add(menuPanel);
        frame.revalidate();
        frame.repaint();

        startButton = new JButton("Start Game");
        startButton.setBounds(20, 650, 150, 50);
        menuPanel.add(startButton);
        startButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (sgopa == 0)
                {
                    startButton.setEnabled(false);
                    spawn.allowBullets = true;
                    spawn.planetRun = true;
                    sgopa = 1;
                    startGameThread();
                    spawn.start();
                    setUpLives();
                    frame.requestFocusInWindow();
                }

                else if (sgopa == 1)
                {
                    startButton.setEnabled(false);
                    spawn.allowBullets = true;
                    spawn.planetRun = true;
                    setNewSpawners();
                    spawn.start();
                    frame.requestFocusInWindow();
                    resetShip();
                    setUpLives();
                    frame.addKeyListener(MapView.this);
                }
            }
        });

        exitButton = new JButton("Exit");
        exitButton.setBounds(200, 650, 150, 50);
        menuPanel.add(exitButton);

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        setUpPoint();

        menuPanel.revalidate();
        menuPanel.repaint();

    }



    protected boolean detectCollisionWithBullets(int x, int y, JLabel planet) {
        if ((x <= planet.getX() + 90) && (x >= planet.getX()) && (y <= planet.getY() + 90) && (y >= planet.getY())) {
            System.out.println(planet.getX() + " x bullet");
            return true;
        }
        return false;
    }

    protected boolean detectCollisionWithShip(int x, int y, JLabel planet) {
        if ((x <= planet.getX() + 90) && (x >= planet.getX()) && (y <= planet.getY() + 90) && (y >= planet.getY())) {
            System.out.println(planet.getX() + " x ship");
            return true;
        }
        return false;
    }


    private void startGameThread()
    {
        this.start();
    }

    private void setNewSpawners()
    {
        spawn = new Spawners(this);
    }


    private void resetShip()
    {
        if (ship != null)
        {
            gamePanel.remove(ship);
        }

        setUpRocketShip();
    }


    protected void setImage(String fileName, JLabel label, int width, int height)
    {
        ImageIcon image2 = new ImageIcon(fileName);
        Image img = image2.getImage();
        Image scaledImage = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon2 = new ImageIcon(scaledImage);

        label.setIcon(scaledIcon2);
        System.out.println("sätter blitd????");
    }





    private void setUpLives() {
        // hearts 1
        heart1 = new JLabel();
        System.out.println(heart1.getX());
        heart1.setBounds(60, 50, 80, 80);
        heart1.setBackground(Color.gray);
        setImage("src/redHeart.png", heart1, 80, 80);
        heart1.setOpaque(true);

        menuPanel.add(heart1);
        menuPanel.revalidate();
        menuPanel.repaint();
        System.out.println("Heart added at " + heart1.getX() + ", " + heart1.getY());



        //hearts 2
        heart2 = new JLabel();
        System.out.println(heart1.getX());
        heart2.setBounds(160, 50, 80, 80);
        heart2.setBackground(Color.gray);
        setImage("src/redHeart.png", heart2, 80, 80);
        heart2.setOpaque(true);

        menuPanel.add(heart2);
        menuPanel.revalidate();
        menuPanel.repaint();
        System.out.println("Heart added at " + heart2.getX() + ", " + heart2.getY());


        heart3 = new JLabel();
        System.out.println(heart1.getX());
        heart3.setBounds(260, 50, 80, 80);
        heart3.setBackground(Color.gray);
        setImage("src/redHeart.png", heart3, 80, 80);
        heart3.setOpaque(true);

        menuPanel.add(heart3);
        menuPanel.revalidate();
        menuPanel.repaint();
        System.out.println("Heart added at " + heart3.getX() + ", " + heart3.getY());


        System.out.println("bilderna redo");
    }




}