# Meep's Demo
The Meep's demo invokes a REST API every 30 seconds to obtain the information of vehicles in a certain geographical area.

The project structure and how to compile/run it, will be explained below.

## Project structure

```
src
├─┬ main
│ ├─┬ java
│ │ └─┬ es.meep.demo                            → Main package
│ │   ├─┬ config
│ │   │ └── MeepDevProperties.java              → Contains the properties of Meep's Dev Server
│ │   ├─┬ domain
│ │   │ ├── StringListConverter.java            → Converter for DB transformations
│ │   │ └── Vehicle.java                        → Vehicle DB model
│ │   ├─┬ repository
│ │   │ └── VehicleRepository.java              → JPA Repository for Vehicle's Model
│ │   ├─┬ service
│ │   │ └── VehicleService.java                 → Contains the scheduled task to obtain info about vehicles
│ │   └── MeepDemoApp.java                      → Project's main class
│ └─┬ resources   
│   ├── application.yml                         → Application's config file
│   └── banner.txt                              → The banner to be shown on startup
├── build.gradle                                → Gradle's config file
└── README.md                                   → This file
```

## Compile project
In the project's root folder, run:
#### Linux

```
./gradlew clean build
```

#### Windows

```
.\gradlew.bat clean build
```


## First App run
To run the application, execute:

#### Linux
```
./gradlew bootRun
```

#### Windows

```
.\gradlew.bat bootRun
```
#### Or using Java's executable

```
java -jar build/libs/demo-1.0.jar
```