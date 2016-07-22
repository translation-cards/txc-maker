# Development Environment Setup

## 1. Get the source code

1. Install [Git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git)
2. Clone the [txc-maker repo](https://github.com/translation-cards/txc-maker.git)
3. Get the `client_secrets.json` file from a Translation Cards team member and copy it into the directory `src/main/webapp/WEB-INF/`


## 2. Install Docker

We have created a [Docker](https://www.docker.com/what-docker) image that contains the development environment. Depending on your system, you will have to install a different version of Docker. If you don't want to use Docker or can't get it to work, see the list of tools and dependencies to install below.

 Docker runs natively several Linux distributions. Find the instructions [here](https://docs.docker.com/engine/installation/linux/ubuntulinux/).

 For Max OS X, Docker has a native application in beta. If your system doesn't meet the requirements in the installation instructions, you will have to install the Docker Toolbox. There is additional information about Docker Toolbox below. Find the instructions [here](https://docs.docker.com/engine/installation/mac/).

 Certain versions of Windows 10 also have a native Docker application in beta. Otherwise, Windows users should install Docker Toolbox. Find the instructions [here](https://docs.docker.com/engine/installation/windows/).

 ## 3. Run the application
1. Using the terminal, go into the base project directory and run the following command:

  `docker run -ti --name txcmaker-develop -p 8080:8080 -p 8000:8000 -v [absolute path]:/app atamrat/txc-maker bash`

  Be sure to replace `[absolute path]` with the absolute path to your project directory. On Windows, you must allow access to your drive via Docker->Settings->Shared Drives before running. Find the explanation for this command below.

7. You should now have a command prompt open inside the development container, which is based off Debian Jessie. Move into the project directory: `cd /app`.
   * If you exit the container by typing `exit`, you can enter it again by running

    `docker start -i txcmaker-develop`
8. The webapp is built and deployed with Maven and Google AppEngine. The following commands will be useful:
   * To run locally, use `mvn appengine:devserver`
    * Access the app at http://localhost:8080
   * To deploy, use `mvn appengine:update`
    * Access the app at http://translation-cards-dev.appspot.com
   * To clean, use `mvn clean`

## Appendix

### Docker Toolbox

Download the appropriate installer [here](https://www.docker.com/products/docker-toolbox). It should guide you through the process. Once installation is complete, run the Docker Quickstart Terminal to use Docker commands.

You will have to modify your hosts file for the application to work. In the Docker Quickstart Terminal, type `docker-machine ip` and record the ip address it gives you. Then, follow [these instructions](https://support.rackspace.com/how-to/modify-your-hosts-file/) for changing your hosts file in order to add the following line:

  `[ip address] translation-cards-docker.com`

Where [ip address] is the ip address you recorded from the docker-machine command above. Now, after following the instructions in section 3, the application should be accessible at http://translation-cards-docker.com

### Docker Command

The command to run the development environment's container is a mouthful. Here is a basic explanation of its parts:

`docker run -ti --name txcmaker-develop -p 8080:8080 -p 8000:8000 -v [absolute path]:/app atamrat/txc-maker bash`

* This command runs a container in interactive mode (`run -ti`)
* It gives a name to the container (`--name txcmaker-develop`)
* It forwards standard web ports for the container (`-p 8080:8080`)
* It forwards the debugging ports for the container (`-p 8000:8000`)
* It mounts the project directory (`-v [absolute path]:/app`)
* It uses an image for the container `atamrat/txc-maker`
* It opens up a shell on the newly running container (`bash`)

See the [Docker run reference](https://docs.docker.com/engine/reference/run/) for additional details.

### Debug configuration

To debug the application, add the following flags to your debugger's configuration:

`-agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n`
