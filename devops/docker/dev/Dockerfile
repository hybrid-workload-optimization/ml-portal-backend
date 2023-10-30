FROM docker.io/openjdk:8u201-alpine

ENV TZ=Asia/Seoul

#인증서 관련 설정
ADD cer/root.cer /root.cer
ADD cer/ca.cer /ca.cer

RUN keytool -import -trustcacerts -keystore $JAVA_HOME/jre/lib/security/cacerts -alias letsroot -file root.cer -storepass changeit -noprompt
RUN keytool -import -trustcacerts -keystore $JAVA_HOME/jre/lib/security/cacerts -alias letsca -file ca.cer -storepass changeit -noprompt


ARG INSTALL_PATH=/strato
ARG JAR_NAME=ARTIFACT_NAME

RUN mkdir -p ${INSTALL_PATH}
ADD ${JAR_NAME} ${INSTALL_PATH}/${JAR_NAME}

ENV JAR_FILE ${INSTALL_PATH}/${JAR_NAME}

ADD entrypoint.sh ${INSTALL_PATH}/entrypoint.sh
RUN chmod +x ${INSTALL_PATH}/entrypoint.sh

ENTRYPOINT ["/strato/entrypoint.sh"]
EXPOSE 18080