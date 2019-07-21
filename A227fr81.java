//<applet code = "A227fr81.class" width = "864" height = "640"> </applet>

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class A227fr81 extends Applet implements KeyListener {

	String maze[] = {
		//012345678901234567890123456
		 "###########################",
		 "# S###  G#####       ##  ##",
		 "# ##### ############ ## ###",
		 "#   ### ###          ## ###",
		 "### ###     ###########   #",
		 "### ####### ######G###### #",
		 "#        ## ###         # #",
		 "###### #### ### ####### # #",
		 "###### ####     ## #### # #",
		 "##     ######## ## ####   #",
		 "## ############ ## #### ###",
		 "## #####        ##      ###",
		 "##       ##### ############",
		 "##### ####     ####   G####",
		 "##    #### ###      #######",
		 "## #####    ####### #######",
		 "## ######## ##    #      ##",
		 "##       ## ## ######### ##",
		 "########G## ##           ##",
		 "###########################",
	};

	Image iwaImg;	//岩画像
	Image shibaImg;	//芝画像
	Image[] characterImg = new Image[12];	//キャラクター画像
	Image goal0Img, goal1Img;	//ゴール画像

	MediaTracker tracker;

	int windowWidth, windowHeight;	//ウィンドウサイズ
	int blockImgWidth, blockImgHeight;	//迷路ブロックサイズ
	int mapWidth, mapHeight;	//マップのサイズ(ブロックサイズ*ブロック数)
	int characterWidth, characterHeight;	//キャラクターサイズ

	int blockCoordinateX, blockCoordinateY;	//ブロック画像の表示位置（座標
	int characterCoordinateX, characterCoordinateY;	//キャラクターの表示位置（座標
	int characterCurrentLocationX, characterCurrentLocationY;	//キャラクターの現在位置

	Image buffImg;	//バッファ

	int characterLoop;	//ループ時に表示するキャラクター番号
	int addLoop;	//キャラクター番号の
	int loopPivot;	//ループ時の基準値キャラクター直立（1, 4, 7, 10）

	int stride;	//1歩で進む距離 今回はブロックサイズと同じ

	int goals;	//ゴールの数
	int[][] goalLocation;	//0:X座標, 1:Y座標, 2:たどり着いたか(0:たどり着いていない 1:たどり着いた)
	int goalNo;	//ゴールの番号

	boolean gameClear;	//ゲーム状態

	public void init() {

		tracker = new MediaTracker(this);
		int trackNo = 0;

		//画像の読み込みとメディアトラッカーへの追加
		iwaImg = getImage(getDocumentBase(), "img/iwa.png");
		tracker.addImage(iwaImg, trackNo ++);
		shibaImg = getImage(getDocumentBase(), "img/shiba.png");
		tracker.addImage(shibaImg, trackNo ++);
		for (int i = 0; i < characterImg.length; i ++) {
			characterImg[i] = getImage(getDocumentBase(), "img/walk/char" + i + ".png");
			tracker.addImage(characterImg[i], trackNo + i);
		}
		goal0Img = getImage(getDocumentBase(), "img/goal0.png");
		tracker.addImage(goal0Img, trackNo ++);
		goal1Img = getImage(getDocumentBase(), "img/goal1.png");
		tracker.addImage(goal1Img, trackNo);
		try {
			tracker.waitForAll();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		//ウィンドウ，ブロック，マップ，キャラクターのサイズを取得
		windowWidth = getWidth();
		windowHeight = getHeight();
		blockImgWidth = iwaImg.getWidth(this);
		blockImgHeight = iwaImg.getHeight(this);
		mapWidth = blockImgWidth * maze[0].length();
		mapHeight = blockImgHeight * maze.length;
		characterWidth = characterImg[0].getWidth(this);
		characterHeight = characterImg[0].getHeight(this);

		//1歩の距離を設定
		stride = blockImgWidth;

		//マップからスタートを探し、キャラクターの初期位置にする
		searchStart:
		for (int i = 0; i < maze.length; i ++) {
			for (int j = 0; j < maze[i].length(); j ++) {
				if (maze[i].charAt(j) == 'S') {
					characterCurrentLocationX = j;
					characterCurrentLocationY = i;
					break searchStart;
				}
			}
		}

		//ゴールの数を数える
		goals = 0;
		for (int i = 0; i < maze.length; i ++) {
			for (int j = 0; j < maze[i].length(); j ++) {
				if (maze[i].charAt(j) == 'G') {
					goals ++;
				}
			}
		}
		goalLocation = new int[goals][3];

		//ゴールの座標を設定
		goalNo = 0;
		for (int i = 0; i < maze.length; i ++) {
			for (int j = 0; j < maze[i].length(); j ++) {
				if (maze[i].charAt(j) == 'G') {
					goalLocation[goalNo][0] = j;
					goalLocation[goalNo][1] = i;
					goalNo ++;
				}
			}
		}

		//ダブルバッファリング用の画像を作成
		buffImg = createImage(windowWidth, windowHeight);

		//キャラクターの初期
		characterLoop = 1;

		//ゲーム状態は当然未クリア
		gameClear = false;

		//キーリスナーを追加
		addKeyListener(this);

	}

	public void update(Graphics g) {

		//更新の際にちらつきを防止するためclearしない
		paint(g);

	}

	public void paint(Graphics g) {

		Graphics gv = buffImg.getGraphics();	//バッファに描画するためのGraphics

		//全体を一度消す
		gv.clearRect(0, 0, windowWidth, windowHeight);
		blockCoordinateX = 0;
		blockCoordinateY = 0;
		goalNo = 0;
		//マップを左上から走査していき、それぞれに合った画像を表示
		//背景画像は座標ベースで表示
		for (int i = 0; i < maze.length; i ++) {
			for (int j = 0; j < maze[i].length(); j ++) {
				if (maze[i].charAt(j) == '#') {
					//'#'のとき岩の画像を表示
					gv.drawImage(iwaImg, blockCoordinateX, blockCoordinateY, this);
				} else {
					gv.drawImage(shibaImg, blockCoordinateX, blockCoordinateY, this);
				}
				//X座標をブロックサイズ分ずらす
				blockCoordinateX += blockImgWidth;
			}
			//1行終わったらX座標を0に戻し、
			blockCoordinateX = 0;
			//Y座標をブロックサイズ分ずらす
			blockCoordinateY += blockImgHeight;
		}
		for (goalNo = 0; goalNo < goals; goalNo ++) {
			//'G'のときゴールの画像を表示
			if (goalLocation[goalNo][2] == 0) {
				//ゴールに一度もたどり着いていない場合
				gv.drawImage(goal0Img, goalLocation[goalNo][0] * blockImgWidth, goalLocation[goalNo][1] * blockImgHeight, this);
			} else {
				//ゴールにたどり着いている場合
				gv.drawImage(goal1Img, goalLocation[goalNo][0] * blockImgWidth, goalLocation[goalNo][1] * blockImgHeight, this);
			}
		}

		//キャラクター位置の変化はブロック単位で行う
		//キャラクター位置を座標に変換
		characterCoordinateX = characterCurrentLocationX * characterWidth;
		characterCoordinateY = characterCurrentLocationY * characterHeight;
		//キャラクターの表示
		gv.drawImage(characterImg[characterLoop], characterCoordinateX, characterCoordinateY, this);

		//バッファに書き込まれた画像をアプレットに描画
		g.drawImage(buffImg, 0, 0, this);

		//ゲームクリアしたらステータスに表示
		if (gameClear == true) {
			showStatus("Game Clear!!");
		}

		gv.dispose();

		requestFocus();

	}

	public void keyPressed(KeyEvent e) {

		int inputKey = e.getKeyCode();

		if (inputKey == KeyEvent.VK_S || inputKey == KeyEvent.VK_DOWN) {
			//Sまたは↓キーが押されたとき
			if (loopPivot != 1) {
				//他の向きを向いている場合
				characterLoop = 0;
			}
			if (characterCoordinateY < mapHeight - stride) {
				//アプレットサイズをオーバーしない
				if (maze[characterCurrentLocationY + 1].charAt(characterCurrentLocationX) != '#') {
					//進む先が壁でない
					characterCurrentLocationY ++;
				}
			}
			loopPivot = 1;
		} else if (inputKey == KeyEvent.VK_A || inputKey == KeyEvent.VK_LEFT) {
			//Aまたは←キーが押されたとき
			if (loopPivot != 4) {
				//他の向きを向いている場合
				characterLoop = 3;
			}
			if (characterCoordinateX > 0) {
				//アプレットサイズをオーバーしない
				if (maze[characterCurrentLocationY].charAt(characterCurrentLocationX - 1) != '#') {
					//進む先が壁でない
					characterCurrentLocationX --;
				}
			}
			loopPivot = 4;
		} else if (inputKey == KeyEvent.VK_W || inputKey == KeyEvent.VK_UP)  {
			//Wまたは↑キーが押されたとき
			if (loopPivot != 7) {
				//他の向きを向いている場合
				characterLoop = 6;
			}
			if (characterCoordinateY > 0) {
				//アプレットサイズをオーバーしない
				if (maze[characterCurrentLocationY - 1].charAt(characterCurrentLocationX) != '#') {
					//進む先が壁でない
					characterCurrentLocationY --;
				}
			}
			loopPivot = 7;
		} else if (inputKey == KeyEvent.VK_D || inputKey == KeyEvent.VK_RIGHT) {
			//Dまたは→キーが押されたとき
			if (loopPivot != 10) {
				//他の向きを向いている場合
				characterLoop = 9;
			}
			if (characterCoordinateX < mapWidth - stride) {
				//アプレットサイズをオーバーしない
				if (maze[characterCurrentLocationY].charAt(characterCurrentLocationX + 1) != '#') {
					//進む先が壁でない
					characterCurrentLocationX ++;
				}
			}
			loopPivot = 10;
		}

		//キャラクターループ
		if (characterLoop < loopPivot) {
			addLoop = 1;
		} else if (characterLoop > loopPivot){
			addLoop = - 1;
		}
		characterLoop += addLoop;

		//ゴールしたかどうかの判定
		for (goalNo = 0; goalNo < goals; goalNo ++) {
			if (goalLocation[goalNo][0] == characterCurrentLocationX && goalLocation[goalNo][1] == characterCurrentLocationY) {
				//goalLocationとcharacterCurrentLocationが一致したとき
				goalLocation[goalNo][2] = 1;
			}
		}

		//ゲームクリアしたかどうかの判定
		for (goalNo = 0; goalNo < goals; goalNo ++) {
			if (goalLocation[goalNo][2] == 0) {
				break;
			}
			if (goalNo == goals - 1 && goalLocation[goalNo][2] == 1) {
				gameClear = true;
			}
		}

		//デバッグ用キャラクター位置の表示
		//System.out.println(characterCurrentLocationX + " " + characterCurrentLocationY);

		//再描画
		repaint();

	}

	public void keyTyped(KeyEvent e) {

	}

	public void keyReleased(KeyEvent e) {

	}

}
