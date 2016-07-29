# Development Environment Setup

The Deck Maker runs on a Java server hosted in Google AppEngine. It interacts with AppEngine services for storage and task management, and utilizes the Google Drive API to pull translation files and deploy finished decks.

The frontend is being written in AngularJS. The Angular app lives in the folder `src/UI`.

## 1. Get the source code

1. Install [Git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git)
2. Fork and clone the [txc-maker repo](https://github.com/translation-cards/txc-maker.git)
3. Get the `client_secrets.json` file from a Translation Cards team member and copy it into the directory `src/main/webapp/WEB-INF/`
 * For the Women Hack Syria hackathon, this file will be distributed at the event.

## 2. Local setup

You should install the following dependencies on your machine. Alternatively, follow the Docker setup below. It allows you to install and run the app without managing conflicting versions of the above dependencies. It also provides you with a pre-configured image of the environment, so the app should run out of the box.

1. Java 7 SDK
2. Maven >3.1
3. Node.js v6

## 3. Run the application

The webapp is built and deployed with Maven, NPM, and Google AppEngine.

1. from `src/UI`, run `npm install`
 * This sets up the angular code
2. from the project root, run `mvn appengine:devserver`
  * This runs the server at http://localhost:8080/get-txc

In order for your changes to the Angular app to show up while you're running the server, you will have to copy the contents of `src/UI/app` into `target/txcmaker-1.0-SNAPSHOT`. Otherwise, you will have to restart the server to see the changes.

## 4. Develop on the frontend

From the `src/UI` directory:

* `npm install` : install dependencies
 * `npm install --unsafe-perm` : use this if using docker
* `npm run test` : Run the Jasmine tests through Karma.   
 * Connect to http://localhost:9876 to debug with your browser.
* `npm run start` : Start a simple server which serves the files. This won't connect to the backend, it's just for quick feedback

## Debug configuration (optional)

To debug the Java server, add the following flags to your debugger's configuration:

`-agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n`

## Docker Setup (optional)

We have created a [Docker](https://www.docker.com/what-docker) image that contains the development environment, which is based off of Debian Jessie. Setting up and running Docker is not always straightforward, so proceed at your own risk.

Depending on your system, you will have to install a different version of Docker. If you're unable to install Docker v1.12, please read the section on Docker Toolbox.

1. [Install Docker](https://docs.docker.com/engine/installation/).

1. Using the terminal, go into the base project directory and run the following command:

   `docker run -ti --name txcmaker-develop -p 8080:8080 -p 8000:8000 -p 8554:8554 -p 9876:9876 -v [absolute path]:/app atamrat/txcmaker bash`

   Be sure to replace `[absolute path]` with the absolute path to your project directory. Find the explanation for this command below.

2. You should now have a command prompt open inside the development container. Move into the project directory: `cd /app` and continue with the instructions to run the app.
  * If you exit the container by typing `exit`, you can enter it again by running

   `docker start -i txcmaker-develop`

### Docker on Windows

* You must allow access to your drive via Docker->Settings->Shared Drives before running (requires administrator access).

### Docker Toolbox

Download the appropriate installer [here](https://www.docker.com/products/docker-toolbox). It should guide you through the process. Once installation is complete, run the Docker Quickstart Terminal to use Docker commands.

You will have to modify your hosts file for the application to work. In the Docker Quickstart Terminal, type `docker-machine ip` and record the ip address it gives you. Then, follow [these instructions](https://support.rackspace.com/how-to/modify-your-hosts-file/) for changing your hosts file in order to add the following line:

  `[ip address] translation-cards-docker.com`

Where [ip address] is the ip address you recorded from the docker-machine command above. Now, after following the instructions in section 3, the application should be accessible at http://translation-cards-docker.com. In the above steps, you will have to use this URL (or the IP of your docker machine) instead of localhost.

### Docker Command

The command to run the development environment's container is a mouthful. Here is a basic explanation of its parts:

`docker run -ti --name txcmaker-develop -p 8080:8080 -p 8000:8000 -p 8554:8554 -p 9876:9876 -v [absolute path]:/app atamrat/txcmaker bash`

* This command runs a container in interactive mode (`run -ti`)
* It gives a name to the container (`--name txcmaker-develop`)
* It publishes/forwards various ports:
 * `-p 8080:8080` : web port for the Java server
 * `-p 8000:8000` : debug port for the Java server
 * `-p 8554:8554` : web port for the Nodejs server
 * `-p 9876:9876` : debug port for Karma test runner
* It mounts the project directory (`-v [absolute path]:/app`)
* It uses an image for the container `atamrat/txcmaker`
* It opens up a shell on the newly running container (`bash`)

See the [Docker run reference](https://docs.docker.com/engine/reference/run/) for additional details.
