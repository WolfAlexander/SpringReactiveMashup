[![Build Status](https://travis-ci.org/WolfAlexander/SpringReactiveMashup.svg?branch=master)](https://travis-ci.org/WolfAlexander/SpringReactiveMashup)

## Mashup service
This REST API is a mashup of MusicBrainz, Wikipedia and Cover Art Archive API:s to provide detailed
information about music artists.

### Functional View
 ##### Description
 The consumer of this API provides a MBID (MusicBrainz Identifier) and gets back 
 the Wikipedia description of the artist and list of all the albums created by the 
 artist, available at MusicBrainz. The album description includes the identification
 number, title and cover image.
 
 ##### API Endpoints
 * Artist details by MBID: HTTP GET request to ```/v1/{mbid}```
 
     Example:
     
     Request: ```/v1/5b11f4ce-a62d-471e-81fc-a69a8278c7da```
     
     Response JSON:
     ```json
     {
       "mbid": "5b11f4ce-a62d-471e-81fc-a69a8278c7da",
       "description": "<p><b>Nirvana</b> was an American rock band formed by singer and guitarist Kurt Cobain and bassist Krist Novoselic in Aberdeen, Washington, in 1987. Nirvana went through a succession of drummers, the longest-lasting being Dave Grohl, who joined in 1990. Despite releasing only three full-length studio albums in their seven-year career, Nirvana has come to be regarded as one of the most influential and important alternative bands in history. Though the band dissolved in 1994 after the suicide of Cobain, their music maintains a popular following and continues to influence modern rock and roll culture.</p>\n<p>In the late 1980s, Nirvana established itself as part of the Seattle grunge scene, releasing its first album, <i>Bleach</i>, for the independent record label Sub Pop in 1989. They developed a sound that relied on dynamic contrasts, often between quiet verses and loud, heavy choruses. After signing to major label DGC Records, Nirvana found unexpected success with \"Smells Like Teen Spirit\", the first single from the band's second album <i>Nevermind</i> (1991). Nirvana's sudden success widely popularized alternative rock, and Cobain found himself referred to in the media as the \"spokesman of a generation\", with Nirvana considered the \"flagship band\" of Generation X. Nirvana's third studio album, <i>In Utero</i> (1993), released to critical acclaim, featured an abrasive, less mainstream sound and challenged the group's audience.</p>\n<p>Nirvana's active career ended following the death of Cobain in 1994, but various posthumous releases have been issued since, overseen by Novoselic, Grohl, and Cobain's widow Courtney Love. Since its debut, the band has sold over 25 million records in the United States alone, and over 75 million records worldwide, making them one of the best-selling bands of all time. Nirvana was inducted into the Rock and Roll Hall of Fame in 2014, in its first year of eligibility.</p>\n<p></p>",
       "albums": [
         {
           "id": "01cf1391-141b-3c87-8650-45ade6e59070",
           "title": "Incesticide",
           "coverImage": "http://coverartarchive.org/release/726ca690-fe70-4d3f-86b5-f8347f1a1af0/1289818412.jpg"
         },
         {
           "id": "178b993e-fa9c-36d3-9d73-c5a8ba0c748d",
           "title": "Wipeout",
           "coverImage": "http://coverartarchive.org/release/00681632-b53b-4aae-89c2-470150f33fe3/1898023189.jpg"
         }
      //more albums
       ]
     }
    ```
 ##### Error responses
 * HTTP 503 "One of external services is unavailable. Try again a few seconds later." - an external service is not available. Since currently
 MusicBrainz API is returning 503 every few seconds, it most likely is the problem. Try to call the API again.
 * HTTP 404 "Not found" - artist with given MBID has not been found.
 * HTTP 400 "Invalid mbid." - invalid MBID is entered. Check if the MBID entered has proper format: [a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}
 * HTTP 500 "An unknown error happen during the request" - some kind of unknown error happen that not supposed to happen.
 
### Design choices
##### General
This a single service layered application. Application receives a HTTP request to the REST controller. Artist Mashup component
gathers data from different services using service clients. Each service client has their own objects and JSON deserializers
to convert JSON to POJO (are not shown in the model below to keep the model clean ). 
Artist Mashup transforms the information to Artist and Album representations and returns to the REST controller. An
Error Handler handles any error that might happend during the execution and converts them to a user friendly message with
HTTP status code. 

![Conceptual model](/docs/SpringReaciveMashup.png)
*Conceptual model showing major components*

##### Reactive streams
A mashup service is requesting several remote API:s for information. The external API:s 
can be slow, they have consumption restrictions and application also has to be able to handle big loads. 
Those are the reasons to that
this service is built using reactive streams. Reactive streams are asynchronous and has non-blocking back pressure.

##### Cache
Cache is used to reduce response time and handle the problem of MusicBrainz API returning 503 every few requests.
 
### Implementation

 ##### Tools
 * Spring Boot 2.0 M1
 * Spring Framework 5 RC1
 * Reactor 3.0.7
 * JUnit 4
 * Mockito
 * Docker
 * Maven
 
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