FROM plasma147/basejava8

MAINTAINER Andy Lee <plasma147@gmail.com>

CMD ["/sbin/my_init"]

RUN wget https://github.com/plasma147/github-thrift-client-server/raw/dist/data/0.1.0-SNAPSHOT/thrift-server_0.1.0_amd64.deb -P /tmp/
RUN dpkg -i /tmp/thrift-server_0.1.0_amd64.deb

EXPOSE 8080

RUN apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*
