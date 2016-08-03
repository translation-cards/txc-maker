FROM maven:3-jdk-7
MAINTAINER agarrard@thoughtworks.com

RUN curl -sL https://deb.nodesource.com/setup_6.x | bash - &&\
    apt-get install -y nodejs

RUN useradd --user-group --create-home --shell /bin/false app
ENV HOME=/home/app

COPY src/UI/package.json src/UI/bower.json src/UI/.bowerrc $HOME/txc-maker/src/UI/

RUN chown -R app:app $HOME/*

USER app
WORKDIR $HOME/txc-maker
RUN cd src/UI && npm install
COPY pom.xml $HOME/txc-maker
RUN mvn dependency:resolve


