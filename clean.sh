#!/bin/bash

echo "🧹 CodeBreak 프로젝트 정리 시작..."

# bin 폴더 전체 삭제 (컴파일된 파일들)
if [ -d "bin" ]; then
    echo "📂 bin 폴더 삭제 중..."
    rm -rf bin
    echo "✅ bin 폴더 삭제 완료"
fi

# .DS_Store 파일 삭제 (맥 시스템 파일)
if [ -f ".DS_Store" ]; then
    echo "🗑️ .DS_Store 파일 삭제 중..."
    rm -f .DS_Store
    echo "✅ .DS_Store 파일 삭제 완료"
fi

# src/data 빈 폴더 삭제
if [ -d "src/data" ] && [ -z "$(ls -A src/data)" ]; then
    echo "📁 빈 src/data 폴더 삭제 중..."
    rmdir src/data
    echo "✅ 빈 src/data 폴더 삭제 완료"
fi

echo ""
echo "🎉 정리 완료!"
echo "💡 다시 컴파일하려면: make 또는 ./run.sh"
echo ""
