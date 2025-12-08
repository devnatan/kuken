FROM gradle:9.2.1-jdk21-jammy
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
ENV PRODUCTION=true
CMD ["gradle", "run", "--no-daemon"]