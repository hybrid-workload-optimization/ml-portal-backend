FROM docker.io/openjdk:8u201-alpine

ARG INSTALL_PATH=/strato
ARG JAR_NAME=ARTIFACT_NAME

RUN mkdir -p ${INSTALL_PATH}
ADD ${JAR_NAME} ${INSTALL_PATH}/${JAR_NAME}

ENV JAR_FILE ${INSTALL_PATH}/${JAR_NAME}

ADD entrypoint.sh ${INSTALL_PATH}/entrypoint.sh
RUN chmod +x ${INSTALL_PATH}/entrypoint.sh

ENTRYPOINT ["/strato/entrypoint.sh"]
EXPOSE 18080