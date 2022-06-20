FROM ${HARBOR_HOST}/ps/openjdk:8-jdk
MAINTAINER Rain

RUN rm -f /etc/localtime \
&& ln -sv /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
&& echo "Asia/Shanghai" > /etc/timezone

copy xxl-job-admin*.jar /xxl-job-admin.jar
copy bootstrap.yml /bootstrap.yml
copy start.sh /start.sh
RUN chmod 755 /start.sh
ENTRYPOINT ["./start.sh"]
