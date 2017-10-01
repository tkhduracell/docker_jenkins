FROM jenkinsci/jenkins:lts

USER root

ENV JHOMEREF /usr/share/jenkins/ref/

COPY UpgradeWizard.state $JHOMEREF/jenkins.install.UpgradeWizard.state
COPY basic-security.groovy $JHOMEREF/init.groovy.d/basic-security.groovy
COPY seed-job.groovy $JHOMEREF/init.groovy.d/seed-job.groovy

COPY plugins.txt $JHOMEREF/plugins.txt

RUN plugins.sh $JHOMEREF/plugins.txt

ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false -Dpermissive-script-security.enabled=true"

ENV DOCKER_VERSION "17.06.2-ce"

RUN wget --quiet https://download.docker.com/linux/static/stable/x86_64/docker-$DOCKER_VERSION.tgz && \
	tar --strip-components=1 -xvzf docker-$DOCKER_VERSION.tgz -C /usr/local/bin
