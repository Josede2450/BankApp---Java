FROM jenkins/jenkins:lts

USER root

# Install curl and other tools if needed
RUN apt-get update && apt-get install -y curl

# Copy plugins.txt into container
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt

# Use jenkins-plugin-cli (already included in image)
RUN jenkins-plugin-cli --plugin-file /usr/share/jenkins/ref/plugins.txt

USER jenkins
