@echo off

rem �N���X����
set STUNO=A227

rem appletviewer���đł̖ʓ|�������ˁH

rem �I�v�V�����w��̗L��
if "%1" == "" (
	set option=-help
	goto opt
)
set work=%1
if %work:~0,1% == - (
	set option=%1
	set no=%2
) else (
	set option=null
	set no=%1
)

rem �I�v�V�����̏���
:opt
if %option% == null (
	rem �R���p�C���{���s
	call :compile
	call :run
	goto bye
)
if %option% == -cr (
	rem �R���p�C���{���s
	call :compile
	call :run
	goto bye
)
if %option% == -r (
	echo ���s�̂�
	call :run
	goto bye
)
if %option% == -c (
	echo �R���p�C���̂�
	call :compile
	goto bye
)
if %option% == -e (
	rem bat�t�@�C����ҏW
	notepad .\a.bat
	goto bye
)
if %option% == -help (
	echo �g�p���@:
	echo 	a [-r][-c] �ۑ�ԍ�
	echo 	a [-e] [-help]
	echo.
	echo �I�v�V����:
	echo 	�Ȃ�	: �R���p�C���{���s
	echo 	-cr	: �R���p�C���{���s
	echo 	-r	: ���s�̂�
	echo�@	-c	: �R���p�C���̂�
	echo�@	-e	: bat�t�@�C����ҏW
	echo�@	-help	: ���̃w���v��\��
	echo.
	echo �N���X������ύX�������ꍇ��[-e]��STUNO��ύX���Ă�������
	goto bye
)

rem �ȉ��̓R���p�C���Ǝ��s�̃T�u���[�`��

rem �R���p�C��
:compile
if exist %STUNO%%no%.java (
	echo �R���p�C�����c
	javac %STUNO%%no%.java
) else (
	rem error:%STUNO%%no%.java��������܂���
	echo error:�ۑ�ԍ��ԈႦ�Ă˂����H
	goto bye
)
exit /b

rem ���s
:run
if exist %STUNO%%no%.class (
	for %%i in (%STUNO%%no%.class) do (
		echo class�̍ŏI�X�V %%~ti
	)
	echo ���s���c
	appletviewer %STUNO%%no%.java
) else (
	rem error:class�t�@�C����������܂���
	echo error:�Ȃ񂩃~�X���Ă񂼁i����
)
exit /b

:bye