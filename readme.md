Memory Game (Dec 2004)
----------------------

![](https://github.com/hectormoralespiloni/Memory/blob/master/memory_full.jpg)

1. SUMMARY 
	* This is a port of the classic cards memory game; it runs as a Java applet
	on any PC or Mac.

2. REQUIREMENTS TO RUN THE APPLET
	* Java 2 Runtime Environment
	* A web browser
	
3. HOW TO PLAY
	* Compiled class files are located in the "build" folder, just open memory.html
	* Click on a card to open it, choose a second one and if you get the pair you get 
	another move (until you fail)
	
4. HOW TO COMPILE
	* The easiest way to go is download the Netbeans IDE from: netbeans.org
	* There's already an nbproject folder for netbeans you just have to 
	select the memory folder in netbeans to open it.

5. CODE STURCTURE
	* The images folder contains all the jpg used in the game.
	* There is only 1 class: memory which extends JApplet and implements a
	runnable interface. The click coordinates of the board are set manually and
	the initialization of the board is set randomly (random card for each position)
	The code is pretty straightforward to follow, the CPUmove() method picks a
	random card from the board and right now every unhidden card remains in CPU's
	memory which makes the game very challenging. 
	Some people complains because they don't have good memory, you can set the CPU
	memory based on a random pick, for instance.