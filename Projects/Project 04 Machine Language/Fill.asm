// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel;
// the screen should remain fully black as long as the key is pressed. 
// When no key is pressed, the program clears the screen, i.e. writes
// "white" in every pixel;
// the screen should remain fully clear as long as no key is pressed.

// Put your code here.

(START)
	@counter
	M=0

(LOOP)
	@KBD		// check if key is being pressed
	D=M
	@WHITE
	D;JEQ		// if not go to white (D == 0)
	@BLACK
	D;JGT		// if true go to black (D > 0)
	
(WHITE)
	@counter		// go to counter
	D=M				// get counter value
	@SCREEN			// A = 16384 first pixel of screen
	A=D+A			// A = RAM[SCREEN+counter] to go to current pixel
	M=0				// paint it white
	@counter		// increment counter
	MD=M+1
	@8192			// check if at final pixel
	D=D-A			// 8192 - counter
	@START
	D;JEQ			// reset if at final pixel
	@LOOP
	D;JLT			// keep painting white
	
(BLACK)
	@counter
	D=M				// get counter value
	@SCREEN
	A=D+A			// add it to screen to get current position
	M=-1			// paint it black
	@counter
	MD=M+1			// increment counter
	@8192
	D=D-A			// check if at the last pixel
	@START
	D;JEQ			// reset if at final pixel
	@LOOP
	D;JLT			// keep painting black
@START
0;JMP				// infinite loop to reset just in case