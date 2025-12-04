@echo off

cd ./dashboard

SET "folderPath=./node_modules"

IF EXIST "%folderPath%/" (
    npx electron .
) ELSE (
    npm install
    npx electron .
)
