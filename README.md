# SPARQL Endpoint Status

## Introduction

SPARQL Endpoint Status project aims at monitoring SPARQL Endpoints based on 4 aspects:

* **Discoverability** analyses how SPARQL endpoints can be located, what meta-data are available for them, etc.
* **Interoperability** identifies which features of SPARQL 1.0 and SPARQL 1.1 standards are supported by an endpoint
* **Performance** measures generic performance aspects such as result-streaming, atomic lookups and simple-joins over a HTTP connection.
* **Availability** monitors the uptimes of a SPARQL endpoint.

## Directory structure
```
-backend/ (backend Java code used to monitor the SPARQL Endpoints)
-bin/ (shell scripts used to run global operations such as dumping the data)
-data/ (sample data to populate MongoDB for setting up or testing purposes)
-frontend/ (frontend code based on NodeJS technology)
```

## Deploying via Docker Compose

Prerequisites:

- Docker
- Docker Compose

Run the first time (or when you need to recreate the DB from the dump):

```
docker image rm sparqles_database-svc --force ; \
    rm -rf data/db/ ;
    docker-compose up --build
```

With a production database:

```
docker-compose up -d
```

While the MongoDB is running, you need to initialize the scheduler by fetching the endpoints from from the databus.io first and then generating the schedule:

```
sh bin/sparqles -p src/main/resources/sparqles.properties -i
sh bin/sparqles -p src/main/resources/sparqles.properties -rs
```

## Deploying the application manually

### Prerequisites
In order to run both backend and frontend of SPARQLES application you need to install the following programs:
- Java (tested with version 1.7)
- MongoDB (tested with version 2.4.9)
- NodeJS (tested with version 0.12.4)
- npm

### Loading sample data
For you to test the frontend, you can load the sample data provided in the **data** folder. Use **mongorestore** command to load the unzipped data into a database named **sparqles**:

    cd data
    unzip mongoDump.zip
    mongod &
    mongorestore -d sparqles dump/sparqles
    rm -rf dump/    

### Running the frontend
Make sure the **sparqles** database is present in MongoDB and populated. You can now run the frontend by executing the following command:

    cd frontend
    npm install
    npm start


You should see the following message: `Express server listening on port 3001`.
You can now access your application at the following URL: [http://localhost:3001/](http://localhost:3001/)

## Running the backend

### From the command-line

Make sure the `mongod` is running. Then, under the project folder root, run the following:

    cd backend
    mvn clean package appassembler:assemble
    sh bin/sparqles -p src/main/resources/sparqles.properties -i

The first command compiles the code, packages the jar, and generates wrapper scripts. The second command initialises the MongoDB database (as specified in `/src/main/resources/sparqles.properties`, by default `sparqles` on `localhost`) with the datasets from the [Datahub](https://datahub.io).

In order to obtain all metrics and update the stats counters, run the following commands (inside `backend/`):

    sh bin/sparqles -p src/main/resources/sparqles.properties -run atask
    sh bin/sparqles -p src/main/resources/sparqles.properties -run ptask
    sh bin/sparqles -p src/main/resources/sparqles.properties -run itask
    sh bin/sparqles -p src/main/resources/sparqles.properties -run dtask
    sh bin/sparqles -p src/main/resources/sparqles.properties -r
    sh bin/sparqles -p src/main/resources/sparqles.properties -iv

### From Eclipse

- Git clone the project.
- Copy the cloned folder under Eclipse "workspace" and then run "create project" using that path (make sure the folder is in your workspace otherwise Eclipse complains)
- Install Maven plugin for Eclipse to handle dependencies http://www.eclipse.org/m2e/index.html 
- Once plugin installed, select Configure>Convert to Maven Project
- That's it, you should be able to run from command line using these arguments: `SPARQLES -p src/main/resources/sparqles.properties -h`

## License
SPARQLES code and dataset are licensed under a [Creative Commons Attribution 4.0 International License]( https://creativecommons.org/licenses/by/4.0/).
