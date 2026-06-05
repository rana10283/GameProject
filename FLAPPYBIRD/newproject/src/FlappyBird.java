import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {

    int boardWidth = 360;
    int boardHeight = 640;

    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    int birdX = boardWidth / 8;
    int birdY = boardHeight / 2;
    int birdWidth = 34;
    int birdHeight = 24;

    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    Bird bird;
    ArrayList<Pipe> pipes;

    int velocityX = -4;
    int velocityY = 0;
    int gravity = 1;

    Timer gameLoop;
    Timer placePipeTimer;

    boolean gameOver = false;
    double score = 0;

    public FlappyBird() {

        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        backgroundImg = new ImageIcon(getClass().getResource("flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("bottompipe.png")).getImage();

        bird = new Bird(
                birdX,
                birdY,
                birdWidth,
                birdHeight,
                birdImg);

        pipes = new ArrayList<>();

        placePipeTimer = new Timer(1500, e -> placePipes());
        placePipeTimer.start();

        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();
    }

    public void placePipes() {

        int randomPipeY =
                (int) (pipeY - pipeHeight / 4
                        - Math.random() * (pipeHeight / 2));

        int openingSpace = boardHeight / 4;

        Pipe topPipe = new Pipe(
                pipeX,
                randomPipeY,
                pipeWidth,
                pipeHeight,
                topPipeImg);

        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(
                pipeX,
                topPipe.getY() + pipeHeight + openingSpace,
                pipeWidth,
                pipeHeight,
                bottomPipeImg);

        pipes.add(bottomPipe);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {

        g.drawImage(
                backgroundImg,
                0,
                0,
                boardWidth,
                boardHeight,
                null);

        g.drawImage(
                bird.getImg(),
                bird.getX(),
                bird.getY(),
                bird.getWidth(),
                bird.getHeight(),
                null);

        for (Pipe pipe : pipes) {

            g.drawImage(
                    pipe.getImg(),
                    pipe.getX(),
                    pipe.getY(),
                    pipe.getWidth(),
                    pipe.getHeight(),
                    null);
        }

        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.PLAIN, 32));

        if (gameOver) {
            g.drawString(
                    "Game Over: " + (int) score,
                    10,
                    35);
        } else {
            g.drawString(
                    "Score: " + (int) score,
                    10,
                    35);
        }
    }

    public void move() {

        velocityY += gravity;

        bird.setY(
                bird.getY() + velocityY);

        bird.setY(
                Math.max(bird.getY(), 0));

        for (Pipe pipe : pipes) {

            pipe.setX(
                    pipe.getX() + velocityX);

            if (!pipe.isPassed()
                    && bird.getX()
                    > pipe.getX()
                    + pipe.getWidth()) {

                score += 0.5;
                pipe.setPassed(true);
            }

            if (collision(bird, pipe)) {
                gameOver = true;
            }
        }

        if (bird.getY() > boardHeight) {
            gameOver = true;
        }
    }

    public boolean collision(Bird a, Pipe b) {

        return a.getX() < b.getX() + b.getWidth()
                && a.getX() + a.getWidth() > b.getX()
                && a.getY() < b.getY() + b.getHeight()
                && a.getY() + a.getHeight() > b.getY();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        move();
        repaint();

        if (gameOver) {
            gameLoop.stop();
            placePipeTimer.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_SPACE) {

            velocityY = -9;

            if (gameOver) {

                bird.setY(birdY);

                velocityY = 0;
                score = 0;

                pipes.clear();

                gameOver = false;

                gameLoop.start();
                placePipeTimer.start();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}