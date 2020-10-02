## Areas of Improvement
- MapGameService.checkForWin will construct the winning conditions on every
invocation. These conditions never change, so they could be made static/constant

- MapGameService.getGameState and MapGameService.placeMark share the first
16 lines of code, that could be refactor to separate method.

- MapGameService.checkForWin uses encoding of winning condition that is not
at all clear. It would be nice to see a comment in the code or in Readme.md
about this.

- GameRepository and PlayerRepository are backed by ConcurrentHashMap however
the Game and Player themselves are not thread-safe. This can lead to inconsistent
state due to concurrent nature of HTTP.

- in the build.sh there's "mvn clean install" followed by "mvn clean package".
In this context "mvn clean install" is redundant.

- in the run.sh "docker-compose build" will run even if mvn before it fails

- "docker-compose" is not a core part of docker. This put extra dependency on the
building machine.

- the run.sh uses "docker-compose" which is not part of core docker.

- when a player tries to place mark out of his turn a 200 response is returned
while the specification asked for non 200 codes for error conditions.