//<applet code = "A227jv86.class" width = "800" height = "600"> </applet>

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class A227jv86 extends Applet implements KeyListener {

	int charWidth, charHeight;	//char画像の大きさ
	int w, h;					//アプレットサイズ
	int x, y;					//キャラクターの座標
	int Stride = 20;	//1歩

	Image[] charImg = new Image[12];
	Image buffImg;

	int charLoop;
	int addLoop;
	int pivot;

	MediaTracker tracker;

	public void init() {

		tracker = new MediaTracker(this);

		for (int i = 0; i < charImg.length; i ++) {
			charImg[i] = getImage(getDocumentBase(), "img/walk/char" + i + ".png");
			tracker.addImage(charImg[i], i);
		}

		try {
			tracker.waitForAll();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		charWidth = charImg[0].getWidth(this);
		charHeight = charImg[0].getHeight(this);

		x = 0;
		y = 0;

		addKeyListener(this);

	}

	public void update(Graphics g) {

		paint(g);

	}

	public void paint(Graphics g) {

		w = getWidth();
		h = getHeight();
		buffImg = createImage(w, h);

		Graphics gv = buffImg.getGraphics();
		
		gv.clearRect(0, 0, w, h);

		//System.out.println(charLoop);
		try{
			Thread.sleep(35);
		}catch (InterruptedException delay) {
			delay.printStackTrace();
		}
		gv.drawImage(charImg[charLoop], x, y, charWidth * 3, charHeight * 3, this);

		g.drawImage(buffImg, 0, 0, this);
		gv.dispose();

		requestFocus();

	}

	public void keyPressed(KeyEvent e) {

		int inputKey = e.getKeyCode();

		if (inputKey == KeyEvent.VK_S || inputKey == KeyEvent.VK_DOWN) {
			if (pivot != 1) {
				charLoop = 0;
			}
			if (y < h - (charHeight * 3 + Stride)) {
				y += Stride;
			}
			pivot = 1;
		} else if (inputKey == KeyEvent.VK_A || inputKey == KeyEvent.VK_LEFT) {
			if (pivot != 4) {
				charLoop = 3;
			}
			if (x > 0) {
				x -= Stride;
			}
			pivot = 4;
		} else if (inputKey == KeyEvent.VK_W || inputKey == KeyEvent.VK_UP)  {
			if (pivot != 7) {
				charLoop = 6;
			}
			if (y > 0) {
				y -= Stride;
			}
			pivot = 7;
		} else if (inputKey == KeyEvent.VK_D || inputKey == KeyEvent.VK_RIGHT) {
			if (pivot != 10) {
				charLoop = 9;
			}
			if (x < w - (charWidth * 3 + Stride)) {
				x += Stride;
			}
			pivot = 10;
		}

		if (charLoop < pivot) {
			addLoop = 1;
		} else if (charLoop > pivot){
			addLoop = - 1;
		}
		charLoop += addLoop;
		
		repaint();

	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
	}

}