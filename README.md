## Mashup service
This REST API is a mashup of MusicBrainz, Wikipedia and Cover Art Archive API:s to provide detailed
information about music artists.

 ### Functional View
 The consumer of this API provides a MBID (MusicBrainz Identifier) and gets back 
 the Wikipedia description of the artist and list of all the albums created by the 
 artist, available at MusicBrainz. The album description includes the identification
 number, title and cover image.
 
 ### Design
 //TODO: Add class diagram
 
 ### Tools
 * Spring Boot 2.0 M1
 * Spring Framework 5 RC1
 * Reactor 3.0.7
 * JUnit 4
 * Mockito
 * Docker
 
 ### Installation and running
 ##### Maven JAR
 1. Clone the project
 2. Go to the root directory of the project
 3. Run in terminal: <code>mvn spring-boot:run</code>
 
 ##### Docker
 
 ### Problems