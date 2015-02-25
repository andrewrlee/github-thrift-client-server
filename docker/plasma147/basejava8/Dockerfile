FROM phusion/baseimage:0.9.16

MAINTAINER Andy Lee <plasma147@gmail.com>

CMD ["/sbin/my_init"]

#RUN apt-get install -y unzip

# install Oracle JDK 8
RUN apt-get install -q -y software-properties-common
RUN add-apt-repository ppa:webupd8team/java -y
RUN apt-get update
RUN echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections
RUN apt-get install oracle-java8-installer -y

RUN apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*
