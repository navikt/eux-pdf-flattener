#FROM ghcr.io/navikt/baseimages/node-express:18-alpine
FROM ghcr.io/navikt/baseimages/temurin:21

USER root
# RUN wget http://us.archive.ubuntu.com/ubuntu/pool/main/a/apt/apt_2.9.6_amd64.deb
# RUN dpkg -i apt_2.9.6_amd64.deb
# RUN apk update
# RUN apk add
RUN  apt-get update -y && \
     apt-get install -y \
    --no-install-recommends \
     gettext \
     less \
     lsof \
     apt-transport-https \
     libpixman-1-0 \
     libpixman-1-dev \
     git-all \
     apt-transport-https  && \
     apt-get clean && \
     rm -rf /var/lib/apt/lists/*
# RUN apt-get update && apt-get install -y

ENV NVM_DIR /usr/local/nvm
#ENV NVM_DIR ~/.nvm
ENV TEST test
ENV NODE_VERSION 18.20.4
RUN mkdir -p $NVM_DIR
RUN wget -qO- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.7/install.sh | bash
RUN /bin/bash -c "source $NVM_DIR/nvm.sh && nvm install $NODE_VERSION && nvm use --delete-prefix $NODE_VERSION"

#    && . $NVM_DIR/nvm.sh \
#    && nvm install $NODE_VERSION \
#    && nvm alias default $NODE_VERSION \
#    && nvm use default
ENV NODE_PATH $NVM_DIR/versions/node/v$NODE_VERSION/lib/node_modules
ENV PATH      $NVM_DIR/versions/node/v$NODE_VERSION/bin:$PATH

# RUN  echo $(ls /usr/local/nvm/)
#RUN file="$(ls -1 /usr/local/nvm/ -al)" && echo $file
#RUN file="$(ls -1 /usr/local/nvm/nvm-exec -al)" && echo $file
#RUN ls ~/.nvm/ -al
#RUN echo `ps -p $$`
# RUN source ~/.bashrc

#RUN echo $(ls /usr/local/opt/nvm/ -al)

# RUN /usr/local/nvm/nvm.sh install node
# RUN /usr/local/nvm/nvm-exec install node $

RUN wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb

RUN apt-get update && apt -y install  ./google-chrome-stable_current_amd64.deb

RUN mkdir /home/apprunner
RUN mkdir pdf.js
RUN chmod 777 /home/apprunner
RUN chmod 777 pdf.js
RUN cd pdf.js && npm install -g gulp-cli
RUN npm install -g -save html-pdf-chrome
RUN npm install -g -save pdfjs-dist@3.11.174
RUN npm install -g pm2
COPY ./print.js ./xfa.pdf ./count.js ./medical.pdf /
RUN mkdir tmppdf
RUN chmod 777 tmppdf
COPY ./xfa.pdf tmppdf/xfa.pdf
RUN chmod 777 /print.js
RUN chmod 777 /count.js
# RUN chmod 777 /chrome.sh
# RUN chmod 777 /entrypoint.sh
RUN chmod 777 /xfa.pdf
RUN chmod 777 /medical.pdf
RUN chmod 777 tmppdf/xfa.pdf
RUN chown apprunner pdf.js

# RUN ls -l
USER apprunner
ENV NODE_PATH $NVM_DIR/versions/node/v$NODE_VERSION/lib/node_modules
ENV PATH      $NVM_DIR/versions/node/v$NODE_VERSION/bin:$PATH


RUN git clone https://github.com/mozilla/pdf.js.git
# RUN ls -l
RUN cd pdf.js && npm install

#RUN cd pdf.js && npm install
# RUN cd pdf.js && npm link module gulp

RUN cd pdf.js && mkdir out && mkdir in
# RUN cp tmppdf/xfa.pdf pdf.js/in/xfa.pdf
USER root
RUN cd pdf.js && npm link gulp
USER apprunner
RUN chmod -R 777 pdf.js
RUN google-chrome --version


# RUN file="$(ls -1 /usr/local/ -al)" && echo $file
# RUN file="$(ls -1 /usr/local/nvm/versions/node -al)" && echo $file





# ADD eux-pdf-flattener-webapp/target/eux-pdf-flattener.jar /app/app.jar

# CMD ["node" , "print.js"]
#ENTRYPOINT ["/entrypoint.sh"]
# CMD ["tail", "-f", "/dev/null"]
#CMD ["/chrome.sh"]
COPY ./.nais/03-start-pm2-gulp.sh /init-scripts/03-start-pm2-gulp.sh

COPY eux-pdf-flattener-webapp/target/eux-pdf-flattener.jar /app/app.jar
EXPOSE 8080

WORKDIR /app

# ENTRYPOINT ["/entrypoint.sh"]

# CMD ["tail", "-f", "/dev/null"]
