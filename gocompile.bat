rd /s /q bin
mkdir bin
copy .\src\*.xml .\bin
copy .\src\*.properties .\bin
javac -encoding UTF-8 -d ./bin -classpath .;./lib/* ./src/*.java