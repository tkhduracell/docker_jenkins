# Automated zero config Jenkins with Docker in Docker support
This is a docker image that setups up jenkins instanse with basically no manual labor. 

## Purpose
 * Setup a jenkins instance from scratch. 
 * Configure SSH keys and setup a seed job to pull job DSLs from an external git repository.
 * Setup an admin user with password.
 * Enable jenkins to use docker inside jobs. 

## Usage
```sh
    # Build image
    docker build -t docker-jenkins-zc-did:latest .

    mkdir -p ./.jenkins_data
    mkdir -p ./.jenkins_workspace
    mkdir -p ./.ssh

    # Run on Debian / Ubuntu 16.04
    docker run \
        -p 8080:8080 \
        -e "SEEDJOB_INITIAL_USER=admin" \
        -e "SEEDJOB_INITIAL_PASS=somerandom" \
        -e "SEEDJOB_SSH_ID=bitbucket" \
        -e "SEEDJOB_SSH_USER=bitbucket" \
        -e "SEEDJOB_SSH_SECRET=$(cat .ssh/id_rsa)" \
        -e "SEEDJOB_GIT=git@bitbucket.org:myuser/job_dsls.git" \
        -e "SEEDJOB_NAME=PullSCMJobs" \
        -v $PWD/.jenkins_data/:/data \
        -v $PWD/.jenkins_workspace/:/var/jenkins_home/workspace/ \
        -v /var/run/docker.sock:/var/run/docker.sock:ro \
        docker-jenkins-zc-did:latest

```

## Usage in docker-compose
It's a great fit with `jwilder/nginx-proxy` that automatically sets up an reverse proxy using nginx. 

```yml
---
  version: "2.2"

  services:

    jenkins:
      build: ./jenkins
      container_name: jenkins
      expose:
        - "8080"
      environment:
        - "VIRTUAL_HOST=j.mydomain.com"
        - "VIRTUAL_PORT=8080"
        - "SEEDJOB_INITIAL_USER=adimin"
        - "SEEDJOB_INITIAL_PASS=somerandom"
        - "SEEDJOB_SSH_ID=bitbucket"
        - "SEEDJOB_SSH_USER=bitbucket"
        - "SEEDJOB_SSH_SECRET=DFGVCFTHGHJHJ"
        - "SEEDJOB_GIT=git@bitbucket.org:myuser/job_dsls.git"
        - "SEEDJOB_NAME=PullSCMJobs"
      volumes:
        - ./jenkins_data/:/data/
        - ./jenkins_workspace/:/var/jenkins_home/workspace/
        - /var/run/docker.sock:/var/run/docker.sock:ro

    nginx:
      image: jwilder/nginx-proxy
      container_name: nginx
      ports:
        - "80:80"
      volumes:
        - /var/run/docker.sock:/tmp/docker.sock:ro

```

        