# install Medieval Factions
mvn install:install-file -Dfile=C:\projects\Democracy\dependencies\Ponder-1.0.jar -DgroupId=dansplugins -DartifactId=medievalfactions -Dversion=1.0 -Dpackaging=Jar

# install Ponder
mvn install:install-file -Dfile=${PATH_TO_PONDER_JAR} -DgroupId=preponderous -DartifactId=ponder -Dversion=${PONDER_VERSION} -Dpackaging=Jar