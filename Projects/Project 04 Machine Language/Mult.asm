// check if R0 or R1 is 0, if so skip to end
// otherwise continue
	@2			// reset answer
	M=0

	@0			// A = 0
	D=M			// check if @0 == 0 (D = data in RAM[0])
	@END
	D;JEQ		// skip to end if zero

	@1			// A = 1
	D=M			// check if RAM[1] == 0 (D = data in RAM[1])
	@END
	D;JEQ		// skip to end if 0

(LOOP)
	@2			// set A to where product is being stored (A = 2)
	D=M			// take the product already calculated (D = data in RAM[2])
	@0			// go to R0
	D=D+M		// and add it to the product (M+D)
	@2
	M=D			// store the product back into R2
	@1			// take value in R1
	MD=M-1		// and decrement how many times we need to add R0 to R2 (and store it in D register to compare)
	@END
	D;JEQ		// check if R1 is at 0 and end loop
	@LOOP
	D;JGT		// otherwise continue multiplication

(END)			// infinite loop to end program
	@END
	0;JMP