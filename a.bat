@echo off

rem クラス氏名
set STUNO=A227

rem appletviewerって打つの面倒くさくね？

rem オプション指定の有無
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

rem オプションの処理
:opt
if %option% == null (
	rem コンパイル＋実行
	call :compile
	call :run
	goto bye
)
if %option% == -cr (
	rem コンパイル＋実行
	call :compile
	call :run
	goto bye
)
if %option% == -r (
	echo 実行のみ
	call :run
	goto bye
)
if %option% == -c (
	echo コンパイルのみ
	call :compile
	goto bye
)
if %option% == -e (
	rem batファイルを編集
	notepad .\a.bat
	goto bye
)
if %option% == -help (
	echo 使用方法:
	echo 	a [-r][-c] 課題番号
	echo 	a [-e] [-help]
	echo.
	echo オプション:
	echo 	なし	: コンパイル＋実行
	echo 	-cr	: コンパイル＋実行
	echo 	-r	: 実行のみ
	echo　	-c	: コンパイルのみ
	echo　	-e	: batファイルを編集
	echo　	-help	: このヘルプを表示
	echo.
	echo クラス氏名を変更したい場合は[-e]でSTUNOを変更してください
	goto bye
)

rem 以下はコンパイルと実行のサブルーチン

rem コンパイル
:compile
if exist %STUNO%%no%.java (
	echo コンパイル中…
	javac %STUNO%%no%.java
) else (
	rem error:%STUNO%%no%.javaが見つかりません
	echo error:課題番号間違えてねえか？
	goto bye
)
exit /b

rem 実行
:run
if exist %STUNO%%no%.class (
	for %%i in (%STUNO%%no%.class) do (
		echo classの最終更新 %%~ti
	)
	echo 実行中…
	appletviewer %STUNO%%no%.java
) else (
	rem error:classファイルが見つかりません
	echo error:なんかミスってんぞ（煽り
)
exit /b

:bye