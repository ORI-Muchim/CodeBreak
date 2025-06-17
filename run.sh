#!/bin/bash

# Code ∧ Break 컴파일 및 실행 스크립트 (Unix/Linux/macOS)
# 최적화된 버전 - constants 패키지 포함

echo "Code ∧ Break 컴파일 중..."

# 컴파일 디렉토리 생성
mkdir -p bin

# Java 파일 컴파일 (모든 패키지 포함)
javac -d bin -sourcepath src \
    src/CodeBreakApplication.java \
    src/constants/*.java \
    src/model/*.java \
    src/view/*.java \
    src/controller/*.java \
    src/events/*.java

if [ $? -ne 0 ]; then
    echo "❌ 컴파일 실패!"
    echo "오류 확인 후 다시 시도해주세요."
    exit 1
fi

echo "✅ 컴파일 성공!"
echo ""
echo "🚀 Code ∧ Break 실행 중..."

# 애플리케이션 실행
cd bin
java CodeBreakApplication

echo ""
echo "📴 프로그램이 종료되었습니다."
