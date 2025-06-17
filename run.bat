@echo off
REM Code & Break 컴파일 및 실행 스크립트 (Windows)

echo Code & Break 컴파일 중...

REM 컴파일 디렉토리 생성
if not exist "bin" mkdir bin

REM Java 파일 컴파일
javac -d bin -sourcepath src src\CodeBreakApplication.java src\model\*.java src\view\*.java src\controller\*.java

if %ERRORLEVEL% NEQ 0 (
    echo 컴파일 실패!
    pause
    exit /b 1
)

echo 컴파일 성공!
echo.
echo Code & Break 실행 중...

REM 애플리케이션 실행
cd bin
java CodeBreakApplication

pause
