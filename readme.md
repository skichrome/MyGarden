## My Garden App

This is an app connected to my backend that get ambient measurements like temperature, humidity,
etc. The aim of this app is to display an historic of measures, and change measuring devices
parameters.

## Build, configuration

[![Build Status](https://jenkins.campeoltoni.fr/buildStatus/icon?job=MyGarden)](https://jenkins.campeoltoni.fr/job/MyGarden/)

To build this project you need to add `apiKey` and `apiBaseUrl` variables in a file
named `credentials.properties` in the root of the project.

```groovy
apiKey = "TODO-api-key"
apiBaseUrl = "TODO-api-base-url/@"
```