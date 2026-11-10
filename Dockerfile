# --------- Etapa de compilación ---------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY src ./src
COPY web ./web
COPY libs ./libs

RUN mkdir -p build/WEB-INF/classes build/WEB-INF/lib \
 && find src/java -name '*.java' > sources.txt \
 && javac -encoding UTF-8 --release 21 -cp 'libs/*' -d build/WEB-INF/classes @sources.txt \
 && cp -R web/. build/ \
 && cp libs/*.jar build/WEB-INF/lib/ \
 && rm -f build/META-INF/context.xml \
 && cd build && jar --create --file /app/ROOT.war .

# --------- Etapa de ejecución ---------
FROM tomcat:11.0-jdk21-temurin
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /app/ROOT.war /usr/local/tomcat/webapps/ROOT.war
COPY docker/setenv.sh /usr/local/tomcat/bin/setenv.sh
RUN chmod +x /usr/local/tomcat/bin/setenv.sh \
 && sed -i 's/port="8080"/port="${http.port}"/' /usr/local/tomcat/conf/server.xml
EXPOSE 8080
CMD ["catalina.sh", "run"]
