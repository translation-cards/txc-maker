# TXC Maker

## Hackathon setup
1. Install [Git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git)
2. Clone the [txc-maker repo](https://github.com/translation-cards/txc-maker.git)
3. Check out the hackathon branch: `git checkout women-hack-syria`
4. Install the latest version of [Docker](https://docs.docker.com/engine/installation/) (v1.12)
5. Run the following command to start your development environment. Be sure to replace `[absolute path]` with the absolute path to your project directory. On Windows, you must allow access to your drive via Docker->Settings->Shared Drives.

  `docker run -ti --name txcmaker-develop -p 8080:8080 -p 8000:8000 -v [absolute path]:/app atamrat/txc-maker bash`

 * This command runs a container in interactive mode (`run -ti`)
 * It gives a name to the container (`--name txcmaker-develop`)
 * It forwards standard web ports for the container (`-p 8080:8080`)
 * It mounts the project directory (`-v [absolute path]:/app`)
 * It uses an image for the container `atamrat/txc-maker`
 * It opens up a shell on the newly running container (`bash`)
6. You should now have a command prompt open inside the development container, which is based off Debian Jessie. Move into the project directory: `cd /app`.
 * If you exit the container by typing `exit`, you can enter it again by running `docker start -i txcmaker-develop`
7. The webapp is built and deployed with Maven and Google AppEngine. The following tasks will be useful:
 * To run the webapp locally, use `mvn appengine:devserver`
 * To deploy the webapp, use `mvn appengine:update`
 * To clean, use `mvn clean`


## Building and Running

Although you can run the app locally, it can't do anything interesting without connecting to Google Drive. That requires OAuth, which requires running on AppEngine (so you can give Drive an OAuth callback address).

Requirements:
*  Maven 3.1 or higher.

To get the app up and running on AppEngine:
1.  Register for an app on AppEngine.
2.  Register the app for the Drive API, and enable OAuth2 for your web app (when it asks for a callback URL, put in http://<APP-ID>.appspot.com/oauth2callback). Put the client_secrets.json file at src/main/webapp/WEB-INF/client_secrets.json (**don't** check that file in though; they're secrets, after all).
3.  Put your app ID in pom.xml in place of the string "APP-ID-HERE" (optionally, also set a different version).
4.  Run "mvn appengine:update".
