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

	Image iwaImg;	//��摜
	Image shibaImg;	//�ŉ摜
	Image[] characterImg = new Image[12];	//�L�����N�^�[�摜
	Image goal0Img, goal1Img;	//�S�[���摜

	MediaTracker tracker;

	int windowWidth, windowHeight;	//�E�B���h�E�T�C�Y
	int blockImgWidth, blockImgHeight;	//���H�u���b�N�T�C�Y
	int mapWidth, mapHeight;	//�}�b�v�̃T�C�Y(�u���b�N�T�C�Y*�u���b�N��)
	int characterWidth, characterHeight;	//�L�����N�^�[�T�C�Y

	int blockCoordinateX, blockCoordinateY;	//�u���b�N�摜�̕\���ʒu�i���W
	int characterCoordinateX, characterCoordinateY;	//�L�����N�^�[�̕\���ʒu�i���W
	int characterCurrentLocationX, characterCurrentLocationY;	//�L�����N�^�[�̌��݈ʒu

	Image buffImg;	//�o�b�t�@

	int characterLoop;	//���[�v���ɕ\������L�����N�^�[�ԍ�
	int addLoop;	//�L�����N�^�[�ԍ���
	int loopPivot;	//���[�v���̊�l�L�����N�^�[�����i1, 4, 7, 10�j

	int stride;	//1���Ői�ދ��� ����̓u���b�N�T�C�Y�Ɠ���

	int goals;	//�S�[���̐�
	int[][] goalLocation;	//0:X���W, 1:Y���W, 2:���ǂ蒅������(0:���ǂ蒅���Ă��Ȃ� 1:���ǂ蒅����)
	int goalNo;	//�S�[���̔ԍ�

	boolean gameClear;	//�Q�[�����

	public void init() {

		tracker = new MediaTracker(this);
		int trackNo = 0;

		//�摜�̓ǂݍ��݂ƃ��f�B�A�g���b�J�[�ւ̒ǉ�
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

		//�E�B���h�E�C�u���b�N�C�}�b�v�C�L�����N�^�[�̃T�C�Y���擾
		windowWidth = getWidth();
		windowHeight = getHeight();
		blockImgWidth = iwaImg.getWidth(this);
		blockImgHeight = iwaImg.getHeight(this);
		mapWidth = blockImgWidth * maze[0].length();
		mapHeight = blockImgHeight * maze.length;
		characterWidth = characterImg[0].getWidth(this);
		characterHeight = characterImg[0].getHeight(this);

		//1���̋�����ݒ�
		stride = blockImgWidth;

		//�}�b�v����X�^�[�g��T���A�L�����N�^�[�̏����ʒu�ɂ���
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

		//�S�[���̐��𐔂���
		goals = 0;
		for (int i = 0; i < maze.length; i ++) {
			for (int j = 0; j < maze[i].length(); j ++) {
				if (maze[i].charAt(j) == 'G') {
					goals ++;
				}
			}
		}
		goalLocation = new int[goals][3];

		//�S�[���̍��W��ݒ�
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

		//�_�u���o�b�t�@�����O�p�̉摜���쐬
		buffImg = createImage(windowWidth, windowHeight);

		//�L�����N�^�[�̏���
		characterLoop = 1;

		//�Q�[����Ԃ͓��R���N���A
		gameClear = false;

		//�L�[���X�i�[��ǉ�
		addKeyListener(this);

	}

	public void update(Graphics g) {

		//�X�V�̍ۂɂ������h�~���邽��clear���Ȃ�
		paint(g);

	}

	public void paint(Graphics g) {

		Graphics gv = buffImg.getGraphics();	//�o�b�t�@�ɕ`�悷�邽�߂�Graphics

		//�S�̂���x����
		gv.clearRect(0, 0, windowWidth, windowHeight);
		blockCoordinateX = 0;
		blockCoordinateY = 0;
		goalNo = 0;
		//�}�b�v�����ォ�瑖�����Ă����A���ꂼ��ɍ������摜��\��
		//�w�i�摜�͍��W�x�[�X�ŕ\��
		for (int i = 0; i < maze.length; i ++) {
			for (int j = 0; j < maze[i].length(); j ++) {
				if (maze[i].charAt(j) == '#') {
					//'#'�̂Ƃ���̉摜��\��
					gv.drawImage(iwaImg, blockCoordinateX, blockCoordinateY, this);
				} else {
					gv.drawImage(shibaImg, blockCoordinateX, blockCoordinateY, this);
				}
				//X���W���u���b�N�T�C�Y�����炷
				blockCoordinateX += blockImgWidth;
			}
			//1�s�I�������X���W��0�ɖ߂��A
			blockCoordinateX = 0;
			//Y���W���u���b�N�T�C�Y�����炷
			blockCoordinateY += blockImgHeight;
		}
		for (goalNo = 0; goalNo < goals; goalNo ++) {
			//'G'�̂Ƃ��S�[���̉摜��\��
			if (goalLocation[goalNo][2] == 0) {
				//�S�[���Ɉ�x�����ǂ蒅���Ă��Ȃ��ꍇ
				gv.drawImage(goal0Img, goalLocation[goalNo][0] * blockImgWidth, goalLocation[goalNo][1] * blockImgHeight, this);
			} else {
				//�S�[���ɂ��ǂ蒅���Ă���ꍇ
				gv.drawImage(goal1Img, goalLocation[goalNo][0] * blockImgWidth, goalLocation[goalNo][1] * blockImgHeight, this);
			}
		}

		//�L�����N�^�[�ʒu�̕ω��̓u���b�N�P�ʂōs��
		//�L�����N�^�[�ʒu�����W�ɕϊ�
		characterCoordinateX = characterCurrentLocationX * characterWidth;
		characterCoordinateY = characterCurrentLocationY * characterHeight;
		//�L�����N�^�[�̕\��
		gv.drawImage(characterImg[characterLoop], characterCoordinateX, characterCoordinateY, this);

		//�o�b�t�@�ɏ������܂ꂽ�摜���A�v���b�g�ɕ`��
		g.drawImage(buffImg, 0, 0, this);

		//�Q�[���N���A������X�e�[�^�X�ɕ\��
		if (gameClear == true) {
			showStatus("Game Clear!!");
		}

		gv.dispose();

		requestFocus();

	}

	public void keyPressed(KeyEvent e) {

		int inputKey = e.getKeyCode();

		if (inputKey == KeyEvent.VK_S || inputKey == KeyEvent.VK_DOWN) {
			//S�܂��́��L�[�������ꂽ�Ƃ�
			if (loopPivot != 1) {
				//���̌����������Ă���ꍇ
				characterLoop = 0;
			}
			if (characterCoordinateY < mapHeight - stride) {
				//�A�v���b�g�T�C�Y���I�[�o�[���Ȃ�
				if (maze[characterCurrentLocationY + 1].charAt(characterCurrentLocationX) != '#') {
					//�i�ސ悪�ǂłȂ�
					characterCurrentLocationY ++;
				}
			}
			loopPivot = 1;
		} else if (inputKey == KeyEvent.VK_A || inputKey == KeyEvent.VK_LEFT) {
			//A�܂��́��L�[�������ꂽ�Ƃ�
			if (loopPivot != 4) {
				//���̌����������Ă���ꍇ
				characterLoop = 3;
			}
			if (characterCoordinateX > 0) {
				//�A�v���b�g�T�C�Y���I�[�o�[���Ȃ�
				if (maze[characterCurrentLocationY].charAt(characterCurrentLocationX - 1) != '#') {
					//�i�ސ悪�ǂłȂ�
					characterCurrentLocationX --;
				}
			}
			loopPivot = 4;
		} else if (inputKey == KeyEvent.VK_W || inputKey == KeyEvent.VK_UP)  {
			//W�܂��́��L�[�������ꂽ�Ƃ�
			if (loopPivot != 7) {
				//���̌����������Ă���ꍇ
				characterLoop = 6;
			}
			if (characterCoordinateY > 0) {
				//�A�v���b�g�T�C�Y���I�[�o�[���Ȃ�
				if (maze[characterCurrentLocationY - 1].charAt(characterCurrentLocationX) != '#') {
					//�i�ސ悪�ǂłȂ�
					characterCurrentLocationY --;
				}
			}
			loopPivot = 7;
		} else if (inputKey == KeyEvent.VK_D || inputKey == KeyEvent.VK_RIGHT) {
			//D�܂��́��L�[�������ꂽ�Ƃ�
			if (loopPivot != 10) {
				//���̌����������Ă���ꍇ
				characterLoop = 9;
			}
			if (characterCoordinateX < mapWidth - stride) {
				//�A�v���b�g�T�C�Y���I�[�o�[���Ȃ�
				if (maze[characterCurrentLocationY].charAt(characterCurrentLocationX + 1) != '#') {
					//�i�ސ悪�ǂłȂ�
					characterCurrentLocationX ++;
				}
			}
			loopPivot = 10;
		}

		//�L�����N�^�[���[�v
		if (characterLoop < loopPivot) {
			addLoop = 1;
		} else if (characterLoop > loopPivot){
			addLoop = - 1;
		}
		characterLoop += addLoop;

		//�S�[���������ǂ����̔���
		for (goalNo = 0; goalNo < goals; goalNo ++) {
			if (goalLocation[goalNo][0] == characterCurrentLocationX && goalLocation[goalNo][1] == characterCurrentLocationY) {
				//goalLocation��characterCurrentLocation����v�����Ƃ�
				goalLocation[goalNo][2] = 1;
			}
		}

		//�Q�[���N���A�������ǂ����̔���
		for (goalNo = 0; goalNo < goals; goalNo ++) {
			if (goalLocation[goalNo][2] == 0) {
				break;
			}
			if (goalNo == goals - 1 && goalLocation[goalNo][2] == 1) {
				gameClear = true;
			}
		}

		//�f�o�b�O�p�L�����N�^�[�ʒu�̕\��
		//System.out.println(characterCurrentLocationX + " " + characterCurrentLocationY);

		//�ĕ`��
		repaint();

	}

	public void keyTyped(KeyEvent e) {

	}

	public void keyReleased(KeyEvent e) {

	}

}
