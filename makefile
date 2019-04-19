JCC = javac



JFLAGS = -g



all: cserver.class contestmeister.class contestant.class createserver createmeisterclient createcontestantclient

cserver.class: cserver.java
	$(JCC) $(JFLAGS) cserver.java

contestmeister.class: contestmeister.java
	$(JCC) $(JFLAGS) contestmeister.java

contestant.class: contestant.java
	$(JCC) $(JFLAGS) contestant.java

createserver: cserver.class
	$ echo 'java -XX:ParallelGCThreads=2 cserver' > cserver
	$ chmod 777 cserver

createmeisterclient: contestmeister.class
	$ echo 'java -XX:ParallelGCThreads=2 contestmeister $$1 $$2' > contestmeister
	$ chmod 777 contestmeister

createcontestantclient: contestant.class
	$ echo 'java -XX:ParallelGCThreads=2 contestant $$1 $$2' > contestant
	$ chmod 777 contestant
