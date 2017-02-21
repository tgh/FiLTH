# Overall Description
A personal webapp to manage my movies--movies I have seen, movies I want to see,
filmographies, oscar winners/nominees, best-of lists, etc. Yes, I know IMDB
exists.

# Environment Setup (Ubuntu)
These setup instructions were created based on Ubuntu 14.04 LTS, and assumes you already have [git](https://git-scm.com/) setup.

1. Clone this repository under $HOME/workspace (assuming you already have that directory, otherwise create it: `mkdir $HOME/workspace`)
1. Set some required environment variables:

    - **WORKSPACE** ($HOME/workspace)

    - **FILTH_PATH** ($WORKSPACE/FiLTH)

1. Install **Java 8**

    - Download [here](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

        - at the time of this writing: Under **Java SE Development Kit 8u71** accept the
          Oracle license agreement and click the **Download** link for **Linux x64**: `jdk-8u71-linux-x64.tar.gz`

    - move extracted tar contents to /usr/lib/jvm:

      `sudo mv jdk<version> /usr/lib/jvm/`

    - install new java:

      `sudo update-alternatives --install /usr/bin/javac javac /usr/lib/jvm/jdk<version>/bin/javac 1`

      `sudo update-alternatives --install /usr/bin/java java /usr/lib/jvm/jdk<version>/bin/java 1`

      `sudo update-alternatives --install /usr/bin/javaws javaws /usr/lib/jvm/jdk<version>/bin/javaws 1`

      `sudo update-alternatives --install /usr/bin/jar jar /usr/lib/jvm/jdk<version>/bin/jar 1`

    - Add an enironment variable for Java's JDK directory: **JAVA_HOME** (for example: `/usr/lib/jvm/jdk<version>`)

    - or, alternatively, if you need multiple versions of java on your system, create a script to switch to a version you want to use. For example, include these lines in the script:

      - `sudo update-alternatives --config java`

      - `sudo update-alternatives --config javac`

      and update the **JAVA_HOME** environment variable as you need

1. Install **ant**

    - `sudo apt-get install ant`

    - *This may not be required:* Download **ant-contrib-1.0b3.jar** [here](http://sourceforge.net/projects/ant-contrib/files/ant-contrib/1.0b3/ant-contrib-1.0b3-bin.tar.gz/download), extract the contents, and place the jar file (found in ant-contrib, the root directory of the extracted content) in /usr/share/ant/lib (create the dir if it doesn't exist)--there is a danger this could be oerriden if you ever upgrade ant; as an alternative you can place the jar in $HOME/.ant/lib. [Ant-Contrib](http://ant-contrib.sourceforge.net/tasks/tasks/index.html) provides a number of useful tasks such as `foreach` and `if`.

    - Add an environment variable for ant's home directory (probably /usr/share/ant): **ANT_HOME**

1. Install [Ivy](http://ant.apache.org/ivy/) (for dependency management)

    - Download the latest binary package **_(2.4 or higher is required)_** [here](http://ant.apache.org/ivy/download.cgi) and extract the contents. Copy the ivy-\[version\].jar jar file to /usr/share/ant/lib/ (or, $HOME/.ant/lib if you put the ant-contrib jar there). You can delete everything else.

    \* _2.4 or higher is required because the `<bintray/>` ivy resolver used in ivysettings.xml doesn't work with versions prior to 2.4_

1. **CHECKPOINT**

    run:

    `ant ivy-resolve`

     it should complete successfully

1. Install **Tomcat 8**

    - Download [here](https://tomcat.apache.org/download-80.cgi)

    - Move exctracted contents (the tomcat8 directory) to $WORKSPACE

    - Add an environment variable for the tomcat8 directory: **CATALINA_HOME** ($WORKSPACE/tomcat8)

    - (you may also need to make all *.sh files under \[tomcat8\]/bin/ executable (see http://www.coderanch.com/t/85334/Tomcat/define-BASEDIR-env-variable-Tomcat)

1. Install **PostgreSQL**

1. Setup database

1. Install LaTeX

# Environment Setup (Mac)

These instructions assume you have `brew` installed. If you do not, go to [brew.sh](http://brew.sh) and download it.

1. Clone this repository under $HOME/workspace (assuming you already have that directory, otherwise create it: `mkdir $HOME/workspace`)
1. Set some required environment variables:

    - **WORKSPACE** ($HOME/workspace)

    - **FILTH_PATH** ($WORKSPACE/FiLTH)

1. Install **Java 8**

1. Install **ant**: `brew install ant` (at the time of this writing it was `ant` 1.10.0)

1. Install [Ivy](http://ant.apache.org/ivy/) (for dependency management)
 
    - `brew install ivy` (at the time of this writing it was Ivy 2.4.0--_NOTE: version 2.4 or higher is required because the `<bintray/>` ivy resolver used in ivysettings.xml doesn't work with versions prior to 2.4_)

    - Copy the ivy jar file into the ant lib directory: `cp /usr/local/Cellar/ivy/{version}/libexec/ivy-{version}.jar /usr/local/Cellar/ant/{version}/libexec/lib/` (or wherever your brew installations reside)

1. **CHECKPOINT**

    run:

    `ant ivy-resolve`

    it should coomplete successfully

1. Install **Tomcat**

    - `brew install tomcat` (at the tome of this writing it was tomcat 8.5.11)

    - Add an environment variable for the tomcat8 directory: **CATALINA_HOME** (if `brew` was used with it's default installation directory, then it would be `/usr/local/Cellar/tomcat/{version}/libexec`)

1. Install **PostgreSQL** (at the time of this writing we are using PostgreSQL 9.5)

    - Download and run the PostgreSQL installer [here](https://www.bigsql.org/postgresql/installers.jsp). This installs BigSQL Manager, pgAdminIII (if you choose), and a `psql` interactive shell.

    - For convenience to `psql` in your terminal (rather than using the interactive shell) you may want to add this to your `.bashrc` or `.bash_profile`: `source \[path-to-postgres\]/pg95/pg95.env`

    - Create `filth_admin` user: 

1. Setup database

1. Install LaTeX

    - Download and run the installer for `MacTeX` [here](http://www.tug.org/mactex/index.html)