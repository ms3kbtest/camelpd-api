# Personal Development API
API that helps track and manage a user's day to day activities through appointments, tasks, and journal entries.

### OpenAPI Specification
[OAS Document](https://app.swaggerhub.com/apis/kbanaag9/PersonalDevelopment/1.1.4)

### Technologies Used
* Java 11
* Camel
* Kong
* Kubernetes
* Lua
* Data Sonnet
* Spring Boot
* MySQL
* Maven
* JUnit

### Database

Entity Relationship Diagram
![](resources/ERDiagram.png)

##### Users
Have a personalized experience through the Users resource.
```json
{
    "userId": 1,
    "firstName": "Johnny",
    "lastName": "Boy",
    "username": "jboy21",
    "email": "jboy21@hotmail.com",
    "birthday": "1950-02-03",
    "settings": {
      "appAlerts": false,
      "emailNotifications": false,
      "theme": "light"
    },
    "appointments": [
      {
        "appointmentId": 1,
        "userId": 1,
        "date": "2020-12-12",
        "startTime": "2020-12-12T09:00:00.000Z",
        "endTime": "2020-12-12T10:00:00.000Z",
        "description": "Doctor's appointment",
        "appointmentNotes": "Need more sleep, and a healthier diet."
      }
    ],
    "tasks": [
      {
        "taskId": 1,
        "userId": 1,
        "done": false,
        "name": "Project OAS",
        "deadline": "2021-04-02T00:00:00.000Z",
        "progress": 50,
        "priority": 10
      }
    ]
  }
```

##### Appointments
Create and manage prearranged activities through Appointments.
```json
{
    "appointmentId": 1,
    "userId": 1,
    "date": "2020-12-12",
    "startTime": "2020-12-12T09:00:00.000Z",
    "endTime": "2020-12-12T10:00:00.000Z",
    "description": "Doctor's appointment",
    "appointmentNotes": "Need more sleep, and a healthier diet."
}
```

##### Tasks
Add and Check Off items that need to be accomplished throughout the day through Tasks.
```json
{
    "taskId": 1,
    "userId": 1,
    "done": false,
    "name": "Project OAS",
    "deadline": "2021-04-02T10:00:00.000Z",
    "progress": 50,
    "priority": 10
 }
```

##### Journal Entries
Log end of the day thoughts and experiences through Journal Entries.
```json
{
    "entryId": 1,
    "userId": 1,
    "date": "2021-04-02",
    "summary": "Something about the user's day.",
    "positives": "Some positive things that happened today",
    "moodScore": 95
}
```

# pd-sys-api

This API project was generated using MS3's [Camel OpenAPI Archetype](https://github.com/MS3Inc/camel-archetypes), version 0.2.7.

### Getting Started

**Running on the Command Line**

```
mvn spring-boot:run
```

<!-- 
**Running Locally using IDE**

This project uses Spring profiles, and corresponding pd-sys-api-<env>.yaml files.

Use the following environment variables: 
   * ```spring.profiles.active=<env>```
   * ```spring.config.name=pd-sys-api```

**Running on Command Line**

```
mvn spring-boot:run -Dspring-boot.run.profiles=<env> -Dspring-boot.run.arguments="--spring.config.name=pd-sys-api"
```
-->

### Actuator Endpoints

To access the list of available Actuator endpoints, go to: http://localhost:8080/actuator or `{{url}}/actuator`

The available endpoints are as follows:

* `/health`
* `/metrics`
* `/info`

#### Metrics

List of available metrics can be found here: http://localhost:8080/actuator/metrics/

Add the metric name in `/metrics/<metric name>` to access the metric for that particular topic.

Sample metric: http://localhost:8080/actuator/metrics/jvm.memory.used

```
{
    "name": "jvm.memory.used",
    "description": "The amount of used memory",
    ...
}
```


# Running on Kubernetes
To run the application on Kubernetes, we would need an ingress controller.
The simplest ingress controller that we can use is:
```
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v0.45.0/deploy/static/provider/cloud/deploy.yaml
```
After creating the ingress controller, we can start with creating a kubernetes namespace.
```
kubectl apply -f ./kubernetes/camelpd-api-secrets.yaml
```
We want to deploy the mysql database first, since the api depends on it for storing and accessing data.
```
kubectl apply -f kubernetes/camelpd-api-mysql.yaml
```
Deploy the API.
```
kubectl apply -f kubernetes/camelpd-api-deployment.yaml
```
Create an ingress so that the ingress controller can communicate with the API.
```
echo '
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: camelpd-api
  namespace: camelpd-dev
  annotations:
    kubernetes.io/ingress.class: "nginx"
spec:
  rules:
  - host: camelpd.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: camelpd-api-dpl-svc
            port:
              number: 8090
' | kubectl apply -f -
```
Test with curl or postman.
```
curl -i camelpd.com:80/api/users
```
# Running on Kong
There are different ways of deploying in Kong. Here are the 2 configured ways of deploying this project on Kong.
## Kong Enterprise
Kong enterprise comes with all the components that allows the use of kong manager and the developer portal. 
To run kong enterprise:
```
// run this file by typing its path
kong-ee-full-deploy-up.bat
```
This command will add all necessary components needed to deploy kong enterprise.

To use kong manager, we must port forward port 8001 so admin api can be accessible
```
kubectl port-forward deploy/kong-ee-kong 8001
```

To access dev portal and portal api, we need to port forward 8003 and 8004.
```
kubectl port-forward deploy/kong-ee-kong 8003
kubectl port-forward deploy/kong-ee-kong 8004
```
### Developer Portal
Upload the open api yaml file to enable the try it out section.
```
curl -X POST http://camelpd.com:8001/default/files -F "path=specs/pd-sys-api.yaml" -F "contents=@kong/crd/pd-sys-api.yaml" -H "Kong-Admin-Token: kong"
```
Add this token for the Authorization.
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzb21lLWtleSJ9.N_XnPZrw35jox93foK5q7p-nvZdNnZHMpPaTfJR2x7w
```
## Hybrid Deployment
The other way of deploying is a hybrid deployment. The configuration will run a single control plane and two data planes.

```
// run this file by typing its path
hybrid-deploy-up.bat
```
Add CRD's then test on port 8000 and 9000
```
curl -i -H "Authorization: Bearer <insert jwt token>" http://camelpd.com:8000/api/users
// or 
curl -i -H "Authorization: Bearer <insert jwt token>" http://camelpd.com:9000/api/users
```
## API Deployment and CRD's
The API is bundled with a convenient command that will run all necessary components. CRD's can be mixed and matched depending on the use case and need for it.
```
// run this file by typing its path
camelpd.deploy-up.bat
```
This command will start the API database and the API deployment.
<br>To test deployment:
```
curl -i -H "Authorization: Bearer <insert jwt token>" http://camelpd.com/api/users
```