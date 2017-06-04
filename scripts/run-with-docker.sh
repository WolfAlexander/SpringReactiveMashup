#!/usr/bin/env bash

echo Starting Spring Reactive Machup Service
docker run -d -p "8000:8000" -e "SPRING_PROFILES_ACTIVE=production" music-mashup/spring-reactive-mashup