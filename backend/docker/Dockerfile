FROM epicsoft/openjdk:jre17
#语言字符
ENV LC_ALL C.UTF-8
ENV LANG C.UTF-8
ENV LANGUAGE C.UTF-8
#时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
#将springboot的jar包打入镜像
COPY ./target/backend-1.0-SNAPSHOT.jar /opt/ROOT.jar
WORKDIR /opt
#启动命令
CMD sleep 20;java -Djava.security.egd=file:/dev/./urandom -jar ROOT.jar
