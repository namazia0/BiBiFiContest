FROM ubuntu:18.04
MAINTAINER Arian Namazi <@uni-bonn.de>

RUN adduser --disabled-password --gecos "" ubuntu; usermod -a -G sudo ubuntu;
RUN apt-get update ; apt-get install -y openjdk-11-jdk openjdk-11-demo openjdk-11-doc openjdk-11-jre-headless openjdk-11-source build-essential

