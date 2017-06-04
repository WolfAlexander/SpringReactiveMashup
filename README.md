[![Build Status](https://travis-ci.org/WolfAlexander/SpringReactiveMashup.svg?branch=master)](https://travis-ci.org/WolfAlexander/SpringReactiveMashup)

## Mashup service
This REST API is a mashup of MusicBrainz, Wikipedia and Cover Art Archive API:s to provide detailed
information about music artists.

### Functional View
 The consumer of this API provides a MBID (MusicBrainz Identifier) and gets back 
 the Wikipedia description of the artist and list of all the albums created by the 
 artist, available at MusicBrainz. The album description includes the identification
 number, title and cover image.
 
### Design choices
#####General
This a single service layered application.
![Conceptual model](/docs/SpringReaciveMashup.png)
*Conceptual model showing major components*

##### Reactive streams
A mashup service is requesting several remote API:s for information. The external API:s 
can be slow, they have consumption restrictions and application also has to be able to handle big loads. Those are the reasons to that
this service is built using reactive streams. Reactive streams are asynchronous and has non-blocking back pressure.

##### Cache
Cache is used to reduce response time and handle the problem of MusicBrainz API returning 503 every few requests.
 
###Implementation

 ##### Tools
 * Spring Boot 2.0 M1
 * Spring Framework 5 RC1
 * Reactor 3.0.7
 * JUnit 4
 * Mockito
 * Docker
 
 ### Build and run
 ##### Spring JAR
 1. Clone the project
 2. Go to the root directory of the project
 3. Run in terminal: <code>mvn spring-boot:run</code>
 
 ##### Docker
 1. Define <code>DOCKER_HOST</code> and <code>DOCKER_CERT_PATH</code> in <code>build-with-docker.sh</code> script
 2. Run the <code>build-with-docker.sh</code> script
 3. Run the <code>run-with-docker.sh</code> script
 
 ### Known issues
 ##### Cover Art Archive redirect
 Cover Art Archive API redirects request two times. Currently, with reactive WebClient in Spring Framework 5 RC1, it
 looks like redirect has to be handled manually. Manual representation should be rewritten.