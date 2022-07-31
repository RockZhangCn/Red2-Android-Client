all:
	#./gradlew app:clean
	./gradlew app:assembleRelease
	rm -rf release_apk
	mv ./app/build/outputs/apk/release/ release_apk
